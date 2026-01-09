// Agent wasp in project melissa
// LLM-powered predator agent that hunts sentinel bees

/* Initial beliefs */
health(100).
alive(true).
battle_started(false).  // Track if battle has actually started

/* Initial goals */
!start_hunt.

/* Startup Plan */
+!start_hunt
<-  .print("=== WASP AGENT STARTED ===");
    .print("I am an LLM-powered predator!");
    .print("My goal: Hunt all Sentinel bees!");
    joinWorkspace("hiveOrg", Workspace);
    makeArtifact("WaspBattle", "artifact.WaspArtifact", [], ArtId);
    focus(ArtId);
    .print("Battle artifact created. Let the hunt begin!").

/* Mark battle as started when first sentinel count received */
+sentinel_count(N) : N > 0 & not battle_started(true)
<-  +battle_started(true);
    .print("Sentinels remaining: ", N).

/* React to sentinel count changes - only after battle started */
+sentinel_count(N) : N > 0 & battle_started(true)
<-  .print("Sentinels remaining: ", N).

/* React to health changes */
+wasp_health(H) : H < 50 & H > 0
<-  .print("WARNING: Health critical! ", H, " HP remaining!").

/* React to wasp health reaching zero - DEFEAT */
+wasp_health(0)
<-  .print("I have been defeated...").

/* React to wasp defeat (wasp_alive becomes false) */
+wasp_alive(false)
<-  .print("=== DEFEATED! Sentinels overwhelmed me! ===");
    .print("JaCaMo-based Sentinels are the WINNERS!").

/* React to battle ending with wasp still alive - VICTORY */
+battle_active(false) : wasp_alive(true)
<-  .print("=== VICTORY! All sentinels eliminated! ===");
    .print("LLM-based Wasp Agent is the WINNER!").

/* React to attack decisions from LLM */
+attack_target(X, Y, Reasoning) : X >= 0 & Y >= 0
<-  .print("LLM Strategy: Moving to (", X, ",", Y, ")");
    .print("Reasoning: ", Reasoning).

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
