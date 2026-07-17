# PKGBadges — Patch de sécurité (CraftItemPacket)

**Mod concerné :** [PKGBadges 7.0](https://modrinth.com/mod/pkgbadges) (NeoForge 1.21.1) — PoyrazPKG / KayraPKG  
**Faille :** give arbitraire 1× item via `pkgbadges:craft_item`  
**Statut officiel :** **non corrigé** sur Modrinth/CurseForge (2026-07-17)  
**Release patch :** [v7.0.1-security](https://github.com/Sweizeur/pkgbadges-security-patch/releases/tag/v7.0.1-security)

Ce dépôt contient le correctif pour `pkgbadges.network.CraftItemPacket` (pas de repo source public chez l’auteur).

---

## La faille en bref

| | |
|---|---|
| **Paquet** | `pkgbadges:craft_item` |
| **Impact** | N’importe quel joueur peut demander **1× de n’importe quel item** sans matériaux |
| **Cause** | `getRequiredMaterials(item)` renvoie une liste **vide** hors recettes badges → `hasRequiredMaterials` accepte |
| **Bonus toxique** | `Thread.sleep(4000)` sur le thread serveur à chaque give (lag serveur) |

---

## Patcher son serveur

Tu n’as **pas besoin du code source complet** de PKGBadges pour sécuriser un serveur. Voici les options, de la plus simple à la plus propre.

### Option 1 — Retirer PKGBadges (2 minutes)

Si les badges gym ne sont pas essentiels sur ton serveur :

1. Arrête le serveur.
2. Supprime ou déplace `mods/PKGBadges-7.0.jar` (et variantes du même mod).
3. Redémarre.

Les clients qui ont encore le mod en local ne pourront plus rejoindre tant que le mod est requis côté client — retire-le aussi du modpack client ou remplace-le par une version patchée (option 2 ou 3).

---

### Option 2 — Remplacer une classe dans le JAR (recommandé pour hébergeurs)

Tu modifies **un seul fichier** dans le JAR officiel : `pkgbadges/network/CraftItemPacket.class`.

#### Étape 0 — Sauvegarde

```bash
cp mods/PKGBadges-7.0.jar mods/PKGBadges-7.0.jar.bak
```

#### Étape 1 — Obtenir la classe patchée

**A) Tu compiles toi-même** (Java 21 + NeoForge 1.21.1 MDK) :

1. Télécharge [PKGBadges-7.0.jar](https://cdn.modrinth.com/data/vEkz5T55/versions/mIe06LCy/PKGBadges-7.0.jar) dans un dossier `libs/`.
2. Copie `patched/CraftItemPacket.java` depuis cette release dans  
   `src/main/java/pkgbadges/network/CraftItemPacket.java` d’un projet NeoForge 1.21.1 minimal.
3. Dans `build.gradle` du projet :

```gradle
dependencies {
    compileOnly files('libs/PKGBadges-7.0.jar')
}
```

4. Compile :

```bash
./gradlew compileJava
```

5. Récupère le `.class` généré :  
   `build/classes/java/main/pkgbadges/network/CraftItemPacket.class`

**B) Tu n’as pas Gradle** — utilise [Recaf](https://github.com/Col-E/Recaf) :

1. Ouvre `PKGBadges-7.0.jar` dans Recaf.
2. Navigue vers `pkgbadges/network/CraftItemPacket`.
3. Exporte / décompile la classe.
4. Applique manuellement les changements de `patched/CraftItemPacket.java` (voir section *Ce que fait le patch*).
5. Recompile la classe dans Recaf et exporte le JAR modifié.

#### Étape 2 — Injecter la classe dans le JAR du serveur

Depuis le dossier qui contient `CraftItemPacket.class` (arborescence `pkgbadges/network/…`) :

```bash
cd /chemin/vers/classes-compilées   # doit contenir pkgbadges/network/CraftItemPacket.class
jar uf /chemin/vers/mods/PKGBadges-7.0.jar pkgbadges/network/CraftItemPacket.class
```

Sous Windows (PowerShell), avec le JDK installé :

```powershell
jar uf mods\PKGBadges-7.0.jar -C build\classes\java\main pkgbadges\network\CraftItemPacket.class
```

#### Étape 3 — Redémarrer et vérifier

1. Redémarre le serveur.
2. Avec un client qui a encore l’exploit / le mod : tenter un give `minecraft:diamond` via `craft_item` → **ne doit plus donner** de diamant.
3. Le craft **légitime** d’un badge (avec tissus) doit toujours fonctionner.

> **Important :** garde le même nom de fichier `PKGBadges-7.0.jar` ou renomme-le clairement (`PKGBadges-7.0-patched.jar`) et mets à jour le modpack client avec **le même JAR** pour éviter les mismatches.

---

### Option 3 — Recompiler tout le mod (mainteneurs / si tu as les sources)

Si PoyrazPKG te partage les sources ou ouvre un repo :

```bash
# Dans l’arborescence source PKGBadges 7.x
cp /chemin/vers/patched/CraftItemPacket.java src/main/java/pkgbadges/network/CraftItemPacket.java
./gradlew build
# Déploie build/libs/PKGBadges-*.jar sur serveur + clients
```

Ou avec le diff :

```bash
patch -p3 < patches/CraftItemPacket-7.0-security.patch
```

---

### Option 4 — Attendre une release officielle

Suis l’issue de disclosure : [PoyrazPKG/PKG-TEAM#1](https://github.com/PoyrazPKG/PKG-TEAM/issues/1)

Dès qu’une **7.0.1+** officielle sort sur Modrinth/CurseForge, remplace le JAR patché maison par la version upstream.

---

## Ce que fait le patch

Fichiers : `patched/CraftItemPacket.java` ou `patches/CraftItemPacket-7.0-security.patch`

1. **Refuse les recettes inconnues** — si `getRequiredMaterials(item)` est vide → message *Malzemeler eksik!* et pas de give.
2. **Refuse l’air** — `item == null || item == Items.AIR`.
3. **Supprime le `Thread.sleep(4000)`** sur le thread serveur (give immédiat après vérif matériaux).
4. **Une seule résolution des matériaux** — la liste est passée aux helpers (pas de double appel).

---

## Fichiers de la release

| Fichier | Usage |
|---------|--------|
| `PKGBadges-7.0.1-security-patch.zip` | Archive complète |
| `CraftItemPacket-7.0-security.patch` | Diff pour sources |
| `patched/CraftItemPacket.java` | Source corrigée |
| `reference/CraftItemPacket.java` | Référence vulnérable (décompilée du JAR 7.0) |

Téléchargement : [Releases](https://github.com/Sweizeur/pkgbadges-security-patch/releases)

---

## Divulgation & audit

- Audit : [exploit-mffs — RPG Edition](https://github.com/Sweizeur/exploit-mffs/blob/main/security-audit/rpg-edition/reports/RPG_EDITION_AUDIT.md)
- Issue auteur : [PoyrazPKG/PKG-TEAM#1](https://github.com/PoyrazPKG/PKG-TEAM/issues/1)

---

## Licence

Référence décompilée depuis PKGBadges 7.0 (Academic Free License v3.0). Usage **sécurité / correctif** uniquement ; droits du mod chez PoyrazPKG.
