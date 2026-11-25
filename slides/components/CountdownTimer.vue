<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'

const props = withDefaults(defineProps<{
  /** Total talk time in minutes. */
  minutes?: number
  /** Start counting down as soon as the deck is opened. */
  autostart?: boolean
}>(), {
  minutes: 10,
  autostart: true,
})

const totalMs = computed(() => props.minutes * 60_000)

/** Milliseconds already elapsed, accumulated across pause/resume cycles. */
const elapsedMs = ref(0)
/** performance.now() at the moment the clock last started running; null while paused. */
const startedAt = ref<number | null>(null)
const running = computed(() => startedAt.value !== null)

const halfRung = ref(false)
const endRung = ref(false)
const pulsing = ref(false)

let frame = 0

const remainingMs = computed(() => Math.max(0, totalMs.value - elapsedMs.value))
const finished = computed(() => remainingMs.value === 0)
/** 0 → 1 as the talk progresses; drives how much of the ring is filled. */
const progress = computed(() => Math.min(1, elapsedMs.value / totalMs.value))

const label = computed(() => {
  const totalSeconds = Math.ceil(remainingMs.value / 1000)
  const mm = Math.floor(totalSeconds / 60)
  const ss = totalSeconds % 60
  return `${mm}:${String(ss).padStart(2, '0')}`
})

/* ---------------------------------------------------------------- bell ---- */

let audioCtx: AudioContext | null = null

function ctx(): AudioContext | null {
  if (typeof window === 'undefined')
    return null
  const Ctor = window.AudioContext ?? (window as any).webkitAudioContext
  if (!Ctor)
    return null
  audioCtx ??= new Ctor()
  if (audioCtx!.state === 'suspended')
    void audioCtx!.resume()
  return audioCtx
}

/** One struck-bell strike: a few inharmonic partials with exponential decay. */
function strike(at: number, base: number, gain: number) {
  const ac = ctx()
  if (!ac)
    return
  const partials: Array<[ratio: number, level: number, decay: number]> = [
    [1, 1, 2.4],
    [2, 0.5, 1.6],
    [2.76, 0.28, 1.1],
    [5.4, 0.12, 0.7],
  ]
  for (const [ratio, level, decay] of partials) {
    const osc = ac.createOscillator()
    const env = ac.createGain()
    osc.type = 'sine'
    osc.frequency.value = base * ratio
    env.gain.setValueAtTime(0, at)
    env.gain.linearRampToValueAtTime(gain * level, at + 0.004)
    env.gain.exponentialRampToValueAtTime(0.0001, at + decay)
    osc.connect(env).connect(ac.destination)
    osc.start(at)
    osc.stop(at + decay + 0.05)
  }
}

function ring(strikes: number) {
  const ac = ctx()
  if (!ac)
    return
  for (let i = 0; i < strikes; i++)
    strike(ac.currentTime + 0.02 + i * 0.6, 880, 0.16)

  pulsing.value = true
  window.setTimeout(() => (pulsing.value = false), 1800)
}

/** Browsers keep audio suspended until a gesture; the first key/click unlocks it. */
function unlockAudio() {
  ctx()
}

/* ---------------------------------------------------------------- clock ---- */

function tick() {
  if (startedAt.value !== null) {
    const now = performance.now()
    elapsedMs.value += now - startedAt.value
    startedAt.value = now
  }

  if (!halfRung.value && elapsedMs.value >= totalMs.value / 2) {
    halfRung.value = true
    ring(1)
  }

  if (elapsedMs.value >= totalMs.value) {
    elapsedMs.value = totalMs.value
    startedAt.value = null // progress stops at the end
    if (!endRung.value) {
      endRung.value = true
      ring(2)
    }
    return // no further frames: the ring is frozen full and red
  }

  frame = requestAnimationFrame(tick)
}

function start() {
  if (finished.value || running.value)
    return
  startedAt.value = performance.now()
  cancelAnimationFrame(frame)
  frame = requestAnimationFrame(tick)
}

function pause() {
  if (!running.value)
    return
  elapsedMs.value += performance.now() - startedAt.value!
  startedAt.value = null
  cancelAnimationFrame(frame)
}

