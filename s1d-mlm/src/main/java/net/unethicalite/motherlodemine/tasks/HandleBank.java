package net.unethicalite.motherlodemine.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.DepositBox;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.motherlodemine.S1dMotherlodeMinePlugin;
import net.unethicalite.motherlodemine.data.Activity;
import net.unethicalite.motherlodemine.utils.Constants;
import net.unethicalite.motherlodemine.utils.S1dDepositBox;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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
        if (DepositBox.isOpen())
        {
            this.setActivity(Activity.BANKING);

            List<Integer> idsToKeep = new ArrayList<>();
            idsToKeep.add(ItemID.HAMMER);
            idsToKeep.add(ItemID.OPEN_GEM_BAG);
            idsToKeep.addAll(Constants.PICKAXE_IDS);
            idsToKeep.add(ItemID.HAMMER);

            if (this.getLastGemBagEmpty() < 1)
            {
                S1dDepositBox.emptyGemBag();
                log.info("Emptying Gem Bag...");
                this.setLastGemBagEmpty(1);
            }

            S1dDepositBox.depositAllExcept(idsToKeep);
            log.info("Banking...");
            Time.sleepTicksUntil(() -> this.isCurrentActivity(Activity.IDLE), 10);
            return -1;

        }
        final TileObject depositBox = TileObjects.getNearest("Bank deposit box");
        if (depositBox != null && !DepositBox.isOpen() && this.isCurrentActivity(Activity.IDLE))
        {
            depositBox.interact("Deposit");
            sleepUntil(DepositBox::isOpen, 4000);
            return -1;
        }
        return 0;
    }
}
