package org.ioprocessing.modernManhunt.commands.subcommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ioprocessing.modernManhunt.commands.style.SystemMessage;
import org.ioprocessing.modernManhunt.items.CustomItems;

import java.util.List;
import java.util.Map;

public class GiveCommand {

    /**
     * Handle the /mm give <selector> <item> command (default amount = 1)
     * Gives players custom items belonging to the ModernManhunt plugin
     */
    public int handleNoArg(CommandContext<CommandSourceStack> context) {
        return handleGiveInternal(context, 1);
    }

    /**
     * Handle the /mm give <selector> <item> <count> command
     * Gives players custom items belonging to the ModernManhunt plugin
     */
    public int handleWithCount(CommandContext<CommandSourceStack> context) {
        int amount = IntegerArgumentType.getInteger(context, "count");
        return handleGiveInternal(context, amount);
    }

    /**
     * Internal method to handle giving items to players
     */
    private int handleGiveInternal(CommandContext<CommandSourceStack> context, int amount) {
        CommandSourceStack source = context.getSource();
        try {
            List<Player> players = resolveSelector(context);
            String itemId = StringArgumentType.getString(context, "item");

            // Check if item exists
            if (!CustomItems.CUSTOM_ITEM_MAP.containsKey(itemId)) {
                context.getSource().getSender().sendMessage(
                        SystemMessage.error(String.format("Item {%s} not found in the ModernManhunt item list", itemId)));
                return 0;
            }

            if (players.isEmpty()) {
                source.getSender().sendMessage(SystemMessage.error("No players matched that selector"));
                return 0;
            }

            ItemStack templateItem = CustomItems.CUSTOM_ITEM_MAP.get(itemId);
            int maxStackSize = templateItem.getMaxStackSize();
            int maxAmount = maxStackSize * 100;

            // Fail the command if amount exceeds the maximum amount (based on vanilla command)
            if (amount > maxAmount) {
                context.getSource().getSender().sendMessage(
                        SystemMessage.error(String.format("Can't give more than {%d} of ", maxAmount))
                        .append(templateItem.displayName().color(NamedTextColor.RED)));
                return 0;
            }

            int successCount = 0;
            for (Player target : players) {
                int remaining = amount;

                while (remaining > 0) {
                    int batchSize = Math.min(remaining, maxStackSize);
                    ItemStack givenItem = templateItem.clone();
                    givenItem.setAmount(batchSize);

                    Map<Integer, ItemStack> excess = target.getInventory().addItem(givenItem);
                    excess.values().forEach(target::dropItem);

                    int addedInBatch = batchSize - excess.values().stream()
                            .mapToInt(ItemStack::getAmount)
                            .sum();

                    if (addedInBatch > 0) {
                        Sound pickupSound = Sound.sound(org.bukkit.Sound.ENTITY_ITEM_PICKUP, Sound.Source.PLAYER, 0.2f, (float) (0.8 + Math.random() * 1.2));
                        target.playSound(pickupSound);
                    }

                    remaining -= batchSize;
                }

                successCount++;
            }

            // Send success message
            if (successCount == 1) {
                context.getSource().getSender().sendMessage(Component.text().append(
                        SystemMessage.success("Gave " + amount + " "))
                        .append(templateItem.displayName().color(NamedTextColor.GREEN))
                        .append(Component.text(" to ", NamedTextColor.GRAY))
                        .append(Component.text(players.getFirst().getName(), NamedTextColor.GREEN)));
            } else {
                context.getSource().getSender().sendMessage(Component.text().append(
                        SystemMessage.success("Gave " + amount + " "))
                        .append(templateItem.displayName().color(NamedTextColor.GREEN))
                        .append(Component.text(" to ", NamedTextColor.GRAY))
                        .append(Component.text(successCount, NamedTextColor.GREEN))
                        .append(Component.text(" players", NamedTextColor.GRAY)));
            }

            return successCount;
        } catch (Exception e) {
            context.getSource().getSender().sendMessage(
                    SystemMessage.error(String.format("Failed to give item: {%s}", e.getMessage())));
            return 0;
        }
    }

    /**
     * Get target players from selector or player name
     */
    private List<Player> resolveSelector(CommandContext<CommandSourceStack> context)
            throws CommandSyntaxException {

        PlayerSelectorArgumentResolver resolver =
                context.getArgument("player", PlayerSelectorArgumentResolver.class);

        return resolver.resolve(context.getSource());
    }
}
