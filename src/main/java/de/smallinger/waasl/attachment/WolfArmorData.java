package de.smallinger.waasl.attachment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

/**
 * Attachment für Wolf Armor Daten
 * Speichert die ausgerüstete Rüstung des Wolfs
 */
public class WolfArmorData {
    
    private ItemStack armorStack;
    
    public static final MapCodec<WolfArmorData> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            ItemStack.OPTIONAL_CODEC.fieldOf("armor").forGetter(WolfArmorData::getArmorStack)
        ).apply(instance, WolfArmorData::new)
    );

    /**
     * StreamCodec für Network-Synchronisation
     * Synchronisiert den Armor-ItemStack zum Client
     * Verwendet OPTIONAL_STREAM_CODEC um leere ItemStacks zu unterstützen
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, WolfArmorData> STREAM_CODEC = StreamCodec.composite(
        ItemStack.OPTIONAL_STREAM_CODEC,
        WolfArmorData::getArmorStack,
        WolfArmorData::new
    );

    public WolfArmorData() {
        this.armorStack = ItemStack.EMPTY;
    }

    public WolfArmorData(ItemStack armorStack) {
        this.armorStack = armorStack;
    }

    /**
     * Gibt den Rüstungs-ItemStack zurück
     */
    public ItemStack getArmorStack() {
        return this.armorStack;
    }

    /**
     * Setzt den Rüstungs-ItemStack
     */
    public void setArmorStack(ItemStack stack) {
        this.armorStack = stack;
    }

    /**
     * Prüft ob der Wolf Rüstung trägt
     */
    public boolean hasArmor() {
        return !this.armorStack.isEmpty();
    }

    /**
     * Entfernt die Rüstung und gibt den ItemStack zurück
     */
    public ItemStack removeArmor() {
        ItemStack removed = this.armorStack.copy();
        this.armorStack = ItemStack.EMPTY;
        return removed;
    }

    /**
     * Erstellt eine Kopie dieser Daten
     */
    public WolfArmorData copy() {
        return new WolfArmorData(this.armorStack.copy());
    }
}
