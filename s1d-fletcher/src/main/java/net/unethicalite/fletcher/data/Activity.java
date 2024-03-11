package net.unethicalite.fletcher.data;
import lombok.Value;

@Value
public class Activity
{

    public static final Activity IDLE = new Activity("Idle");
    public static final Activity BANKING = new Activity("Banking");
    public static final Activity BUYING = new Activity("Buying");
    // Fletching longbow activity
    public static final Activity FLETCHING = new Activity("Fletching");
    // Fletching shortbow activity
    public static final Activity FLETCHING_SHORTBOW = new Activity("Fletching Shortbow");
    // Fletching longbow activity
    public static final Activity FLETCHING_LONGBOW = new Activity("Fletching Longbow");
    // Fletching Shield activity
    public static final Activity FLETCHING_SHIELD = new Activity("Fletching Shield");
    // Fletching Stocks activity
    public static final Activity FLETCHING_STOCKS = new Activity("Fletching Stocks");
    // Fletching shafts activity
    public static final Activity FLETCHING_SHAFTS = new Activity("Fletching Shafts");
    // Stringing longbow activity
    public static final Activity STRINGING_LONGBOW = new Activity("Stringing Longbow");
    // Stringing shortbow activity
    public static final Activity STRINGING_SHORTBOW = new Activity("Stringing Shortbow");


    // AFK Activity
    public static final Activity AFK = new Activity("AFK");

    String name;
}

