package net.unethicalite.motherlodemine.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.motherlodemine.S1dMotherlodeMinePlugin;
import net.unethicalite.motherlodemine.data.Activity;
import net.unethicalite.motherlodemine.utils.S1dBank;

import javax.inject.Inject;

import static net.unethicalite.api.commons.Time.sleepUntil;
@Slf4j
public class HandleBank extends MotherlodeMineTask
{
    public HandleBank(S1dMotherlodeMinePlugin context)
    {
        super(context);
    }

    @Inject
    private S1dMotherlodeMinePlugin plugin;


    @Override
    public boolean validate()
    {
        return (this.isCurrentActivity(Activity.IDLE) || this.isCurrentActivity(Activity.BANKING))
                && !this.isUpperFloor()
                && Inventory.contains(
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
        if (Bank.isOpen())
        {
            this.setActivity(Activity.BANKING);

            // Get the Pickaxe id if there is a pickaxe in the inventory


            S1dBank.depositAllExcept(true,ItemID.HAMMER,ItemID.OPEN_GEM_BAG);
            log.info("Depositing");
            Time.sleepTicksUntil(() -> this.isCurrentActivity(Activity.IDLE), 10);
            return -1;

        }
        TileObject bank = TileObjects.getNearest(obj -> obj.hasAction("Use") && obj.getName().equals("Bank chest"));
        if (bank != null && !Bank.isOpen() && this.isCurrentActivity(Activity.IDLE))
        {
            bank.interact("Use");
            sleepUntil(Bank::isOpen, 4000);
            return -1;
        }
        return 0;
    }
}
