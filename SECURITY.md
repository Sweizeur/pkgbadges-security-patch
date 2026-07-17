# Security advisory — PKGBadges CraftItemPacket

## Summary

PKGBadges **7.0** (NeoForge 1.21.1) allows arbitrary item creation via client packet `pkgbadges:craft_item`.

## Affected versions

- **PKGBadges 7.0** (NeoForge) — confirmed vulnerable via JAR analysis  
- Older Forge 5.x/6.x may use different networking (`PokeFashionCraft`) — not audited here  

## Not affected

- Items that have a defined fabric recipe in `getRequiredMaterials` still require materials.

## Severity

**Critical** — survival multiplayer, no OP, ~4s cooldown per item.

## Recommendation for server operators

Until an official fix is released:

1. Remove PKGBadges from the server modlist, or  
2. Block `pkgbadges:craft_item` with a mixin / packet filter, or  
3. Apply the patch in `patched/CraftItemPacket.java` and rebuild the mod.

## Recommendation for maintainers (PoyrazPKG)

Minimum fix in `hasRequiredMaterials` / `handle`:

```java
List<ItemStack> materials = getRequiredMaterials(item);
if (materials.isEmpty()) {
    return; // or show "Malzemeler eksik!"
}
```

Do **not** treat an empty requirement list as “materials satisfied”.

Also remove `Thread.sleep` on the server thread when granting items.

## Credits

Discovered during Cobblemon RPG Edition / ATM security audit (Sweizeur exploit-mffs project).
