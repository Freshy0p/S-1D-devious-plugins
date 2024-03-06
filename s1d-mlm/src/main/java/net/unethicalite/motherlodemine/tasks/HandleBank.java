package net.unethicalite.motherlodemine.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.TileObject;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.motherlodemine.S1dMotherlodeMinePlugin;
import net.unethicalite.motherlodemine.data.Activity;

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

    private int lastGemBagEmpty;

    @Override
    public boolean validate()
    {
        return this.isCurrentActivity(Activity.IDLE)
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
                ItemID.UNCUT_DRAGONSTONE);

    }

    @Override
    public int execute()
    {
        TileObject bank = TileObjects.getNearest(obj -> obj.hasAction("Use") && obj.getName().equals("Bank chest"));
        if (Bank.isOpen())
        {

            Bank.depositAllExcept(ItemID.IMCANDO_HAMMER,
                    ItemID.HAMMER,
                    ItemID.OPEN_GEM_BAG);
            log.info("Depositing");
            final Item gemBag = Bank.Inventory.getFirst(ItemID.OPEN_GEM_BAG);

            if (gemBag != null)
            {
                log.info("Emptying gem bag");
                gemBag.interact("Empty");

            }
            return -1;

        }
        else if (bank != null)
        {
            this.setActivity(Activity.DEPOSITING);
            bank.interact("Use");
            sleepUntil(Bank::isOpen, 4000);
        }
        return 0;
    }
}
