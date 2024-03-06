package net.unethicalite.motherlodemine.tasks;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.motherlodemine.Config;
import net.unethicalite.motherlodemine.MiningArea;
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
        oreVein = MiningArea.UPSTAIRS.getNearestOreVein();
        return this.isCurrentActivity(Activity.IDLE) && !this.isSackFull()
                && !Inventory.isFull() && (oreVein = MiningArea.UPSTAIRS.getNearestOreVein()) != null && this.isUpperFloor();
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
