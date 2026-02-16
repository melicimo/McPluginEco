package fr.maitre.economy.hooks;

import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.inventory.ItemStack;

public class OraxenHook implements ItemHook {
    
    @Override
    public String getItemId(ItemStack item) {
        if (item == null) return null;
        return OraxenItems.getIdByItem(item);
    }
    
    @Override
    public ItemStack getItemStack(String itemId) {
        return OraxenItems.getItemById(itemId).build();
    }
    
    @Override
    public boolean isCustomItem(ItemStack item) {
        return OraxenItems.exists(item);
    }
    
    @Override
    public String getName() {
        return "Oraxen";
    }
}
