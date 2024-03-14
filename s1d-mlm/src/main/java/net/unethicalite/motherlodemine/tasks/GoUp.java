package net.unethicalite.motherlodemine.tasks;


import net.runelite.api.ItemID;
import net.runelite.api.NullObjectID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.motherlodemine.Config;
import net.unethicalite.motherlodemine.S1dMotherlodeMinePlugin;
import net.unethicalite.motherlodemine.data.Activity;

import javax.inject.Inject;

public class GoUp extends MotherlodeMineTask
{
    public GoUp(S1dMotherlodeMinePlugin context)
    {
        super(context);
    }

    @Inject
    private S1dMotherlodeMinePlugin plugin;
    @Inject
    private Config config;
    @Override
    public boolean validate()
    {
        return !this.isUpperFloor() && this.isCurrentActivity(Activity.IDLE) && !Inventory.contains("Pay-dirt") && !this.isSackFull() && !Inventory.contains(
                ItemID.RUNITE_ORE,
                ItemID.ADAMANTITE_ORE,
                ItemID.MITHRIL_ORE,
                ItemID.GOLD_ORE,
                ItemID.COAL,
                ItemID.UNCUT_SAPPHIRE,
                ItemID.UNCUT_EMERALD,
                ItemID.UNCUT_RUBY,
                ItemID.UNCUT_DIAMOND,
                ItemID.UNCUT_DRAGONSTONE)
                && !this.isAssistedMining();

    }

    @Override
    public int execute()
    {
        final TileObject ladder = TileObjects.getNearest(NullObjectID.NULL_19044);
        if (ladder != null)
        {
            ladder.interact("Climb");
            Time.sleepTicksUntil(() -> this.isUpperFloor(), 20);
            return 0;
        }
        return 0;
    }
}
