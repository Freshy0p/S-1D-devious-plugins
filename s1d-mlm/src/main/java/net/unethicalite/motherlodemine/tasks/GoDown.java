package net.unethicalite.motherlodemine.tasks;

import net.runelite.api.NullObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
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
        return this.isCurrentActivity(Activity.IDLE)
                && this.isUpperFloor()
                && Inventory.isFull()
                && !this.isAssistedMining() || this.isSackFull() && this.isUpperFloor()
                && !this.isAssistedMining();
    }

    @Override
    public int execute()
    {
        // If player x and y is greater than or equal to 3757, 5677, 0 then clear the rockfall
        if (Players.getLocal().getWorldLocation().getX() >= 3757 && Players.getLocal().getWorldLocation().getY() >= 5677)
        {
            this.mineRockfall(3757, 5677);
        }
        final TileObject ladder = TileObjects.getNearest(NullObjectID.NULL_19045);
        if (ladder == null || !ladder.hasAction("Climb"))
        {
            Movement.walk(new WorldPoint(3755, 5675, 0));

            Time.sleepTicksUntil(() ->
            {
                final TileObject ladder2 = TileObjects.getNearest(NullObjectID.NULL_19045);

                return ladder2 != null && ladder2.hasAction("Climb");
            }, 10);

            return 0;
        }

        ladder.interact("Climb");
        Time.sleepTicksUntil(() -> !this.isUpperFloor(), 20);
        this.setActivity(Activity.IDLE);
        return 0;
    }
}
