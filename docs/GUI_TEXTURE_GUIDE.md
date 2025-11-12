# Wolf Inventory GUI - Textur-Anleitung

## 📐 Layout-Übersicht

```
Textur-Datei: wolf_inventory.png
Größe: 256x256 (Standard Minecraft GUI Textur)
Verwendete Bereich: 176x166 Pixel

┌──────────────────────────────────────────────┐
│ GUI Hintergrund (176x166)                     │
├──────────────────────────────────────────────┤
│                                               │
│  [Armor]  [Storage Storage Storage...]       │  Y: 18
│   8,18                   62,18                │
│                                               │
│  [Chest]  [Storage Storage Storage...]       │  Y: 36
│   8,36    [Storage Storage Storage...]       │  Y: 54
│                                               │
│           (15 Slots in 3x5 Grid)              │
│                                               │
├──────────────────────────────────────────────┤
│  Player Inventory (9x3 Grid)                 │  Y: 84
│  [][][][][][][][][]                           │
│  [][][][][][][][][]                           │
│  [][][][][][][][][]                           │
│                                               │
│  Hotbar (9 Slots)                             │  Y: 142
│  [][][][][][][][][]                           │
└──────────────────────────────────────────────┘
```

## 🎨 Slot-Positionen (genau)

### Wolf-Spezifische Slots:
- **Armor Slot**: X=8, Y=18 (oben links)
  - Zeige Rüstungs-Symbol (Chestplate Icon)
  
- **Chest Upgrade Slot**: X=8, Y=36 (direkt unter Armor)
  - Zeige Truhen-Symbol (Chest Icon)

### Storage Slots (3x5 Grid):
Startposition: X=62, Y=18
```
Row 0: (62,18) (80,18) (98,18) (116,18) (134,18)
Row 1: (62,36) (80,36) (98,36) (116,36) (134,36)
Row 2: (62,54) (80,54) (98,54) (116,54) (134,54)
```
Abstand zwischen Slots: 18 Pixel

### Player Inventory:
- **Main Inventory**: X=8, Y=84 (3 Reihen à 9 Slots)
- **Hotbar**: X=8, Y=142 (1 Reihe à 9 Slots)

## 🖼️ Visuelle Hinweise

### Armor Slot (8,18):
```
┌─────┐
│  🛡️  │  <- Rüstungs-Icon
└─────┘
```

### Chest Slot (8,36):
```
┌─────┐
│  📦  │  <- Truhen-Icon
└─────┘
```

### Storage Grid (62,18):
```
┌─────┬─────┬─────┬─────┬─────┐
│     │     │     │     │     │
├─────┼─────┼─────┼─────┼─────┤
│     │     │     │     │     │
├─────┼─────┼─────┼─────┼─────┤
│     │     │     │     │     │
└─────┴─────┴─────┴─────┴─────┘
```

## 📝 Hinweise für Textureditor

1. **Basis**: Kopiere die Standard Minecraft Container-Textur als Basis
2. **Slot-Rahmen**: Zeichne dunkle Rahmen (18x18) an die o.g. Positionen
3. **Icons**: 
   - Armor Slot: Füge ein Rüstungs-Symbol ein (optional, aber hilfreich)
   - Chest Slot: Füge ein Truhen-Symbol ein
4. **Deaktivierte Slots**: Storage-Slots können grauer dargestellt werden (sie werden via Code deaktiviert wenn kein Chest)

## 🎯 Schnell-Test

Wenn du die Textur nicht bearbeiten möchtest:
- Die aktuelle `wolf_inventory.png` funktioniert wahrscheinlich auch
- Die Slots sind nur minimal verschoben
- Teste erst ob es funktioniert, dann optimiere die Textur

## 🔧 Alternative: Vanilla Container nutzen

Du kannst auch temporär die Standard-Container-Textur verwenden:
```java
private static final ResourceLocation TEXTURE = 
    ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
```
