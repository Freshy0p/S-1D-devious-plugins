package net.unethicalite.motherlodemine.tasks;

import net.runelite.api.TileObject;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.motherlodemine.S1dMotherlodeMinePlugin;
import net.unethicalite.motherlodemine.data.Activity;

import javax.inject.Inject;

public class Deposit extends MotherlodeMineTask
{
    public Deposit(S1dMotherlodeMinePlugin context)
    {
        super(context);
    }

    @Inject
    private S1dMotherlodeMinePlugin plugin;
    @Override
    public boolean validate()
    {
        return this.isCurrentActivity(Activity.IDLE)
                && !this.isUpperFloor()
                && Inventory.contains("Pay-dirt")
                && Inventory.isFull();
    }

    @Override
    public int execute()
    {
        this.setActivity(Activity.DEPOSITING);

        final TileObject hopper = TileObjects.getNearest("Hopper");
        if (hopper != null)
        {
            hopper.interact("Deposit");
            this.setActivity(Activity.IDLE);
            return 4000;
        }
        return -1;
    }
}
