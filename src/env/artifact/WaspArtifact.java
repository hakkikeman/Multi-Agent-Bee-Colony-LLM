// WaspArtifact.java - CArtAgO Artifact for Wasp Agent
package artifact;

import java.util.List;

import cartago.Artifact;
import cartago.INTERNAL_OPERATION;
import cartago.OPERATION;
import cartago.ObsProperty;
import graphic.Environment;

import model.Position;
import model.Wasp;

/**
 * CArtAgO Artifact for the Wasp agent.
 * Provides operations for hunting sentinels using LLM strategy.
 */
public class WaspArtifact extends Artifact {

    private Wasp wasp;
    private GeminiService geminiService;
    private int targetX = -1;
    private int targetY = -1;
    private String lastReasoning = "";
    private boolean battleActive = true;

    void init() {
        wasp = Wasp.getInstance();
        geminiService = GeminiService.getInstance();

        // Observable properties for the agent
        defineObsProperty("wasp_position", wasp.getPosition().getX(), wasp.getPosition().getY());
        defineObsProperty("wasp_health", wasp.getHealth());
        defineObsProperty("wasp_alive", wasp.isAlive());
        defineObsProperty("attack_target", -1, -1, "none");
        defineObsProperty("sentinel_count", 0);
        defineObsProperty("battle_active", true);

        // Start the delayed initialization to wait for JavaFX
        execInternalOp("delayedInit");
    }

    /**
     * Wait for JavaFX to be ready before registering wasp visually
     */
    @INTERNAL_OPERATION
    void delayedInit() {

        // Wait for the graphics to be ready (queen starts the Map artifact which
        // launches JavaFX)
        int maxWaitSeconds = 30;
        int waited = 0;

        while (waited < maxWaitSeconds) {
            try {
                // Check if EnvironmentApplication is ready
                if (graphic.EnvironmentApplication.getInstance() != null) {

                    // Small additional delay to ensure UI components are initialized
                    await_time(2000);

                    // Now register the wasp
                    Environment.getInstance().registerWasp(wasp);

                    // Start the battle loop
                    execInternalOp("battleLoop");
                    return;
                }
            } catch (Exception e) {
                // JavaFX not ready yet
            }

            await_time(1000);
            waited++;

        }

        System.err.println("[WaspArtifact] ERROR: JavaFX did not initialize in time! Battle cannot start.");
    }

    /**
     * Scan for sentinel positions - updates observable property
     */
    @OPERATION
    void scanSentinels() {
        List<Position> sentinelPositions = Environment.getInstance().getSentinelPositions();
        int count = sentinelPositions.size();

        ObsProperty prop = getObsProperty("sentinel_count");
        prop.updateValue(count);

        if (count == 0) {
            System.out.println("[WaspArtifact] No sentinels remaining!");
            battleActive = false;
            getObsProperty("battle_active").updateValue(false);
            Environment.getInstance().declareWaspVictory();
        }
    }

    /**
     * Request attack strategy from LLM
     */
    @OPERATION
    void requestAttackTarget() {
        if (!wasp.isAlive() || !battleActive) {
            return;
        }

        List<Position> sentinelPositions = Environment.getInstance().getSentinelPositions();

        if (sentinelPositions.isEmpty()) {
            targetX = -1;
            targetY = -1;
            lastReasoning = "No targets available";
            updateAttackTarget();
            return;
        }

        // Query LLM for strategy
        GeminiService.AttackDecision decision = geminiService.getAttackStrategy(
                sentinelPositions,
                wasp.getPosition(),
                Environment.getInstance().getWidth(),
                Environment.getInstance().getHeight());

        targetX = decision.targetX;
        targetY = decision.targetY;
        lastReasoning = decision.reasoning;

        updateAttackTarget();
        System.out.println("[WaspArtifact] LLM Target: (" + targetX + ", " + targetY + ") - " + lastReasoning);
    }

    private void updateAttackTarget() {
        ObsProperty prop = getObsProperty("attack_target");
        prop.updateValues(new Object[] { targetX, targetY, lastReasoning });
    }

