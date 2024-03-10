package net.unethicalite.fletcher;


import lombok.extern.slf4j.Slf4j;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.plugins.Task;
import net.unethicalite.api.plugins.TaskPlugin;
import org.pf4j.Extension;

import javax.inject.Singleton;

@Extension
@PluginDescriptor(
        name = "<html>[<font color=#f44336>\uD83D\uDC24</font>] Fletcher</html>",
        description = "Fletches logs into unstrung shortbows, longbows, Stocks, Shields or Arrow shafts. Supports banking, and stringing bows.",
        enabledByDefault = false,
        tags =
                {
                        "Fletching",
                        "skilling",
                }
)
@Slf4j
@Singleton
public class S1dFletcherPlugin extends TaskPlugin
{
    @Override
    public Task[] getTasks() {
        return new Task[0];
    }
}