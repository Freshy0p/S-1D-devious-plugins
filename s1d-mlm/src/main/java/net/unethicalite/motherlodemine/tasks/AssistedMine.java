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
public class AssistedMine extends MotherlodeMineTask
{
    public AssistedMine(S1dMotherlodeMinePlugin context)
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


        return this.isCurrentActivity(Activity.ASSISTED_MINING) && !this.isSackFull()
                && !Inventory.isFull() && oreVein != null;
    }

    @Override
    public int execute()
    {
        this.setOreVein(oreVein);
        this.setActivity(Activity.MINING);
        oreVein.interact("Mine");
        Time.sleepTicksUntil(() -> this.isCurrentActivity(Activity.IDLE), 10);
        return 1;
    }
}
