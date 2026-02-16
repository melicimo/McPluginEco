package fr.maitre.economy.hooks;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderHook implements ItemHook {
    
    @Override
    public String getItemId(ItemStack item) {
        if (item == null) return null;
        CustomStack customStack = CustomStack.byItemStack(item);
        return customStack != null ? customStack.getNamespacedID() : null;
    }
    
    @Override
    public ItemStack getItemStack(String itemId) {
        CustomStack customStack = CustomStack.getInstance(itemId);
        return customStack != null ? customStack.getItemStack() : null;
    }
    
    @Override
    public boolean isCustomItem(ItemStack item) {
        return CustomStack.byItemStack(item) != null;
    }
    
    @Override
    public String getName() {
        return "ItemsAdder";
    }
}
