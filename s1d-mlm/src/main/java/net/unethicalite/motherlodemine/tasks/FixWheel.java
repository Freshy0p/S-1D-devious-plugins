package net.unethicalite.motherlodemine.tasks;

import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.motherlodemine.S1dMotherlodeMinePlugin;
import net.unethicalite.motherlodemine.data.Activity;

import javax.inject.Inject;

public class FixWheel extends MotherlodeMineTask
{
    public FixWheel(S1dMotherlodeMinePlugin context)
    {
        super(context);
    }

    @Inject
    private S1dMotherlodeMinePlugin plugin;
    @Override
    public boolean validate()
    {
        return this.isCurrentActivity(Activity.IDLE)
                && !this.isUpperFloor()
                && this.wasPreviousActivity(Activity.DEPOSITING)
                && TileObjects.getAll(ObjectID.BROKEN_STRUT).size() == 2
                && !this.isAssistedMining();
    }

    @Override
    public int execute()
    {
        TileObject brokenStrut = TileObjects.getNearest(ObjectID.BROKEN_STRUT);
        if (brokenStrut != null)
        {
            this.setActivity(Activity.REPAIRING);
            brokenStrut.interact("Hammer");
            return 4000;
        }
        return 0;
    }
}
