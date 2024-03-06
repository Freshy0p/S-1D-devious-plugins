package net.unethicalite.motherlodemine.tasks;

import net.runelite.api.ItemID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.motherlodemine.S1dMotherlodeMinePlugin;
import net.unethicalite.motherlodemine.data.Activity;

import javax.inject.Inject;

public class WithdrawSack extends MotherlodeMineTask
{
    public WithdrawSack(S1dMotherlodeMinePlugin context)
    {
        super(context);
    }

    @Inject
    private S1dMotherlodeMinePlugin plugin;

    @Override
    public boolean validate()
    {
        return isCurrentActivity(Activity.IDLE)
                && !plugin.isUpperFloor()
                && plugin.isSackFull()
                && !Inventory.contains(
                ItemID.PAYDIRT,
                ItemID.RUNITE_ORE,
                ItemID.ADAMANTITE_ORE,
                ItemID.MITHRIL_ORE,
                ItemID.GOLD_ORE,
                ItemID.COAL,
                ItemID.UNCUT_SAPPHIRE,
                ItemID.UNCUT_EMERALD,
                ItemID.UNCUT_RUBY,
                ItemID.UNCUT_DIAMOND,
                ItemID.UNCUT_DRAGONSTONE);
    }

    @Override
    public int execute()
    {
        TileObject sack = TileObjects.getNearest("Sack");
        if (sack != null)
        {
            setActivity(Activity.WITHDRAWING);
            sack.interact("Fill");
            Time.sleepTicksUntil(() -> isCurrentActivity(Activity.IDLE), 20);
            return 0;
        }
        return 0;
    }
}