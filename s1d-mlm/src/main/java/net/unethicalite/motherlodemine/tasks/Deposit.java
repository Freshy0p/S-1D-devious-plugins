package net.unethicalite.motherlodemine.tasks;

import net.runelite.api.ObjectID;
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
                this.setLastGemBagEmpty(0);
            }
            hopper.interact("Deposit");

            Time.sleepTicksUntil(() -> this.isCurrentActivity(Activity.IDLE), 15);
            if (TileObjects.getAll(ObjectID.BROKEN_STRUT).size() == 2)
            {
                TileObject brokenStrut = TileObjects.getNearest(ObjectID.BROKEN_STRUT);
                if (brokenStrut != null)
                {
                    this.setActivity(Activity.REPAIRING);
                    brokenStrut.interact("Hammer");
                    return 4000;
                }
            }
            this.refreshSackValues();
            return 200;
        }
        return -1;
    }
}
