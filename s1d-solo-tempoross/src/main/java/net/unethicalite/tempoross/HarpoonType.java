package net.unethicalite.tempoross;

import net.runelite.api.ItemID;
public enum HarpoonType
{

// HARPOON, BARBTAIL_HARPOON, DRAGON_HARPOON, INFERNAL_HARPOON, CRYSTAL_HARPOON

    HARPOON(ItemID.HARPOON, "Harpoon"),
    BARBTAIL_HARPOON(ItemID.BARBTAIL_HARPOON, "Barb-tail harpoon"),
    DRAGON_HARPOON(ItemID.DRAGON_HARPOON, "Dragon harpoon"),
    INFERNAL_HARPOON(ItemID.INFERNAL_HARPOON, "Infernal harpoon"),
    CRYSTAL_HARPOON(ItemID.CRYSTAL_HARPOON, "Crystal harpoon");


    private final int id;
    private final String name;

    HarpoonType(int id, String name)
    {
        this.id = id;
        this.name = name;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }


}
