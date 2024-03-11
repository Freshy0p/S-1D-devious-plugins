package net.devious.plugins.zeahrc.tasks;

import net.devious.plugins.zeahrc.data.Constants;
import net.devious.plugins.zeahrc.framework.SessionUpdater;
import net.runelite.api.Item;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.plugins.Task;

public class ChipEssence extends SessionUpdater implements Task
{
    @Override
    public boolean validate()
    {
        return Inventory.isFull()
                && !Inventory.contains(c -> c.getName().equalsIgnoreCase(Constants.EssenceFragments))
                || (!Inventory.contains(c -> c.getName().equalsIgnoreCase(Constants.EssenceFragments))
                && Inventory.contains(Constants.DarkEssenceBlock)
                && TileObjects.getNearest(c -> c.hasAction("Bind")) != null);
    }

    @Override
    public int execute()
    {
        getSession().setCurrentTask("Chipping essence");

        Item chisel = Inventory.getFirst("Chisel");
        if (chisel == null)
        {
            return 600;
        }



            //Get all the dark essence blocks in the inventory and use the chisel on them
            for (Item block : Inventory.getAll(Constants.DarkEssenceBlock))
            {
                if (block != null)
                {
                    chisel.useOn(block);
                }
                //Sleep until inventory no longer contains dark essence blocks
                if (!Inventory.contains(Constants.DarkEssenceBlock))
                {
                    return -1;
                }
                //Sleep between each chisel use to avoid spam clicking
                //calculate the number of actions per tick
                int actionsPerTick = 3;
                //Sleep for the correct amount of milliseconds to do 3 actions per tick(600ms)
                Time.sleep(600 / actionsPerTick);

        }
        return -4;
    }
}
