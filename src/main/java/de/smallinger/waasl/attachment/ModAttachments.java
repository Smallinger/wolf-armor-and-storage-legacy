package de.smallinger.waasl.attachment;

import de.smallinger.waasl.WolfArmorandStorageLegacy;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Registrierung aller Data Attachments für Wolf-Daten
 */
public class ModAttachments {
    
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = 
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, WolfArmorandStorageLegacy.MODID);

    /**
     * Wolf Armor Attachment - Speichert die Rüstung des Wolfs
     * Wird beim Tod kopiert (copyOnDeath)
     * Wird zum Client synchronisiert (sync)
     */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<WolfArmorData>> WOLF_ARMOR = 
            ATTACHMENT_TYPES.register("wolf_armor", () -> 
                AttachmentType.<WolfArmorData>builder(() -> new WolfArmorData())
                    .serialize(WolfArmorData.CODEC)
                    .sync(WolfArmorData.STREAM_CODEC)
                    .copyOnDeath()
                    .build()
            );

    /**
     * Wolf Inventory Attachment - Speichert das Inventar des Wolfs
     * Wird beim Tod kopiert (copyOnDeath)
     * Wird zum Client synchronisiert (sync)
     */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<WolfInventoryData>> WOLF_INVENTORY = 
            ATTACHMENT_TYPES.register("wolf_inventory", () -> 
                AttachmentType.<WolfInventoryData>builder(() -> new WolfInventoryData())
                    .serialize(WolfInventoryData.CODEC)
                    .sync(WolfInventoryData.STREAM_CODEC)
                    .copyOnDeath()
                    .build()
            );
}
