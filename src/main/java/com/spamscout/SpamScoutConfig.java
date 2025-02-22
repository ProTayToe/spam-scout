package com.spamscout;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("spamscout")
public interface SpamScoutConfig extends Config
{
	@ConfigItem(
			position = 0,
			keyName = "isLayoutGroupOneEnabled",
			name = "Enable Layout Group 1",
			description = "Enables/disables all scouts in layout group 1"
	)
	default boolean isLayoutGroupOneEnabled()
	{
		return true;
	}

	@ConfigItem(
		position = 1,
		keyName = "layoutGroupOne",
		name = "Layout Group 1",
		description = "A list of CoX layouts separated by new line e.g. SCFCPCSCFS: Guardians, Mystics, Shamans, Tightrope, *"
	)
	default String layoutGroupOne()
	{
		return "SCFCPCSCFS: Guardians, Mystics, Shamans, Tightrope, *";
	}

	@ConfigItem(
			position = 2,
			keyName = "isLayoutGroupTwoEnabled",
			name = "Enable Layout Group 2",
			description = "Enables/disables all scouts in layout group 2"
	)
	default boolean isLayoutGroupTwoEnabled()
	{
		return false;
	}

	@ConfigItem(
		position = 3,
		keyName = "layoutGroupTwo",
		name = "Layout Group 2",
		description = "A list of CoX layouts separated by new line e.g. SCFCPCSCFS: Guardians, Mystics, Shamans, Tightrope, *"
	)
	default String layoutGroupTwo()
	{
		return "";
	}

	@ConfigItem(
			position = 4,
			keyName = "isLayoutGroupThreeEnabled",
			name = "Enable Layout Group 3",
			description = "Enables/disables all scouts in layout group 3"
	)
	default boolean isLayoutGroupThreeEnabled()
	{
		return false;
	}

	@ConfigItem(
		position = 5,
		keyName = "layoutGroupThree",
		name = "Layout Group 3",
		description = "A list of CoX layouts separated by new line e.g. SCFCPCSCFS: Guardians, Mystics, Shamans, Tightrope, *"
	)
	default String layoutGroupThree()
	{
		return "";
	}
}
