package net.unethicalite.wintertodt;

import net.runelite.client.config.*;

@ConfigGroup("s1dwintertodt")
public interface s1dWintertodtConfig extends Config
{
	@ConfigItem(keyName = "Food name", name = "Food name", description = "The food to use", position = 1)
	default String foodName()
	{
		return "Monkfish";
	}

	@Range(max = 16)
	@ConfigItem(keyName = "Food amount", name = "Food amount", description = "The food amount to take from bank", position = 2)
	default int foodAmount()
	{
		return 5;
	}

	//Min food amount to start a new game
	@Range(max = 28)
	@ConfigItem(keyName = "Min food amount", name = "Min food amount", description = "Min food amount to start a new game", position = 3)
	default int minFoodAmount()
	{
		return 2;
	}

	@Range(max = 100)
	@ConfigItem(keyName = "Health percent", name = "Health %", description = "Health % to eat at", position = 3)
	default int healthPercent()
	{
		return 65;
	}

	@ConfigItem(keyName = "Brazier location", name = "Brazier location", description = "The brazier to use", position = 4)
	default BrazierLocation brazierLocation()
	{
		return BrazierLocation.RANDOM;
	}

	@ConfigItem(keyName = "Fix brazier", name = "Fix broken brazier", description = "Fixes broken brazier if has hammer in inventory", position = 5)
	default boolean fixBrokenBrazier()
	{
		return true;
	}

	@ConfigItem(keyName = "Light brazier", name = "Light unlit brazier", description = "Light unlit brazier if has tinderbox in inventory", position = 6)
	default boolean lightUnlitBrazier()
	{
		return true;
	}

	@ConfigItem(keyName = "Fletching enabled", name = "Fletching enabled", description = "Enables fletching if has knife in inventory", position = 7)
	default boolean fletchingEnabled()
	{
		return true;
	}

	@Range(max = 24)
	@ConfigItem(keyName = "Max resources", name = "Max resources", description = "Max amount of Bruma kindling/roots in inventory before feeding the brazier", position = 8)
	default int maxResources()
	{
		return 8;
	}

	//Min inventory space setting
	@Range(max = 28)
	@ConfigItem(keyName = "Min inventory space", name = "Min inventory space", description = "Min inventory space before leaving", position = 9)
	default int minInventorySpace()
	{
		return 5;
	}

	//Smart mode setting
	@ConfigItem(keyName = "Smart mode", name = "Smart mode", description = "Smart mode makes sure you don't waste any xp", position = 10)
	default boolean smartMode()
	{
		return true;
	}

	@ConfigItem(keyName = "Overlay enabled", name = "Overlay enabled", description = "Enables overlay", position = 11)
	default boolean overlayEnabled()
	{
		return true;
	}

	// Use Spec setting, checkbox
	@ConfigItem(keyName = "UseSpec", name = "UseSpec", description = "Use dragon axe spec", position = 12)
	default boolean useSpec()
	{
		return true;
	}
	// Use Bruma Torch instead of tinderbox setting, checkbox
	@ConfigItem(keyName = "Bruma torch", name = "Bruma torch", description = "Use the Bruma torch instead of tinderbox, can be equipped or in inventory", position = 13)
	default boolean useBrumaTorch()
	{
		return true;
	}

	@ConfigItem(keyName = "Start", name = "Start/Stop", description = "Start/Stop button", position = 20)
	default Button startStopButton()
	{
		return new Button();
	}
}
