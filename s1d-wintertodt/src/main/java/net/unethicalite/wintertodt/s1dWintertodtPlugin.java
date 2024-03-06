/*
 * Copyright (c) 2022, Melxin <https://github.com/melxin/>,
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.unethicalite.wintertodt;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.events.ExperienceGained;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.input.Keyboard;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.plugins.LoopedPlugin;
import net.unethicalite.api.widgets.Dialog;
import net.unethicalite.api.widgets.Widgets;
import net.unethicalite.wintertodt.utils.TimeUtils;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.util.SplittableRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.unethicalite.api.commons.Time.sleep;
import static net.unethicalite.api.commons.Time.sleepUntil;

@Extension
@PluginDescriptor(
	name = "S-1D Wintertodt",
	description = "does wintertodt",
	enabledByDefault = false,
	tags =
		{
			"wintertodt",
			"minigame"
		}
)
@Slf4j
@Singleton
public class s1dWintertodtPlugin extends LoopedPlugin
{
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private s1dWintertodtOverlay s1dWintertodtOverlay;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private s1dWintertodtConfig config;

	@Provides
	private s1dWintertodtConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(s1dWintertodtConfig.class);
	}

	@Getter(AccessLevel.PACKAGE)
	private State currentState;

	@Getter(AccessLevel.PACKAGE)
	private int won;

	@Getter(AccessLevel.PACKAGE)
	private int lost;

	@Getter(AccessLevel.PACKAGE)
	private int logsCut;

	@Getter(AccessLevel.PACKAGE)
	private int logsFletched;

	@Getter(AccessLevel.PACKAGE)
	private int braziersFixed;

	@Getter(AccessLevel.PACKAGE)
	private int braziersLit;

	@Getter(AccessLevel.PACKAGE)
	private int foodConsumed;

	@Getter(AccessLevel.PACKAGE)
	private int timesBanked;

	@Getter(AccessLevel.PACKAGE)
	private boolean scriptStarted;

	private Instant scriptStartTime;

	protected String getTimeRunning()
	{
		return scriptStartTime != null ? TimeUtils.getFormattedDurationBetween(scriptStartTime, Instant.now()) : "";
	}

	private int random;
	private static final SplittableRandom splittableRandom = new SplittableRandom();

	private void generateRandom()
	{
		this.random = splittableRandom.nextInt(0, BrazierLocation.values().length - 1);
	}

	protected BrazierLocation getBrazierLocation()
	{
		final BrazierLocation brazierLocation = config.brazierLocation();

		if (brazierLocation == BrazierLocation.RANDOM)
		{
			return random <= random / 2 ? BrazierLocation.WEST : BrazierLocation.EAST;
		}
		return brazierLocation;
	}

	@Override
	protected void startUp()
	{
		this.overlayManager.add(s1dWintertodtOverlay);
	}

	@Override
	protected void shutDown()
	{
		reset();
		this.overlayManager.remove(s1dWintertodtOverlay);
	}

	/**
	 * Reset/stop
	 */
	private void reset()
	{
		this.won = 0;
		this.lost = 0;
		this.logsCut = 0;
		this.logsFletched = 0;
		this.braziersFixed = 0;
		this.braziersLit = 0;
		this.foodConsumed = 0;
		this.timesBanked = 0;
		this.random = 0;
		this.currentState = null;
		this.scriptStartTime = null;
		this.scriptStarted = false;
	}

	@Subscribe
	public void onConfigButtonPressed(ConfigButtonClicked event)
	{
		if (!event.getGroup().contains("s1dwintertodt")
			|| !event.getKey().toLowerCase().contains("start"))
		{
			return;
		}

		if (scriptStarted)
		{
			reset();
		}
		else
		{
			this.scriptStartTime = Instant.now();
			this.scriptStarted = true;
			this.generateRandom();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().contains("s1dwintertodt"))
		{
			return;
		}

		if (event.getKey().contains("Brazier location")
			&& config.brazierLocation() == BrazierLocation.RANDOM)
		{
			this.generateRandom();
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		ChatMessageType chatMessageType = chatMessage.getType();
		MessageNode messageNode = chatMessage.getMessageNode();

		if (!scriptStarted
			|| !isInWintertodtRegion()
			|| chatMessageType != ChatMessageType.GAMEMESSAGE
			&& chatMessageType != ChatMessageType.SPAM)
		{
			return;
		}

		if (messageNode.getValue().startsWith("The brazier is broken and shrapnel"))
		{
			if (config.fixBrokenBrazier()
				&& Inventory.contains(ItemID.HAMMER))
			{
				this.currentState = State.FIX_BRAZIER;
			}
		}

		if (messageNode.getValue().startsWith("The brazier has gone out"))
		{
			if (config.lightUnlitBrazier()
				&& Inventory.contains(ItemID.TINDERBOX))
			{
				this.currentState = State.LIGHT_BRAZIER;
			}
		}

		if (messageNode.getValue().startsWith("You fix the brazier"))
		{
			braziersFixed++;
		}

		if (messageNode.getValue().startsWith("You light the brazier"))
		{
			braziersLit++;
		}

		if (messageNode.getValue().startsWith("You have gained a supply crate"))
		{
			won++;
		}

		if (messageNode.getValue().startsWith("You did not earn enough points"))
		{
			lost++;
		}

		if (messageNode.getValue().startsWith("Your hands are too cold to fletch here"))
		{
			Movement.walkTo(getBrazierLocation().getWorldPoint());
		}
	}

	/**
	 * Broadcast a chat message
	 *
	 * @param message
	 */
	private void broadcastMessage(String message)
	{
		chatMessageManager.queue(QueuedMessage.builder()
			.runeLiteFormattedMessage(
				new ChatMessageBuilder()
					.append(ChatColorType.NORMAL)
					.append("[s1dWintertodt] ")
					.append(ChatColorType.HIGHLIGHT)
					.append(message)
					.build()
			)
			.type(ChatMessageType.BROADCAST)
			.build());
	}

	private boolean isInWintertodtRegion()
	{
		Player localPlayer = client.getLocalPlayer();
		return localPlayer != null && localPlayer.getWorldLocation().getRegionID() == 6462;
	}

	private boolean isGameStarted()
	{
		if (isInWintertodtRegion())
		{
			Widget w = Widgets.get(396, 20);
			return w == null || !w.getText().contains("The Wintertodt returns in");
		}
		return false;
	}

	//Get wintertodt energy(health) from widget
	public int getWintertodtEnergy()
	{
		if (!isInWintertodtRegion())
		{
			return 0;
		}
		Widget w = Widgets.get(396, 20);
		if (w != null)
		{
			// Pull just the numbers from the Widget's text property ("Wintertodt Energy: 100%")
			Pattern regex = Pattern.compile("\\d+");
			Matcher bossEnergy = regex.matcher(w.getText().toString());

			if (bossEnergy.find())
			{
				return Integer.parseInt(bossEnergy.group(0));
			}
		}
		return 0;
	}

	//Get current number of resources in inventory
	private int getResourcesInInventory()
	{
		return Inventory.getCount(ItemID.BRUMA_KINDLING) + Inventory.getCount(ItemID.BRUMA_ROOT);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (!scriptStarted)
		{
			return;
		}

		// Skip dialog when leaving wintertodt area and game is started
		Widget skipDialog = Widgets.get(WidgetInfo.DIALOG_OPTION_OPTION1);
		if (isGameStarted()
			&& skipDialog != null
			&& skipDialog.isVisible())
		{
			Dialog.chooseOption(1);
		}

		// Try to remove this widget when bank is closed and widget is still there
		Widget enterAmountWidget = Widgets.get(WidgetInfo.CHATBOX_TITLE);
		if (!Bank.isOpen()
			&& Inventory.getCount(i -> i != null && i.getName().toLowerCase().contains(config.foodName().toLowerCase())) >= config.foodAmount()
			&& enterAmountWidget != null
			&& enterAmountWidget.isVisible()
			&& enterAmountWidget.getText().startsWith("Enter amount"))
		{
			Keyboard.type(0);
			Keyboard.sendEnter();
		}
	}

	@Subscribe
	public void onExperienceGained(ExperienceGained event)
	{
		if (!scriptStarted
			|| !isGameStarted())
		{
			return;
		}

		if (event.getSkill() == Skill.WOODCUTTING)
		{
			logsCut++;
		}

		if (event.getSkill() == Skill.FLETCHING)
		{
			logsFletched++;
		}
	}

	private enum State
	{
		BANK,
		ENTER_WINTERTODT,
		WALK_TO_BRAZIER,
		EAT_FOOD,
		CUT_TREE,
		FLETCH_LOGS,
		FIX_BRAZIER,
		LIGHT_BRAZIER,
		FEED_BRAZIER,
		LEAVE_WINTERTODT,
		SLEEP
	}

	private State getState()
	{
		final Player localPlayer = client.getLocalPlayer();

		if (Combat.getHealthPercent() <= config.healthPercent())
		{
			if (Inventory.contains(i -> i != null && i.getName().toLowerCase().contains(config.foodName().toLowerCase())))
			{
				return State.EAT_FOOD;
			}
			else
			{
				if (isInWintertodtRegion())
				{
					return State.LEAVE_WINTERTODT;
				}
			}
		}

		if (!isInWintertodtRegion())
		{
			if (Inventory.getCount(i -> i != null && i.getName().toLowerCase().contains(config.foodName().toLowerCase())) < config.foodAmount()
				|| Inventory.getFreeSlots() < config.minInventorySpace())
			{
				return State.BANK;
			}
			else
			{
				return State.ENTER_WINTERTODT;
			}
		}

		if (isInWintertodtRegion())
		{
			if (isGameStarted())
			{
				TileObject brokenBrazier = TileObjects.getFirstSurrounding(localPlayer.getWorldLocation(), 10, obj -> obj.hasAction("Fix"));
				TileObject unlitBrazier = TileObjects.getFirstSurrounding(localPlayer.getWorldLocation(), 10, obj -> obj.hasAction("Light"));
				TileObject burningBrazier = TileObjects.getFirstSurrounding(localPlayer.getWorldLocation(), 10, obj -> obj.hasAction("Feed") || obj.getName().startsWith("Burning brazier"));
				TileObject currentBrazier = brokenBrazier != null ? brokenBrazier : unlitBrazier != null ? unlitBrazier : burningBrazier;
				if (currentBrazier != null)
				{
					if (Inventory.getCount(ItemID.BRUMA_KINDLING) >= config.maxResources() && config.fletchingEnabled()
						|| Inventory.getCount(ItemID.BRUMA_ROOT) >= config.maxResources() && !config.fletchingEnabled()
						|| Inventory.contains(ItemID.BRUMA_KINDLING) && localPlayer.distanceTo(currentBrazier) <= 4
						|| Inventory.contains(ItemID.BRUMA_ROOT) && localPlayer.distanceTo(currentBrazier) <= 4
						|| currentBrazier == brokenBrazier && localPlayer.distanceTo(currentBrazier) <= 4
						|| currentBrazier == unlitBrazier && localPlayer.distanceTo(currentBrazier) <= 4
						|| getResourcesInInventory() >= getWintertodtEnergy())
					{
						if (currentBrazier == brokenBrazier
							&& config.fixBrokenBrazier()
							&& Inventory.contains(ItemID.HAMMER))
						{
							return State.FIX_BRAZIER;
						}
						else if (currentBrazier == unlitBrazier
							&& config.lightUnlitBrazier()
							&& Inventory.contains(ItemID.TINDERBOX))
						{
							return State.LIGHT_BRAZIER;
						}
						else if (currentBrazier == burningBrazier)
						{

							return State.FEED_BRAZIER;
						}
					}
				}

				if (getResourcesInInventory() < config.maxResources() && !Inventory.isFull())
				{
					return State.CUT_TREE;
				}
				else if (Inventory.contains(ItemID.BRUMA_ROOT)
					&& Inventory.contains(ItemID.KNIFE)
					&& config.fletchingEnabled())
				{
					return State.FLETCH_LOGS;
				}
			}
			else if (Inventory.getCount(i -> i != null && i.getName().toLowerCase().contains(config.foodName().toLowerCase())) < config.minFoodAmount()
				|| Inventory.getFreeSlots() < config.minInventorySpace())
			{
				return State.LEAVE_WINTERTODT;
			}
			else if (localPlayer.distanceTo(getBrazierLocation().getWorldPoint()) > 1)
			{
				return State.WALK_TO_BRAZIER;
			}
		}
		return State.SLEEP;
	}

	@Override
	protected int loop()
	{
		if (!scriptStarted
			|| client.getGameState() == GameState.LOGIN_SCREEN)
		{
			return -1;
		}

		final Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null)
		{
			return -1;
		}

		currentState = getState();
		switch (currentState)
		{
			case BANK:
				TileObject bank = TileObjects.getFirstSurrounding(localPlayer.getWorldLocation(), 10, obj -> obj.hasAction("Bank") || obj.getName().startsWith("Collect"));
				if (Bank.isOpen())
				{
					sleep(Constants.GAME_TICK_LENGTH);
					Bank.depositAllExcept(item -> item != null && item.getName().toLowerCase().contains(config.foodName().toLowerCase()) || item.getName().endsWith("axe") || item.getName().equals("Knife") || item.getName().equals("Hammer") || item.getName().equals("Tinderbox"));
					sleep(Constants.GAME_TICK_LENGTH);

					//Bug, if raw or burnt version is in a earlier bank slot than the cooked, it will withdraw the raw/burnt food that can't be eaten

					//Fix for bug, ignore food that starts with "Burnt" or "Raw"
					if (Bank.getFirst(i -> i != null && i.getName().toLowerCase().contains(config.foodName().toLowerCase()) && !i.getName().startsWith("Burnt") && !i.getName().startsWith("Raw")) == null)
					{
						log.error("No {} was found in bank", config.foodName());
						broadcastMessage("No " + config.foodName() + " was found in bank");
						reset();
						return -1;
					}
					//Old code
//					if (Bank.getFirst(i -> i != null && i.getName().toLowerCase().contains(config.foodName().toLowerCase())) == null)
//					{
//						log.error("No {} was found in bank", config.foodName());
//						broadcastMessage("No " + config.foodName() + " was found in bank");
//						reset();
//						return -1;
//					}

					int foodAmountToWithdraw = config.foodAmount() - Inventory.getCount(i -> i != null && i.getName().toLowerCase().contains(config.foodName().toLowerCase()));
					if (foodAmountToWithdraw > 0)
					{
						// apply bug fix to ignore food that starts with "Burnt" or "Raw" also check for "Big" for those collectors
						Bank.withdraw(i -> i != null && i.getName().toLowerCase().contains(config.foodName().toLowerCase()) && !i.getName().startsWith("Burnt") && !i.getName().startsWith("Raw") && !i.getName().startsWith("Big"), foodAmountToWithdraw, Bank.WithdrawMode.ITEM);
						//Old code
						//Bank.withdraw(i -> i != null && i.getName().toLowerCase().contains(config.foodName().toLowerCase()), foodAmountToWithdraw, Bank.WithdrawMode.ITEM);
						sleep(Constants.GAME_TICK_LENGTH);
						sleepUntil(() -> Inventory.getCount(i -> i != null && i.getName().toLowerCase().contains(config.foodName().toLowerCase())) == config.foodAmount(), 3000);
					}

					Bank.close();
					timesBanked++;
					this.generateRandom();
				}
				else if (bank != null)
				{
					bank.interact("Bank");
					sleepUntil(() -> Bank.isOpen(), 1000);
				}
				else
				{
					Movement.walkTo(new WorldPoint(1640, 3944, 0));
				}
				return -1;

			case ENTER_WINTERTODT:
				if (!isInWintertodtRegion())
				{
					TileObject door = TileObjects.getFirstSurrounding(localPlayer.getWorldLocation(), 10, obj -> obj.getName().startsWith("Door") && obj.hasAction("Enter"));
					if (door != null)
					{
						door.interact("Enter");
						sleepUntil(this::isInWintertodtRegion, 5000);
						sleep(Constants.GAME_TICK_LENGTH);
					}
					else
					{
						Movement.walkTo(new WorldPoint(1630, 3962, 0));
					}
				}
				return -1;

			case WALK_TO_BRAZIER:
				Movement.walkTo(getBrazierLocation().getWorldPoint());
				return -1;

			case EAT_FOOD:
				Item item = Inventory.getFirst(i -> i != null && i.getName().toLowerCase().contains(config.foodName().toLowerCase()));
				if (item != null)
				{
					item.interact(x -> x != null && (x.toLowerCase().contains("eat") || x.toLowerCase().contains("drink")));
					sleep(Constants.GAME_TICK_LENGTH);
					foodConsumed++;
				}
				return -1;

			case CUT_TREE:
				if (!localPlayer.isAnimating()
					&& isInWintertodtRegion())
				{
					TileObject tree = TileObjects.getFirstSurrounding(getBrazierLocation().getWorldPoint(), 10, obj -> obj.hasAction("Chop") || obj.getName().startsWith("Bruma roots"));
					if (tree != null)
					{
						tree.interact("Chop");
						sleep(Constants.GAME_TICK_LENGTH);
					}
					else
					{
						Movement.walkTo(getBrazierLocation().getWorldPoint());
					}
				}
				return -1;

			case FLETCH_LOGS:
				if (!localPlayer.isAnimating()
					&& isInWintertodtRegion()
					|| Inventory.getCount(ItemID.BRUMA_ROOT) >= config.maxResources()
					&& localPlayer.getAnimation() != AnimationID.FLETCHING_BOW_CUTTING)
				{
					Inventory.getFirst(ItemID.KNIFE).useOn(Inventory.getFirst(ItemID.BRUMA_ROOT));
					sleep(Constants.GAME_TICK_LENGTH);
					sleepUntil(() -> !Inventory.contains(ItemID.BRUMA_ROOT) || localPlayer.getAnimation() != AnimationID.FLETCHING_BOW_CUTTING, 5500);
				}
				return -1;

			case FIX_BRAZIER:
				TileObject brokenBrazier = TileObjects.getFirstSurrounding(localPlayer.getWorldLocation(), 10, obj -> obj.hasAction("Fix"));
				if (brokenBrazier != null)
				{
					brokenBrazier.interact("Fix");
					sleep(Constants.GAME_TICK_LENGTH);
				}
				return -1;

			case LIGHT_BRAZIER:
				TileObject unlitBrazier = TileObjects.getFirstSurrounding(localPlayer.getWorldLocation(), 10, obj -> obj.hasAction("Light"));
				if (unlitBrazier != null)
				{
					unlitBrazier.interact("Light");
					sleep(Constants.GAME_TICK_LENGTH);
				}
				return -1;

			case FEED_BRAZIER:
				if (isInWintertodtRegion())
				{
					TileObject burningBrazier = TileObjects.getFirstSurrounding(localPlayer.getWorldLocation(), 10, obj -> obj.hasAction("Feed") || obj.getName().startsWith("Burning brazier"));
					if (Inventory.getCount(ItemID.BRUMA_ROOT) >= config.maxResources() || localPlayer.getAnimation() != AnimationID.LOOKING_INTO)
					{
						if (burningBrazier != null)
						{
							burningBrazier.interact("Feed");
							sleep(Constants.GAME_TICK_LENGTH);
							sleepUntil(() -> currentState != State.FEED_BRAZIER || burningBrazier == null || !Inventory.contains(ItemID.BRUMA_KINDLING) && !Inventory.contains(ItemID.BRUMA_ROOT), 7500);
						}
					}
				}
				return -1;

			case LEAVE_WINTERTODT:
				if (isInWintertodtRegion())
				{
					TileObject door = TileObjects.getFirstSurrounding(localPlayer.getWorldLocation(), 10, obj -> obj.getName().startsWith("Door") && obj.hasAction("Enter"));
					if (door != null
						&& localPlayer.distanceTo(door.getWorldLocation()) <= 3)
					{
						door.interact("Enter");
						sleepUntil(() -> !isInWintertodtRegion(), 5000);
					}
					else
					{
						Movement.walkTo(new WorldPoint(631, 3969, 0));
					}
				}
				return -1;

			case SLEEP:
				return -1;
		}
		return -1;
	}
}
