package net.unethicalite.tempoross;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("s1dsolotempoross")
public interface S1dSoloTemporossConfig extends Config
{
	//sections
	//info text box to inform user how to use the plugin properly
	@ConfigItem(
			keyName = "info",
			name = "Info",
			description = "Info",
			position = 999
	)
	default String info()
	{
		return "Welcome to S1d's Solo Tempoross plugin! \n" +
				"\n" +
				"This plugin is designed to solo Tempoross with the Infernal Harpoon or with high fishing the Dragon Harpoon.\n" +
				" \n" +
				"Make sure you have the correct equipment, tools and MINIMUM 19 free inventory slots.\n" +
				"\n" +
				"Use at your own risk.";


	}

	//general
	@ConfigSection(
			name = "General",
			description = "General settings",
			position = 991,
			closedByDefault = true
	)
	String general = "General";

	//equipment
	@ConfigSection(
			name = "Equipment",
			description = "Equipment settings",
			position = 992,
			closedByDefault = true
	)
	String equipment = "Equipment";

	//tools
	@ConfigSection(
			name = "Tools",
			description = "Tools settings",
			position = 993,
			closedByDefault = true
	)
	String tools = "Tools";

	//debug
	@ConfigSection(
			name = "Debug",
			description = "Debug settings",
			position = 994,
			closedByDefault = true
	)
	String debug = "Debug";

	@ConfigItem(
			keyName = "cook",
			name = "Cook",
			description = "Cook harpoonfish",
			position = 0,
			section = general
	)
	default boolean cook()
	{
		return true;
	}

	//Tool settings, what kind of harpoon to use, display as a dropdown
	@ConfigItem(
			keyName = "harpoonType",
			name = "Harpoon type",
			description = "Harpoon type to use",
			position = 1,
			section = tools
	)
	default HarpoonType harpoonType()
	{
		return HarpoonType.INFERNAL_HARPOON;
	}

	//Bucket setting, how many buckets to bring, min 4

	@ConfigItem(
			keyName = "buckets",
			name = "Buckets",
			description = "Number of buckets to bring",
			position = 2,
			section = tools
	)
	default int buckets()
	{
		return 6;
	}

	//Hammer setting, should bring a hammer or not, display as a checkbox
	@ConfigItem(
			keyName = "hammer",
			name = "Hammer",
			description = "Hammertime?",
			position = 3,
			section = tools
	)
	default boolean hammer()
	{
		return true;
	}

	//Rope setting, should bring a rope or not, display as a checkbox
	@ConfigItem(
			keyName = "rope",
			name = "Rope",
			description = "Rope?",
			position = 4,
			section = tools
	)
	default boolean rope()
	{
		return true;
	}

	//Debug settings, click delay, min, max, target delay, deviation.
	@ConfigItem(
			keyName = "clickDelayMin",
			name = "Click delay min",
			description = "Minimum click delay",
			position = 0,
			section = debug
	)
	default int clickDelayMin()
	{
		return 100;
	}

	@ConfigItem(
			keyName = "clickDelayMax",
			name = "Click delay max",
			description = "Maximum click delay",
			position = 1,
			section = debug
	)
	default int clickDelayMax()
	{
		return 200;
	}

	@ConfigItem(
			keyName = "targetDelay",
			name = "Target delay",
			description = "Target delay",
			position = 2,
			section = debug
	)
	default int targetDelay()
	{
		return 150;
	}

	@ConfigItem(
			keyName = "deviation",
			name = "Deviation",
			description = "Deviation",
			position = 3,
			section = debug
	)
	default int deviation()
	{
		return 100;
	}

	//Debug settings, Energy, Essence, and Intensity
	@ConfigItem(
			keyName = "energy",
			name = "Energy",
			description = "Energy",
			position = 5,
			section = debug
	)
	default int energy()
	{
		return 100;
	}

	@ConfigItem(
			keyName = "essence",
			name = "Essence",
			description = "Essence",
			position = 6,
			section = debug
	)
	default int essence()
	{
		return 100;
	}

	@ConfigItem(
			keyName = "intensity",
			name = "Intensity",
			description = "Intensity",
			position = 7,
			section = debug
	)
	default int intensity()
	{
		return 0;
	}

	//Equipment settings, if player has Spirit Angler's outfit, display as a checkbox
	@ConfigItem(
			keyName = "hasSpiritAngler",
			name = "Spirit Angler's outfit",
			description = "Spirit Angler's outfit",
			position = 0,
			section = equipment
	)
	default boolean hasSpiritAngler()
	{
		return true;
	}
}
