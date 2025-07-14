package me.Ford.modernManhunt.Records;

import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public record BarteringDrops(int weight, Supplier<ItemStack> generator) {}