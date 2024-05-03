package net.unethicalite.fighter;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.util.Text;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.WildcardMatcher;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileItems;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.game.Game;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.magic.Magic;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.api.plugins.LoopedPlugin;
import net.unethicalite.api.plugins.Plugins;
import net.unethicalite.api.utils.MessageUtils;
import net.unethicalite.api.widgets.Dialog;
import net.unethicalite.api.widgets.Prayers;
import net.unethicalite.fighter.utils.BankLocation;
import net.unethicalite.fighter.utils.Constants;
import net.unethicalite.fighter.utils.S1dBank;
import org.pf4j.Extension;

import javax.inject.Inject;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@PluginDescriptor(
		name = "<html>[<font color=#f44336>\uD83D\uDC24</font>] Fighter",
		description = "A simple fighter plugin that can bank, loot, and alch. It also has loot only mode.",
		enabledByDefault = false,
		tags = {"combat", "loot", "alch", "bank", "npc", "monster", "fight", "fighter", "looter", "alcher", "banker", "s1d"}
)
@Slf4j
@Extension
public class FighterPlugin extends LoopedPlugin
{
	private static final Pattern WORLD_POINT_PATTERN = Pattern.compile("^\\d{4,5} \\d{4,5} \\d$");

	private ScheduledExecutorService executor;

	@Inject
	private FighterConfig config;

	@Inject
	private ItemManager itemManager;

	@Inject
	private Client client;

	@Inject
	private ConfigManager configManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private FighterOverlay fighterOverlay;

	private final List<TileItem> notOurItems = new ArrayList<>();

	private boolean startedScript = false;
	private boolean menuFight = false;

	private boolean isFighting = false;

	private boolean isLooting = false;

	private boolean isBanking = false;

	int min = 100;
	int max = 250;
	int target = 175;
	int deviation = 50;

	public static int calculateClickDelay(int min, int max, int target, int deviation)
	{
		Random rand = new Random();
		// Generate a Gaussian-distributed value centered around 0 with a standard deviation of 1
		double gaussianValue = rand.nextGaussian();

		// Scale and shift the Gaussian value to have a mean of `target` and a standard deviation of `deviation / 3`
		// The division by 3 makes it more likely that the value falls within one standard deviation from the mean
		int delay = (int) (target + gaussianValue * (deviation / 3));

		// Ensure the delay is within the specified bounds [min, max]
		if (delay < min) delay = min;
		if (delay > max) delay = max;

		return delay;
	}

	@Override
	public void startUp() throws Exception
	{
		super.startUp();
		overlayManager.add(fighterOverlay);
		executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleWithFixedDelay(() ->
		{
			try
			{
				if (!Game.isLoggedIn())
				{
					return;
				}

				if (config.quickPrayer() && !Prayers.isQuickPrayerEnabled())
				{
					Prayers.toggleQuickPrayer(true);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}, 0, 100, TimeUnit.MILLISECONDS);

		if (Game.isLoggedIn())
		{
			setCenter(Players.getLocal().getWorldLocation());
		}
	}

	@Provides
	public FighterConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FighterConfig.class);
	}

	@Override
	public void shutDown()
	{
		overlayManager.remove(fighterOverlay);
		if (executor != null)
		{
			executor.shutdown();
		}
	}

	// reset method
	public void reset()
	{
		startedScript = false;
		menuFight = false;
		resetCenter();
	}

	@Subscribe
	public void onConfigButtonPressed(ConfigButtonClicked event)
	{
		if (!event.getGroup().contains("hootfighter")
				|| !event.getKey().toLowerCase().contains("start"))
		{
			return;
		}

		if (startedScript)
		{
			reset();
		}
		else
		{
			startedScript = true;
		}
	}

