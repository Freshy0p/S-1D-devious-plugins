package net.unethicalite.motherlodemine.tasks;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import net.runelite.api.Actor;
import net.runelite.api.AnimationID;
import net.runelite.api.ItemID;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.plugins.Task;
import net.unethicalite.motherlodemine.S1dMotherlodeMinePlugin;
import net.unethicalite.motherlodemine.data.Activity;

@RequiredArgsConstructor
public abstract class MotherlodeMineTask implements Task
{
    @Delegate
    private final S1dMotherlodeMinePlugin context;

    // plugin startup boolean
    public boolean running = false;



    @Subscribe
    private void onAnimationChanged(AnimationChanged event)
    {
        Actor actor = event.getActor();
        if (!context.isRunning() || actor == null || actor != Players.getLocal())
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
                context.setActivity(Activity.MINING);
                break;
            default:
        }
    }

    @Subscribe
    private void onGameObjectDespawned(GameObjectDespawned event)
    {
        if (context.isCurrentActivity(Activity.REPAIRING)
                && event.getGameObject().getName().equals("Broken strut"))
        {
            context.setActivity(Activity.IDLE);
        }
    }

    @Subscribe
    private void onItemContainerChanged(ItemContainerChanged event)
    {
        if (context.isCurrentActivity(Activity.DEPOSITING))
        {
            if (!Inventory.contains(ItemID.PAYDIRT))
            {
                context.setActivity(Activity.IDLE);
            }
        }
        else if (context.isCurrentActivity(Activity.WITHDRAWING))
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
                context.setActivity(Activity.IDLE);
            }
        }
        else if (context.isCurrentActivity(Activity.MINING))
        {
            if (Inventory.isFull())
            {
                context.setActivity(Activity.IDLE);
            }
        }
    }

    @Subscribe
    private void onVarbitChanged(VarbitChanged event)
    {
        if (context.isRunning() && context.inMotherlodeMine())
        {
            context.refreshSackValues();
            if (context.curSackSize >= context.maxSackSize - 26)
            {
                context.sackFull = true;
            }
        }
    }

}
