# SpawnFilter

A small server-side Fabric mod that stops new players from spawning on top of stuff that will instantly kill them or otherwise ruin their first 5 seconds in the world.

If you've ever set up a server and watched everyone spawn on top of trees, this fixes that.

## What it actually does

When a player joins for the first time, SpawnFilter looks at the block underneath them. If that block is on the blocklist, the mod searches outward for a nearby surface spot that's safe, teleports the player there, and updates their spawn point so they respawn there too.

It only does this once per player. After the first join the player gets a flag saved to their NBT (`spawnfilter:hasSpawned`), so the mod leaves them alone forever after that.

## Defaults

Out of the box it blocks spawns on:

**Individual blocks**
- `minecraft:water`
- `minecraft:lava`
- `minecraft:magma_block`

**Block tags**
- `#minecraft:fire`
- `#minecraft:wool`
- `#c:fluids`
- `#minecraft:leaves`

## Configuration

The config file lives at `config/spawnfilter.toml` and gets generated on first run. It looks like this:

```toml
[spawnfilter]
# Individual blocks the player cannot spawn on
blocked_blocks = [
    "minecraft:water",
    "minecraft:lava",
    "minecraft:magma_block"
]

# Block tags whose member blocks the player cannot spawn on
# Use the # prefix convention to denote tags, mirroring Minecraft tag syntax
blocked_tags = [
    "#minecraft:fire",
    "#minecraft:wool",
    "#c:fluids",
    "#minecraft:leaves"
]
```

Add whatever block IDs or tag IDs you want. The `#` prefix on tags is required, same as you'd use in a datapack.

The config gets re-resolved when the server starts and when datapacks reload (`/reload`), so if you change tags via a datapack the changes take effect without restarting.

## Supported versions

- 1.20.1
- 1.21.x
- 26.1.x

Fabric only. Requires Fabric API.

## Installation

Drop the jar in your server's `mods` folder along with Fabric API. That's it. The mod runs on the server side. Clients don't need it unless you are trying to fix this issue in Singleplayer as well.

## Why it exists

CozyStudios runs servers and modpacks with random first-spawn placement and the failure rate on bad spawns was high enough to be annoying. There were existing solutions but most of them either ran a full safe-spawn rewrite on every respawn or required complicated region setup. This one does the minimum thing: check once on first join, move them if needed, get out of the way.

## Credits

- Tokimi
- CozyStudios

## Links

- Discord: https://discord.gg/qHB5D9uzHe
- Source: https://github.com/CozyCord/SpawnFilter
- Issues: https://github.com/CozyCord/SpawnFilter/issues
- Modrinth: https://modrinth.com/organization/cozystudios

Licensed ARR.