    /**
     * Move wasp toward target position
     */
    @OPERATION
    void moveToTarget() {
        if (!wasp.isAlive() || targetX < 0 || targetY < 0) {
            return;
        }

        // Move with speed multiplier
        wasp.moveToward(targetX, targetY, 2);

        // Update position in environment
        Environment.getInstance().updateWaspPosition(wasp.getPosition());

        // Update observable property
        ObsProperty posProp = getObsProperty("wasp_position");
        posProp.updateValues(new Object[] { wasp.getPosition().getX(), wasp.getPosition().getY() });
    }

    /**
     * Execute attack at current position
     */
    @OPERATION
    void executeAttack() {
        if (!wasp.isAlive()) {
            return;
        }

        Position waspPos = wasp.getPosition();
        int attackRadius = wasp.getAttackRadius();
        int maxKills = wasp.getMaxKillsPerAttack();

        // Find sentinels within attack radius
        List<String> killedSentinels = Environment.getInstance().attackSentinelsInRadius(
                waspPos.getX(), waspPos.getY(), attackRadius, maxKills);

        if (!killedSentinels.isEmpty()) {
            System.out.println("[WaspArtifact] Killed " + killedSentinels.size() + " sentinels: " + killedSentinels);
        }

        // Check for counter-attack from nearby sentinels
        int nearbySentinels = Environment.getInstance().countSentinelsInRadius(
                waspPos.getX(), waspPos.getY(), attackRadius);

        if (nearbySentinels >= 2) {
            // Counter-attack! 20 HP damage (10% of 200)
            System.out.println("[WaspArtifact] COUNTER-ATTACK! " + nearbySentinels + " sentinels attacking!");
            wasp.takeDamageAmount(20);

            // Update health observable
            ObsProperty healthProp = getObsProperty("wasp_health");
            healthProp.updateValue(wasp.getHealth());

            // Update alive status
            ObsProperty aliveProp = getObsProperty("wasp_alive");
            aliveProp.updateValue(wasp.isAlive());

            // Update graphic
            Environment.getInstance().updateWaspHealth(wasp.getHealth(), wasp.getMaxHealth());

            if (!wasp.isAlive()) {
                battleActive = false;
                getObsProperty("battle_active").updateValue(false);
                Environment.getInstance().declareSentinelVictory();
            }
        }
    }

    /**
     * Check if wasp has reached target
     */
    @OPERATION
    void checkReachedTarget() {
        if (targetX < 0 || targetY < 0) {
            return;
        }

        double distance = wasp.distanceTo(new Position(targetX, targetY));
        if (distance < 10) {
            // At target - execute attack
            signal("at_attack_position");
        }
    }

