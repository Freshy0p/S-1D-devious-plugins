package net.unethicalite.motherlodemine;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.TileObject;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.coords.RectangularArea;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.movement.Reachable;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Getter
public enum MiningArea
{
    UPSTAIRS(
            new RectangularArea(
                    new WorldPoint(3747, 5676, 0),
                    new WorldPoint(3754, 5684, 0)
            ),
            null
    ),
    BEHIND_SHORTCUT(
            new RectangularArea(
                    new WorldPoint(3764, 5657, 0),
                    new WorldPoint(3773, 5668, 0)
            ),
            ImmutableSet.of(
                    new WorldPoint(3773, 5656, 0),
                    new WorldPoint(3764, 5665, 0)
            )
    ),
    NORTH(
            new RectangularArea(
                    new WorldPoint(3733, 5687, 0),
                    new WorldPoint(3743, 5692, 0)
            ),
            null
    );

    private final RectangularArea miningArea;
    private final Set<WorldPoint> ignorePoints;

    // Get nearest ore vein(wallObject) to player
    public TileObject getNearestOreVein()
    {
        final List<TileObject> oreVeins = TileObjects.getAll(x -> x.getName().equals("Ore vein") && x.hasAction("Mine"));
        if (oreVeins.isEmpty())
        {
            return null;
        }
        TileObject oreVein = null;
        float nearestDistance = Float.MAX_VALUE;
        final WorldPoint current = Players.getLocal().getWorldLocation();
        for (TileObject vein : oreVeins)
        {
            for (Direction dir : Direction.values())
            {
                final WorldPoint neighbor = Reachable.getNeighbour(dir, vein.getWorldLocation());

                if (Reachable.isWalkable(neighbor))
                {
                    final float distance = current.distanceToHypotenuse(neighbor);
                    if (distance < nearestDistance)
                    {
                        nearestDistance = distance;
                        oreVein = vein;
                    }
                    else if (distance == nearestDistance)
                    {
                        oreVein = current.distanceToHypotenuse(oreVein.getWorldLocation())
                                > current.distanceToHypotenuse(vein.getWorldLocation()) ? vein : oreVein;
                    }
                }
            }
        }

        return oreVein;
    }

}