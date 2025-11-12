# Wolf Armor Textures (Equipment System)

**✅ DIES IST DER KORREKTE ORDNER FÜR MINECRAFT 1.21+**

In Minecraft 1.21+ werden Wolf-Armor-Texturen im **Equipment-Ordner** gespeichert.

**Dieser Pfad (KORREKT):**
```
assets/wolfarmorandstoragelegacy/textures/entity/equipment/wolf_body/
```

**Vanilla Minecraft verwendet:**
```
assets/minecraft/textures/entity/equipment/wolf_body/armadillo_scute.png
```

---

## Benötigte Texturen

Die folgenden Texture-Dateien müssen erstellt werden (64x32 PNG Format):

### Base Armor Textures:
- `leather.png` - Leather wolf armor texture
- `copper.png` - Copper wolf armor texture  
- `chainmail.png` - Chainmail wolf armor texture
- `iron.png` - Iron wolf armor texture
- `gold.png` - Gold wolf armor texture
- `diamond.png` - Diamond wolf armor texture
- `netherite.png` - Netherite wolf armor texture

### Overlay Textures (für dyeable leather):
- `leather_overlay.png` - Leather overlay for dyeing

⚠️ **WICHTIG**: Dateinamen sind OHNE `wolf_armor_` Prefix!
- ✅ `leather.png` (korrekt)
- ❌ `wolf_armor_leather.png` (falsch)

## Texture Format

- **Size**: 64x32 pixels
- **Format**: PNG with transparency
- **UV Mapping**: Must match Minecraft's wolf model UV layout
- **Reference**: See Minecraft's vanilla wolf armor textures or `assets/minecraft/textures/entity/wolf/wolf.png`

## Creating Textures

### Option 1: Use Vanilla Wolf Armor Texture as Base
1. Copy Minecraft's vanilla wolf armor texture from:
   - `assets/minecraft/textures/entity/wolf/wolf_armor.png`
2. Modify colors/materials for each armor type
3. For leather overlay: Only include the dyeable parts

### Option 2: Create from Scratch
1. Use the base wolf texture as a template (`wolf.png`)
2. Draw armor pieces on the appropriate UV sections:
   - Head armor (helmet/cap)
   - Body armor (chestplate/saddle)
   - Legs armor (leggings)
3. For overlay textures: Only include the parts that should be dyed
4. Use transparency for areas without armor

### UV Layout Reference
The wolf model uses a 64x32 texture with these key areas:
- **Head**: Top-left area (0-32, 0-16)
- **Body**: Middle area (16-48, 16-32)
- **Legs**: Bottom areas (0-16, 16-32) and (48-64, 16-32)
- **Tail**: Right area (48-64, 0-16)

## Temporary Placeholders

Until custom textures are created, you can:
1. Copy Vanilla wolf armor texture and rename it
2. Use a solid color PNG for testing
3. Reference Forge 1.12.2 version textures if available

## Notes

- Leather armor uses TWO textures: base + overlay (dyeable part)
- All other armors use only ONE texture (non-dyeable)
- Make sure textures align with the wolf model's UV mapping
- Test in-game to ensure proper alignment

## Render Layer Implementation

The render layer is implemented in:
- `WolfArmorRenderLayer.java` - Uses Minecraft 1.21's new WolfRenderState system
- Renders both base texture and overlay (for leather)
- Supports dyeing via DyedItemColor component

