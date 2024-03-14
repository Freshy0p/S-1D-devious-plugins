package net.unethicalite.motherlodemine.tasks;

import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
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
                && Inventory.isFull()
                && !this.isAssistedMining();
    }

    @Override
    public int execute()
    {
        this.setActivity(Activity.DEPOSITING);
        final TileObject hopper = TileObjects.getNearest("Hopper");
        if (hopper != null)
        {
            if (this.getRemainingDeposits() <= 1)
            {
                this.setSackFull(true);
            }
            hopper.interact("Deposit");
            Time.sleepTicksUntil(() -> this.isCurrentActivity(Activity.IDLE), 15);
            this.refreshSackValues();
            return 200;
        }
        return -1;
    }
}
