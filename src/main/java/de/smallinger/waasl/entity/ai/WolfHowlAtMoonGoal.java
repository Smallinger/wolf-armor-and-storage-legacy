package de.smallinger.waasl.entity.ai;

import de.smallinger.waasl.sound.ModSounds;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

/**
 * AI Goal that makes wolves howl at the full moon during nighttime.
 * Based on the original Wolf Armor and Storage mod by satyrnfirefly.
 * Wolf sits down and looks up at the moon while howling.
 */
public class WolfHowlAtMoonGoal extends Goal {
    
    private final Wolf wolf;
    private int howlTimer;
    private boolean isHowling; // Are we in the howling phase (sitting, looking up)?
    private static final int MIN_HOWL_DELAY = 300; // 15 seconds minimum before howl
    private static final int MAX_RANDOM_DELAY = 2400; // Up to 2 minutes random delay
    private static final int HOWL_DURATION = 60; // How long the wolf sits and howls (3 seconds)
    
    public WolfHowlAtMoonGoal(Wolf wolf) {
        this.wolf = wolf;
        // Start without flags - we only block when actively howling
        this.setFlags(EnumSet.noneOf(Goal.Flag.class));
    }
    
    @Override
    public boolean canUse() {
        // Only wild wolves howl at the moon (not tamed)
        if (wolf.isTame()) {
            return false;
        }
        
        Level level = wolf.level();
        
        // Only during full moon (moon phase 0)
        int moonPhase = level.getMoonPhase();
        if (moonPhase != 0) {
            return false;
        }
        
        // Only at night around midnight (time between 16000 and 20000)
        // 18000 = midnight, ±2000 ticks = ~3 minutes 20 seconds window
        long dayTime = level.getDayTime() % 24000;
        if (dayTime < 16000 || dayTime > 20000) {
            return false;
        }
        
        // Random chance to start howling (happens roughly every 15 seconds to 2.25 minutes)
        if (wolf.getRandom().nextInt(MIN_HOWL_DELAY + MAX_RANDOM_DELAY) > MIN_HOWL_DELAY) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public void start() {
        // Set random timer before starting to howl (15 seconds to 2.25 minutes)
        howlTimer = MIN_HOWL_DELAY + wolf.getRandom().nextInt(600) + 
                    wolf.getRandom().nextInt(600) + 
                    wolf.getRandom().nextInt(600) + 
                    wolf.getRandom().nextInt(600);
        isHowling = false;
    }
    
    @Override
    public boolean canContinueToUse() {
        Level level = wolf.level();
        int moonPhase = level.getMoonPhase();
        long dayTime = level.getDayTime() % 24000;
        
        // Stop if tamed, moon phase changes, or becomes day
        if (wolf.isTame()) {
            return false;
        }
        if (moonPhase != 0) {
            return false;
        }
        if (dayTime < 16000 || dayTime > 20000) {
            return false;
        }
        
        // Stop immediately if wolf takes damage or enters combat (only during howling phase)
        if (isHowling) {
            if (wolf.getTarget() != null || wolf.getLastHurtMob() != null || wolf.getLastHurtByMob() != null) {
                return false;
            }
        }
        
        if (howlTimer <= 0) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public void tick() {
        if (howlTimer > 0) {
            howlTimer--;
            
            // Check if we just entered the howling phase
            if (!isHowling && howlTimer <= HOWL_DURATION) {
                isHowling = true;
                
                // NOW block other AI (movement and looking)
                this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
                
                // Stop any current movement immediately
                wolf.getNavigation().stop();
                wolf.setDeltaMovement(0, wolf.getDeltaMovement().y, 0); // Keep Y for gravity, zero X and Z
                
                // Make wolf sit down
                if (!wolf.isOrderedToSit()) {
                    wolf.setInSittingPose(true);
                }
                
                // Play howl sound at the start of sitting
                float pitchVariation = wolf.isBaby() ? 1.25F : 0.25F;
                float pitch = 0.75f + (wolf.getRandom().nextFloat() * pitchVariation);
                wolf.playSound(ModSounds.WOLF_HOWL.get(), 5.0F, pitch);
            }
            
            // While howling: keep sitting and look up at the moon
            if (isHowling) {
                // Stop any movement every tick
                wolf.getNavigation().stop();
                wolf.setDeltaMovement(0, wolf.getDeltaMovement().y, 0);
                
                // Keep wolf sitting (re-apply every tick)
                if (!wolf.isOrderedToSit() && !wolf.isInSittingPose()) {
                    wolf.setInSittingPose(true);
                }
                
                // Make wolf look up at the moon
                double lookUpX = wolf.getX();
                double lookUpY = wolf.getY() + 10.0; // Look high up towards the moon
                double lookUpZ = wolf.getZ();
                wolf.getLookControl().setLookAt(lookUpX, lookUpY, lookUpZ, 30.0F, 30.0F);
            }
        }
    }
    
    @Override
    public void stop() {
        // Clear flags when goal ends
        this.setFlags(EnumSet.noneOf(Goal.Flag.class));
        
        // Stand wolf back up (only if not ordered to sit by player)
        if (isHowling && wolf.isInSittingPose() && !wolf.isOrderedToSit()) {
            wolf.setInSittingPose(false);
        }
        isHowling = false;
    }
}
