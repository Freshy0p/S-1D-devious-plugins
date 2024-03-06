package net.unethicalite.motherlodemine.tasks;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import net.unethicalite.api.plugins.Task;
import net.unethicalite.motherlodemine.S1dMotherlodeMinePlugin;

@RequiredArgsConstructor
public abstract class MotherlodeMineTask implements Task
{
    @Delegate
    private final S1dMotherlodeMinePlugin context;


}
