package net.unethicalite.fletcher;


import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.TileObject;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.unethicalite.api.events.ExperienceGained;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.plugins.Task;
import net.unethicalite.api.plugins.TaskPlugin;
import net.unethicalite.fletcher.data.Activity;
import net.unethicalite.fletcher.data.Material;
import net.unethicalite.fletcher.data.Mode;
import net.unethicalite.fletcher.tasks.*;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

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
    @Inject
    private S1dFletcherConfig config;
    @Inject
    @Getter
    private Client client;
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private S1dFletcherOverlay s1dFletcherOverlay;

    @Getter
    @Setter
    private Activity currentState;

    @Getter
    @Setter
    private int xpGained;

    @Getter
    @Setter
    private int xpPerHour;

    @Getter
    @Setter
    private int nextLevelExperience;

    @Getter
    @Setter
    private int xpRemaining;

    @Getter
    @Setter
    private int timeToLevel;

    @Getter
    @Setter
    private int logsFletched;

    @Setter
    @Getter
    public int timesBanked;

    @Setter
    @Getter
    private int startedExperience;

    @Setter
    @Getter
    private int startedLevel;

    @Getter
    @Setter
    private int levelsGained;

    public static final int[] XP_TABLE =
            {
                    0, 0, 83, 174, 276, 388, 512, 650, 801, 969, 1154,
                    1358, 1584, 1833, 2107, 2411, 2746, 3115, 3523, 3973, 4470, 5018,
                    5624, 6291, 7028, 7842, 8740, 9730, 10824, 12031, 13363, 14833,
                    16456, 18247, 20224, 22406, 24815, 27473, 30408, 33648, 37224,
                    41171, 45529, 50339, 55649, 61512, 67983, 75127, 83014, 91721,
                    101333, 111945, 123660, 136594, 150872, 166636, 184040, 203254,
                    224466, 247886, 273742, 302288, 333804, 368599, 407015, 449428,
                    496254, 547953, 605032, 668051, 737627, 814445, 899257, 992895,
                    1096278, 1210421, 1336443, 1475581, 1629200, 1798808, 1986068,
                    2192818, 2421087, 2673114, 2951373, 3258594, 3597792, 3972294,
                    4385776, 4842295, 5346332, 5902831, 6517253, 7195629, 7944614,
                    8771558, 9684577, 10692629, 11805606, 13034431, 200000000
            };

    @Getter
    @Setter
    private boolean scriptStarted;

    @Getter
    @Setter
    private Instant scriptStartTime;

    protected String getTimeRunning()
    {
        return scriptStartTime != null ? getFormattedDurationBetween(scriptStartTime, Instant.now()) : "";
    }
    private Activity currentActivity;
    private Activity previousActivity;
    private Activity nextActivity;
    private Mode currentMode;


    private int taskCooldown;

    public static String getFormattedDurationBetween(Instant start, Instant finish)
    {
        Duration duration = Duration.between(start, finish);
        return String.format("%d:%02d:%02d",
                duration.toHours(),
                duration.toMinutesPart(),
                duration.toSecondsPart());
    }

    // get formated time to level
    public String getFormattedTimeToLevel()
    {
        return getFormattedDurationBetween(Instant.now(), Instant.now().plusMillis(getTimeToLevel()));
    }
    public void setActivity(Activity activity)
    {
        if (activity != Activity.IDLE)
        {
            log.info("Switching to this activity: " + activity.getName());
        }
        if (activity == Activity.IDLE && currentActivity != Activity.IDLE)
        {
            previousActivity = currentActivity;
        }

        currentActivity = activity;

    }
    // get current activity
    public String getActivity()
    {
        if (isCurrentActivity(Activity.AFK))
        {
            return Activity.AFK.getName() + " (" + taskCooldown + ")";
        }
        return currentActivity.getName();
    }

    public final boolean isCurrentActivity(Activity activity)
    {
        return currentActivity == activity;
    }

    public final boolean wasPreviousActivity(Activity activity)
    {
        return previousActivity == activity;
    }

    public final boolean hasMaterial()
    {
        boolean hasMaterial = false;

        if (Inventory.contains(config.logType().getLogID()))
        {
            hasMaterial = true;
        }
        // make an extra check if we are fletching shields as they require 2 logs
        if (isMode(Mode.FLETCHING_SHIELD) && Inventory.getCount(config.logType().getLogID()) < 2)
        {
            hasMaterial = false;
        }
        return hasMaterial;
    }

    // check if we have finished product
    public final boolean hasFinishedProduct()
    {
        boolean hasFinishedProduct = false;
        // Finished product list
        int[] finishedProduct = config.logType().getProductID();
        for (int i : finishedProduct)
        {
            if (Inventory.contains(i))
            {
                hasFinishedProduct = true;
                break;
            }
        }
        return hasFinishedProduct;
    }

    public boolean hasUnstrungBow()
    {
        boolean hasUnstrungBow = false;
        if (Inventory.contains(config.logType().getProductID()[1]) || Inventory.contains(config.logType().getProductID()[2]))
        {
            hasUnstrungBow = true;
        }
        return hasUnstrungBow;
    }

    public final boolean isMode(Mode mode)
    {
        return currentMode == mode;
    }
    //set mode
    public void setMode(Mode mode)
    {
        currentMode = mode;
    }

    //get mode
    public Mode getMode()
    {
        return currentMode;
    }

    // check if knife is in inventory
    public final boolean hasKnife()
    {
        return Inventory.contains(ItemID.KNIFE);
    }

    // check if bowstring is in inventory
    public final boolean hasBowString()
    {
        return Inventory.contains(ItemID.BOW_STRING);
    }

    // get Material
    public Material getMaterial()
    {
        return config.logType();
    }

    // check if fletching widget is open
    public final boolean isFletchingWidgetOpen()
    {
        return client.getWidget(270, 14) != null;
    }

    private void reset()
    {
        setXpGained(0);
        setTimeToLevel(0);
        setLogsFletched(0);
        setTimesBanked(0);
        setScriptStartTime(null);
        setActivity(Activity.AFK);
        setStartedExperience(0);
        setStartedLevel(0);
        setXpGained(0);
        setXpPerHour(0);
        setNextLevelExperience(0);
        setXpRemaining(0);
        setLevelsGained(0);
        setScriptStarted(false);
    }

    @Provides
    public S1dFletcherConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(S1dFletcherConfig.class);
    }

    private final Task[] tasks =
            {
                    // BankHandle, BuyMaterial, FletchBow, FletchShaft, FletchShield, FletchStock, StringBow
                    new BankHandle(this),
                    new FletchBow(this),
                    new FletchShaft(this),
                    new FletchShield(this),
                    new FletchStock(this),
                    new StringBow(this),
                    new BuyMaterial(this)

            };

    @Override
    public Task[] getTasks()
    {
        return tasks;
    }

    // set task cooldown optinal add next Activity

    public void setTaskCooldown(Optional<Activity> newActivity)
    {
        taskCooldown = new Random().nextInt(config.taskCooldownMax() - config.taskCooldownMin() + 1)
                + config.taskCooldownMin();
        if (newActivity.isPresent())
        {
            nextActivity = newActivity.get();
        }
    }
    public void setTaskCooldown(int cd)
    {
        taskCooldown = cd;
    }


    // Startup and shutdown methods

    @Override
    protected void startUp() throws Exception
    {
        super.startUp();
        this.overlayManager.add(s1dFletcherOverlay);
        reset();
        setMode(config.fletchingMode());


    }

    @Override
    protected void shutDown() throws Exception
    {
        super.shutDown();
        this.overlayManager.remove(s1dFletcherOverlay);
    }

    @Subscribe
    public void onConfigButtonPressed(ConfigButtonClicked event)
    {
        if (!event.getGroup().contains("s1dfletcher")
                || !event.getKey().toLowerCase().contains("start"))
        {
            return;
        }

        if (scriptStarted)
        {
            reset();
        }
        else
        {
            setScriptStarted(true);
            setScriptStartTime(Instant.now());
            setMode(config.fletchingMode());
            setActivity(Activity.IDLE);
            setStartedExperience(client.getSkillExperience(Skill.FLETCHING));
            setStartedLevel(client.getRealSkillLevel(Skill.FLETCHING));



        }
    }
    @Subscribe
    public void onExperienceGained(ExperienceGained event)
    {
        if (!isScriptStarted())
        {
            return;
        }

        if (event.getSkill() == Skill.FLETCHING)
        {
            setXpGained(getXpGained() + event.getXpGained());
            setNextLevelExperience(XP_TABLE[client.getRealSkillLevel(Skill.FLETCHING) + 1]);
            setXpRemaining(getNextLevelExperience() - client.getSkillExperience(Skill.FLETCHING));
            if (client.getRealSkillLevel(Skill.FLETCHING) > getStartedLevel())
            {
                setLevelsGained(getLevelsGained() + 1);
            }
            logsFletched++;
        }
    }
    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (isRunning() && isScriptStarted())
        {
            setXpPerHour((int) (getXpGained() * 3600000D / (System.currentTimeMillis() - scriptStartTime.toEpochMilli())));
            if(getXpGained() > 0)
            {
                setTimeToLevel((int) (getXpRemaining() * 3600000D / (getXpPerHour())));
            }
            if (taskCooldown > 0)
            {
                if (!isCurrentActivity(Activity.AFK))
                {
                    setActivity(Activity.AFK);
                }
                log.info("Task cooldown: " + taskCooldown);
                taskCooldown--;
            }
            else if (isCurrentActivity(Activity.AFK))
            {
                if (nextActivity != null)
                {
                    setActivity(nextActivity);
                    nextActivity = null;
                }
                else
                {
                    setActivity(Activity.IDLE);
                }
            }

        }
    }
}
