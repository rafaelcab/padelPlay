```markdown
# Design System Strategy: Kinetic Precision

## 1. Overview & Creative North Star
The Creative North Star for this design system is **"The Kinetic Court."** 

Padel is a game of high-speed geometry, rebounds, and social energy. To move beyond a generic "sporty" template, this system rejects static, boxy layouts in favor of an **Editorial-Athletic** aesthetic. We achieve this through "The Power of the Void"—using expansive negative space punctuated by high-voltage color hits (`primary`) and bold, asymmetric typography. 

The experience must feel like a premium membership club: elite but high-energy. We avoid "standard" UI by layering surfaces like the glass walls of a padel court, utilizing transparency and depth rather than rigid lines to define the field of play.

---

## 2. Colors: Tonal Depth & The "No-Line" Rule
The palette is rooted in the high-contrast tension between the obsidian depths of the court and the neon vibration of the ball.

### The Palette
*   **Primary (`#f4ffc6` / `#d1fc00`):** Use sparingly for "High-Voltage" moments—CTAs, active states, and score highlights.
*   **Surface Hierarchy:**
    *   `surface` (#0c0e10): The "Base Plate."
    *   `surface-container-low` (#111416): Secondary content areas.
    *   `surface-container-high` (#1d2023): Elevated cards and interactive modules.
*   **Error (`#ff7351`):** A "Clay Court" orange-red, used for high-urgency alerts.

### The "No-Line" Rule
**Explicit Instruction:** Designers are prohibited from using 1px solid borders to section content. Boundaries must be defined solely through background color shifts. 
*   *Example:* A player’s match history card (`surface-container-high`) should sit directly on the `surface` background. The elevation is felt through the color shift, not a stroke.

### Glass & Gradient Signature
To achieve a "Premium Sport" feel, use **Glassmorphism** for floating headers or navigation bars. 
*   **Token:** Use `surface` at 70% opacity with a `20px` backdrop-blur. 
*   **Signature Texture:** Apply a subtle linear gradient from `primary` (#f4ffc6) to `primary-container` (#d1fc00) on primary buttons to create a "glowing ball" effect that flat color cannot replicate.

---

## 3. Typography: Editorial Authority
We utilize a dual-typeface system to balance "High-Performance" with "Social Accessibility."

*   **Display & Headline (Plus Jakarta Sans):** These are your "Power Shots." Use `display-lg` (3.5rem) with tight tracking (-0.02em) for hero headers. This typeface’s geometric curves mirror the rounded corners of the padel racket.
*   **Body & Labels (Inter):** The "Footwork." Inter provides maximum legibility for dense match data and player stats. 
*   **Visual Hierarchy:** Always pair a `display-sm` headline in `primary` color with a `body-md` description in `on-surface-variant` (#aaabad) to create an immediate editorial focal point.

---

## 4. Elevation & Depth: Tonal Layering
We do not use shadows to create "pop"; we use them to create "atmosphere."

*   **The Layering Principle:** Stack surfaces to indicate importance. A "Match Invite" modal should be `surface-container-highest` (#232629) placed over a blurred `surface` background.
*   **Ambient Shadows:** For floating action buttons or high-priority cards, use an ultra-diffused shadow: `0px 20px 40px rgba(0, 0, 0, 0.4)`. Never use pure black shadows; always tint them with the background hue to maintain tonal richness.
*   **The "Ghost Border" Fallback:** If accessibility requires a container definition, use `outline-variant` (#46484a) at **15% opacity**. It should be felt, not seen.

---

## 5. Components: Functional Geometry

### Buttons: The Kinetic Trigger
*   **Primary:** `primary-fixed` (#d1fc00) background with `on-primary-fixed` (#3c4a00) text. Shape: `rounded-full`.
*   **Secondary:** `surface-container-highest` with a `Ghost Border`. This provides a "stealth" look for secondary actions like "View Rules."

### Cards: The Court View
*   **Rule:** Forbid all divider lines. 
*   **Structure:** Use `spacing-6` (1.5rem) of internal padding. Separate player names from match times using a background shift to `surface-container-low` for the time-slot "badge."

### Chips: Live Stats
*   **Action Chips:** Use `rounded-md` (0.75rem). For "Active" filters, use a `primary` glow (subtle inner shadow) to signify the "Live" nature of the game.

### Input Fields: Focused Action
*   **Style:** Minimalist. No bottom line. Use a `surface-container-high` fill with `rounded-md` corners. On focus, transition the background to `surface-bright` and add a `primary` "Ghost Border" at 20% opacity.

### Custom Component: The "Match-Pulse" Progress Bar
For live matches, use a thick (`spacing-2`) bar with a `surface-container-highest` track and a `primary` fill that has a subtle horizontal pulse animation to signify active play.

---

## 6. Do’s and Don’ts

### Do:
*   **Do** use intentional asymmetry. Place a player’s action shot overlapping the edge of a `surface-container` to create a sense of movement.
*   **Do** use `primary` (#d1fc00) as a laser-pointer. It should guide the eye to the most important "Win" state on the screen.
*   **Do** use high-quality, desaturated imagery of courts, then overlay `primary` color elements to make the UI feel "integrated" into the sport.

### Don't:
*   **Don't** use standard `1px` borders or dividers. It makes the app feel like a generic utility rather than a premium club.
*   **Don't** use pure white (#ffffff) for large text blocks. Use `secondary` (#e2e2e5) to reduce eye strain against the deep charcoal background.
*   **Don't** use sharp corners. Everything in padel—from the ball to the racket to the swing—is fluid. Stick strictly to the `Roundedness Scale` (defaulting to `md` or `xl`).

---

## 7. Spacing & Rhythm
This system breathes. Use `spacing-12` (3rem) and `spacing-16` (4rem) to separate major sections. We are not trying to cram information; we are curating an experience. High-end design is defined by the confidence to leave space empty.```