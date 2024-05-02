package net.unethicalite.motherlodemine.utils;

import net.runelite.api.NullItemID;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.unethicalite.api.items.DepositBox;
import net.unethicalite.api.widgets.Widgets;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class S1dDepositBox {
    private static final int EMPTY_SLOT_ID = NullItemID.NULL_6512;

    public static List<Widget> getAll(Predicate<Widget> filter) {
        return getAll().stream().filter(filter).collect(Collectors.toList());
    }

    public static List<Widget> getAll() {
        if (!DepositBox.isOpen()) {
            return new LinkedList<>();
        }

        return Widgets.getChildren(
                WidgetInfo.DEPOSIT_BOX_INVENTORY_ITEMS_CONTAINER,
                w -> w.getItemId() != S1dDepositBox.EMPTY_SLOT_ID);
    }

    public static void depositAll(Widget widget) {
        widget.interact("Deposit-All");
    }
    // empty gem bag
    public static void EmptyGemBag(Widget widget) {
        widget.interact("Empty");
    }

    public static void depositAll(int... ids) {
        depositAll(w -> Arrays.stream(ids).anyMatch(id -> w.getItemId() == id));
    }

    public static void depositAll(String... names) {
        depositAll(
                w -> Arrays.stream(names).anyMatch(name -> Text.standardize(w.getName()).equals(name)));
    }

    public static void depositAll(Predicate<Widget> filter) {
        Set<Widget> items =
                getAll(filter).stream()
                        .filter(S1dPredicates.distinctByProperty(Widget::getItemId))
                        .collect(Collectors.toSet());

        items.forEach(S1dDepositBox::depositAll);
    }

    public static void depositAllExcept(int... ids) {
        depositAllExcept(w -> Arrays.stream(ids).anyMatch(id -> w.getItemId() == id));
    }
    public static void depositAllExcept(List<Integer> ids) {
        depositAllExcept(x -> ids.contains(x.getItemId()));
    }

    public static void depositAllExcept(String... names) {
        depositAllExcept(
                w -> Arrays.stream(names).anyMatch(name -> Text.standardize(w.getName()).equals(name)));
    }

    public static void depositAllExcept(Predicate<Widget> filter) {
        depositAll(filter.negate());
    }

    // empty open gem bag
    public static void emptyGemBag() {
        List<Widget> gemBags = getAll(w -> w.getName().contains("Open gem bag"));

        Widget gemBag = gemBags.get(0);
        EmptyGemBag(gemBag);
    }
}