package de.smallinger.waasl.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * Attachment für Wolf Inventory Daten
 * Speichert das Inventar (Chest-Storage) des Wolfs
 */
public class WolfInventoryData {
    
    public static final int INVENTORY_SIZE = 14; // 2 Reihen à 7 Slots
    
    private final NonNullList<ItemStack> items;
    private boolean hasChest;
    
    public static final MapCodec<WolfInventoryData> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.BOOL.fieldOf("has_chest").forGetter(WolfInventoryData::hasChest),
            ItemStack.OPTIONAL_CODEC.listOf().fieldOf("items").forGetter(data -> data.items)
        ).apply(instance, WolfInventoryData::new)
    );

    /**
     * StreamCodec für Network-Synchronisation
     * Synchronisiert hasChest-Flag und alle Inventory-Items zum Client
     * Verwendet OPTIONAL_STREAM_CODEC um leere ItemStacks zu unterstützen
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, WolfInventoryData> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        WolfInventoryData::hasChest,
        ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(INVENTORY_SIZE)),
        data -> data.items,
        WolfInventoryData::new
    );

    public WolfInventoryData() {
        this.items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
        this.hasChest = false;
    }

    private WolfInventoryData(boolean hasChest, List<ItemStack> items) {
        this.items = NonNullList.withSize(INVENTORY_SIZE, ItemStack.EMPTY);
        this.hasChest = hasChest;
        for (int i = 0; i < Math.min(items.size(), INVENTORY_SIZE); i++) {
            this.items.set(i, items.get(i));
        }
    }

    /**
     * Gibt die Items-Liste zurück
     */
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    /**
     * Prüft ob der Wolf eine Truhe hat
     */
    public boolean hasChest() {
        return this.hasChest;
    }

    /**
     * Setzt ob der Wolf eine Truhe hat
     */
    public void setHasChest(boolean hasChest) {
        this.hasChest = hasChest;
    }

    /**
     * Gibt einen ItemStack an einer Position zurück
     */
    public ItemStack getStackInSlot(int slot) {
        if (slot < 0 || slot >= INVENTORY_SIZE) {
            return ItemStack.EMPTY;
        }
        return this.items.get(slot);
    }

    /**
     * Setzt einen ItemStack an einer Position
     */
    public void setStackInSlot(int slot, ItemStack stack) {
        if (slot >= 0 && slot < INVENTORY_SIZE) {
            this.items.set(slot, stack);
        }
    }

    /**
     * Prüft ob das Inventar leer ist
     */
    public boolean isEmpty() {
        for (ItemStack stack : this.items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Leert das gesamte Inventar und gibt alle Items zurück
     */
    public NonNullList<ItemStack> clearInventory() {
        NonNullList<ItemStack> drops = NonNullList.create();
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            ItemStack stack = this.items.get(i);
            if (!stack.isEmpty()) {
                drops.add(stack.copy());
                this.items.set(i, ItemStack.EMPTY);
            }
        }
        return drops;
    }

    /**
     * Erstellt eine Kopie dieser Daten
     */
    public WolfInventoryData copy() {
        WolfInventoryData copy = new WolfInventoryData();
        copy.hasChest = this.hasChest;
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            copy.items.set(i, this.items.get(i).copy());
        }
        return copy;
    }
}
