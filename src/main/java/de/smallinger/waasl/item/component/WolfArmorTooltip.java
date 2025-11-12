package de.smallinger.waasl.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

/**
 * Tooltip-Daten für Wolf Armor Items
 * Zeigt Defense, Toughness und Knockback Resistance an
 */
public record WolfArmorTooltip(int defense, float toughness, float knockbackResistance) implements TooltipProvider {
    
    public static final Codec<WolfArmorTooltip> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("defense").forGetter(WolfArmorTooltip::defense),
            Codec.FLOAT.fieldOf("toughness").forGetter(WolfArmorTooltip::toughness),
            Codec.FLOAT.fieldOf("knockback_resistance").forGetter(WolfArmorTooltip::knockbackResistance)
        ).apply(instance, WolfArmorTooltip::new)
    );

    public static final StreamCodec<ByteBuf, WolfArmorTooltip> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, WolfArmorTooltip::defense,
        ByteBufCodecs.FLOAT, WolfArmorTooltip::toughness,
        ByteBufCodecs.FLOAT, WolfArmorTooltip::knockbackResistance,
        WolfArmorTooltip::new
    );

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag, DataComponentGetter componentGetter) {
        // Defense-Wert (immer anzeigen)
        tooltipAdder.accept(Component.translatable("item.wolfarmorandstoragelegacy.wolf_armor.defense", this.defense));
        
        // Toughness-Wert (nur wenn > 0)
        if (this.toughness > 0) {
            tooltipAdder.accept(Component.translatable("item.wolfarmorandstoragelegacy.wolf_armor.toughness", 
                    String.format("%.1f", this.toughness)));
        }
        
        // Knockback Resistance (nur wenn > 0)
        if (this.knockbackResistance > 0) {
            tooltipAdder.accept(Component.translatable("item.wolfarmorandstoragelegacy.wolf_armor.knockback_resistance", 
                    String.format("%.1f", this.knockbackResistance * 10)));
        }
    }
}