	@Subscribe
	private void onMenuOptionClicked(MenuOptionClicked event)
	{
		// check if config is enabled
		if (!config.insertMenu())
		{
			return;
		}

		// check if the selected option is our custom option "S1-D Fight"
		if (event.getMenuOption().equals("S1-D Fight"))
		{
			menuFight = true;
			//get the name of the npc we are fighting
			String npcName = event.getMenuTarget();
			// strip the color codes from the name
			npcName = npcName.replaceAll("<[^>]*>", "");
			//strip the level from the name
			npcName = npcName.replaceAll("\\(.*\\)", "");





			//set the npc name to the config
			configManager.setConfiguration("hootfighter", "monster", npcName);
			startedScript = true;
		}
		else if (event.getMenuOption().equals("Stop S1-D Fight"))
		{

			reset();

		}
	}
	@Subscribe
	private void onMenuEntryAdded(MenuEntryAdded event) {
		if (!config.insertMenu() || !event.getOption().equals("Attack")) {
			return;
		}

		if (!menuFight) {
			addMenuEntry(event, "S1-D Fight");
		} else {
			addMenuEntry(event, "Stop S1-D Fight");
		}
	}

	private void addMenuEntry(MenuEntryAdded event, String option) { //TODO: Update to new menu entry
		client.createMenuEntry(-1).setOption(option)
				.setTarget(event.getTarget())
				.setIdentifier(0)
				.setParam1(0)
				.setParam1(0)
				.setType(MenuAction.RUNELITE);
	}
	@Override
	protected int loop()
	{
		if (!startedScript)
		{
			return -1;
		}
		WorldPoint center = getCenter();
		if (center == null)
		{
			if (Game.isLoggedIn())
			{
				setCenter(Players.getLocal().getWorldLocation());
			}

			return -1;
		}
		if (Game.isLoggedIn() && config.lootOnlyMode())
		{
			setCenter(Players.getLocal().getWorldLocation());
		}

		if (Movement.isWalking())
		{
			return -1;
		}

		if (config.flick() && Prayers.isQuickPrayerEnabled())
		{
			Prayers.toggleQuickPrayer(false);
		}

		if (config.eat() && Combat.getHealthPercent() <= config.healthPercent())
		{
			List<String> foods = Text.fromCSV(config.foods());
			Item food = Inventory.getFirst(x -> (x.getName() != null && foods.stream().anyMatch(a -> x.getName().contains(a)))
					|| (foods.contains("Any") && x.hasAction("Eat")));
			if (food != null)
			{
				Time.sleep(calculateClickDelay(min, max, target, deviation));
				food.interact("Eat");
				return -3;
			}
		}

		if (config.restore() && Prayers.getPoints() < 5)
		{
			Item restorePotion = Inventory.getFirst(x -> x.hasAction("Drink")
					&& (x.getName().contains("Prayer potion") || x.getName().contains("Super restore")));
			if (restorePotion != null)
			{
				Time.sleep(calculateClickDelay(min, max, target, deviation));
				restorePotion.interact("Drink");
				return -3;
			}
		}

		if (config.antipoison() && Combat.isPoisoned())
		{
			Item antipoison = Inventory.getFirst(
					config.antipoisonType().getDose1(),
					config.antipoisonType().getDose2(),
					config.antipoisonType().getDose3(),
					config.antipoisonType().getDose4()
			);
			if (antipoison != null)
			{
				Time.sleep(calculateClickDelay(min, max, target, deviation));
				antipoison.interact("Drink");
				return -1;
			}
		}

		if (config.buryBones())
		{
			Item bones = Inventory.getFirst(x -> x.hasAction("Bury") || x.hasAction("Scatter"));
			if (bones != null)
			{
				Time.sleep(calculateClickDelay(min, max, target, deviation));
				bones.interact(bones.hasAction("Bury") ? "Bury" : "Scatter");
				return -1;
			}
		}

		int minimumFreeSlots = config.minFreeSlots();
		if (Inventory.getFreeSlots() <= minimumFreeSlots && config.bank())
		{
			isBanking = true;
			if(Bank.isOpen())
			{
				// Deposit all items except the ones we want to keep according to the config
				// create a list of item ids to keep
				// check config for items to keep (boolean values in config)
				List<Integer> idsToKeep = new ArrayList<>();
				if (config.ignoreTeleport())
				{
					idsToKeep.addAll(Constants.TELEPORT_IDS);
				}
				if (config.ignoreStamina())
				{
					idsToKeep.addAll(Constants.STAMINA_POTION_IDS);
				}
				if (config.ignorePrayer())
				{
					idsToKeep.addAll(Constants.PRAYER_RESTORE_POTION_IDS);
				}
				if (config.ignoreFood())
				{
					idsToKeep.addAll(Constants.FOOD_ITEM_IDS);
				}
				if (config.ignoreAntipoison())
				{
					idsToKeep.addAll(Constants.ANTI_POISON_POTION_IDS);
				}
				if (config.ignoreAntifire())
				{
					idsToKeep.addAll(Constants.ANTI_FIRE_POTION_IDS);
				}
				if (config.ignoreCombat())
				{
					idsToKeep.addAll(Constants.STRENGTH_POTION_IDS);
				}
				if (config.ignoreRestore())
				{
					idsToKeep.addAll(Constants.RESTORE_POTION_IDS);
				}



				// deposit all items except the ones we want to keep
				S1dBank.depositAllExcept(true, idsToKeep);
				Time.sleepTick();
				Bank.close();
				return -1;
			}
			WorldPoint bankArea;

			bankArea = BankLocation.getNearest().getArea();

			NPC banker = NPCs.getNearest(npc -> npc.hasAction("Collect"));
			if (banker != null && !Bank.isOpen() && !Movement.isWalking())
			{
				Time.sleep(calculateClickDelay(min, max, target, deviation));
				// only interact if we are next to the banker
				if (banker.getWorldLocation().distanceTo(Players.getLocal().getWorldLocation()) <= 2)
				{
					banker.interact("Bank");
					Time.sleepTicksUntil(Bank::isOpen, 20);
					return -1;
				}
				Movement.walkNextTo(banker);
				return -1;
			}


			TileObject bank = TileObjects.getFirstSurrounding(client.getLocalPlayer().getWorldLocation(), 10, obj -> obj.hasAction("Collect") || obj.getName().startsWith("Bank")&& obj.hasAction("Use"));
			if (bank != null && banker == null && !Bank.isOpen() && !Movement.isWalking())
			{
				Time.sleep(calculateClickDelay(min, max, target, deviation));
				// only interact if we are next to the bank
				if (bank.getWorldLocation().distanceTo(Players.getLocal().getWorldLocation()) <= 2)
				{
					bank.interact("Bank", "Use");
					Time.sleepTicksUntil(Bank::isOpen, 20);
					return -1;
				}
				Movement.walkNextTo(bank);
				return 0;
			}
			if (bank == null && banker == null && !Bank.isOpen())
			{
				Time.sleep(calculateClickDelay(min, max, target, deviation));
				Movement.walkTo(bankArea);
				return -1;
			}
		}
		else {
			isBanking = false;
		}

		Player local = Players.getLocal();
		TileItem loot = TileItems.getFirstSurrounding(center, config.attackRange(), x ->
				!notOurItems.contains(x)
						&& !shouldNotLoot(x) && (shouldLootByName(x) || shouldLootUntradable(x) || shouldLootByValue(x))
		);
		if (loot != null)
		{

			isLooting = true;
		}
		else {
			isLooting = false;
		}
		if (loot != null && canPick(loot) && !local.isMoving())
		{

			if (!Reachable.isInteractable(loot.getTile()))
			{
				Movement.walkTo(loot.getTile().getWorldLocation());
				return 0;
			}
			loot.pickup();

			return 0;
		}

		if (config.alching())
		{
			AlchSpell alchSpell = config.alchSpell();
			if (alchSpell.canCast())
			{
				List<String> alchItems = Text.fromCSV(config.alchItems());
				Item alchItem = Inventory.getFirst(x -> x.getName() != null && textMatches(alchItems, x.getName()));
				if (alchItem != null)
				{
					Time.sleep(calculateClickDelay(min, max, target, deviation));
					Magic.cast(alchSpell.getSpell(), alchItem);
					return -1;
				}
			}
		}

		if (local.getInteracting() != null && !Dialog.canContinue())
		{
			return -1;
		}

		if (config.antifire() && (!Combat.isAntifired() && !Combat.isSuperAntifired()))
		{
			Item antifire = Inventory.getFirst(
					config.antifireType().getDose1(),
					config.antifireType().getDose2(),
					config.antifireType().getDose3(),
					config.antifireType().getDose4()
			);
			if (antifire != null)
			{
				Time.sleep(calculateClickDelay(min, max, target, deviation));
				antifire.interact("Drink");
				return -1;
			}
		}

		// Check if loot only mode is enabled
		boolean lootOnlyMode = config.lootOnlyMode();

		if (!lootOnlyMode && !isLooting)
		{
			// This block will execute if loot only mode is not enabled
			// Look for and attack NPCs
			List<String> mobs = Text.fromCSV(config.monster());
			NPC mob = Combat.getAttackableNPC(x -> x.getName() != null
					&& textMatches(mobs, x.getName()) && !x.isDead()
					&& x.getWorldLocation().distanceTo(center) < config.attackRange()
			);
			if (mob == null)
			{
				if (local.getWorldLocation().distanceTo(center) < 3 && !isLooting && !isBanking)
				{
					MessageUtils.addMessage("No attackable monsters in area");
					isFighting = false;
					return -1;
				}
				Time.sleep(calculateClickDelay(min, max, target, deviation));
				Movement.walkTo(center);
				return -4;
			}


			if (!Reachable.isInteractable(mob))
			{
				Time.sleep(calculateClickDelay(min, max, target, deviation));
				Movement.walkTo(mob.getWorldLocation());
				return -4;
			}
			Time.sleep(calculateClickDelay(min, max, target, deviation));

			mob.interact("Attack");
			isFighting = true;
			return -3;
		}
		return  -1;
	}