    /**
     * Main battle loop - internal operation
     */
    @INTERNAL_OPERATION
    void battleLoop() {
        // PHASE 1: Wait for sentinels to register in the system
        int initialSentinelCount = 0;
        int waitCycles = 0;
        int maxWaitCycles = 30; // Max 30 seconds

        while (waitCycles < maxWaitCycles) {
            await_time(1000);
            waitCycles++;

            List<Position> sentinels = Environment.getInstance().getSentinelPositions();
            initialSentinelCount = sentinels.size();

            if (initialSentinelCount > 0) {
                // Wait extra time for all sentinels to register
                await_time(2000);
                sentinels = Environment.getInstance().getSentinelPositions();
                initialSentinelCount = sentinels.size();
                break;
            }
        }

        if (initialSentinelCount == 0) {
            System.err.println("[WaspArtifact] ERROR: No sentinels found after waiting! Battle cannot start.");
            return;
        }

        System.out.println("[WaspArtifact] ===== BATTLE BEGINS: Wasp vs " + initialSentinelCount + " Sentinels! =====");

        // Track that battle has properly started
        boolean battleProperlyStarted = true;

        // PHASE 2: Main battle loop
        while (battleActive && wasp.isAlive()) {
            // === MINIMAL BATTLE CHECK (50ms) ===
            // Minimal wait - just one quick check before getting next target
            // Movement loop handles all battle mechanics anyway
            int maxWaitSteps = 1; // 1 * 50ms = 50ms (minimum for thread sync)

            for (int waitStep = 0; waitStep < maxWaitSteps && wasp.isAlive(); waitStep++) {
                await_time(50);

                Position waspPos = wasp.getPosition();
                int waspAttackRadius = 50;
                int sentinelCounterRadius = 100;

                int nearbyForAttack = Environment.getInstance().countSentinelsInRadius(
                        waspPos.getX(), waspPos.getY(), waspAttackRadius);
                int allWithin100px = Environment.getInstance().countSentinelsInRadius(
                        waspPos.getX(), waspPos.getY(), sentinelCounterRadius);

                // WASP ATTACK: Kill 1 or 2 sentinels within 50px (during wait phase)
                if ((nearbyForAttack == 1 || nearbyForAttack == 2)) {
                    List<String> killed = Environment.getInstance().attackSentinelsInRadius(
                            waspPos.getX(), waspPos.getY(), waspAttackRadius, 2);
                    if (!killed.isEmpty()) {
                        System.out.println("[WaspArtifact] *** IDLE ATTACK! Killed " + killed.size() + " sentinel(s)");
                    }
                }

                // SENTINEL COUNTER-ATTACK: 2+ sentinels within 100px (during wait phase)
                if (allWithin100px >= 2 && waitStep % 10 == 0) { // Every 500ms
                    System.out.println(
                            "[WaspArtifact] COUNTER-ATTACK during wait! " + allWithin100px + " sentinels nearby!");
                    wasp.takeDamage(10);
                    updateWaspHealthUI();

                    if (!wasp.isAlive()) {
                        battleActive = false;
                        getObsProperty("battle_active").updateValue(false);
                        Environment.getInstance().declareSentinelVictory();
                        break;
                    }
                }
            }

            if (!wasp.isAlive())
                break;

            // Scan for sentinels
            List<Position> sentinels = Environment.getInstance().getSentinelPositions();
            ObsProperty countProp = getObsProperty("sentinel_count");
            countProp.updateValue(sentinels.size());

            // Only declare victory if battle properly started AND no sentinels remain
            if (sentinels.isEmpty() && battleProperlyStarted) {
                System.out.println("[WaspArtifact] All sentinels eliminated! Wasp wins!");
                battleActive = false;
                getObsProperty("battle_active").updateValue(false);
                Environment.getInstance().declareWaspVictory();
                break;
            }

            // === OPTIMIZED LLM STRATEGY: Use prefetch if available ===
            GeminiService.AttackDecision decision = geminiService.getPrefetchedDecision();

            if (decision == null) {
                // No prefetch ready - get synchronously (first iteration or fallback)
                decision = geminiService.getAttackStrategy(
                        sentinels,
                        wasp.getPosition(),
                        Environment.getInstance().getWidth(),
                        Environment.getInstance().getHeight());
            } else {
                System.out.println("[WaspArtifact] Using prefetched target - NO WAIT!");
            }

            targetX = decision.targetX;
            targetY = decision.targetY;
            lastReasoning = decision.reasoning;
            updateAttackTarget();

            // Calculate initial distance for prefetch trigger
            double initialDistance = wasp.distanceTo(new Position(targetX, targetY));
            boolean prefetchStarted = false;

            // Move toward target - FASTER battle loop
            int steps = 0;
            int counterAttackCooldown = 0;
            int waspAttackCooldown = 0; // New cooldown for Wasp attacks

            while (wasp.distanceTo(new Position(targetX, targetY)) > 20 && steps < 100 && wasp.isAlive()) {
                wasp.moveToward(targetX, targetY, 3); // Original speed
                Environment.getInstance().updateWaspPosition(wasp.getPosition());

                ObsProperty posProp = getObsProperty("wasp_position");
                posProp.updateValues(new Object[] { wasp.getPosition().getX(), wasp.getPosition().getY() });

                await_time(50); // 50ms - fast reaction
                steps++;
                counterAttackCooldown++;
                waspAttackCooldown++;

                Position waspPos = wasp.getPosition();

                // === BATTLE MECHANICS ===
                // Wasp attack radius = 50px (kills 1-2 sentinels)
                int waspAttackRadius = 50;
                // Sentinel counter-attack range = 100px (3+ sentinels deal damage)
                int sentinelCounterRadius = 100;

                // Count sentinels in Wasp's attack range (0-50px)
                int nearbyForAttack = Environment.getInstance().countSentinelsInRadius(
                        waspPos.getX(), waspPos.getY(), waspAttackRadius);

                // Count ALL sentinels within 100px for counter-attack check
                int allWithin100px = Environment.getInstance().countSentinelsInRadius(
                        waspPos.getX(), waspPos.getY(), sentinelCounterRadius);

                // WASP ATTACK: Kill 1 or 2 sentinels within 50px range
                if ((nearbyForAttack == 1 || nearbyForAttack == 2) && waspAttackCooldown >= 10) {
                    waspAttackCooldown = 0; // Reset cooldown

                    List<String> killed = Environment.getInstance().attackSentinelsInRadius(
                            waspPos.getX(), waspPos.getY(), waspAttackRadius, 2); // Max 2 kills
                    if (!killed.isEmpty()) {
                        System.out.println(
                                "[WaspArtifact] *** ATTACK! Killed " + killed.size() + " sentinel(s): " + killed);
                    }
                }

                // SENTINEL COUNTER-ATTACK: 2+ sentinels anywhere within 100px deal damage
                // This includes sentinels IN the 50px zone - if 2+ are nearby, they fight back!
                if (allWithin100px >= 2 && counterAttackCooldown >= 20) {
                    System.out.println(
                            "[WaspArtifact] COUNTER-ATTACK! " + allWithin100px + " sentinels within 100px!");
                    wasp.takeDamageAmount(20); // 20 damage (10%)
                    updateWaspHealthUI();
                    counterAttackCooldown = 0;

                    if (!wasp.isAlive()) {
                        battleActive = false;
                        getObsProperty("battle_active").updateValue(false);
                        Environment.getInstance().declareSentinelVictory();
                        break;
                    }
                }

                // === PREFETCH TRIGGER: Start LLM query at 50% distance ===
                double remainingDistance = wasp.distanceTo(new Position(targetX, targetY));
                if (!prefetchStarted && remainingDistance < initialDistance * 0.5) {
                    // Refresh sentinel positions for prefetch
                    List<Position> currentSentinels = Environment.getInstance().getSentinelPositions();
                    geminiService.prefetchNextStrategy(
                            currentSentinels,
                            wasp.getPosition(),
                            Environment.getInstance().getWidth(),
                            Environment.getInstance().getHeight());
                    prefetchStarted = true;
                }
            }

            if (!wasp.isAlive())
                break;

            // Final attack at target location
            Position waspPos = wasp.getPosition();
            int nearbyCount = Environment.getInstance().countSentinelsInRadius(
                    waspPos.getX(), waspPos.getY(), wasp.getAttackRadius());

            if (nearbyCount == 1) {
                List<String> killed = Environment.getInstance().attackSentinelsInRadius(
                        waspPos.getX(), waspPos.getY(), wasp.getAttackRadius(), wasp.getMaxKillsPerAttack());

                if (!killed.isEmpty()) {
                    System.out.println(
                            "[WaspArtifact] *** ATTACK SUCCESS! Killed " + killed.size() + " sentinels: " + killed);
                }
            } else if (nearbyCount >= 2) {
                System.out.println("[WaspArtifact] Too many bees! Taking damage and retreating...");
            } else {
                System.out.println("[WaspArtifact] No targets. Moving to next location...");
            }
        }

        System.out.println("[WaspArtifact] Battle loop ended. Wasp alive: " + wasp.isAlive());
    }

    private void updateWaspHealthUI() {
        ObsProperty healthProp = getObsProperty("wasp_health");
        healthProp.updateValue(wasp.getHealth());

        ObsProperty aliveProp = getObsProperty("wasp_alive");
        aliveProp.updateValue(wasp.isAlive());

        Environment.getInstance().updateWaspHealth(wasp.getHealth(), wasp.getMaxHealth());
    }
}
