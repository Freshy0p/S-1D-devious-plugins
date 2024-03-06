package net.unethicalite.motherlodemine.tasks;

import net.runelite.api.TileObject;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.motherlodemine.Config;
import net.unethicalite.motherlodemine.S1dMotherlodeMinePlugin;
import net.unethicalite.motherlodemine.data.Activity;

import javax.inject.Inject;

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
        return isCurrentActivity(Activity.IDLE)
                && !Inventory.isFull()
                && (isUpperFloor() || !config.upstairs())
                && (oreVein = plugin.getMiningArea().getNearestOreVein()) != null
                && Reachable.isInteractable(oreVein);
    }

    @Override
    public int execute()
    {
        setActivity(Activity.MINING);
        oreVein.interact("Mine");
        return 4000;
    }

    @Subscribe
    private void onGameTick(GameTick event)
    {
        if (plugin.isRunning() && isCurrentActivity(Activity.MINING))
        {
            if (oreVein == null || !Reachable.isInteractable(oreVein))
            {
                setActivity(Activity.IDLE);
            }
            else
            {
                final TileObject oreVeinCheck = TileObjects.getFirstAt(oreVein.getWorldLocation(), o -> o.hasAction("Mine"));
                if (oreVeinCheck == null)
                {
                    oreVein = null;
                    setActivity(Activity.IDLE);
                }
            }
        }
    }
}
