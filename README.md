# PKGBadges — Security patch reference (CraftItemPacket)

**Upstream:** [PKGBadges 7.0](https://modrinth.com/mod/pkgbadges) (NeoForge 1.21.1) by PoyrazPKG / KayraPKG  
**Status:** Vulnerability **not fixed** in official release as of 2026-07-17  
**Public source repo:** **none** (Modrinth `source_url: null`, CurseForge JAR only)

This repository is a **security reference fork** produced from the published JAR (`PKGBadges-7.0.jar`) because no official Git repository exists to open a pull request against.

## Vulnerability (CVE pending)

| | |
|---|---|
| **Packet** | `pkgbadges:craft_item` |
| **Class** | `pkgbadges.network.CraftItemPacket` |
| **Impact** | Any client can request **1× of any item id** without materials |
| **Root cause** | `getRequiredMaterials(item)` returns an **empty list** for non-recipe items; `hasRequiredMaterials` treats empty as success |
| **Side effect** | `Thread.sleep(4000)` on server thread during give (DoS) |

### Exploit (confirmed)

```
Client → pkgbadges:craft_item(ResourceLocation("minecraft:diamond"))
Server → inventory.add(diamond) with no material check
```

## Fix (this repo)

See `patches/CraftItemPacket-7.0-security.patch` or `patched/CraftItemPacket.java`:

1. **Reject unknown recipes** — if `getRequiredMaterials(item)` is empty → deny craft  
2. **Reject air/unknown items** — `item == null || item == Items.AIR`  
3. **Remove server-thread sleep** — give item immediately after material removal  
4. **Avoid double `getRequiredMaterials` calls** — pass materials list to helper methods  

## Apply to upstream

Replace `src/main/java/pkgbadges/network/CraftItemPacket.java` in the PKGBadges NeoForge 7.x tree with `patched/CraftItemPacket.java`, or:

```bash
patch -p3 < patches/CraftItemPacket-7.0-security.patch
```

## Disclosure

- Audit: [exploit-mffs RPG Edition audit](https://github.com/Sweizeur/exploit-mffs/blob/main/security-audit/rpg-edition/reports/RPG_EDITION_AUDIT.md)
- Issue opened: [PoyrazPKG/PKG-TEAM#1](https://github.com/PoyrazPKG/PKG-TEAM/issues/1)

## License note

Decompiled reference from PKGBadges 7.0 (Academic Free License v3.0). For security remediation only; full mod rights remain with PoyrazPKG.
