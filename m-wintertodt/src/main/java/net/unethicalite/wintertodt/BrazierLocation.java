package net.unethicalite.wintertodt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
@Getter
public enum BrazierLocation
{
	SOUTH(new WorldPoint(1620, 3997, 0)),
	EAST(new WorldPoint(1638, 3997, 0)),
	;

	private WorldPoint worldPoint;
}
