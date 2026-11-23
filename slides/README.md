# Slides

Slidev deck for the 30-minute talk. Speaker notes (with timing, commands, fallbacks and cut-if-late hints) are in the HTML comments under each slide.

## Setup (Node 18+ required)

```bash
cd slides
npm install
npx playwright-chromium install chromium   # only needed for PDF export
npm run dev        # http://localhost:3030 ; presenter view at /presenter
npm run export     # writes autopilot-airline.pdf  (keep as the fallback)
```

## Before the talk

- Export the PDF and keep it on a USB stick and a second device.
- Run the deck locally; do not depend on venue Wi-Fi.
- Pre-warm the Maven cache (`./mvnw -o verify` at every demo tag).
- Record fallback clips into `public/` (`demo3.mp4`) and save the baseline report as `public/before.txt`.
