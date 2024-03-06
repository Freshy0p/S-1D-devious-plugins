package net.unethicalite.motherlodemine.tasks;

import net.runelite.api.Constants;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.TileObject;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.motherlodemine.S1dMotherlodeMinePlugin;
import net.unethicalite.motherlodemine.data.Activity;

import javax.inject.Inject;

import static net.unethicalite.api.commons.Time.sleep;
import static net.unethicalite.api.commons.Time.sleepUntil;

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
        return isCurrentActivity(Activity.IDLE)
                && !plugin.isUpperFloor()
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
        TileObject bank = TileObjects.getNearest(obj -> obj.hasAction("Deposit"));
        if (Bank.isOpen() && isCurrentActivity(Activity.DEPOSITING))
        {
            sleep(Constants.GAME_TICK_LENGTH);
            Bank.depositAllExcept(ItemID.IMCANDO_HAMMER,
                    ItemID.HAMMER,
                    ItemID.OPEN_GEM_BAG);
            final Item gemBag = Bank.Inventory.getFirst(ItemID.OPEN_GEM_BAG);
            sleep(Constants.GAME_TICK_LENGTH);
            if (gemBag != null && plugin.getClient().getTickCount() - lastGemBagEmpty > 200)
            {
                gemBag.interact("Empty");
                lastGemBagEmpty = plugin.getClient().getTickCount();
                sleep(Constants.GAME_TICK_LENGTH);
            }
            Bank.close();
            setActivity(Activity.IDLE);
            return -1;

        }
        else if (bank != null)
        {
            setActivity(Activity.DEPOSITING);
            bank.interact("Deposit");
            sleepUntil(Bank::isOpen, 4000);
        }
        return 0;
    }
}