	@Subscribe
	public void onChatMessage(ChatMessage e)
	{
		String message = e.getMessage();
		if (message.contains("other players have dropped"))
		{
			var notOurs = TileItems.getAt(Players.getLocal().getWorldLocation(), x -> true);
			log.debug("{} are not our items", notOurs.stream().map(TileItem::getName).collect(Collectors.toList()));
			notOurItems.addAll(notOurs);
		}
		else if (config.disableAfterSlayerTask() && message.contains("You have completed your task!"))
		{
			SwingUtilities.invokeLater(() -> Plugins.stopPlugin(this));
		}
	}

	private boolean shouldNotLoot(TileItem item)
	{
		return textMatches(Text.fromCSV(config.dontLoot()), item.getName());
	}

	private boolean shouldLootUntradable(TileItem item)
	{
		return config.untradables()
				&& (!item.isTradable() || item.hasInventoryAction("Destroy"))
				&& item.getId() != ItemID.COINS_995;
	}

	private boolean shouldLootByValue(TileItem item)
	{
		return config.lootByValue()
				&& config.lootValue() > 0
				&& itemManager.getItemPrice(item.getId()) * item.getQuantity() > config.lootValue();
	}

	private boolean shouldLootByName(TileItem item)
	{
		return textMatches(Text.fromCSV(config.loots()), item.getName());
	}

	private boolean textMatches(List<String> itemNames, String itemName)
	{
		return itemNames.stream().anyMatch(name -> WildcardMatcher.matches(name, itemName));
	}

	private void setCenter(WorldPoint worldPoint)
	{
		configManager.setConfiguration(
				"hootfighter",
				"centerTile",
				String.format("%s %s %s", worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane())
		);
	}

	// reset Center to nothing
	private void resetCenter()
	{
		configManager.setConfiguration(
				"hootfighter",
				"centerTile",
				""
		);
	}

	protected WorldPoint getCenter()
	{
		String textValue = config.centerTile();
		if (textValue.isBlank() || !WORLD_POINT_PATTERN.matcher(textValue).matches())
		{
			return null;
		}

		List<Integer> split = Arrays.stream(textValue.split(" "))
				.map(Integer::parseInt)
				.collect(Collectors.toList());

		return new WorldPoint(split.get(0), split.get(1), split.get(2));
	}

	protected boolean canPick(TileItem tileItem)
	{
		return tileItem != null && tileItem.distanceTo(client.getLocalPlayer().getWorldLocation()) <= config.attackRange() && !Inventory.isFull();
	}
}
