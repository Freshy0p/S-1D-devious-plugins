package net.unethicalite.fighter.utils;

import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.movement.Movement;

import java.util.Arrays;
import java.util.Comparator;
public enum BankLocation {
    VARROCK_WEST_BANK(new WorldPoint(3185, 3441, 0)),
    FALADOR_WEST_BANK(new WorldPoint(2946, 3367, 0)),
    EDGEVILLE_BANK(new WorldPoint(3095, 3494, 0)),
    DRAYNOR_BANK(new WorldPoint(3093, 3244, 0)),
    VARROCK_EAST_BANK(new WorldPoint(3254, 3421, 0)),
    FALADOR_EAST_BANK(new WorldPoint(3015, 3357, 0)),
    AL_KHARID_BANK(new WorldPoint(3269, 3167, 0)),
    CATHERBY_BANK(new WorldPoint(2810, 3442, 0)),
    ZANARIS_BANK(new WorldPoint(2383, 4459, 0)),
    SEERS_VILLAGE_BANK(new WorldPoint(2725, 3492, 0)),
    ARDOUGNE_SOUTH_BANK(new WorldPoint(2653, 3284, 0)),
    ARDOUGNE_NORTH_BANK(new WorldPoint(2617, 3333, 0)),
    TUTORIAL_ISLAND_BANK(new WorldPoint(3122, 3124, 0)),
    YANILLE_BANK(new WorldPoint(2613, 3093, 0)),
    GNOME_BANK(new WorldPoint(2445, 3425, 1)),
    GNOME_TREE_BANK_SOUTH(new WorldPoint(2449, 3482, 1)),
    GNOME_TREE_BANK_WEST(new WorldPoint(2442, 3488, 1)),
    SHILO_VILLAGE_BANK(new WorldPoint(2852, 2955, 0)),
    SHANTAY_PASS_BANK(new WorldPoint(3308, 3119, 0)),
    LEGENDS_GUILD_BANK(new WorldPoint(2733, 3379, 2)),
    ISLE_OF_SOULS(new WorldPoint(2212, 2859, 0)),
    MAGE_ARENA(new WorldPoint(3090, 3956, 0)),
    FISHING_GUILD_BANK(new WorldPoint(2588, 3416, 0)),
    DUEL_ARENA_BANKS(new WorldPoint(3384, 3270, 0)),
    CANIFIS_BANK(new WorldPoint(3512, 3478, 0)),
    ETCETERIA_BANK(new WorldPoint(2620, 3895, 0)),
    CASTLE_WARS(new WorldPoint(2445, 3082, 0)),
    PORT_PHASMATYS_BANK(new WorldPoint(3692, 3468, 0)),
    KELDAGRIM_BANK(new WorldPoint(2838, 10210, 0)),
    EMERALD_BENEDICT(new WorldPoint(3042, 4972, 0)),
    LLETYA(new WorldPoint(2352, 3162, 0)),
    MOR_UL_REK_NORTH_BANK(new WorldPoint(2446, 5179, 0)),
    NARDAH_BANK(new WorldPoint(3427, 2891, 0)),
    MOS_LEHARMLESS_BANK(new WorldPoint(3681, 2982, 0)),
    CULINAROMANCERS_CHEST(new WorldPoint(3219, 9623, 0)),
    BURGH_DE_ROTT_BANK(new WorldPoint(3496, 3211, 0)),
    VOID_KNIGHT_BANK(new WorldPoint(2667, 2653, 0)),
    PISCATORIS_BANK(new WorldPoint(2330, 3690, 0)),
    WARRIORS_GUILD_BANK(new WorldPoint(2843, 3543, 0)),
    LUNAR_ISLE_BANK(new WorldPoint(2101, 3919, 0)),
    LUMBRIDGE_CASTLE_BANK(new WorldPoint(3209, 3219, 2)),
    SOPHANEM(new WorldPoint(3312, 2799, 0)),
    ODOVACAR(new WorldPoint(3195, 4570, 0)),
    NEITIZNOT_BANK(new WorldPoint(2337, 3807, 0)),
    JATIZSO_BANK(new WorldPoint(2417, 3801, 0)),
    DORGESH_KAAN_BANK(new WorldPoint(2702, 5350, 0)),
    LUMBRIDGE_PVP_CHEST(new WorldPoint(3221, 3219, 0)),
    FALADOR_PVP_CHEST(new WorldPoint(2970, 3342, 0)),
    EDGEVILLE_PVP_CHEST(new WorldPoint(3094, 3469, 0)),
    CAMELOT_PVP_CHEST(new WorldPoint(2757, 3478, 0)),
    BLAST_FURNACE_BANK(new WorldPoint(2931, 10195, 0)),
    MOTHERLOAD_MINE_BANK(new WorldPoint(3119, 9698, 0)),
    GRAND_EXCHANGE(new WorldPoint(3165, 3490, 0)),
    CRAFTING_GUILD(new WorldPoint(2936, 3280, 0)),
    BARBARIAN_BANK(new WorldPoint(2537, 3574, 0)),
    PORT_KHAZARD_BANK(new WorldPoint(2662, 3161, 0)),
    ARCEUUS_BANK(new WorldPoint(1630, 3745, 0)),
    BLAST_MINE_BANK(new WorldPoint(1489, 3864, 0)),
    CHARCOAL_CAMP_BANK(new WorldPoint(1717, 3464, 0)),
    HOSIDIUS_BANK(new WorldPoint(1748, 3599, 0)),
    HOSIDIUS_KITCHEN_BANK(new WorldPoint(1676, 3616, 0)),
    KOUREND_CASTLE_BANK(new WorldPoint(1612, 3682, 2)),
    LOVAKENGJ_BANK(new WorldPoint(1526, 3739, 0)),
    LOVAKENGJ_MINE_BANK(new WorldPoint(1438, 3829, 0)),
    PISCARILIUS_BANK(new WorldPoint(1803, 3788, 0)),
    SHAYZIEN(new WorldPoint(1487, 3593, 0)),
    SULPHUR_MINE_BANK(new WorldPoint(1455, 3860, 0)),
    WAR_TENT_BANK(new WorldPoint(1484, 3644, 0)),
    VINERY_BANK(new WorldPoint(1809, 3567, 0)),
    MARIM_BANK(new WorldPoint(2781, 2784, 0)),
    LANDS_END_CHEST(new WorldPoint(1513, 3421, 0)),
    MOR_UL_REK_EAST_BANK(new WorldPoint(2543, 5144, 0)),
    MINING_GUILD(new WorldPoint(3012, 9718, 0)),
    VOLCANIC_MINE_BANK(new WorldPoint(3819, 3809, 0)),
    MYTHS_BANK(new WorldPoint(2463, 2848, 1)),
    FARMING_GUILD_CHEST(new WorldPoint(1253, 3742, 0)),
    PRIFDDINAS_SOUTH_BANK(new WorldPoint(3297, 6060, 0)),
    PRIFDDINAS_NORTH_BANK(new WorldPoint(3257, 6108, 0)),
    DARKMEYER_BANK(new WorldPoint(3602, 3366, 0)),
    SEPULCHRE_CHEST(new WorldPoint(2400, 5983, 0)),
    FEROX(new WorldPoint(3130, 3632, 0)),
    TROUBLE_BREWING_BANK(new WorldPoint(3810, 3020, 0)),
    TEMPEROSS_BANK(new WorldPoint(3156, 2837, 0)),
    CLAN_HALL_BANK(new WorldPoint(1748, 5476, 0)),
    NEX_BANK(new WorldPoint(2904, 5205, 0)),
    GUARDIANS_OF_THE_RIFT_BANK(new WorldPoint(3619, 9473, 0)),
    TOA_BANK(new WorldPoint(3345, 2725, 0)),
    BOUNTY_HUNTER_BANK(new WorldPoint(3426, 4064, 0)),
    CAM_TORUM_BANK(new WorldPoint(1456, 9568, 0)),
    HUNTER_GUILD_BANK(new WorldPoint(1542, 3040, 0)),
    FORTIS_EAST_BANK(new WorldPoint(1780, 3096, 0)),
    FORTIS_WEST_BANK(new WorldPoint(1648, 3118, 0)),
    FORTIS_COLOSSEUM_CHEST(new WorldPoint(1805, 9501, 0)),
;
    private final WorldPoint area;
    BankLocation(WorldPoint area) {
        this.area = area;
    }
    public WorldPoint getArea() {
        return area;
    }
    public static BankLocation getNearest()
    {
        return Arrays.stream(values())
                .min(Comparator.comparingInt(x -> x.getArea().distanceTo2D(Players.getLocal().getWorldLocation())))
                .orElse(null);
    }

    public static BankLocation getNearestPath()
    {
        return Arrays.stream(values())
                .min(Comparator.comparingInt(x -> Movement.calculateDistance(x.getArea())))
                .orElse(null);
    }


}