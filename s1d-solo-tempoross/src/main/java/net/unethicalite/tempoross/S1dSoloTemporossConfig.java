package net.unethicalite.tempoross;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("s1dsolotempoross")
public interface S1dSoloTemporossConfig extends Config
{
	//sections

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
		return HarpoonType.HARPOON;
	}

	//Bucket setting, how many buckets to bring, min 4, max 6

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
}
