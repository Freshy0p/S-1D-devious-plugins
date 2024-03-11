package net.devious.plugins.zeahrc.tasks;

import lombok.extern.slf4j.Slf4j;
import net.devious.plugins.zeahrc.data.Constants;
import net.devious.plugins.zeahrc.data.Locations;
import net.devious.plugins.zeahrc.framework.SessionUpdater;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.account.LocalPlayer;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.api.plugins.Task;

import java.util.Random;

@Slf4j
public class GoToBloodAltar extends SessionUpdater implements Task
{
    @Override
    public boolean validate()
    {
        return Inventory.contains(c -> c.getName().equalsIgnoreCase(Constants.EssenceFragments))
                && Inventory.contains(c -> c.getName().equalsIgnoreCase(Constants.DarkEssenceBlock))
                && LocalPlayer.get().distanceTo(Locations.BloodAltar) >= 8;
    }

    @Override
    public int execute()
    {
        getSession().setCurrentTask("Go to blood altar");

        WorldPoint sw = new WorldPoint(1735, 3844, 0);
        WorldPoint nw = new WorldPoint(1740, 3856, 0);
        WorldArea worldArea = new WorldArea(sw, nw);
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(worldArea.toWorldPointList().size());
        WorldPoint randomPoint = (WorldPoint)worldArea.toWorldPointList().get(index);
        //Get the path to the blood altar and walk it

        //If Blood Altar is not reachable, get a random point within the world area and walk to it
        if (!Reachable.isWalkable(Locations.BloodAltar))
        {
            Movement.walk(randomPoint);
            Time.sleepTick();
            log.info("Walking to random point:" + Movement.getDestination() + "as blood altar is not reachable");
            // Sleep until we are at the destination
            Time.sleepTicksUntil(() -> Movement.getDestination().distanceTo(LocalPlayer.get()) < 3, 100);
            return -1;
        }
        else
        {
            Movement.walk(Locations.BloodAltar);
            Time.sleepTicksUntil(() -> Movement.getDestination().distanceTo(LocalPlayer.get()) < 3, 100);
        }

        return -1;
    }
}
