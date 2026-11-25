<script setup lang="ts">
import { computed, ref } from 'vue'

// % of the application that stays on Spring Boot's defaults
const defaults = ref(80)

const control = computed(() => 100 - defaults.value)
// Effort grows faster than control: the heavier the tool, the more it costs (illustrative curve).
const effort = computed(() => Math.round(100 * Math.pow(control.value / 100, 1.5)))

const level = computed(() => {
  const c = control.value
  if (c === 0) return { n: 0, name: 'Pure autopilot', color: '#9aa0a6' }
  if (c <= 25) return { n: 1, name: 'Level 1 · Properties', color: '#6db33f' }
  if (c <= 50) return { n: 2, name: 'Level 2 · Customizers', color: '#4aa3ff' }
  if (c <= 75) return { n: 3, name: 'Level 3 · Dependency swap', color: '#f5b83d' }
  return { n: 4, name: 'Level 4 · Ownership', color: '#ef5b5b' }
})
</script>

<template>
  <div class="cs">
    <div class="cs-side cs-effort">
      <div class="cs-label">EFFORT</div>
      <div class="cs-value">{{ effort }}<span>%</span></div>
      <div class="cs-meter"><div class="cs-fill" :style="{ height: effort + '%' }"></div></div>
    </div>

    <div class="cs-center">
      <div class="cs-defaults">
        <span class="cs-big">{{ defaults }}%</span>
        <span class="cs-sub">defaults</span>
      </div>
      <input class="cs-range" type="range" min="0" max="100" step="1" v-model.number="defaults"
             :style="{ '--p': defaults + '%' }" aria-label="Percent of the app on Spring Boot defaults" />
      <div class="cs-ends"><span>◄ You own it</span><span>Autopilot ►</span></div>
      <div class="cs-level" :style="{ '--c': level.color }">{{ level.name }}</div>
    </div>

    <div class="cs-side cs-control">
      <div class="cs-label">CONTROL</div>
      <div class="cs-value">{{ control }}<span>%</span></div>
      <div class="cs-meter"><div class="cs-fill" :style="{ height: control + '%' }"></div></div>
    </div>
  </div>
</template>

<style scoped>
.cs {
  display: grid;
  grid-template-columns: 11rem 1fr 11rem;
  gap: 2rem;
  align-items: center;
  margin-top: 0.4rem;
}
.cs-side {
  --a: #b48cff;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.3rem;
  padding: 0.8rem 0.6rem;
  border: 2px solid var(--a);
  border-radius: 0.8rem;
  background: color-mix(in srgb, var(--a) 10%, transparent);
}
.cs-control { --a: #2dd4bf; }
.cs-label {
  font-size: 0.75rem;
  letter-spacing: 0.16em;
  font-weight: 700;
  color: var(--a);
}
.cs-value {
  font-size: 3rem;
  font-weight: 800;
  line-height: 1;
  font-variant-numeric: tabular-nums;
}
.cs-value span { font-size: 1.4rem; opacity: 0.7; margin-left: 0.1rem; }
.cs-meter {
  position: relative;
  width: 1.1rem;
  height: 5.5rem;
  border-radius: 0.6rem;
  background: rgba(255, 255, 255, 0.1);
  overflow: hidden;
  margin-top: 0.3rem;
}
.cs-fill {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--a);
  transition: height 0.12s linear;
}
.cs-center {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  gap: 0.6rem;
}
.cs-defaults { text-align: center; }
.cs-big {
  font-size: 3.4rem;
  font-weight: 800;
  color: #6db33f;
  font-variant-numeric: tabular-nums;
}
.cs-sub { font-size: 1.4rem; margin-left: 0.5rem; opacity: 0.85; }
.cs-range {
  -webkit-appearance: none;
  appearance: none;
  width: 100%;
  height: 0.9rem;
  border-radius: 0.5rem;
  outline: none;
  cursor: pointer;
  background: linear-gradient(90deg, #6db33f var(--p), #ef5b5b var(--p));
}
.cs-range::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 2rem;
  height: 2rem;
  border-radius: 50%;
  background: #fff;
  border: 4px solid #101010;
  box-shadow: 0 0 0 3px #fff;
}
.cs-range::-moz-range-thumb {
  width: 2rem;
  height: 2rem;
  border-radius: 50%;
  background: #fff;
  border: 4px solid #101010;
}
.cs-ends {
  display: flex;
  justify-content: space-between;
  font-size: 0.85rem;
  opacity: 0.7;
}
.cs-level {
  align-self: center;
  margin-top: 0.4rem;
  padding: 0.35rem 1rem;
  border: 2px solid var(--c);
  border-radius: 2rem;
  background: color-mix(in srgb, var(--c) 15%, transparent);
  font-weight: 700;
  font-size: 1rem;
}
</style>
