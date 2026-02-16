package fr.maitre.economy.hooks;

import org.bukkit.inventory.ItemStack;

public interface ItemHook {
    
    /**
     * Obtenir l'ID de l'item personnalisé
     */
    String getItemId(ItemStack item);
    
    /**
     * Obtenir un ItemStack à partir de son ID
     */
    ItemStack getItemStack(String itemId);
    
    /**
     * Vérifier si un item est un item personnalisé
     */
    boolean isCustomItem(ItemStack item);
    
    /**
     * Obtenir le nom du plugin (ItemsAdder ou Oraxen)
     */
    String getName();
}
