package net.unethicalite.motherlodemine;

import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

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
}