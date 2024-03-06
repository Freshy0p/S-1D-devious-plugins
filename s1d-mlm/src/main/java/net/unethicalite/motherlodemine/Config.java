package net.unethicalite.motherlodemine;

import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.unethicalite.motherlodemine.data.Activity;

@ConfigGroup("s1dmlm")
public interface Config extends net.runelite.client.config.Config
{

    @ConfigItem(
            keyName = "upstairs",
            name = "Upstairs",
            description = "Enable to mine upstairs",
            position = 0
    )
    default boolean upstairs()
    {
        return true;
    }

    @ConfigItem(
            keyName = "shortcut",
            name = "Use shortcut",
            description = "Enable to use agility shortcut",
            position = 1
    )
    default boolean shortcut()
    {
        return false;
    }

    @ConfigItem(
            keyName = "startButton",
            name = "Start/Stop",
            description = "Start the script",
            position = Integer.MAX_VALUE
    )
    default Button startButton()
    {
        return new Button();
    }

    // Activity config
    @ConfigItem(
            keyName = "activity",
            name = "Activity",
            description = "Activity to perform",
            position = 2
    )
    default Activity activity()
    {
        return Activity.IDLE;
    }
}