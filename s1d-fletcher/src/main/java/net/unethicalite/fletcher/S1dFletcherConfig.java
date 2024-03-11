package net.unethicalite.fletcher;
import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.unethicalite.fletcher.data.Material;
import net.unethicalite.fletcher.data.Mode;

@ConfigGroup("s1dfletcher")
public interface S1dFletcherConfig extends net.runelite.client.config.Config{
    // What type of logs to fletch
    @ConfigItem(
            keyName = "logType",
            name = "Log Type",
            description = "Type of logs to fletch",
            position = 0
    )
    //dropdown based on Materials
    default Material logType()
    {
        return Material.LOG;
    }

    // Fletching mode
    @ConfigItem(
            keyName = "fletchingMode",
            name = "Fletching Mode",
            description = "Fletching mode",
            position = 1
    )
    //dropdown based on Mode
    default Mode fletchingMode()
    {
        return Mode.FLETCHING_LONGBOW;
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

}
