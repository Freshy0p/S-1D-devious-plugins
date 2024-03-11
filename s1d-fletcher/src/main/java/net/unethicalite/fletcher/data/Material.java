package net.unethicalite.fletcher.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;

@Getter
@RequiredArgsConstructor
public enum Material
{
    LOG("Logs",ItemID.LOGS, new int[]{ItemID.ARROW_SHAFT, ItemID.SHORTBOW_U, ItemID.LONGBOW_U, ItemID.SHORTBOW, ItemID.LONGBOW}),
    OAK("Oak Logs",ItemID.OAK_LOGS, new int[]{ItemID.ARROW_SHAFT, ItemID.OAK_SHORTBOW_U, ItemID.OAK_LONGBOW_U, ItemID.OAK_STOCK, ItemID.OAK_SHIELD, ItemID.OAK_SHORTBOW, ItemID.OAK_LONGBOW}),
    WILLOW("Willow Logs",ItemID.WILLOW_LOGS, new int[]{ItemID.ARROW_SHAFT, ItemID.WILLOW_SHORTBOW_U, ItemID.WILLOW_LONGBOW_U, ItemID.WILLOW_STOCK, ItemID.WILLOW_SHIELD, ItemID.WILLOW_SHORTBOW, ItemID.WILLOW_LONGBOW}),
    MAPLE("Maple Logs",ItemID.MAPLE_LOGS, new int[]{ItemID.ARROW_SHAFT, ItemID.MAPLE_SHORTBOW_U, ItemID.MAPLE_LONGBOW_U, ItemID.MAPLE_STOCK, ItemID.MAPLE_SHIELD, ItemID.MAPLE_SHORTBOW, ItemID.MAPLE_LONGBOW}),
    YEW("Yew Logs",ItemID.YEW_LOGS, new int[]{ItemID.ARROW_SHAFT, ItemID.YEW_SHORTBOW_U, ItemID.YEW_LONGBOW_U, ItemID.YEW_STOCK, ItemID.YEW_SHIELD, ItemID.YEW_SHORTBOW, ItemID.YEW_LONGBOW}),
    MAGIC("Magic Logs",ItemID.MAGIC_LOGS, new int[]{ItemID.ARROW_SHAFT, ItemID.MAGIC_SHORTBOW_U, ItemID.MAGIC_LONGBOW_U, ItemID.MAGIC_STOCK, ItemID.MAGIC_SHIELD, ItemID.MAGIC_SHORTBOW, ItemID.MAGIC_LONGBOW}),
    REDWOOD("Redwood Logs",ItemID.REDWOOD_LOGS, new int[]{ItemID.ARROW_SHAFT, ItemID.SHORTBOW_U, ItemID.LONGBOW_U, ItemID.REDWOOD_SHIELD});

    private final String name;
    private final int logID;
    private final int[] productID;


    @Override
    public String toString()
    {
        return name;
    }

}