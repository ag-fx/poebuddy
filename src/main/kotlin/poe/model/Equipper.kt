/*
package poe.model

data class Spell(
    val gems:List<String>
) {
    val colors = gems.map { gem -> Items.cache.values.first { it.name.equals(gem, true) }.socket}
}
data class BuildConfig(
    val spells : List<Spell>
)

fun main(args: Array<String>) {
    fun parse() {
        Items.parseItem(
            "Rarity: Gem\n" +
                    "Raise Zombie\n" +
                    "--------\n" +
                    "Spell, Minion\n" +
                    "Level: 15\n" +
                    "Mana Cost: 33\n" +
                    "Cast Time: 0.85 sec\n" +
                    "Experience: 1,457,352/5,798,936\n" +
                    "--------\n" +
                    "Requirements:\n" +
                    "Level: 52\n" +
                    "Int: 117\n" +
                    "--------\n" +
                    "Raises a zombie minion from a corpse, which will follow you and attack enemies with a melee attack and an area of effect slam which cannot be evaded.\n" +
                    "--------\n" +
                    "Can raise up to 3 Zombies at a time\n" +
                    "--------\n" +
                    "Place into an item socket of the right colour to gain this skill. Right click to remove from a socket.\n"
        )
        Items.parseItem(
            "Rarity: Gem\n" +
                    "Minion Damage Support\n" +
                    "--------\n" +
                    "Support, Minion\n" +
                    "Level: 14\n" +
                    "Mana Multiplier: 130%\n" +
                    "Experience: 1,249,699/2,573,731\n" +
                    "--------\n" +
                    "Requirements:\n" +
                    "Level: 52\n" +
                    "Int: 84\n" +
                    "--------\n" +
                    "Supports minion skills.\n" +
                    "--------\n" +
                    "Supported Skills deal 43% more Minion Damage\n" +
                    "--------\n" +
                    "This is a Support Gem. It does not grant a bonus to your character, but to skills in sockets connected to it. Place into an item socket connected to a socket containing the Active Skill Gem you wish to augment. Right click to remove from a socket.\n"
        )
        Items.parseItem(
            "Rarity: Gem\n" +
                    "Minion Life Support\n" +
                    "--------\n" +
                    "Support, Minion\n" +
                    "Level: 12\n" +
                    "Mana Multiplier: 140%\n" +
                    "Experience: 372,361/2,812,189\n" +
                    "--------\n" +
                    "Requirements:\n" +
                    "Level: 53\n" +
                    "Int: 85\n" +
                    "--------\n" +
                    "Supports minion skills.\n" +
                    "--------\n" +
                    "Supported Skills have 41% more Minion maximum Life\n" +
                    "--------\n" +
                    "This is a Support Gem. It does not grant a bonus to your character, but to skills in sockets connected to it. Place into an item socket connected to a socket containing the Active Skill Gem you wish to augment. Right click to remove from a socket.\n"
        )
        Items.parseItem(
            "Rarity: Gem\n" +
                    "Multistrike Support\n" +
                    "--------\n" +
                    "Attack, Melee, Support\n" +
                    "Level: 8\n" +
                    "Mana Multiplier: 180%\n" +
                    "Experience: 1,097,475/1,638,338\n" +
                    "--------\n" +
                    "Requirements:\n" +
                    "Level: 52\n" +
                    "Str: 53\n" +
                    "Dex: 37\n" +
                    "--------\n" +
                    "Supports melee attack skills, making them repeat twice when used, targeting a random enemy each time. Cannot support Vaal skills, totem skills, channelling skills, or triggered skills.\n" +
                    "--------\n" +
                    "Supported Skills Repeat 2 additional times\n" +
                    "Supported Skills have 82% more Melee Attack Speed\n" +
                    "Supported Skills deal 30% less Attack Damage\n" +
                    "--------\n" +
                    "This is a Support Gem. It does not grant a bonus to your character, but to skills in sockets connected to it. Place into an item socket connected to a socket containing the Active Skill Gem you wish to augment. Right click to remove from a socket.\n"
        )
        Items.parseItem(
            "Rarity: Gem\n" +
                    "Melee Splash Support\n" +
                    "--------\n" +
                    "Support, Melee, Attack, AoE\n" +
                    "Level: 14\n" +
                    "Mana Multiplier: 160%\n" +
                    "Experience: 670,236/2,573,731\n" +
                    "--------\n" +
                    "Requirements:\n" +
                    "Level: 52\n" +
                    "Str: 84\n" +
                    "--------\n" +
                    "Supports single-target melee attack skills, causing their melee strike to deal splash damage around the target.\n" +
                    "--------\n" +
                    "Supported Skills deal Splash Damage to surrounding targets\n" +
                    "Supported Skills deal 29% less Damage to surrounding targets\n" +
                    "Supported Skills have 39% more Melee Splash Area of Effect\n" +
                    "--------\n" +
                    "This is a Support Gem. It does not grant a bonus to your character, but to skills in sockets connected to it. Place into an item socket connected to a socket containing the Active Skill Gem you wish to augment. Right click to remove from a socket.\n"
        )
        Items.parseItem(
            "Rarity: Gem\n" +
                    "Shield Charge\n" +
                    "--------\n" +
                    "Attack, AoE, Movement, Melee\n" +
                    "Level: 11\n" +
                    "Mana Cost: 8\n" +
                    "Effectiveness of Added Damage: 56%\n" +
                    "Experience: 912,277/1,727,879\n" +
                    "--------\n" +
                    "Requirements:\n" +
                    "Level: 45\n" +
                    "Str: 102\n" +
                    "--------\n" +
                    "Charges at a targeted location or enemy, pushing away enemies in your path and repeatedly dealing damage in a small area in front of you. You deal damage in a larger area when you reach the target. The further you travel, the more damage you deal, and the greater your chance of stunning enemies. Cannot be supported by Multistrike.\n" +
                    "--------\n" +
                    "Deals 56% of Base Damage\n" +
                    "50% increased Stun Threshold reduction on enemies at Maximum charge distance\n" +
                    "200% more Damage with Hits at Maximum Charge Distance\n" +
                    "85% increased Movement Speed\n" +
                    "--------\n" +
                    "Place into an item socket of the right colour to gain this skill. Right click to remove from a socket.\n"
        )
        Items.parseItem(
            "Rarity: Gem\n" +
                    "Fortify Support\n" +
                    "--------\n" +
                    "Attack, Support, Melee\n" +
                    "Level: 4\n" +
                    "Mana Multiplier: 110%\n" +
                    "Experience: 336,455/388,734\n" +
                    "--------\n" +
                    "Requirements:\n" +
                    "Level: 38\n" +
                    "Str: 63\n" +
                    "--------\n" +
                    "Supports melee attack skills that are not triggered.\n" +
                    "--------\n" +
                    "Supported Skills grant Fortify on Melee hit\n" +
                    "Supported Skills have 25% increased Fortify duration\n" +
                    "Supported Skills deal 28% increased Melee Physical Damage\n" +
                    "--------\n" +
                    "This is a Support Gem. It does not grant a bonus to your character, but to skills in sockets connected to it. Place into an item socket connected to a socket containing the Active Skill Gem you wish to augment. Right click to remove from a socket.\n"
        )
        Items.parseItem(
            "Rarity: Gem\n" +
                    "Cast when Damage Taken Support\n" +
                    "--------\n" +
                    "Support, Spell, Trigger\n" +
                    "Level: 1\n" +
                    "Cooldown Time: 0.25 sec\n" +
                    "Experience: 388,734/388,734\n" +
                    "--------\n" +
                    "Requirements:\n" +
                    "Level: 38\n" +
                    "Str: 39\n" +
                    "Int: 27\n" +
                    "--------\n" +
                    "Each supported spell skill will track damage you take, and be triggered when the total damage taken reaches a threshold. Cannot support skills used by totems, traps, or mines. Vaal skills, channelling skills, and skills that reserve mana cannot be triggered.\n" +
                    "--------\n" +
                    "This Gem can only Support Skill Gems requiring Level 38 or lower\n" +
                    "Supported Skills deal 70% less Damage\n" +
                    "You cannot Cast Supported Triggerable Spells directly\n" +
                    "Trigger Supported Spells when you take a total of 528 Damage\n" +
                    "--------\n" +
                    "Next-level requirements:\n" +
                    "Level: 40\n" +
                    "Str: 41\n" +
                    "Int: 29\n" +
                    "--------\n" +
                    "This is a Support Gem. It does not grant a bonus to your character, but to skills in sockets connected to it. Place into an item socket connected to a socket containing the Active Skill Gem you wish to augment. Right click to remove from a socket.\n"
        )
        Items.parseItem(
            "Rarity: Gem\n" +
                    "Haste\n" +
                    "--------\n" +
                    "Aura, Spell, AoE\n" +
                    "Level: 2\n" +
                    "Mana Reserved: 50%\n" +
                    "Cooldown Time: 1.20 sec\n" +
                    "Cast Time: Instant\n" +
                    "Experience: 175,816/175,816\n" +
                    "--------\n" +
                    "Requirements:\n" +
                    "Level: 27\n" +
                    "Dex: 64\n" +
                    "--------\n" +
                    "Casts an aura that increases the movement speed, attack speed and cast speed of you and your allies.\n" +
                    "--------\n" +
                    "+1 to radius\n" +
                    "You and nearby allies gain 4% increased Movement Speed\n" +
                    "You and nearby allies gain 9% increased Cast Speed\n" +
                    "You and nearby allies gain 10% increased Attack Speed\n" +
                    "--------\n" +
                    "Next-level requirements:\n" +
                    "Level: 30\n" +
                    "Dex: 71 (unmet)\n" +
                    "--------\n" +
                    "Place into an item socket of the right colour to gain this skill. Right click to remove from a socket.\n"
        )
        Items.parseItem(
            "Rarity: Gem\n" +
                    "Flesh Offering\n" +
                    "--------\n" +
                    "Minion, Spell, Duration\n" +
                    "Level: 6\n" +
                    "Mana Cost: 21\n" +
                    "Cast Time: 1.00 sec\n" +
                    "Experience: 316,271/405,086\n" +
                    "--------\n" +
                    "Requirements:\n" +
                    "Level: 31\n" +
                    "Int: 73\n" +
                    "--------\n" +
                    "Consumes a corpse, which temporarily empowers your minions with swiftness. The skill consumes other nearby corpses, increasing the duration for each corpse consumed.\n" +
                    "--------\n" +
                    "Base duration is 3.00 seconds\n" +
                    "Additional 0.50 seconds Base Duration per extra Corpse consumed\n" +
                    "Grants Minions 22% increased Movement Speed\n" +
                    "Grants Minions 23% increased Cast Speed\n" +
                    "Grants Minions 23% increased Attack Speed\n" +
                    "--------\n" +
                    "Place into an item socket of the right colour to gain this skill. Right click to remove from a socket.\n"
        )
        Items.parseItem(
            "Rarity: Gem\n" +
                    "Convocation\n" +
                    "--------\n" +
                    "Minion, Spell, Duration\n" +
                    "Level: 8\n" +
                    "Mana Cost: 10\n" +
                    "Cooldown Time: 8.00 sec\n" +
                    "Cast Time: Instant\n" +
                    "Experience: 912,277/1,727,879\n" +
                    "--------\n" +
                    "Requirements:\n" +
                    "Level: 45\n" +
                    "Int: 102\n" +
                    "--------\n" +
                    "Recalls all minions that are following you to your location, and grants them a temporary life regeneration effect.\n" +
                    "--------\n" +
                    "Base duration is 2.00 seconds\n" +
                    "57.8 Life regenerated per second\n" +
                    "--------\n" +
                    "Place into an item socket of the right colour to gain this skill. Right click to remove from a socket.\n"
        )
        Items.parseItem(
            "Rarity: Gem\n" +
                    "Hatred\n" +
                    "--------\n" +
                    "Aura, Spell, AoE, Cold\n" +
                    "Level: 6\n" +
                    "Mana Reserved: 50%\n" +
                    "Cooldown Time: 1.20 sec\n" +
                    "Cast Time: Instant\n" +
                    "Experience: 361,245/682,057\n" +
                    "--------\n" +
                    "Requirements:\n" +
                    "Level: 39\n" +
                    "Dex: 57\n" +
                    "Int: 39\n" +
                    "--------\n" +
                    "Casts an aura that grants extra cold damage based on physical damage to you and your allies.\n" +
                    "--------\n" +
                    "+5 to radius\n" +
                    "You and nearby allies gain 28% of Physical Damage as Extra Cold Damage\n" +
                    "--------\n" +
                    "Place into an item socket of the right colour to gain this skill. Right click to remove from a socket.\n"
        )
        Items.parseItem(
            "Rarity: Gem\n" +
                    "Generosity Support\n" +
                    "--------\n" +
                    "Support, Aura\n" +
                    "Level: 11\n" +
                    "Experience: 371,345/1,638,338\n" +
                    "--------\n" +
                    "Requirements:\n" +
                    "Level: 52\n" +
                    "Str: 53\n" +
                    "Int: 37\n" +
                    "--------\n" +
                    "Supports aura skills that affect you and allies, increasing the aura's effect on allies, but preventing it from affecting you at all. Cannot support curse auras, or other auras that only affect enemies. Cannot support skills used by totems.\n" +
                    "--------\n" +
                    "Supported Auras do not affect you\n" +
                    "Non-Curse Auras from Supported Skills have 30% increased Effect\n" +
                    "--------\n" +
                    "This is a Support Gem. It does not grant a bonus to your character, but to skills in sockets connected to it. Place into an item socket connected to a socket containing the Active Skill Gem you wish to augment. Right click to remove from a socket.\n"
        )
    }
    parse()
    val stash = Stash()
    stash.load("inv_all.json")

    val mainTypes = stash.pages.flatMap { it.list.map { it.item } }.groupBy { it.type }
    val subType  = stash.pages.flatMap { it.list.map { it.item } }.groupBy { it.subtype }

    println(mainTypes.keys)
    println(subType.keys)

    val config = BuildConfig(
        listOf(
            Spell(listOf("Raise Zombie", "Minion Damage Support", "Minion Life Support")),
            Spell(listOf("Summon Raging Spirit", "Minion Damage Support", "Multistrike Support", "Melee Splash Support")),
            Spell(listOf("Shield Charge", "Fortify Support", "Faster Attacks Support")),
            Spell(listOf("Cast When Damage Taken Support", "Summon Stone Golem", "Immortal Call")),
            Spell(listOf("Haste")),
            Spell(listOf("Bone Offering")),
            Spell(listOf("Flesh Offering")),
            Spell(listOf("Convocation")),
            Spell(listOf("Hatred", "Generosity Support"))
        )
    )
    val items = Items.cache.values.filter {
        it.type == "one_handed_weapon"
                || it.type == "two_handed_weapon"
                || it.type == "offhand"
                || it.type == "armour"
                || it.type == "Jewellery"
    }

    val map = mutableMapOf<Spell, List<Item>>()
    config.spells.forEach {spell->
        val needReds = spell.colors.sumBy { if( it=="R" ) 1 else 0 }
        val needGreens = spell.colors.sumBy { if( it=="G" ) 1 else 0 }
        val needBlues = spell.colors.sumBy { if( it=="B" ) 1 else 0 }

        map[spell] = items.filter {item->
            val socket = item.socket
            val minLength = spell.colors.size * 2 - 1
            val groups = socket.split(" ")
            val matches = groups.filter{
                it.length>=minLength
            }.filter { group->
                val reds = group.sumBy { if( it=='R' ) 1 else 0 }
                val greens = group.sumBy { if( it=='G' ) 1 else 0 }
                val blues = group.sumBy { if( it=='B' ) 1 else 0 }
                reds>=needReds && greens>=needGreens && blues>=needBlues

            }
            matches.isNotEmpty()
        }
    }
    val set = mutableSetOf<Item>()
    map.forEach { spell, list ->
        set.addAll(list)
    }
    val grouped = set.groupBy { if( it.type=="armour" || it.type=="Jewellery" ) it.subtype else it.type }


    map.forEach { spell, list ->
        val g = list.groupBy { if( it.type=="armour" ) it.subtype else it.type }
        println("${spell}: ${list.size}")
    }


}*/
