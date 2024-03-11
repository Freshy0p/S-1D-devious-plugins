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

public class GoToRunestone extends SessionUpdater implements Task
{

    @Override
    public boolean validate()
    {
        TileObject runeStone = TileObjects.getNearest(c -> c.hasAction("Chip"));
        return runeStone != null && !Inventory.isFull() && !Inventory.contains(Constants.DarkEssenceBlock) && runeStone.distanceTo(LocalPlayer.get()) > 10
                && !(Locations.BloodAltar.distanceTo(LocalPlayer.get()) < 5 && Inventory.contains(Constants.EssenceFragments));
    }

    @Override
    public int execute()
    {
        getSession().setCurrentTask("Going to runestone");

        Movement.walkTo(Locations.DenseRunestoneCenter);
        Time.sleepTicksUntil(() -> Movement.getDestination().distanceTo(LocalPlayer.get()) < 3, 5);

        return -1;
    }

    @Override
    public boolean isBlocking()
    {
        return true;
    }
}
