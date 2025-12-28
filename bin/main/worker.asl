// Agent worker in project melissa

/* Initial beliefs and rules */
energy(100).
maxEnergy(100).
lifespan(45).
niceTemperature(25).
newBees(1).

/* Rules */

is_hot(T) :-
	niceTemperature(NT) &
	T > NT + 1.

is_cold(T) :-
	niceTemperature(NT) &
	T < NT - 1.
	
is_hungry(E) :-
	maxEnergy(M) &
	E <= M * 0.2.
	
is_satisfied(E) :-
	maxEnergy(M) &
	E > M * 0.9.
	
age_to_sentinel :-
	birthDay(N) &
	today(D) &
	D-N >= 18 & 
	role(nurse).
	
age_to_explorer :-
	birthDay(N) &
	today(D) &
	D-N >= 22 & 
	role(sentinel).

too_old :-
	birthDay(N) &
	today(D) &
	D-N >= 45 &
	role(explorer).

/* Initial goals */

/* Organisational Plans */

+obligation(Ag,Norm,committed(Ag,Mission,Scheme),Deadline)
    : .my_name(Ag)
   <- //.print("I am obliged to commit to ",Mission," on ",Scheme);
      commitMission(Mission)[artifact_name(Scheme)].
      
/*   Basic Plans  */
+!born
<-	//.print("I'm being born!");
	.abolish(birthDay(_));
	.abolish(age(_));
	joinWorkspace("hiveOrg",Workspace);
	lookupArtifact("hive1",SchArtId);
	focus(SchArtId);
	!!registerBee.

+!registerBee : age(X)
<-	.random(N);
	lookupArtifact("Map",AId);
	focus(AId);
	?day(D)[artifact_id(AId)];
	+today(D);
	if (X < 18) {
		+birthDay(D-math.floor(18*N));
		adoptRole(nurse);
		commitMission(mNurse);
		registerBee(nurse);
		+role(nurse)
	} else { if (X < 22) {
		+birthDay(D-(18+math.floor(4*N)));
		adoptRole(sentinel);
		commitMission(mSentinel);
		registerBee(sentinel);
		+role(sentinel)
	} else {
		+birthDay(D-(22+math.floor(23*N)));
		adoptRole(explorer);
		commitMission(mExplorer);
		registerBee(explorer);
		+role(explorer)
	}};
	-age(_);
	!!updateDay.

+!registerBee
<-	lookupArtifact("Map",AId);
	focus(AId);
	?day(D)[artifact_id(AId)];
	+today(D);
	+birthDay(D);
	adoptRole(nurse);
	commitMission(mNurse);
	registerBee(nurse);
	+role(nurse);
	!!startNurse. 

+!startNurse
<-	.drop_all_intentions;
	!!feedLarvae;
	!!feedQueen;
	!!feedSelf;
	!!makeHoney;
	!!feedSelf;
	!!updateDay.

+!startSentinel
<-	.drop_all_intentions;
	!!heat;
	!!cool;
	!!feedSelf;
	!!updateDay.

+!startExplorer
<-	.drop_all_intentions;
	!!stopHeatCool;
	!!feedSelf;
	!!searchPollen;
	!!updateDay.

+!updateDay : today(H)
<-	.wait(5000);
	lookupArtifact("Map",AId);
	focus(AId);
	?day(D)[artifact_id(AId)];
	if (D \== H) {
		-+today(D);
		?energy(E);
		-+energy(E-1);
		!!changeStatus;
	};
	!updateDay.

+!changeStatus : too_old
<-	.random(N);
	if(N < 0.5) {
		!!suicide;
	}.
	
+!changeStatus : age_to_explorer
<-	changeRole(explorer); 
	leaveMission(mSentinel);
	adoptRole(explorer);
	commitMission(mExplorer);
	-role(sentinel);
	+role(explorer);
	!startExplorer.
	
+!changeStatus : age_to_sentinel
<-	changeRole(sentinel);
	leaveMission(mNurse);
	adoptRole(sentinel);
	commitMission(mSentinel);
	-role(nurse);
	+role(sentinel);
	!startSentinel.

+!changeStatus.

+!suicide : .my_name(Me)
<- 	.findall(Mission,commit(Me,Mission,_),M);
	if (not .empty(M)) {
		!leaveMission(M);
	};
	.findall(Role,play(Me,Role,_),R);
	if (not .empty(R)) {
		!removeRole(R);
	};
	unRegisterBee;
	.kill_agent(Me).
	
-!suicide <- .wait(500); !suicide.

+!leaveMission([M|R])
<-	leaveMission(M);
	if(not .empty(R)) {
		!leaveMission(R)
	}.

+!removeRole([Role|R])
<-	removeRole(Role);
	if(not .empty(R)) {
		!removeRole(R)
	}.

+!feedSelf: energy(E) & not is_satisfied(E)
<-	eat(1);
	-+energy(E+10);
	.wait(100); 
	!!feedSelf.

-!feedSelf <- .wait(100); !!feedSelf.
+!feedSelf <- .wait(100); !!feedSelf.

+energy(E) : E <= 0 <- !suicide.

/* ---------- Nurse Plans ---------- */

+!makeHoney : energy(E) & not is_hungry(E) & role(nurse)
<-	!tryPollen;	
	.wait(100);
	-+energy(E-1);
	!!makeHoney.
	
+!makeHoney <- .wait(100); !makeHoney.

-!makeHoney
<-	.wait(500);
	!makeHoney.

+!tryPollen
<- 	lookupArtifact("Hive",AId);
	focus(AId);
	if(pollen(P)[artifact_id(AId)] & P>1) {
		processPollen;
	}.

+!feedQueen : energy(E) & role(nurse)
<-	.send(queen, achieve, eat(50));
	-+energy(E-1).

