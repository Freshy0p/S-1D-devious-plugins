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

    // Task cooldown minimum ticks
    @ConfigItem(
            keyName = "taskCooldownMin",
            name = "Task Cooldown Min",
            description = "Minimum ticks between tasks",
            position = 2
    )
    default int taskCooldownMin()
    {
        return 5;
    }

    // Task cooldown maximum ticks
    @ConfigItem(
            keyName = "taskCooldownMax",
            name = "Task Cooldown Max",
            description = "Maximum ticks between tasks",
            position = 3
    )
    default int taskCooldownMax()
    {
        return 10;
    }

    // Assisted mining setting
    @ConfigItem(
            keyName = "assistedMining",
            name = "Assisted Mining",
            description = "Assisted mining allows the script to mine untill inventory is full.",
            position = 4
    )
    default boolean assistedMining()
    {
        return false;
    }
}