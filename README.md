# Pixelmon Battle Info
## Description
A quality of life (QoL) mod that adds some features to the Battle screen in Pixelmon.
This includes
    a tooltip displaying information about enemies and your own Pixelmon,
    removing nicknames,
    and showing the type effectiveness for your moves (1st enemy ONLY).

This mod is intended to only be used client-side ONLY. Works in single-player.

- Versions
  - Minecraft 1.16.5
  - Forge 36.2.34
  - Pixelmon 9.1.7 - Could possibly work for earlier versions, haven't tried

## Current Features
- **Remove nicknames from all Pixelmon**
  - Useful if you just don't know what you're up against because it has a nickname


- **Tooltip**
  - Your first Pixelmon (Maybe you forgot )
    - Species, HP, Types, Ability, Held Item
  - Any enemy Pixelmon
    - Species, Types
    - **_Optional - See Configs_**
      - Exact Current HP
      - Ability
      - Held Item
      - Moveset
        - Type + Effectiveness to your current Pixelmon
        - PP at start of battle (does not update)
        - Category, Power, Accuracy


- **Type Effectiveness Shown in Choose Attack UI**
  - Shows how type effective your moves are against the first (left-most) enemy

### Type Effectiveness Color Chart

| Effectiveness | Multiplier |                       Color                       |
|---------------|:----------:|:-------------------------------------------------:|
| Immune        |     0      |  <span style="color: darkgray;">DARK GRAY</span>  |
| Barely        |    1/4     |   <span style="color: darkred;">DARK RED</span>   |
| Not Very      |    1/2     |       <span style="color: red;">RED</span>        |
| Normal        |     1      |     <span style="color: white;">WHITE</span>      |
| Super         |     2      |     <span style="color: green;">GREEN</span>      |
| Maximum       |     4      | <span style="color: darkgreen;">DARK GREEN</span> |


## Planned Features
- **Battle Discovery**
  - For when certain parts are no longer exposed to the Client
  - What to Discover about Enemies in Battle
    - Moveset - overridden by `TooltipEnemyMoveset`
      - PP Tracking
    - Ability - overridden by `TooltipEnemyAbility`
    - Held Item - overridden by `TooltipEnemyHeldItem`
  - Config


- **Track Stat Increases and Reductions**
  - When stats are increased / reduced
    - Display in Tooltip
    - Display near Pixelmon battle element?
  - Config


## Configs
- `RemoveNicknames`
  - _**Default** = true_
  - Replace Pixelmon Nicknames with Localized Species Name
- `YourMovesetEffectiveness`
  - _**Default** = true_
  - Display your Moveset Effectiveness (1st enemy ONLY)
- `PixelmonTooltip`
  - _**Default** = true_
  - Display Tooltip when Hovering Pixelmon while in Battle (or hold 'alt' for enemy, 'shift' for yours)
  - **Related Configs**
    - `TooltipEnemyHeldItem`
      - _**Default** = true_
      - In PixelmonTooltip, inherently know enemy Held Item
    - `TooltipEnemyMoveset`
      - _**Default** = true_
      - In PixelmonTooltip, inherently know enemy Moveset
    - `TooltipEnemyHP`
      - _**Default** = true_
      - In PixelmonTooltip, inherently know enemy exact HP
    - `TooltipEnemyAbility`
      - _**Default** = true_
      - In PixelmonTooltip, inherently know enemy Ability