+!feedQueen.

+!feedLarvae : role(nurse)
<-  lookupArtifact("Hive",AId);
	focus(AId);
	?larvas(NR);
	if (NR > 0) {
		//.print("Feeding Larva");
		feedLarva(L);
		if (L) {
			!!evolveLarva;
		}
	}
	.wait(300);
	!!feedLarvae[scheme(Sch)].

+!feedLarvae.	
	
-!feedLarvae[error(ia_failed)] <- 
	.print("Could not feed the larvae!");
	.wait(300);
	!!feedLarvae[scheme(Sch)].
-!feedLarvae[error_msg(M)] <- 
	//.print("Could not feed the larvae! Error: ",M);
	.wait(300);
	!!feedLarvae[scheme(Sch)].	
	
//-!feedLarvae <- .wait(300); !!feedLarvae.

+!evolveLarva : newBees(SEQ) & .my_name(N) & role(nurse)
<- //.print("Larva is evolving");
   .concat(N, "_new", NSUFIX);
   .concat(NSUFIX, SEQ, NEWBEE);
   .create_agent(NEWBEE,"worker.asl");
   .send(NEWBEE, achieve, born);
   -+newBees(SEQ+1).
   
+!evolveLarva.

/* ---------- Sentinel Plans ---------- */

+!heat : cooling & energy(E) & not is_hungry(E) & role(sentinel)
<- 	.wait(100+math.random(200));
	lookupArtifact("Hive",AId);
	focus(AId);
	if(intTemperature(T)[artifact_id(AId)] & is_cold(T)) {
		stop_cool;
		-cooling;
		-+energy(E-1)
	};
	!!heat.

+!heat : not heating & energy(E) & not is_hungry(E) & role(sentinel)
<- 	.wait(100+math.random(200));
	lookupArtifact("Hive",AId);
	focus(AId);
	if(intTemperature(T)[artifact_id(AId)] & is_cold(T)) {
		heat;
		+heating;
		-+energy(E-1)
	};
	!!heat.
	
+!heat : role(sentinel) <- .wait(100+math.random(200)); !!heat.

+!heat.

+!cool: heating & energy(E) & not is_hungry(E) & role(sentinel)
<- 	.wait(100+math.random(200));
	lookupArtifact("Hive",AId);
	focus(AId);
	if(intTemperature(T)[artifact_id(AId)] & is_hot(T)) {
		stop_heat;
		-heating;
		-+energy(E-1)
	};
	!!cool.

+!cool: not cooling & energy(E) & not is_hungry(E) & role(sentinel)
<- 	.wait(100+math.random(200));
	lookupArtifact("Hive",AId);
	focus(AId);
	if(intTemperature(T)[artifact_id(AId)] & is_hot(T)) {
		cool;
		+cooling;
		-+energy(E-1)
	};
	!!cool.
	
+!cool : role(sentinel)<- .wait(100+math.random(200)); !!cool.

+!cool.

+!stopHeatCool : role(explorer) & heating
<- stop_heat; -heating; !stopHeatCool.

+!stopHeatCool : role(explorer) & cooling
<- stop_cool; -cooling; !stopHeatCool.

+!stopHeatCool.

/* ---------- Explorer Plans ---------- */
	
+!searchPollen : energy(E) & not is_hungry(E) & role(explorer)
<-	lookupArtifact("Map",AId);
	focus(AId);
	.findall(r(X, Y, WIDTH, HEIGHT), pollenField(_, X, Y, WIDTH, HEIGHT)[artifact_id(AId)], List);
	!flyToField(List);
	if(collect) {
		-collect;
		!collectPollen;
		!bringPollen
	} else {
		!!searchPollen
	}.

+!searchPollen : role(explorer) <- .wait(100); !!searchPollen.
+!searchPollen.

-!searchPollen[error(ia_failed)] <- .print("Could not search!").
-!searchPollen[error_msg(M)]     <- .print("Error searchPollen in: ",M); !!searchPollen.

+!flyToField([r(X0,Y0,W,H)|L]) : not flying
<-	+flying;
	.random(N);
	if (N < 0.2) {
		.random(R1);
		X = X0 + math.floor(W*R1);
		.random(R2);
		Y = Y0 + math.floor(H*R2);
		//.print("Going to (", X, ",",Y,")");
		flyTo(X,Y);
		+collect
	} else { if(not .empty(L)) {
		-flying;
		!flyToField(L)
	} else {
		
	}};
	-flying.

+!flyToField(R) <- .wait(200); !flyToField(R).

+!bringPollen : role(explorer)
<-	!flyToHive;
	!storePollen.
	
+!bringPollen.

-!bringPollen[error(ia_failed)] <- .print("I didn't in!").
-!bringPollen[error_msg(M)]     <- .print("Error in: ",M).

+!flyToHive : not flying
<-	+flying;
	lookupArtifact("Map",AId);
	focus(AId);
	?hive(X0,Y0,W,H)[artifact_id(AId)];
	.random(R1);
	X = X0 + math.floor(W*R1);
	.random(R2);
	Y = Y0 + math.floor(H*R2);
	//.print("Going to (", X, ",",Y,")");
	flyTo(X,Y);
	-flying.
	
+!flyToHive <- .wait(200); !flyToHive.

+!storePollen : energy(E) & role(explorer)
<-	delivery;
	-+energy(E-10);
	!!searchPollen.

+!storePollen <- .wait(500); !!searchPollen.

-!storePollen[error(ia_failed)] <- !!searchPollen.
-!storePollen[error_msg(M)] <- .print("Error: ", M); !!searchPollen.

+!collectPollen : role(explorer) <- collect.

+!collectPollen.

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }
{ include("$jacamoJar/templates/org-obedient.asl") }