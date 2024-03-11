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

public class StringBow extends FletcherTask {
    public StringBow(S1dFletcherPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return this.isCurrentActivity(Activity.IDLE) && this.hasUnstrungBow() && this.hasBowString() && this.isMode(Mode.STRINGING_LONGBOW) || this.isMode(Mode.STRINGING_SHORTBOW);
    }

    @Override
    public int execute() {
        Material material = this.getMaterial();
        Item bowString = Inventory.getFirst(ItemID.BOW_STRING);
        if (this.isMode(Mode.STRINGING_LONGBOW)) {
            this.setActivity(Activity.STRINGING_LONGBOW);
            Item unstrungLongbow = Inventory.getFirst(material.getProductID()[2]);
            bowString.useOn(unstrungLongbow);
            Time.sleepTicksUntil(() -> this.isFletchingWidgetOpen(), 30);

            Widget fletchingWidget = this.getClient().getWidget(270, 14);
            Time.sleep(Constants.GAME_TICK_LENGTH);
            fletchingWidget.interact("String");
            Time.sleepTicksUntil(() -> !this.hasBowString(), 80);
            this.setTaskCooldown(Optional.of(Activity.IDLE));



        } else if (this.isMode(Mode.STRINGING_SHORTBOW)) {
            this.setActivity(Activity.STRINGING_SHORTBOW);
            Item unstrungShortbow = Inventory.getFirst(material.getProductID()[1]);
            bowString.useOn(unstrungShortbow);
            Time.sleepTicksUntil(() -> this.isFletchingWidgetOpen(), 30);

            Widget fletchingWidget = this.getClient().getWidget(270, 14);
            Time.sleep(Constants.GAME_TICK_LENGTH);
            fletchingWidget.interact("String");
            Time.sleepTicksUntil(() -> !this.hasBowString(), 80);
            this.setTaskCooldown(Optional.of(Activity.IDLE));
        }
        return 0;
    }
}
