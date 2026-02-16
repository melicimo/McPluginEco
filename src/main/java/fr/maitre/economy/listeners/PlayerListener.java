package fr.maitre.economy.listeners;

import fr.maitre.economy.EconomyPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {
    
    private final EconomyPlugin plugin;
    
    public PlayerListener(EconomyPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Créer le compte si nécessaire
        plugin.getDataManager().getData(player.getUniqueId());
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // Sauvegarder périodiquement
        plugin.getDataManager().saveData();
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        // Gérer l'encaissement des billets de banque
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) {
            return;
        }
        
        if (!item.getItemMeta().hasDisplayName()) {
            return;
        }
        
        String displayName = item.getItemMeta().getDisplayName();
        
        // Vérifier si c'est un billet de banque
        if (displayName.contains(plugin.getConfig().getString("settings.currency-symbol", "€"))) {
            // Extraire le montant du nom
            String moneyStr = displayName.replaceAll("[^0-9.]", "");
            
            try {
                double amount = Double.parseDouble(moneyStr);
                
                // Ajouter l'argent au joueur
                plugin.getEconomyManager().addBalance(event.getPlayer(), amount);
                
                // Retirer l'item
                if (item.getAmount() > 1) {
                    item.setAmount(item.getAmount() - 1);
                } else {
                    event.getPlayer().getInventory().setItemInMainHand(null);
                }
                
                event.getPlayer().sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                    "&aVous avez encaissé " + plugin.formatMoney(amount) + "!"));
                
                event.setCancelled(true);
            } catch (NumberFormatException ignored) {
            }
        }
    }
}
