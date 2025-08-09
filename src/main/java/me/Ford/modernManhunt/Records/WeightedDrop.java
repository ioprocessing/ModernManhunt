package me.Ford.modernManhunt.Records;

import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

public interface WeightedDrop {
    int weight();
    Supplier<ItemStack> generator();
}
