package net.unethicalite.motherlodemine.tasks;

import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.motherlodemine.S1dMotherlodeMinePlugin;
import net.unethicalite.motherlodemine.data.Activity;

import javax.inject.Inject;

public class GoDown extends MotherlodeMineTask
{
    public GoDown(S1dMotherlodeMinePlugin context)
    {
        super(context);
    }

    @Inject
    private S1dMotherlodeMinePlugin plugin;

    @Override
    public boolean validate()
    {
        return isCurrentActivity(Activity.IDLE)
                && isUpperFloor()
                && Inventory.isFull();
    }

    @Override
    public int execute()
    {
        final TileObject ladder = TileObjects.getNearest(ObjectID.LADDER_19049);
        if (ladder == null || !ladder.hasAction("Climb"))
        {
            Movement.walk(new WorldPoint(3755, 5675, 0));

            Time.sleepTicksUntil(() ->
            {
                final TileObject ladder2 = TileObjects.getNearest(ObjectID.LADDER_19049);

                return ladder2 != null && ladder2.hasAction("Climb");
            }, 10);

            return 0;
        }
        ladder.interact("Climb");
        Time.sleepTicksUntil(() -> !isUpperFloor(), 20);
        return 0;
    }
}
