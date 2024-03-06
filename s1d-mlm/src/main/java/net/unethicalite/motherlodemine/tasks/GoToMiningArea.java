package net.unethicalite.motherlodemine.tasks;

import net.unethicalite.motherlodemine.S1dMotherlodeMinePlugin;

public class GoToMiningArea extends MotherlodeMineTask
{
    public GoToMiningArea(S1dMotherlodeMinePlugin context)
    {
        super(context);
    }

    @Override
    public boolean validate()
    {
        return false;
    }

    @Override
    public int execute()
    {
        return 0;
    }
}
