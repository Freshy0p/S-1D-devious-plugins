package net.unethicalite.motherlodemine;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Vars;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.plugins.Task;
import net.unethicalite.api.plugins.TaskPlugin;
import net.unethicalite.motherlodemine.data.Activity;
import net.unethicalite.motherlodemine.tasks.*;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.Set;


@Extension
@PluginDescriptor(
        name = "<html>[<font color=#25c550>S-1D</font>] Motherlode Mine",
        enabledByDefault = false
)
@Slf4j
public class S1dMotherlodeMinePlugin extends TaskPlugin
{
    private static final Set<Integer> MOTHERLODE_MAP_REGIONS =
            ImmutableSet.of(14679, 14680, 14681, 14935, 14936, 14937, 15191, 15192, 15193);
    private static final int SACK_LARGE_SIZE = 162;
    private static final int SACK_SIZE = 81;
    private static final int UPPER_FLOOR_HEIGHT = -490;

    @Inject
    private Config config;
    public int curSackSize;
    public int maxSackSize;
    public TileObject oreVein;
    @Getter
    public boolean sackFull;
    @Inject
    @Getter
    private Client client;


    private Activity currentActivity;
    private Activity previousActivity;
    protected int taskCooldown;

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

    public void setOreVein(TileObject oreVein)
    {
        this.oreVein = oreVein;
    }

    public final boolean isCurrentActivity(Activity activity)
    {
        return currentActivity == activity;
    }

    public final boolean wasPreviousActivity(Activity activity)
    {
        return previousActivity == activity;
    }

    @Provides
    public Config getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(Config.class);
    }

    private final Task[] tasks =
            {
                new Deposit(this),
                new startTaskPlugin(this),
                new Mine(this),
                new FixWheel(this),
                new GoDown(this),
                new GoUp(this),
                new HandleBank(this),
                new WithdrawSack(this)
    };

    @Override
    public Task[] getTasks()
    {
        return tasks;
    }

    @Override
    protected void startUp()
    {
        refreshSackValues();

        setActivity(Activity.IDLE);
        log.info("S1d Motherlode Mine started");
        log.info("Sack size: " + curSackSize + "/" + maxSackSize);
        log.info("Active activity: " + currentActivity.getName());
        if (curSackSize >= maxSackSize - 26)
        {
            sackFull = true;
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged event)
    {

        Actor actor = event.getActor();
        if (!isRunning() || actor == null || actor != Players.getLocal())
        {
            return;
        }
        switch (Players.getLocal().getAnimation())
        {
            case AnimationID.MINING_MOTHERLODE_BRONZE:
            case AnimationID.MINING_MOTHERLODE_IRON:
            case AnimationID.MINING_MOTHERLODE_STEEL:
            case AnimationID.MINING_MOTHERLODE_BLACK:
            case AnimationID.MINING_MOTHERLODE_MITHRIL:
            case AnimationID.MINING_MOTHERLODE_ADAMANT:
            case AnimationID.MINING_MOTHERLODE_RUNE:
            case AnimationID.MINING_MOTHERLODE_DRAGON:
            case AnimationID.MINING_MOTHERLODE_DRAGON_OR:
            case AnimationID.MINING_MOTHERLODE_DRAGON_UPGRADED:
            case AnimationID.MINING_MOTHERLODE_CRYSTAL:
            case AnimationID.MINING_MOTHERLODE_GILDED:
            case AnimationID.MINING_MOTHERLODE_INFERNAL:
            case AnimationID.MINING_MOTHERLODE_3A:
                setActivity(Activity.MINING);
                break;
            default:
        }
    }


    @Subscribe
    public void onWallObjectSpawned(WallObjectSpawned event)
    {
        WallObject wallObject = event.getWallObject();
        if (isCurrentActivity(Activity.MINING) && wallObject.getName().equals("Depleted vein"))
        {

            if (wallObject.getWorldLocation().equals(oreVein.getWorldLocation()))
            {
                log.info("Vein i was mining turned into a depleted vein");
                setOreVein(null);
                setActivity(Activity.IDLE);
            }
        }
    }
    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event)
    {

        if (isCurrentActivity(Activity.REPAIRING)
                && event.getGameObject().getName().equals("Broken strut"))
        {
            log.info("Strut despawned");
            setActivity(Activity.IDLE);
        }
    }
    @Subscribe
    public void onGameTick(GameTick event)
    {
        if (isRunning() && inMotherlodeMine())
        {
            if (taskCooldown > 0)
            {
                taskCooldown--;
            }
        }
    }
    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event)
    {
        log.info("Item container changed");
        if (isCurrentActivity(Activity.DEPOSITING))
        {
            if (!Inventory.contains(ItemID.PAYDIRT))
            {
                setActivity(Activity.IDLE);
            }
        }
        else if (isCurrentActivity(Activity.WITHDRAWING))
        {
            if (Inventory.contains(
                    ItemID.RUNITE_ORE,
                    ItemID.ADAMANTITE_ORE,
                    ItemID.MITHRIL_ORE,
                    ItemID.GOLD_ORE,
                    ItemID.COAL,
                    ItemID.UNCUT_SAPPHIRE,
                    ItemID.UNCUT_EMERALD,
                    ItemID.UNCUT_RUBY,
                    ItemID.UNCUT_DIAMOND,
                    ItemID.UNCUT_DRAGONSTONE))
            {
                setActivity(Activity.IDLE);
            }
        }
        else if (isCurrentActivity(Activity.MINING))
        {
            if (Inventory.isFull())
            {
                log.info("Inventory full");
                setActivity(Activity.IDLE);
            }
        }
    }

    @Subscribe
    public void onVarbitChanged(VarbitChanged event)
    {
        if (isRunning() && inMotherlodeMine())
        {
            log.info("Varbit changed in MLM");
            refreshSackValues();
            log.info("Sack size: " + curSackSize + "/" + maxSackSize);
            if (curSackSize >= maxSackSize - 26)
            {
                sackFull = true;
            }
        }
    }

    public boolean isUpperFloor()
    {
        return Perspective.getTileHeight(client, client.getLocalPlayer().getLocalLocation(), 0) < UPPER_FLOOR_HEIGHT;
    }

    // mine rockfall
    public void mineRockfall(final int x, final int y)
    {
        final TileObject rockfall = TileObjects.getFirstAt(x, y, 0,
                ObjectID.ROCKFALL, ObjectID.ROCKFALL_26680, ObjectID.ROCKFALL_28786);

        if (rockfall != null)
        {
            rockfall.interact("Mine");
            Time.sleepTicksUntil(
                    () -> TileObjects.getFirstAt(x, y, 0,
                            ObjectID.ROCKFALL, ObjectID.ROCKFALL_26680, ObjectID.ROCKFALL_28786) == null, 50
            );
        }
    }

    // in motherlode mine
    public boolean inMotherlodeMine()
    {
        return MOTHERLODE_MAP_REGIONS.contains(client.getLocalPlayer().getWorldLocation().getRegionID());
    }

    public MiningArea getMiningArea()
    {

            return MiningArea.UPSTAIRS;

    }
    public void refreshSackValues()
    {
        curSackSize = Vars.getBit(Varbits.SACK_NUMBER);
        boolean sackUpgraded = Vars.getBit(Varbits.SACK_UPGRADED) == 1;
        maxSackSize = sackUpgraded ? SACK_LARGE_SIZE : SACK_SIZE;

        if (curSackSize == 0)
        {
            sackFull = false;
        }
    }
}
