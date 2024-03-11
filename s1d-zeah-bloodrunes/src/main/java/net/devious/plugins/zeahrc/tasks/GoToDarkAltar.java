package net.devious.plugins.zeahrc.tasks;

import net.devious.plugins.zeahrc.data.Constants;
import net.devious.plugins.zeahrc.data.Locations;
import net.devious.plugins.zeahrc.framework.SessionUpdater;
import net.runelite.api.TileObject;
import net.unethicalite.api.account.LocalPlayer;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.plugins.Task;

public class GoToDarkAltar extends SessionUpdater implements Task
{
    @Override
    public boolean validate()
    {
        TileObject darkAltar = TileObjects.getNearest(c -> c.getName().equalsIgnoreCase(Constants.DarkAltar));

        return Inventory.isFull()
                && Inventory.contains(Constants.DenseEssenceBlock)
                && darkAltar != null && darkAltar.distanceTo(LocalPlayer.get()) > 10;
    }

    @Override
    public int execute()
    {
        getSession().setCurrentTask("Go to dark altar");

        if (Locations.DenseRunestoneCenter.distanceTo(LocalPlayer.get()) < 15)
        {
            getSession().setCurrentTask("Walking to shortcut");

            Movement.walk(Locations.SmallRocksShortcutSouthSide);
            //Wait until we are no longer moving
            Time.sleepTicksUntil(() -> !LocalPlayer.get().isMoving(), 10);
            return -2;
        }

        if (Locations.SmallRocksShortcutSouthSide.distanceTo(LocalPlayer.get()) < Locations.SmallRocksShortcutNorthSide.distanceTo(LocalPlayer.get()))
        {
            getSession().setCurrentTask("Climbing shortcut");

            TileObject rockShortcut = TileObjects.getNearest(c -> c.hasAction("Climb"));
            if (rockShortcut != null)
            {
                rockShortcut.interact("Climb");
                //Wait until we are no longer animating
                Time.sleepTicksUntil(() -> !LocalPlayer.get().isAnimating(), 10);
            }
            return -1;
        }

        getSession().setCurrentTask("Walking to dark altar");
        Movement.walk(Locations.DarkAltarPoint);
        //Wait until we are no longer moving
        Time.sleepTicksUntil(() -> !LocalPlayer.get().isMoving(), 10);
        return -1;
    }
}
