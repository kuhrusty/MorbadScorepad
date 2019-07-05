morbad.setSkills([
	{
		"id": "alchemy",
		"mastery": {"text": "{{settlement}} Choose an Alchemical Item in the Loot or Epic Loot discard pile & make a MAG Test. If you pass & the {:GP:} cost of the Item is equal or less than your highest single die, you may buy the chosen Item for half its standard cost."},
		"name": "Alchemy",
		"req": "Scholar",
		"skill": {
			"text": "{{town}} Choose an Alchemical Item in the Loot discard pile & make a MAG Test. If you pass & the {:GP:} cost of the Item is equal or less than your highest single die, you may buy the chosen Item for its standard cost.",
			"xp": 8
		}
	},
	{
		"id": "backstab",
		"mastery": {"text": "{{assault}} If you are using a Blade, your Attacks against monsters that are not targeting you ignore 2 points of ARM, or inflict +1 damage if the target has 0 ARM."},
		"name": "Backstab",
		"req": "Hunter or Rogue",
		"skill": {
			"text": "{{assault}} If you are using a Blade, your Attacks against monsters that are not targeting you ignore 1 point of ARM.",
			"xp": 10
		}
	},
	{
		"id": "black_market",
		"mastery": {"text": "{{settlement}} Draw a card from the Loot deck. If it is an Ally, Item, or Service, you may purchase it for normal cost *(otherwise discard it)*. In addition, you may sell Items for half their listed cost."},
		"name": "Black Market",
		"req": "Merchant or Rogue",
		"skill": {
			"text": "{{bold settlement}} Draw a card from the Loot deck. If it is an Ally, Item, or Service, you may purchase it for normal cost *(otherwise discard it)*.",
			"xp": 8
		}
	},
	{
		"id": "bleeding",
		"mastery": {"text": "{{town || camp}} Choose an adventurer in your party *(or yourself)* & roll 3 dice. The chosen adventurer becomes Fatigued & takes pierching damage equal to your lowest single die. Then, remove a Plague{:PLAGUE:} counter from that adventurer for each {::6::} rolled. If you roll triples, the chosen adventurer may also discard a Disease card."},
		"name": "Bleeding",
		"req": "Scholar",
		"skill": {
			"text": "{{town || camp}} Choose an adventurer in your party *(or yourself)* & roll 2 dice. The chosen adventurer becomes Fatigued & takes piercing damage equal to your lowest single die. Then, remove a {:PLAGUE:} counter from that adventurer for each {::6::} rolled",
			"xp": 8
		}
	},
	{
		"id": "blood_curse",
		"mastery": {"text": "**QUICK ACTION:** Make a MAG Test. If you pass, please this card on target monster. Return this card to your hand at the end of the fight, or when that monster is destroyed. --- If you take {:WOUNDS:} while the card is on a monster, place the same number of {:WOUNDS:} on the attached monster."},
		"name": "Blood Curse",
		"req": "Warlock",
		"skill": {
			"text": "**COMBAT ACTION:** Make a MAG Test. If you pass, place this card on target monster. Return this card to your hand at the end of the fight, or when that monster is destroyed. --- If you take {:WOUNDS:} while the card is on a monster, also place half that many {:WOUNDS:} on the attached monster.",
			"xp": 8
		}
	},
	{
		"id": "bloodlust",
		"mastery": {"text": "{{assault}} When you destroy a monster, you become Exalted. Then, make a MAG Test & if you pass you become Invigorated & Focused."},
		"name": "Bloodlust",
		"req": "Warlock or Wild",
		"skill": {
			"text": "{{assault}} Make a MAG Test when you destroy an enemy. If you pass, you become Invigorated & Focused.",
			"xp": 13
		}
	},
	{
		"id": "burly",
		"mastery": {"text": "You may carry an additional {:HEAVY:} Item. --- If you are not Weakend, you may become Weakend to Force March while you are Fatigued."},
		"name": "Burly",
		"req": "STR 8",
		"skill": {
			"text": "You may carry an additional {:HEAVY:} Item.",
			"xp": 6
		}
	},
	{
		"id": "call_fire",
		"mastery": {"text": "****QUICK ACTION:**** Make a PER Test & if you pass, place this card on a target monster. Return it to your hand at the end of the fight, or if either you or the attached monster leaves the fight or is destroyed/Defeated. You may choose to return it to your hand at the start of your turn. While this card is attached to a monster, adventurers may target the attached monster with Range or Reach Attacks *(even if they are being attacked & Taunted)*."},
		"name": "Call Fire",
		"req": "Hunter or Militant",
		"skill": {
			"text": "{{guard}} **QUICK ACTION:** Make a PER Test & if you pass, place this card on a target monster. Return it to your hand at the end of the fight, or if either you or the attached monster leaves the fight or is destroyed/Defeated.  While this card is attached to a monster, adventurers may target the attached monster with Range or Reach Attacks *(even if they are being attacked & Taunted)*.",
			"xp": 8
		}
	},
	{
		"id": "claws",
		"mastery": {"text": "ATTACK: AGI (MELEE)  {{assault}} Attacks with this Mastery ignore 1 point of ARM."},
		"name": "Claws",
		"req": "Reptilian, Warlock, or Lycanthrope",
		"skill": {
			"text": "{{assault}} ATTACK: AGI (MELEE)",
			"xp": 6
		}
	},
	{
		"id": "combat_reflexes",
		"mastery": {"text": "{{assault}} [[+1 AGI]] *This bonus does not apply until your first turn in a fight.* You may perform 2 Quick Actions in each Combat Round, but you cannot perform the same Quick Action twice in the same round."},
		"name": "Combat Reflexes",
		"req": "Hunter, or Rogue",
		"skill": {
			"text": "{{assault}} [[+1 AGI]] *This bonus does not apply until your first turn in a fight.*",
			"xp": 13
		}
	},
	{
		"id": "counter",
		"mastery": {"text": "{{guard]} If you are being targeted by more than one monster, you may roll an additional Power die. For each 6 you roll on a Power die, inflict 1 Piercing damage on a monster targeting you without {:RANGE:}."},
		"name": "Counter",
		"req": "AGI 8",
		"skill": {
			"text": "{{guard}} For each 6 you roll on a Power die, inflict 1 Piercing damage on a monster targeting you without {:RANGE:}.",
			"xp": 10
		}
	},
	{
		"id": "dauntless",
		"mastery": {"text": "You may use MRL for Force March in Tests *(in place of CON)*. --- You are immune to Fear."},
		"name": "Dauntless",
		"req": "MRL 8",
		"skill": {
			"text": "You may use MRL for Force March Tests (*in place of CON*).",
			"xp": 6
		}
	},
	{
		"id": "dead_eye_shot",
		"mastery": {"text": "{{assault}} **QUICK ACTION:** If you make a Range Attack this turn, you may swap the value of a Power die with the value of one of your Attack dice, unless your target is an Ooze or Swarm, or has Ghostly.  {{assault}} If you roll {::6::} on a Power die *(after swapping a die or choosing not to)*, your Attack inflicts Piercing damage unless your target is an Ooze or Swarm, or has Ghostly."},
		"name": "Dead Eye Shot",
		"req": "Hunter, Militant, or Wild",
		"skill": {
			"text": "{{assault}} **QUICK ACTION:** If you make a Range Attack this turn, you may swap the value of a Power die with the value of one of your Attack dice, unless your target is an Ooze or Swarm, or has Ghostly.",
			"xp": 13
		}
	},
	{
		"id": "decay",
		"mastery": {"text": "{{assault}} **COMBAT ACTION:** Choose a target monster & make a MAG Test. If you pass & your highest die is equal to or greater than your target's {:XP:} VAL, your target becomes Corroded and takes damage equal to your lowest single die. Inflict damage equal to your highest die instead if the target is a Construct."},
		"name": "Decay",
		"req": "Warlock",
		"skill": {
			"text": "{{assault}} **COMBAT ACTION:** Choose a target monster & make a MAG Test. If you pass & your highest die is equal to or greater than your target's {:XP:} VAL, your target becomes Corroded. If you fail the Test, you become Suppressed.",
			"xp": 8
		}
	},
	{
		"id": "dilettante",
		"mastery": {"text": "When you **Improve**, you may learn any Skill that requires: *Fighter*, *Hunter*, *Merchant*, *Militant*, *Performer*, or *Rogue* *(even if you do not meet its requirements)*.  Place that Skill on this card & while it is there, you always meet that Skill's requirements.  You cannot Master a Skill on this card & you can only learn up to three Skill in this way.  If you discard this Skill, also discard all the attached Skills."},
		"name": "Dilettante",
		"req": "Scholar",
		"skill": {
			"text": "When you **Improve**, you may learn any Skill that requires: *Fighter*, *Hunter*, *Merchant*, *Militant*, *Performer*, or *Rogue* *(even if you do not meet its requirements)*.  Place that Skill on this card & while it is there, you always meet that Skill's requirements.  You cannot Master a Skill on this card & you can only learn one Skill in this way. If you discard this Skill, also discard the attached Skill.",
			"xp": 6
		}
	},
	{
		"id": "dopesmoker",
		"mastery": {"text": "When you make a Test while using an Herb, you may roll an additional die & use any 2.  {{bold settlement}} You may purchase an Herb Item from the Loot discards."},
		"name": "Dopesmoker",
		"req": "MAG 6",
		"skill": {
			"text": "When you make a Test while using an Herb, you may roll an additional die & use any 2.",
			"xp": 6
		}
	},
	{
		"id": "drain_life",
		"mastery": {"text": "**QUICK ACTION:** If you destroyed a monster this round, you recover a number of {:WOUNDS:} equal to that monster's {:XP:} VAL, unless it was a Construct or Undead."},
		"name": "Drain Life",
		"req": "Warlock",
		"skill": {
			"text": "{{assault}} **QUICK ACTION:** If you destroyed a monster this round, you recover a number of {:WOUND:} equal to that monster's {:XP:} VAL, unless it was a Construct or Undead.",
			"xp": 10
		}
	},
	{
		"id": "exploit",
		"mastery": {"text": "{{assault}} Your Attacks inflict +1 damage against monsters that are Dazed or Hexed & monsters with Berzerk, Fury, or Dimwit."},
		"name": "Exploit",
		"req": "Fighter, Hunter, Militant, or Rogue",
		"skill": {
			"text": "{{assault}} Your Attacks inflict +1 damage against monsters that are Dazed or Hexed.",
			"xp": 10
		}
	},
	{
		"id": "exterminator",
		"mastery": {"text": "{{assault}} Your Attacks inflict +1 damage against Filth, Fungus, Lycanthropes, Swarms & Vermin."},
		"name": "Exterminator",
		"req": "Fighter, Hunter, or Wild",
		"skill": {
			"text": "{{assault}} Your Attacks inflict +1 damage against Swarms & Vermin.",
			"xp": 8
		}
	},
	{
		"id": "ferocious_charge",
		"mastery": {"text": "{{assault}} **QUICK ACTION:** If it is your first turn after entering a fight, you may roll an additional Power die when using a STR Attack this turn.  {{assault}} If you roll doubles on your Power dice, your target becomes Dazed until the end of your next turn *(even if you missed)*."},
		"name": "Ferocious Charge",
		"req": "STR 8",
		"skill": {
			"text": "{{assault}} **QUICK ACTION:** If it is your first turn after entering a fight, you may roll an additional Power die when using a STR Attack this turn.",
			"xp": 8
		}
	},
	{
		"id": "fieldcraft",
		"mastery": {"text": "{{outside}} You may use CON for Recovery *(in place of MRL)*.  {{outside}} You do not have to roll for **Stealth** *(you automatically pass your PER Test when using Stealth)*."},
		"name": "Fieldcraft",
		"req": "Wild",
		"skill": {
			"text": "{{outside}} You may use CON for Recovery *(in place of MRL)*.",
			"xp": 6
		}
	},
	{
		"id": "find_weakness",
		"mastery": {"text": "**QUICK ACTION:** Make a PER Test. If you pass, attach this card to target monster *(place this card on that monster's card)*. --- Attacks inflict +1 damage against the attached monster. --- Return this card to your hand at the end of the fight, or at the end of the combat round if the attached monster is removed from the fight."},
		"name": "Find Weakness",
		"req": "Fighter, Hunter, Scholar, or Wild",
		"skill": {
			"text": "{{guard}} **COMBAT ACTION:** Make a PER Test. If you pass, attach this card to target monster *(place this card on that monster's card)*. --- Attacks inflict +1 damage against the attached monster. --- Return this card to your hand at the end of the fight, or at the end of the combat round if the attached monster is removed from the fight.",
			"xp": 8
		}
	},
	{
		"id": "foe_killer",
		"mastery": {"text": "{{assault}} Your Melee Attacks against targets with {:XP:} VAL 2 or more inflict +1 damage & ignore 1 point of ARM."},
		"name": "Foe Killer",
		"req": "Fighter, Hunter, or Militant",
		"skill": {
			"text": "{{assault}} Your Melee Attacks inflict +1 damage against targets with {:XP:} VAL 3 or more.",
			"xp": 8
		}
	},
	{
		"id": "forlorn_hope",
		"mastery": {"text": "While you have {:WOUNDS:} equal to or greater than your CON, you have +1 STR & you are immune to Fear & Pain.  {{guard}} While you have {:WOUNDS:} equal to or greater than your CON, you may use MRL for Defense."},
		"name": "Forlorn Hope",
		"req": "Fighter, Militant, Puritan, or Wild",
		"skill": {
			"text": "{{assault}} While you have {:WOUNDS:} equal to or greater than your CON, you have +1 STR & you are immune to Fear & Pain.",
			"xp": 8
		}
	},
	{
		"id": "goblin_slayer",
		"mastery": {"text": "Your Attacks inflict +1 damage against Goblins.  {{assault}} **QUICK ACTION:** If you destroyed a monster this round, choose a target Goblin & make a STR Test. If you pass & your highest single die is equal to or greater than the target's remaining {:HP:}, discard the target without reward."},
		"name": "Goblin Slayer",
		"req": "Fighter, Hunter, or Wild",
		"skill": {
			"text": "{{assault}} Your Attacks inflict +1 damage against Goblins.",
			"xp": 8
		}
	},
	{
		"id": "gormet",
		"mastery": {"text": "At the end of a fight, you may consume one Brute, Filth, Goblin, or Human that your party destroyed. If you do, make a CON Test. If you pass, recover {:WOUNDS:} equal to your lowest single die. If you fail, you become Poisoned."},
		"name": "Gormet",
		"req": "MRL < 9",
		"skill": {
			"text": "At the end of a fight, you may consume one Brute, Filth, or Goblin that your party destroyed. If you do, make a CON Test. If you pass, recover {:WOUNDS:} equal to your lowest single die. If you fail, you become Poisoned.",
			"xp": 6
		}
	},
	{
		"id": "illusory_double",
		"mastery": {"text": "{{guard}} **QUICK ACTION:** If it is the first round of a fight, roll D6 for each monster targeting you. If you roll a 3 or higher, that monster attacks your duplicate & inflicts no damage on you.  This Skill has no effect on Void monsters."},
		"name": "Illusory Double",
		"req": "Reptilian or Wizard",
		"skill": {
			"text": "{{guard}} **QUICK ACTION:** If it is the first round of a fight, roll D6 for each monster targeting you. If you roll a 5 or higher, that monster attacks your duplicate & inflicts no damage on you.  This Skill has no effect on Void monsters.",
			"xp": 10
		}
	},
	{
		"id": "immunity",
		"mastery": {"text": "You are immune to Poison *(you never become Poisoned)*."},
		"name": "Immunity",
		"req": "Rogue, Scholar, Warlock, or Wild",
		"skill": {
			"text": "If you would become Poisoned, first roll D6. If your roll is equal to or greater than your {:WOUNDS:}, you are not Poisoned.",
			"xp": 8
		}
	},
	{
		"id": "inquisitor",
		"mastery": {"text": "{{assault}} Your attacks inflict +1 damage against Daemons, Heretics, Warlocks, Witches & Wizards."},
		"name": "Inquisitor",
		"req": "Puritan",
		"skill": {
			"text": "{{assault}} Your attacks inflict +1 damage against Daemons & Heretics.",
			"xp": 10
		}
	},
	{
		"id": "intimidate",
		"mastery": {"text": "{{assault}} **QUICK ACTION:** If you destroyed an enemy this round, choose a target Bandit, Heretic, or Human with {:XP:} VAL 2 or less & make a MRL Test. If you pass & your highest single die is equal to or greater than the target's remaining {:HP:}, discard it without reward."},
		"name": "Intimidate",
		"req": "MRL 8 or STR 8",
		"skill": {
			"text": "{{assault}} **QUICK ACTION:** If you destroyed an enemy this round, choose a target Bandit or Heretic with {:XP:} VAL 1 & make a MRL Test. If you pass & your highest single die is equal to or greater than the target's remaining {:HP:}, place the target in its discard pile.",
			"xp": 8
		}
	},
	{
		"id": "levitate",
		"mastery": {"text": "You may use MAG for Forced March Tests. --- You may use MAG for any Test to avoid the effects of a Trap Encounter. --- Your Melee Attacks count as Reach Attacks. --- You may move into a {:DESTROYED:} space."},
		"name": "Levitate",
		"req": "MAG 9",
		"skill": {
			"text": "You may use MAG for Forced March Tests. --- You may use MAG for any Test to avoid the effects of a Trap Encounter. --- Your Melee Attacks count as Reach Attacks.",
			"xp": 6
		}
	},
	{
		"id": "looter",
		"mastery": {"text": "When you make a Loot roll, roll two dice & use either result *(ignore the other die)*. --- If you draw a Disease or Monster while Looting, make a PER Test. If you pass, you may discard that card without resolving it."},
		"name": "Looter",
		"req": "Merchant or Rogue",
		"skill": {
			"text": "When you make a Loot roll, roll two dice & use either result *(ignore the other die)*.",
			"xp": 8
		}
	},
	{
		"id": "lurker",
		"mastery": {"text": "During a fight, you may skip your turn & act after any other player's turn. --- If another member of your party is also using this Skill/Mastery, agree who will act first or roll off."},
		"name": "Lurker",
		"req": "AGI 7 & PER 7",
		"skill": {
			"text": "During a fight, you may skip your turn & act last; after all other adventurers in your party have taken a turn. --- If another member of your party is also using this Skill/Mastery, agree who will act first or roll off.",
			"xp": 6
		}
	},
	{
		"id": "lycanthropy",
		"mastery": {"text": "**QUICK ACTION:** Place a {:WOUND:} counter on this card or remove a {:WOUND:} from it. *Remove all counters from this card at the end of the fight or if you become Defeated.*  *If there are counters on this card:* You take 1 {:WOUND:} at the end of your turn each round. You cannot use 1H, 2H, or LH Items. [[+1 AGI]] [[+1 MAG]] [[+1 PER]]"},
		"name": "Lycanthropy",
		"req": "Lycanthrope",
		"skill": {
			"text": "**QUICK ACTION:** Place a {:WOUND:} counter on this card or remove a {:WOUND:} from it. *Remove all counters from this card at the end of the fight or if you become Defeated, Stunned, or Suppressed. You cannot place counters on this card while you have one of these conditions.*  *If there are counters on this card:* You take 1 {:WOUND:} at the end of your turn each round. You cannot use 1H, 2H, or LH Items. [[+1 AGI]] [[+1 PER]]",
			"xp": 13
		}
	},
	{
		"id": "magic_missile",
		"mastery": {"text": "ATTACK: PER (RANGE) If your Attack dice roll doubles, this Attack inflicts Piercing damage."},
		"name": "Magic Missile",
		"req": "Wizard",
		"skill": {
			"text": "ATTACK: PER (RANGE)",
			"xp": 8
		}
	},
	{
		"id": "martial_discipline",
		"mastery": {"text": "You are immune to Annoy & Taunt. --- You may target any monster in your space, even while you are being targeted."},
		"name": "Martial Discipline",
		"req": "Militant or Fighter",
		"skill": {
			"text": "You are immune to Annoy & Taunt.",
			"xp": 8
		}
	},
	{
		"id": "meditation",
		"mastery": {"text": "{{town || camp}} You may use MAG for Recovery Tests *(in place of MRL)*.  {{town || camp}} If you pass your Recovery Test, you may discard a Skill & gain {:XP:} equal to that Skill's {:XP:} cost *(you cannot exchange Masteries this way)*."},
		"name": "Meditation",
		"req": "Wizard",
		"skill": {
			"text": "{{town || camp}} You may use MAG for Recovery Tests *(in place of MRL)*.",
			"xp": 6
		}
	},
	{
		"id": "mend_wounds",
		"mastery": {"text": "{{town || camp}} Make a MAG Test. If you pass, you & each other adventurer in your party recovers {:WOUND:} equal to your lowest single die & this Skill/Mastery cannot be used by another member of your party this round."},
		"name": "Mend Wounds",
		"req": "Scholar or Wild",
		"skill": {
			"text": "{{town || camp}} Make a MAG Test. If you pass, you & each other adventurer in your party recovers 1 {:WOUND:} & this Skill/Mastery cannot be used by another member of your party this round.",
			"xp": 8
		}
	},
	{
		"id": "misdirection",
		"mastery": {"text": "If this card is attached to a monster during the Establish Targets step, you may choose that monster's target. --- Return this card to your hand if you or the attached monster leaves the fight for any reason. --- You may use this Master to force a monster that targets {:TARGET_ALL:} to garget onely one adventurer. --- **QUICK ACTION:** Choose a target monster & make an AGI or MAG Test. If you pas, place this card on the target."},
		"name": "Misdirection",
		"req": "Performer, Reptilian, or Wizard",
		"skill": {
			"text": "If this card is attached to a monster during the Establish Targets step, you may choose that monster's target. --- Return this card to your hand if you or the attached monster leaves the fight for any reason. --- This Skill has no effect on monsters that target {:TARGET_ALL:}. --- **QUICK ACTION:** Choose a target monster & make an AGI or MAG Test. If you pass & your highest single die is equal to or greater than the target's {:XP:} VAL, place this card on the target.",
			"xp": 8
		}
	},
	{
		"id": "mist_form",
		"mastery": {"text": "{{guard}} You may use MAG to **Escape** *(in place of AGI)* & if you succeed, each other adventurer in your party may immediately attempt to **Escape** as a free bonus action. --- {{guard}} **COMBAT ACTION:** Make a MAG Test. If you pass, monsters cannot inflict {:WOUNDS:} on you this round unless they are Void or Ghostly."},
		"name": "Mist Form",
		"req": "Warlock or Wizard",
		"skill": {
			"text": "{{guard}} You may use MAG to **Escape** *(in place of AGI)*. --- {{guard}} **COMBAT ACTION:** Make a MAG Test. If you pass, monsters cannot inflict {:WOUNDS:} on you this round unless they are Void or Ghostly.",
			"xp": 8
		}
	},
	{
		"id": "mystic_shield",
		"mastery": {"text": "{{guard}} **COMBAT ACTION:** Place {MAG Test} {:WOUNDS:} counters on this card. This card can have no more than 9 {:WOUNDS:} on it. --- While there are counters on this card, if you would take a {:WOUND:}, you may remove a {:WOUND:} from this card instead. --- Remove all counters from this card at the end of the fight, or if you become Stunned or Suppressed."},
		"name": "Mystic Shield",
		"req": "Wizard",
		"skill": {
			"text": "{{guard}} **COMBAT ACTION:** If there are no counters on this card, place {MAG Test} {:WOUND:} counters on this card. --- While there are counters on this card, if you would take a {:WOUND:}, you may remove a {:WOUND:} from this card instead. --- Remove all counters from this card at the end of the fight, or if you become Stunned or Suppressed.",
			"xp": 10
		}
	},
	{
		"id": "performance",
		"mastery": {"text": "{{bold settlement}} Make an AGI, MAG, or STR Test. If you pass, gain {:GP:} equal to your lowest single die, or equal to your highest single die if you are in an {:EXPLORED:} {:TOWN:}."},
		"name": "Performance",
		"req": "Performer",
		"skill": {
			"text": "{{bold settlement}} Make an AGI, MAG, or STR Test. If you pass, gain {:GP:} equal to your lowest single die.",
			"xp": 6
		}
	},
	{
		"id": "polearm_discipline",
		"mastery": {"text": "If you are using a Reach Attack with a 2H {:HEAVY:} Item, you may resolve the effects of your Attack before you resolve the attacks of monsters targeting you without {:RANGE:} on the first round of a fight, unless you are Stunned *(if you destroy your target, it cannot attack)*."},
		"name": "Polearm Discipline",
		"req": "Militant",
		"skill": {
			"text": "{{guard}} If you are using a Reach Attack with a 2H {:HEAVY:} Item, you may resolve the effects of your Attack before you resolve the attacks of monsters targeting you without {:RANGE:} on the first round of a fight, unless you are Stunned *(if you destroy your target, it cannot attack)*.",
			"xp": 8
		}
	},
	{
		"id": "provoke",
		"mastery": {"text": "**QUICK ACTION:** Attach this card to a monster in your space *(place this card on that monster's card)*. --- The attached monster must target you during the Establish Targets step. --- Return this card to your hand at end of fight, or at the end of around if the attached monster is removed from the fight or Provoked by another adventurer."},
		"name": "Provoke",
		"req": "MRL 6",
		"skill": {
			"text": "**QUICK ACTION:** If you are not being targeted this round, attach this card to a monster in your space *(place this card on that monster's card)*. --- The attached monster must target you during the Establish Targets step. --- Return this card to your hand at end of fight, or at the end of a round if the attached monster is removed from the fight or Provoked by another adventurer.",
			"xp": 6
		}
	},
	{
		"id": "rage",
		"mastery": {"text": "{{assault}} Your Attacks using STR inflict +1 damage against monsters that are targeting you.  {{assault}} When you use a STR Attack while you have {:WOUNDS:} equal to or greater than your MRL, you may roll an additional Power die."},
		"name": "Rage",
		"req": "STR 8",
		"skill": {
			"text": "{{assault}} Your Attacks using STR inflict +1 damage against monsters that are targeting you.",
			"xp": 10
		}
	},
	{
		"id": "rebuke",
		"mastery": {"text": "{{assault}} **COMBAT ACTION:** Choose a target Daemon or Undead with {:XP:} VAL 2 or less & make a MAG or MRL Test. If you pass, the target becomes Hexed & you inflict Pierching damage on it equal to your lowest single die."},
		"name": "Rebuke",
		"req": "Puritan or Warlock",
		"skill": {
			"text": "{{assault}} **COMBAT ACTION:** Choose a target Daemon or Undead with {:XP:} VAL 2 or less & make a MAG or MRL Test. If you pass, the target becomes Hexed.",
			"xp": 10
		}
	},
	{
		"id": "repulsion",
		"mastery": {"text": "When you use an **Unarmed Strike** Attack, do not halve your Power dice. --- When you hit with an **Unarmed Strike** & roll a {::6::} on a Power die, your target becomes Dazed until the end of your next turn in combat."},
		"name": "Repulsion",
		"req": "Wizard",
		"skill": {
			"text": "When you use an **Unarmed Strike** Attack, do not halve your Power dice.",
			"xp": 6
		}
	},
	{
		"id": "river_rat",
		"mastery": {"text": "{{river_port}} Your party may River Port Travel for free in any Map Stance *(your party may River Port Travel while {:CAUTIOUS:})*.  {{river_port || settlement}} When you sell a card while Trading, collect an additional 1 {:GP:}."},
		"name": "River Rat",
		"req": "Merchant, Rogue, or Wild",
		"skill": {
			"text": "{{river_port}} You may River Port Travel for free & other members of your party may River Port Travel for 1 {:GP:} each.",
			"xp": 6
		}
	},
	{
		"id": "shieldwall",
		"mastery": {"text": "{{guard}} **COMBAT ACTION:** If you have a Shield equipped, you may defend using STR this turn *(in place of AGI)* & roll an additional Power die this turn."},
		"name": "Shieldwall",
		"req": "STR 8",
		"skill": {
			"text": "{{guard}} **COMBAT ACTION:** If you have a Shield equipped, you may defend using STR this turn *(in place of AGI)*.",
			"xp": 8
		}
	},
	{
		"id": "shock_wave",
		"mastery": {"text": "**COMBAT ACTION:** Make a MAG Test. If you pass, inflict damage equal to your lowest single die on each monster targeting you. If you roll doubles, inflict damage equal to both dice combined"},
		"name": "Shock Wave",
		"req": "Wizard",
		"skill": {
			"text": "**COMBAT ACTION:** Make a MAG Test. If you pass, inflict damage equal to your lowest single die on each monster targeting you.",
			"xp": 10
		}
	},
	{
		"id": "shrug_off_pain",
		"mastery": {"text": "You are immune to Annoy & Pain.  {{guard}} While you are Blessed or Invigorated, you may use CON for Defence Tests *(in place of AGI)*."},
		"name": "Shrug Off Pain",
		"req": "CON 8 or MRL 9",
		"skill": {
			"text": "You are immune to Annoy & Pain.",
			"xp": 9
		}
	},
	{
		"id": "sixth_sense",
		"mastery": {"text": "You & your party are immune to Ambush."},
		"name": "Sixth Sense",
		"req": "MAG 8 or PER 8",
		"skill": {
			"text": "You are immune to Ambush. --- Adventurers in your party add +1 to their PER while they Test against Ambush.",
			"xp": 8
		}
	},
	{
		"id": "sneak_attack",
		"mastery": {"text": "{{assault}} **QUICK ACTION:** If it is your first turn after entering a fight, you may roll an additional Power die when you use an AGI or PER Attack this turn.  {{assault}} If you roll doubles on your Power dice, your target becomes Dazed until the end of your next turn *(even if you missed)*."},
		"name": "Sneak Attack",
		"req": "PER 8",
		"skill": {
			"text": "{{assault}} **QUICK ACTION:** If it is your first turn after entering a fight, you may roll an additional Power die when you use an AGI or PER Attack this turn.",
			"xp": 8
		}
	},
	{
		"id": "streetwise",
		"mastery": {"text": "{{bold settlement}} When you make a PER Test to **Explore**, **Hunt**, or **Search**, roll 3 dice & use any 2.  {{cautious settlement}} When you make a PER Test for **Stealth** or against Ambush, roll 3 dice & use any 2."},
		"name": "Streetwise",
		"req": "Merchant, Rogue, or Performer",
		"skill": {
			"text": "{{bold settlement}} When you make a PER Test to **Explore** or **Hunt**, roll 3 dice & use any 2.",
			"xp": 6
		}
	},
	{
		"id": "to_the_front",
		"mastery": {"text": "{{guard}} If you are being targeted by 3 or more monsters, you may swap the value of any one Power die with any one Defence die."},
		"name": "To the Front",
		"req": "Fighter, Militant, Puritan, or Wild",
		"skill": {
			"text": "{{guard}} If you are being targeted by 4 or more monsters, you may swap the value of any one Power die with any one Defence die.",
			"xp": 8
		}
	},
	{
		"id": "track",
		"mastery": {"text": "{{outside}} You may roll 3 dice & use any 2 when you make a PER test to **Explore**, **Hunt**, or **Search**."},
		"name": "Track",
		"req": "Hunter or Wild",
		"skill": {
			"text": "{{outside}} When you make a PER test to **Explore** or **Hunt**, roll 3 dice & use any 2.",
			"xp": 6
		}
	},
	{
		"id": "transcendence",
		"mastery": {"text": "When you become Blessed, remove Suppressed & while you are Blessed, you cannot become Suppressed. --- When you become Exalted, remove Demoralized & while you are Exalted, you cannot become Demoralized. --- While you are Blessed or Exalted, you are immune to Annoy, Fear, Pain & Taunt."},
		"name": "Transcendence",
		"req": "MRL 9 or MAG 9",
		"skill": {
			"text": "When you become Blessed, remove Suppressed & while you are Blessed, you cannot become Suppressed. --- When you become Exalted, remove Demoralized & while you are Exalted, you cannot become Demoralized.",
			"xp": 6
		}
	},
	{
		"id": "transmute",
		"mastery": {"text": "{{town || camp}} Discard an Ally or Item & make a MAG Test. If you pass, you gain {:GP:} equal to that card's cost."},
		"name": "Transmute",
		"req": "Scholar",
		"skill": {
			"text": "{{town}} Discard an Item & make a MAG Test. If you pass, you gain {:GP:} equal to that Item's cost.",
			"xp": 8
		}
	},
	{
		"id": "voidwalker",
		"mastery": {"text": "{{void}} Ignore {:PERILOUS:}  & you may use MAG for Recovery *(in place of MRL)*. --- You take half damage from Void Shock & when you use the **Dispel** action. --- When you Void Travel from the Maze, draw 4 Danger cards & choose any one of them as your destination."},
		"name": "Voidwalker",
		"req": "Void Witch",
		"skill": {
			"text": "{{void}} Ignore {:PERILOUS:}  & you may use MAG for Recovery *(in place of MRL)*. --- You take half damage from Void Shock & when you use the **Dispel** action. --- When you Void Travel from the Maze, draw 2 Danger cards & choose either as your destination.",
			"xp": 8
		}
	},
	{
		"id": "warding",
		"mastery": {"text": "{{guard}} When you take damage from a Daemon, reduce that damage by 1.  {{guard}} When you pass a Defense Test with doubles, inflict 1 Piercing damage on each Daemon targeting you."},
		"name": "Warding",
		"req": "Puritan or Warlock",
		"skill": {
			"text": "{{guard}} When you take damage from a Daemon, reduce that damage by 1.",
			"xp": 10
		}
	}
]);
