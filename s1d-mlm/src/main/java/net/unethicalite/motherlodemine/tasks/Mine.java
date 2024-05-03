package net.unethicalite.motherlodemine.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.motherlodemine.Config;
import net.unethicalite.motherlodemine.S1dMotherlodeMinePlugin;
import net.unethicalite.motherlodemine.data.Activity;

import javax.inject.Inject;

@Slf4j
public class Mine extends MotherlodeMineTask
{
    public Mine(S1dMotherlodeMinePlugin context)
    {
        super(context);
    }

    @Inject
    private S1dMotherlodeMinePlugin plugin;
    @Inject
    private Config config;
    private TileObject oreVein;
    @Override
    public boolean validate()
    {

        if (this.isUpstairs())
            oreVein = this.getMiningArea().getNearestOreVein(false);
        else
            oreVein = this.getMiningArea().getNearestOreVein(true);
        return this.isCurrentActivity(Activity.IDLE) && !this.isSackFull()
                && !Inventory.isFull() && oreVein != null
                && !this.isAssistedMining()
                && !Inventory.contains(
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
        this.setOreVein(oreVein);
        this.setActivity(Activity.MINING);
        oreVein.interact("Mine");
        log.info("Mining");
        Time.sleepTicksUntil(() -> this.isCurrentActivity(Activity.IDLE), 30);
        return 1;
    }
}
