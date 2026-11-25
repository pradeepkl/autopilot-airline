<script setup lang="ts">
import { computed } from 'vue'
import { useNav } from '@slidev/client'
import CountdownTimer from './components/CountdownTimer.vue'

const { slides, currentSlideNo, total, isPrintMode } = useNav()

function titleOf(no: number): string {
  const route = slides.value[no - 1]
  const title = route?.meta?.slide?.title
  return (title ?? `Slide ${no}`).replace(/^▶\s*/, '▶ ')
}

const prevTitle = computed(() => (currentSlideNo.value > 1 ? titleOf(currentSlideNo.value - 1) : null))
const nextTitle = computed(() => (currentSlideNo.value < total.value ? titleOf(currentSlideNo.value + 1) : null))
</script>

<template>
  <!-- One timer for the whole deck: GlobalBottom is mounted once in the slideshow. -->
  <CountdownTimer v-if="!isPrintMode" :minutes="10" />

  <div class="route-bar">
    <div class="route-side route-prev">
      <template v-if="prevTitle">← {{ prevTitle }}</template>
    </div>
    <div class="route-mid">{{ currentSlideNo }} / {{ total }}</div>
    <div class="route-side route-next">
      <template v-if="nextTitle">{{ nextTitle }} →</template>
    </div>
  </div>
</template>

<style scoped>
.route-bar {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 1rem;
  padding: 0.5rem 1.5rem;
  font-size: 0.85rem;
  opacity: 0.65;
  border-top: 1px solid rgba(128, 128, 128, 0.3);
  background: rgba(0, 0, 0, 0.25);
}
.route-side {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
.route-next {
  text-align: right;
}
.route-mid {
  text-align: center;
  font-variant-numeric: tabular-nums;
}
</style>
