# Wolf Armor and Storage Legacy - NeoForge

[![NeoForge](https://img.shields.io/badge/NeoForge-21.10.47+-orange.svg)](https://neoforged.net/)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.10-green.svg)](https://www.minecraft.net/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A Minecraft mod that brings wolf armor and storage capabilities to your tamed wolves, now updated for NeoForge 1.21.10.

## ✨ Features

### 🛡️ Wolf Equipment
- **Wolf Armor**: Equip your wolves with protective armor in 4 tiers (Leather, Iron, Gold, Diamond)
- **Wolf Chest**: Add 14-slot storage capacity to your wolves (2 rows × 7 columns)
- **Visual Rendering**: Beautiful 3D models for armor and chests

### 🤖 Wolf AI Enhancements
- **Auto-Heal System**: Wolves automatically consume food from their inventory when injured
  - 🏥 Only heals when not in combat
  - ⚡ Fast eating (0.5 seconds per food item)
  - 🔄 Continuous healing until fully restored
  - 🍖 Supports all vanilla food items
  
- **Moon Howling**: Wolves howl at the full moon during midnight
  - 🌕 Active during full moon nights
  - 🕐 Time window: 18000±2000 ticks (20:00-24:00)
  - 🪑 Wolves sit and look up at the moon while howling
  - 🔊 Custom howl sound effects (2 variants with randomized pitch)
  - ⚔️ Interrupted if wolf takes damage

### 🛡️ Armor Protection System
- Reduces incoming damage based on armor points
- 📊 Protection levels:
  - Leather: Basic protection
  - Iron: Moderate protection
  - Gold: Good protection
  - Diamond: Best protection

## 📋 Requirements

- Minecraft **1.21.10**
- NeoForge **21.10.47-beta** or higher
- Java **21+**

## 📥 Installation

1. Install [NeoForge](https://neoforged.net/) for Minecraft 1.21.10
2. Download this mod from [Releases](https://github.com/smallinger/wolf-armor-and-storage-legacy/releases)
3. Place the mod file in your `mods` folder
4. Launch the game and enjoy!

## 🎮 Usage

### Equipping Armor
1. Tame a wolf
2. Right-click the wolf with wolf armor
3. The armor will be equipped and rendered on the wolf

### Adding a Chest
1. Right-click a tamed wolf with a chest
2. The chest will be equipped (visual model appears)
3. Right-click the wolf again to open the inventory GUI

### Opening Wolf Inventory
- Right-click a wolf with a chest to access its 14-slot inventory
- The GUI shows:
  - Wolf armor slot (top)
  - 14 storage slots (2 rows × 7 columns)
  - Wolf name display

### Auto-Heal Feature
- Place food items in the wolf's chest inventory
- When the wolf is injured and not in combat, it will automatically eat food
- The wolf will continue eating until fully healed or food runs out

### Moon Howling
- Wolves will automatically howl at full moons around midnight
- The wolf sits and looks up during the howl (3 seconds)
- Only occurs between 20:00 and 24:00 in-game time
- Can be interrupted by combat

## ⚙️ Configuration

Configuration file: `config/wolfarmorandstoragelegacy-common.toml`

### Available Options:

| Option | Description | Default |
|--------|-------------|---------|
| **autoHealEnabled** | Enable/disable auto-heal feature | `true` |
| **wolfArmorProtectionMultiplier** | Armor damage reduction multiplier | `1.0` |
| **wolfMaxHealth** | Maximum health for wolves | `20.0` |

## 🔧 Building from Source

```bash
# Clone the repository
git clone https://github.com/smallinger/wolf-armor-and-storage-legacy.git
cd wolf-armor-and-storage-legacy/NeoForge\ 1.21.10

# Build the mod
./gradlew build

# The built jar file will be in build/libs/
```

### Running in Dev Environment
```bash
./gradlew runClient
```

## 🙏 Credits

- **Original Mod Author**: [CenturyWarrior](https://github.com/CenturyWarrior) & [Sabarishi](https://github.com/sabarishi) (Minecraft 1.12.2)
- **NeoForge Port**: [Smallinger](https://github.com/smallinger)
- Enhanced with auto-heal and moon howling features

## 📄 License

This mod is licensed under [MIT](LICENSE).

## 🔗 Links

- **Original 1.12.2 Mod**: Wolf Armor and Storage by CenturyWarrior
- **GitHub Repository**: https://github.com/smallinger/wolf-armor-and-storage-legacy
- **Issue Tracker**: https://github.com/smallinger/wolf-armor-and-storage-legacy/issues

## 🐛 Reporting Issues

Found a bug? Please [open an issue](https://github.com/smallinger/wolf-armor-and-storage-legacy/issues) with:
- Minecraft version
- NeoForge version
- Mod version
- Description of the problem
- Steps to reproduce
- Log files (if applicable)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

## 💡 Technical Details

### Compatibility
- ✅ Compatible with Just Enough Items (JEI)
- ✅ Uses NeoForge Attachment API for data storage
- ✅ Custom rendering via RenderStateModifier

### Data Storage
Wolf data is stored using NeoForge Attachments and includes:
- Armor item and durability
- Chest status
- Inventory contents (14 slots)
- All data persists across game sessions

### Project Structure
```
src/main/java/de/smallinger/waasl/
├── attachment/          # Data attachment definitions
├── client/             # Client-side rendering and events
├── entity/ai/          # Custom AI goals (Auto-Heal, Moon Howling)
├── event/              # Event handlers
├── item/               # Wolf armor and chest items
├── menu/               # Inventory GUI
├── sound/              # Sound event registry
└── util/               # Helper utilities
```

## 📝 Changelog

### Version 1.0.0 (NeoForge 1.21.10)
- ✨ Initial port to NeoForge 1.21.10
- 🛡️ Added wolf armor system (Leather, Iron, Gold, Diamond)
- 📦 Added wolf chest storage (14 slots)
- 🎨 Added custom 3D models for armor and chest rendering
- 🏥 Added auto-heal AI for wolves
- 🌙 Added moon howling behavior
- 🔊 Added custom sound effects for howling
- 🛡️ Implemented armor damage reduction system
- 🎮 Added GUI for wolf inventory management

## Support My Work

If you like what I do, consider supporting me:

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/smallpox)

---

Made with ❤️ for the Minecraft community
