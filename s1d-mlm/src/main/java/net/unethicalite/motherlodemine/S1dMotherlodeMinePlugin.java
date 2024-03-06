package net.unethicalite.motherlodemine;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.TileObject;
import net.runelite.api.Varbits;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Vars;
import net.unethicalite.api.plugins.Task;
import net.unethicalite.api.plugins.TaskPlugin;
import net.unethicalite.motherlodemine.tasks.*;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.Set;


@Extension
@PluginDescriptor(
        name = "S1d Motherlode Mine",
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
    @Getter
    public boolean sackFull;
    @Inject
    @Getter
    private Client client;

    @Provides
    public Config getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(Config.class);
    }

    private final Task[] tasks =
            {
                new Deposit(this),
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



    public boolean isUpperFloor()
    {
        return Perspective.getTileHeight(client, client.getLocalPlayer().getLocalLocation(), 0) < UPPER_FLOOR_HEIGHT;
    }

    // mine rockfall
    public void mineRockfall()
    {
        TileObject rockfall = TileObjects.getNearest("Rockfall");
        if (rockfall != null)
        {
            log.info("Mining rockfall");
            rockfall.interact("Mine");
        }
    }

    // in motherlode mine
    public boolean inMotherlodeMine()
    {
        return MOTHERLODE_MAP_REGIONS.contains(client.getLocalPlayer().getWorldLocation().getRegionID());
    }

    public MiningArea getMiningArea()
    {
        if (config.upstairs())
        {
            return MiningArea.UPSTAIRS;
        }
        else if (config.shortcut())
        {
            return MiningArea.BEHIND_SHORTCUT;
        }
        else
        {
            return MiningArea.NORTH;
        }
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
