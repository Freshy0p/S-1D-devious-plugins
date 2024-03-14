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

                // Sleep random time to avoid bot detection
                // set base number of actions per tick to 2
                int actionsPerTick = 2;
                // set base deviation to 50ms

                int deviation = 50;
                // set base tick time to 600
                int tickTime = 600;

                // sleep for a random time based on the base tick time, actions per tick and deviation
                Time.sleep((int) (tickTime / actionsPerTick + Math.random() * deviation));



        }
        return -4;
    }
}
