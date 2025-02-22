package com.spamscout;

import com.google.inject.Provides;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
        name = "CoX Spam Scout",
        description = "A plugin that allows you to spam click reload until a desired CoX scout is found",
        tags = {"cox", "chambers of xeric", "scout", "raid", "raids", "qol", "spam"},
        enabledByDefault = true
)
public class SpamScoutPlugin extends Plugin
{
    private static final int COX_STEPS_OBJECT_ID = 49999;

     private boolean foundRaid;

    @Inject
    private Client client;

    @Inject
    private SpamScoutConfig config;

    @Override
    protected void startUp() throws Exception
    {
        log.info("Spam Scout started!");
        foundRaid = false;
    }

    @Override
    protected void shutDown() throws Exception
    {
        log.info("Spam Scout stopped!");
        foundRaid = false;
    }

    @Provides
    SpamScoutConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(SpamScoutConfig.class);
    }

    @Subscribe
    public void onClientTick(ClientTick event)
    {
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        if (!isInRaid())
        {
            return;
        }

        setReloadAsFirstInMenu();
        if (foundRaid)
        {
            lowerClimbAndReloadInMenu();
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        if (chatMessage.getType() == ChatMessageType.FRIENDSCHATNOTIFICATION)
        {
            if (chatMessage.getSender() != null)
            {
                return;
            }
            if (chatMessage.getMessage() == null)
            {
                return;
            }
            if (!chatMessage.getMessage().startsWith("<colHIGHLIGHT>Layout:"))
            {
                return;
            }

            foundRaid = false;

            String message = chatMessage.getMessage().replace("<colHIGHLIGHT>", "");
            message = message.replace("<colNORMAL>", "");
            message = message.toLowerCase();
            message = message.replace(", ", ",");

            String layout = extractLayout(message);
            String roomTypes = extractRoomTypes(message);

            if (layout == null && roomTypes == null)
            {
                return;
            }

            log.debug("Layout: " + layout);
            log.debug("Room Types: " + roomTypes);

            List<String> layouts = new ArrayList<>();
            if (config.isLayoutGroupOneEnabled())
            {
                layouts.addAll(Arrays.asList(config.layoutGroupOne().split("\n")));
            }
            if (config.isLayoutGroupTwoEnabled())
            {
                layouts.addAll(Arrays.asList(config.layoutGroupTwo().split("\n")));
            }
            if (config.isLayoutGroupThreeEnabled())
            {
                layouts.addAll(Arrays.asList(config.layoutGroupThree().split("\n")));
            }

            for (String layoutEntry : layouts)
            {
                // TODO add optional layout/room patterns
                String[] parts = layoutEntry.split(": ");
                if (parts.length != 2)
                {
                    continue;
                }

                String layoutPattern = parts[0].toLowerCase().replace(", ", ",");
                String roomTypesPattern = parts[1].toLowerCase().replace(", ", ",");

                if (matchesPattern(layout, layoutPattern) && matchesPattern(roomTypes, roomTypesPattern))
                {
                    log.info("Match found: " + layoutEntry);
                    foundRaid = true;
                    break;
                }
            }
        }
    }

    private void setReloadAsFirstInMenu()
    {
        MenuEntry[] entries = client.getMenu().getMenuEntries();
        List<MenuEntry> newEntries = new ArrayList<>();

        MenuEntry reloadEntry = null;
        for (MenuEntry entry : entries)
        {
            if (entry.getOption().equalsIgnoreCase("Reload"))
            {
                reloadEntry = entry;
            } else
            {
                newEntries.add(entry);
            }
        }

        if (reloadEntry != null)
        {
            newEntries.add(reloadEntry);
        }

        client.getMenu().setMenuEntries(newEntries.toArray(new MenuEntry[0]));
    }

    private void lowerClimbAndReloadInMenu()
    {
        MenuEntry[] entries = client.getMenu().getMenuEntries();
        List<MenuEntry> newEntries = new ArrayList<>();

        for (MenuEntry entry : entries)
        {
            if (isReloadOrClimbSteps(entry))
            {
                newEntries.add(0, entry);
            } else
            {
                newEntries.add(entry);
            }
        }

        client.getMenu().setMenuEntries(newEntries.toArray(new MenuEntry[0]));
    }

    private static boolean isReloadOrClimbSteps(MenuEntry menuEntry)
    {
        return menuEntry.getIdentifier() == COX_STEPS_OBJECT_ID
                && (menuEntry.getOption().equalsIgnoreCase("Climb")
                || menuEntry.getOption().equalsIgnoreCase("Reload"));
    }

    private boolean matchesPattern(String input, String pattern)
    {
        if (pattern == null || input == null)
        {
            return false;
        }

        // check if the pattern contains regex-specific characters
        boolean isRegex = pattern.matches(".*[\\[\\]{}()\\^$.|?+].*");

        String regexPattern;
        if (isRegex)
        {
            // treat the pattern as a regex
            regexPattern = pattern;
        } else {
            // treat the pattern as a wildcard pattern
            regexPattern = pattern.replace("*", ".*");
        }

        return input.matches(regexPattern);
    }

    private String extractLayout(String message)
    {
        String layoutRegex = "\\[([a-z,*]+)\\]";
        Pattern layoutPattern = Pattern.compile(layoutRegex);
        Matcher layoutMatcher = layoutPattern.matcher(message);

        if (layoutMatcher.find())
        {
            return layoutMatcher.group(1);
        }
        return null;
    }

    private String extractRoomTypes(String message)
    {
        String roomTypesRegex = "layout: \\[.*\\]: (.+)";
        Pattern roomTypesPattern = Pattern.compile(roomTypesRegex);
        Matcher roomTypesMatcher = roomTypesPattern.matcher(message);

        if (roomTypesMatcher.find())
        {
            return roomTypesMatcher.group(1);
        }
        return null;
    }

    private boolean isInRaid()
    {
        // TODO implement proper isInRaid logic
        return client.getTopLevelWorldView().isInstance();
    }
}