function toggle() {
  unlockAudio()
  running.value ? pause() : start()
}

function reset() {
  cancelAnimationFrame(frame)
  elapsedMs.value = 0
  startedAt.value = null
  halfRung.value = false
  endRung.value = false
  pulsing.value = false
}

onMounted(() => {
  window.addEventListener('pointerdown', unlockAudio, { once: true })
  window.addEventListener('keydown', unlockAudio, { once: true })
  if (props.autostart)
    start()
})

onBeforeUnmount(() => {
  cancelAnimationFrame(frame)
  window.removeEventListener('pointerdown', unlockAudio)
  window.removeEventListener('keydown', unlockAudio)
  void audioCtx?.close()
  audioCtx = null
})

/* ----------------------------------------------------------------- ring ---- */

const R = 44
const CIRCUMFERENCE = 2 * Math.PI * R
const dashOffset = computed(() => CIRCUMFERENCE * (1 - progress.value))
const color = computed(() => (finished.value ? '#ef5b5b' : '#6db33f'))

const title = computed(() => {
  if (finished.value)
    return `Time is up — double-click to reset the ${props.minutes}-minute timer`
  return `${label.value} left of ${props.minutes}:00 — click to ${running.value ? 'pause' : 'resume'}, double-click to reset`
})
</script>

<template>
  <div
    class="talk-timer"
    :class="{ 'is-finished': finished, 'is-paused': !running && !finished, 'is-pulsing': pulsing }"
    role="timer"
    :aria-label="`Talk timer, ${label} remaining`"
    :title="title"
    @click="toggle"
    @dblclick.stop="reset"
  >
    <svg class="talk-timer-ring" viewBox="0 0 100 100" aria-hidden="true">
      <circle class="talk-timer-track" cx="50" cy="50" :r="R" />
      <circle
        class="talk-timer-fill"
        cx="50"
        cy="50"
        :r="R"
        :stroke="color"
        :stroke-dasharray="CIRCUMFERENCE"
        :stroke-dashoffset="dashOffset"
      />
    </svg>
    <div class="talk-timer-label" :style="{ color: finished ? color : undefined }">{{ label }}</div>
  </div>
</template>

<style scoped>
.talk-timer {
  position: absolute;
  right: 1rem;
  bottom: 2.6rem;
  z-index: 20;
  width: 4.6rem;
  height: 4.6rem;
  display: grid;
  place-items: center;
  cursor: pointer;
  opacity: 0.42;
  transition: opacity 0.25s ease, transform 0.25s ease;
  user-select: none;
  -webkit-user-select: none;
}
.talk-timer:hover {
  opacity: 0.95;
}
.talk-timer.is-finished,
.talk-timer.is-pulsing {
  opacity: 0.9;
}

.talk-timer-ring {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  transform: rotate(-90deg);
}
.talk-timer-track {
  fill: rgba(0, 0, 0, 0.35);
  stroke: rgba(255, 255, 255, 0.16);
  stroke-width: 11;
}
.talk-timer-fill {
  fill: none;
  stroke-width: 11;
  stroke-linecap: round;
  transition: stroke 0.6s ease;
}

.talk-timer-label {
  position: relative;
  font-size: 0.86rem;
  font-weight: 700;
  line-height: 1;
  font-variant-numeric: tabular-nums;
  letter-spacing: -0.01em;
}
.talk-timer.is-paused .talk-timer-label {
  opacity: 0.55;
}

/* A brief, quiet halo so the bell is visible as well as audible. */
.talk-timer.is-pulsing::after {
  content: '';
  position: absolute;
  inset: -0.2rem;
  border-radius: 50%;
  border: 2px solid currentColor;
  color: v-bind(color);
  animation: talk-timer-pulse 0.9s ease-out 2;
}
@keyframes talk-timer-pulse {
  from { transform: scale(0.92); opacity: 0.8; }
  to { transform: scale(1.25); opacity: 0; }
}
@media (prefers-reduced-motion: reduce) {
  .talk-timer.is-pulsing::after { animation: none; }
}
</style>
