# RejuvenationSacks

Paper plugin that manages custom portable Sack inventory. Provides basic item restriction features with MMOItems support.

## Features

- Custom inventories with contents stored on disk
- Any item can be given access to an UUID'd Sack inventory
- "Dummy" sacks can be made without UUID, and gain them on first use
- MiniMessage support for item text
- TODO: Sacks may have shared inventories by matching UUIDs

## Installation

1. Download the latest release of the plugin.
2. Place the plugin JAR file in the `plugins` directory of your Paper/Purpur/compatible fork server.
3. Start or restart your server to load the plugin.

## Commands

- `/prsacks reload` - Reloads the plugin.
  - **Permission**: `rejuvenationsacks.reload`

- `/gensack <template_id> [player_name]` - Generates a dummy, UUID-less sack and sends it to the target player (or self if none).
  - **Permission**: `rejuvenationsacks.generate`

## Configuration

Different types of sacks are defined on the `templates.yml` file, where you may define sack rows, item material, item name, item lore, blocked slots and the restriction masks. If you're having issues generating a new sack, odds are your config is malformed.

#### Example `templates.yml` Entry
```
straw_basket:
  rows: 1
  material: WHEAT
  name: "<#aa77ff>Straw Basket"
  lore:
    - "<#ddaaff>Bag"
    - ""
    - "<#aa55ff>-=-=-=-=-"
    - ""
    - "<#ddaaff>A quaint little basket for a picnic."
  blocked-slots: [0,1,7,8]
  mask:
    mmotype: SNACK
```

## Development

### Prerequisites

- Java 21 or higher
- Maven


