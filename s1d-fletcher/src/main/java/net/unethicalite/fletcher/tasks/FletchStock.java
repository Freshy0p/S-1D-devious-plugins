package net.unethicalite.fletcher.tasks;

import net.runelite.api.Constants;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.fletcher.S1dFletcherPlugin;
import net.unethicalite.fletcher.data.Activity;
import net.unethicalite.fletcher.data.Material;
import net.unethicalite.fletcher.data.Mode;

import java.util.Optional;

public class FletchStock  extends FletcherTask {
    public FletchStock(S1dFletcherPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return this.isCurrentActivity(Activity.IDLE) && this.hasMaterial() && this.isMode(Mode.FLETCHING_STOCKS) && this.hasKnife();
    }

    @Override
    public int execute() {
        this.setActivity(Activity.FLETCHING_STOCKS);
        Material material = this.getMaterial();
        Item knife = Inventory.getFirst(ItemID.KNIFE);
        Item logItem = Inventory.getFirst(material.getLogID());

        knife.useOn(logItem);
        Time.sleepTicksUntil(() -> this.isFletchingWidgetOpen(), 30);

        Widget fletchingWidget = this.getClient().getWidget(270, 17);
        Time.sleep(Constants.GAME_TICK_LENGTH);
        fletchingWidget.interact("Make");

        // wait until hasMaterial returns false
        Time.sleepTicksUntil(() -> !this.hasMaterial(), 80);
        this.setTaskCooldown(Optional.of(Activity.IDLE));
        return 0;
    }
}
