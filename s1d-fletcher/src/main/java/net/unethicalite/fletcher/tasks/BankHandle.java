package net.unethicalite.fletcher.tasks;

import net.runelite.api.Constants;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Bank;
import net.unethicalite.fletcher.S1dFletcherPlugin;
import net.unethicalite.fletcher.data.Activity;
import net.unethicalite.fletcher.data.Mode;

import java.util.Optional;

public class BankHandle extends FletcherTask {
    public BankHandle(S1dFletcherPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return this.isCurrentActivity(Activity.IDLE) && !this.hasMaterial() && this.hasFinishedProduct() && !this.hasBowString()
                || this.isCurrentActivity(Activity.IDLE) && !this.hasMaterial() && !this.hasFinishedProduct()
                || this.isCurrentActivity(Activity.IDLE) && !this.hasBowString() && this.hasFinishedProduct() && (this.isMode(Mode.STRINGING_LONGBOW)
                || this.isMode(Mode.STRINGING_SHORTBOW));
    }

    @Override
    public int execute() {


        if (Bank.isOpen()) {
            this.setActivity(Activity.BANKING);
            this.setTimesBanked(this.getTimesBanked() + 1);
            if(this.isMode(Mode.FLETCHING_SHORTBOW) || this.isMode(Mode.FLETCHING_LONGBOW) || this.isMode(Mode.FLETCHING_SHIELD) || this.isMode(Mode.FLETCHING_STOCKS) || this.isMode(Mode.FLETCHING_SHAFTS)) {
                if(this.hasFinishedProduct() && !this.isMode(Mode.FLETCHING_SHAFTS)){
                    Bank.depositAllExcept(ItemID.KNIFE);
                    //sleep one tick
                    Time.sleep(Constants.GAME_TICK_LENGTH);
                }
                if(!this.hasKnife())
                    Bank.withdraw(ItemID.KNIFE, 1, Bank.WithdrawMode.ITEM);
                Bank.withdrawAll(this.getMaterial().getLogID(), Bank.WithdrawMode.ITEM);
                Time.sleepTicksUntil(this::hasMaterial, 20);
                Bank.close();
                this.setTaskCooldown(1);
                return -1;


                // if we are stringing longbow or shortbow
            } else if (this.isMode(Mode.STRINGING_LONGBOW) || this.isMode(Mode.STRINGING_SHORTBOW)) {
                if(this.hasFinishedProduct()){
                    Bank.depositInventory();
                    //sleep one tick
                    Time.sleep(Constants.GAME_TICK_LENGTH);
                }
                if(this.isMode(Mode.STRINGING_LONGBOW)) {
                    // Withdraw 14 bow strings and 14 unstrung bows
                    Bank.withdraw(this.getMaterial().getProductID()[2], 14, Bank.WithdrawMode.ITEM);
                    Bank.withdraw(ItemID.BOW_STRING, 14, Bank.WithdrawMode.ITEM);
                    Time.sleepTicksUntil(this::hasBowString, 20);
                    Bank.close();
                    this.setTaskCooldown(1);
                    return -1;
                }
                if(this.isMode(Mode.STRINGING_SHORTBOW)) {
                    // Withdraw 14 bow strings and 14 unstrung bows
                    Bank.withdraw(this.getMaterial().getProductID()[1], 14, Bank.WithdrawMode.ITEM);
                    Bank.withdraw(ItemID.BOW_STRING, 14, Bank.WithdrawMode.ITEM);
                    Time.sleepTicksUntil(this::hasBowString, 20);
                    Bank.close();
                    this.setTaskCooldown(1);
                    return -1;
                }

            }
        }
        NPC banker = NPCs.getNearest(npc -> npc.hasAction("Collect"));
        if (banker != null && !Bank.isOpen() && this.isCurrentActivity(Activity.IDLE))
        {
            banker.interact("Bank");
            Time.sleepTicksUntil(Bank::isOpen, 20);
            return -1;
        }

        TileObject bank = TileObjects.getFirstSurrounding(this.getClient().getLocalPlayer().getWorldLocation(), 10, obj -> obj.hasAction("Collect") || obj.getName().startsWith("Bank"));
        if (bank != null && banker == null && !Bank.isOpen() && this.isCurrentActivity(Activity.IDLE))
        {
            bank.interact("Bank", "Use");
            Time.sleepTicksUntil(Bank::isOpen, 20);
            return 0;
        }
        return 0;
    }
}
