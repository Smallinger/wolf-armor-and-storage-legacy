package de.smallinger.waasl.entity.ai;

import de.smallinger.waasl.util.WolfHelper;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;

import java.util.EnumSet;

/**
 * AI Goal that makes wolves automatically eat meat from their chest inventory
 * when their health is low.
 */
public class WolfAutoHealGoal extends Goal {
    
    private final Wolf wolf;
    private static final float HEALTH_THRESHOLD = 0.5f; // Eat when below 50% health
    private static final int EAT_COOLDOWN = 100; // Ticks between eating attempts (5 seconds)
    private static final int COMBAT_COOLDOWN = 100; // 5 seconds after last damage before eating (realistic)
    
    private int eatTimer = 0;
    private int eatingTicks = 0;
    private static final int EATING_DURATION = 5; // Fast eating: 0.1 seconds per food item
    
    public WolfAutoHealGoal(Wolf wolf) {
        this.wolf = wolf;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK)); // Pause movement while eating
    }
    
    @Override
    public boolean canUse() {
        // Decrease cooldown timer
        if (eatTimer > 0) {
            eatTimer--;
            return false;
        }
        
        // Only eat if health is below threshold
        float healthPercent = wolf.getHealth() / wolf.getMaxHealth();
        if (healthPercent >= HEALTH_THRESHOLD) {
            return false;
        }
        
        // Don't eat if currently in combat (has attack target)
        if (wolf.getTarget() != null) {
            return false;
        }
        
        // Don't eat if recently took damage (wait for combat cooldown)
        if (wolf.tickCount - wolf.getLastHurtByMobTimestamp() < COMBAT_COOLDOWN) {
            return false;
        }
        
        // Only eat if wolf has a chest
        if (!WolfHelper.hasChest(wolf)) {
            return false;
        }
        
        // Check if there's food in the inventory
        return findFoodInInventory() != null;
    }
    
    @Override
    public boolean canContinueToUse() {
        // Continue eating if:
        // 1. Currently eating (animation in progress)
        // 2. Still injured and have more food available
        if (eatingTicks > 0) {
            return true;
        }
        
        // Stop eating if attacked or new target appears
        if (wolf.getTarget() != null) {
            return false;
        }
        
        // Check if still needs healing and has food
        float healthPercent = wolf.getHealth() / wolf.getMaxHealth();
        if (healthPercent < 1.0f && WolfHelper.hasChest(wolf)) {
            return findFoodInInventory() != null;
        }
        
        return false;
    }
    
    @Override
    public void start() {
        eatingTicks = EATING_DURATION;
        // Wolf sits down while eating
        if (!wolf.isOrderedToSit()) {
            wolf.setInSittingPose(true);
        }
    }
    
    @Override
    public void stop() {
        eatingTicks = 0;
        
        // Only set cooldown if wolf is now fully healed
        float healthPercent = wolf.getHealth() / wolf.getMaxHealth();
        if (healthPercent >= 1.0f) {
            eatTimer = EAT_COOLDOWN; // 5-second cooldown after full heal
        }
        // If still injured, no cooldown - goal will restart immediately
        
        // Stand back up if we made wolf sit
        if (wolf.isInSittingPose() && !wolf.isOrderedToSit()) {
            wolf.setInSittingPose(false);
        }
    }
    
    @Override
    public void tick() {
        eatingTicks--;
        
        // Animation: Look down while eating
        wolf.getNavigation().stop();
        wolf.setXRot(45.0f); // Look down
        
        // Finish eating current food item
        if (eatingTicks <= 0) {
            ItemStack foodStack = findFoodInInventory();
            if (foodStack != null) {
                consumeFood(foodStack);
                
                // Check if we need to eat more
                float healthPercent = wolf.getHealth() / wolf.getMaxHealth();
                if (healthPercent < 1.0f && findFoodInInventory() != null) {
                    // Start eating next food item immediately
                    eatingTicks = EATING_DURATION;
                }
            }
        }
    }
    
    /**
     * Finds the first food item in the wolf's inventory
     */
    private ItemStack findFoodInInventory() {
        var inventory = WolfHelper.getInventory(wolf);
        
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty() && isMeatFood(stack)) {
                return stack;
            }
        }
        
        return null;
    }
    
    /**
     * Checks if an item is meat/food that wolves can eat
     */
    private boolean isMeatFood(ItemStack stack) {
        FoodProperties food = stack.get(DataComponents.FOOD);
        if (food == null) {
            return false;
        }
        
        // Wolves should only eat meat (nutrition >= 3 is usually meat)
        // This includes: beef, pork, chicken, mutton, rabbit, rotten flesh
        return food.nutrition() >= 1; // Accept any food with nutrition
    }
    
    /**
     * Consumes one food item and heals the wolf
     */
    private void consumeFood(ItemStack foodStack) {
        FoodProperties food = foodStack.get(DataComponents.FOOD);
        if (food == null) {
            return;
        }
        
        // Heal the wolf based on food nutrition
        // Each nutrition point = 1 HP (like vanilla wolves)
        float healAmount = food.nutrition() * 1.0f;
        wolf.heal(healAmount);
        
        // Remove one item from the stack
        var inventory = WolfHelper.getInventory(wolf);
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (stack == foodStack) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    inventory.set(i, ItemStack.EMPTY);
                }
                break;
            }
        }
        
        // Play eating sound
        wolf.playSound(net.minecraft.sounds.SoundEvents.GENERIC_EAT.value(), 1.0f, 1.0f);
        
        // Spawn eating particles
        if (!wolf.level().isClientSide()) {
            for (int i = 0; i < 5; i++) {
                double offsetX = wolf.getRandom().nextGaussian() * 0.02;
                double offsetY = wolf.getRandom().nextGaussian() * 0.02;
                double offsetZ = wolf.getRandom().nextGaussian() * 0.02;
                
                wolf.level().addParticle(
                    net.minecraft.core.particles.ParticleTypes.ITEM_SNOWBALL,
                    wolf.getX() + offsetX,
                    wolf.getY() + 0.5 + offsetY,
                    wolf.getZ() + offsetZ,
                    0, 0, 0
                );
            }
        }
    }
    
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
