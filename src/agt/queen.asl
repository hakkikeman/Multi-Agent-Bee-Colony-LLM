// Agent queen in project melissa

/* Initial beliefs and rules */

maxEnergy(1000).
energy(1000).
	
is_hungry :-
	energy(E) &
	maxEnergy(M) &
	E <= M * 0.2.

/* Initial goals */

!startOrg("hive1").

/* Organisational Plans */

+!startOrg(Id)
<-  makeArtifact(Id, "ora4mas.nopl.SchemeBoard",["src/org/organisation.xml", doSimulation],SchArtId);
	debug(inspector_gui(on))[artifact_id(SchArtId)];
	.my_name(Me); setOwner(Me)[artifact_id(SchArtId)];  // I am the owner of this scheme!
	focus(SchArtId);
	.wait(200);
	addScheme(Id);  // set the group as responsible for the scheme
	adoptRole(monarch);
	commitMission(mQueen)[artifact_id(SchArtId)];
	commitMission(mFeeding)[artifact_id(SchArtId)];
	commitMission(mRenewal)[artifact_id(SchArtId)];
	commitMission(mTemperature)[artifact_id(SchArtId)].
	
+!start[scheme(Sch)]                        // plan for the goal start defined in the scheme
<- 	makeArtifact("Hive", "artifact.HiveArtifact", [], HiveId); // create the hive artifact
    focus(HiveId);  // place observable properties of ArtId into a name space
    .print("Starting hive artifact");
      
  	makeArtifact("Map", "artifact.MapArtifact", [], MapId); // create the map artifact
    focus(MapId);  // place observable properties of ArtId into a name space
    .print("Starting map artifact");
	
	.wait(6000).
	
+!registerBee[scheme(Sch)]
<-	registerBee(monarch); 
	hiveStart.
		
/* Management Plans */		
		
+!feeding : goalState(_,feeding,_,_,satisfied)
<-	resetGoal(feeding).
+!swarmRenewal : goalState(_,swarmRenewal,_,_,satisfied)
<-	resetGoal(swarmRenewal).
+!temperatureControl : goalState(_,temperatureControl,_,_,satisfied)
<-	resetGoal(temperatureControl).

+!feeding 			<- .wait(100); !feeding.
-!feeding 			<- .wait(100); !feeding.
+!swarmRenewal 		<- .wait(100); !swarmRenewal.
-!swarmRenewal 		<- .wait(100); !swarmRenewal.
+!temperatureControl 	<- .wait(100); !temperatureControl.
-!temperatureControl 	<- .wait(100); !temperatureControl.

/* Renew Plans */
		
+!layEggs[scheme(Sch)] : energy(E) & not is_hungry
<-	-+energy(E-5);
	createLarva;
	.wait(7000);
	!!layEggs[scheme(Sch)].
	
+!eat(X) : energy(E) <-	eat(math.floor(X/10)); -+energy(E+X).
-!eat(X).

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }