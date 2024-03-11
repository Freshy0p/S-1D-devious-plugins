package net.unethicalite.fletcher.tasks;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import net.unethicalite.api.plugins.Task;
import net.unethicalite.fletcher.S1dFletcherPlugin;

@RequiredArgsConstructor
public abstract class FletcherTask implements Task {

    @Delegate
    private final S1dFletcherPlugin context;
}
