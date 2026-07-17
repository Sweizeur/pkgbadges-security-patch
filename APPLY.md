# Apply PKGBadges 7.0 security patch

## For mod developers (PoyrazPKG)

1. Clone or open your PKGBadges NeoForge 7.x source tree.
2. Replace `src/main/java/pkgbadges/network/CraftItemPacket.java` with `patched/CraftItemPacket.java` from this release.
3. Rebuild and publish as **7.0.1** or **7.1**.

Or apply the unified diff:

```bash
cd /path/to/pkgbadges-source
patch -p3 < CraftItemPacket-7.0-security.patch
```

Paths in the patch assume:

```
pkgbadges/network/CraftItemPacket.java
```

## For server operators (no source)

Until an official build is published:

1. **Remove** PKGBadges from the server, or
2. Ask the maintainer to merge this patch, or
3. Block packet `pkgbadges:craft_item` with a dedicated mixin mod.

This release does **not** ship a pre-built JAR (no public upstream sources to rebuild against).

## Verify fix

After patching, sending `pkgbadges:craft_item` with `minecraft:diamond` must **not** add diamonds unless the player has badge fabric materials.

Legitimate badge crafts must still work.
