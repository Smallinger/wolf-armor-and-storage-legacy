package de.smallinger.waasl.sound;

import de.smallinger.waasl.WolfArmorandStorageLegacy;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * Registry for custom sound events
 */
public class ModSounds {
    
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = 
        DeferredRegister.create(Registries.SOUND_EVENT, WolfArmorandStorageLegacy.MODID);
    
    /**
     * Wolf howl sound (plays howl1.ogg or howl2.ogg randomly)
     */
    public static final Supplier<SoundEvent> WOLF_HOWL = SOUND_EVENTS.register(
        "wolf_howl",
        () -> SoundEvent.createVariableRangeEvent(
            ResourceLocation.fromNamespaceAndPath(WolfArmorandStorageLegacy.MODID, "wolf_howl")
        )
    );
}
