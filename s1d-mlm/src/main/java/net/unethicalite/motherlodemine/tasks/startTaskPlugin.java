package net.unethicalite.motherlodemine.tasks;

import net.unethicalite.motherlodemine.S1dMotherlodeMinePlugin;
import net.unethicalite.motherlodemine.data.Activity;

import javax.inject.Inject;

public class startTaskPlugin extends net.unethicalite.motherlodemine.tasks.MotherlodeMineTask
{

    public startTaskPlugin(S1dMotherlodeMinePlugin context)
    {
        super(context);
    }

    @Inject
    private S1dMotherlodeMinePlugin plugin;
    @Override
    public boolean validate()
    {
        return !running;
    }

    @Override
    public int execute()
    {
        this.setActivity(Activity.IDLE);
        running = true;
        return 0;
    }
}
