# Apply PKGBadges 7.0 security patch

See **[README.md](README.md)** — section **« Patcher son serveur »** for full server operator instructions (French).

Quick links:

- **Remove mod** — delete `PKGBadges-7.0.jar` from `mods/`
- **Patch JAR** — replace `pkgbadges/network/CraftItemPacket.class` using `patched/CraftItemPacket.java`
- **Official fix** — track https://github.com/PoyrazPKG/PKG-TEAM/issues/1

## Verify

After patching, `pkgbadges:craft_item` with `minecraft:diamond` must **not** grant diamonds. Legitimate badge crafts must still work.
