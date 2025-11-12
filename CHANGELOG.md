# Changelog

All notable changes to Wolf Armor and Storage Legacy will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2025-11-12

### Added
- 🛡️ **Wolf Armor System** - 7 armor tiers (Leather, Iron, Gold, Diamond, Chainmail, Copper, Netherite)
- 📦 **Wolf Chest Storage** - 14-slot inventory for wolves (2 rows × 7 columns)
- 🎨 **Custom 3D Models** - Beautiful armor and chest rendering on wolves
- 🏥 **Auto-Heal AI** - Wolves automatically eat food from inventory when injured
  - Combat-aware system (only heals when safe)
  - Fast eating speed (0.5 seconds per food item)
  - Continuous healing until fully restored
  - Supports all vanilla food items
- 🌙 **Moon Howling Behavior** - Wolves howl at full moon during midnight
  - Active time window: 18000±2000 ticks (20:00-24:00)
  - Wolves sit and look up at the moon while howling
  - Custom howl sound effects (2 variants with pitch variation)
  - Interrupted by combat damage
- 🎮 **Wolf Inventory GUI** - User-friendly interface for managing wolf equipment
  - Armor slot display
  - 14 storage slots
  - Wolf name header
- 🛡️ **Armor Protection System** - Damage reduction based on armor tier
- 🔊 **Custom Sound Effects** - Two unique wolf howl sounds
- 📋 **Crafting Recipes** - All armor tiers and chest are craftable
- ⚙️ **Configuration System** - Customizable settings via config file
  - Auto-heal toggle
  - Armor protection multiplier
  - Wolf max health adjustment
- 🌍 **Localization Support** - English and German translations

### Technical
- Initial port to NeoForge 1.21.10 from original 1.12.2 mod
- Uses NeoForge Attachment API for persistent data storage
- Custom AI Goal system for wolf behaviors
- Client-side rendering layers for armor and chest
- Data Components for item durability tracking
- Compatible with Just Enough Items (JEI)

### Credits
- Original mod by **CenturyWarrior** (Minecraft 1.12.2)
- Maintained by **Sabarishi** for 1.12.2
- NeoForge 1.21.10 port by **Smallinger**

[1.0.0]: https://github.com/Smallinger/wolf-armor-and-storage-legacy/releases/tag/v1.0.0
