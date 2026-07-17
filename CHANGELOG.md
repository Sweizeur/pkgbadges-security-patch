# Changelog

## v7.0.1-security (2026-07-17)

Security release for **PKGBadges 7.0** (NeoForge 1.21.1).

### Fixed

- **Critical:** `pkgbadges:craft_item` allowed free 1× give of any item id when `getRequiredMaterials` returned an empty list.
- **DoS:** removed `Thread.sleep(4000)` on the server thread during item grant.
- Reject `Items.AIR` / unknown registry entries.

### Files

- `CraftItemPacket-7.0-security.patch` — unified diff
- `CraftItemPacket-patched.java` — full patched source
- `CraftItemPacket-reference.java` — vulnerable reference (decompiled from official 7.0 JAR)

### Upstream

- Official PKGBadges 7.0 remains vulnerable on Modrinth/CurseForge.
- Disclosure: https://github.com/PoyrazPKG/PKG-TEAM/issues/1
