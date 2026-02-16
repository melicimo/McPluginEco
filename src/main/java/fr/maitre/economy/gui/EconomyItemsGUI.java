package fr.maitre.economy.gui;

import fr.maitre.economy.EconomyPlugin;
import fr.maitre.economy.managers.ItemValueManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class EconomyItemsGUI implements Listener {
    
    private final EconomyPlugin plugin;
    private final Map<UUID, GUISession> sessions;
    
    public EconomyItemsGUI(EconomyPlugin plugin) {
        this.plugin = plugin;
        this.sessions = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    public void open(Player player) {
        String title = plugin.colorize(plugin.getConfig().getString("gui.title", "&6&lGestion Items Économiques"));
        int size = plugin.getConfig().getInt("gui.size", 54);
        
        Inventory inventory = Bukkit.createInventory(null, size, title);
        
        // Remplir avec des items de décoration
        fillBorders(inventory, size);
        
        // Ajouter les boutons d'action
        addActionButtons(inventory, size);
        
        // Afficher les items économiques existants
        displayEconomyItems(inventory, player);
        
        // Créer une session
        GUISession session = new GUISession(player, inventory);
        sessions.put(player.getUniqueId(), session);
        
        player.openInventory(inventory);
    }
    
    private void fillBorders(Inventory inv, int size) {
        String fillerMaterial = plugin.getConfig().getString("gui.filler-item", "BLACK_STAINED_GLASS_PANE");
        ItemStack filler = createItem(Material.valueOf(fillerMaterial), " ", new ArrayList<>());
        
        // Remplir les bordures
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, filler);
        }
        for (int i = size - 9; i < size; i++) {
            inv.setItem(i, filler);
        }
        for (int i = 9; i < size - 9; i += 9) {
            inv.setItem(i, filler);
            inv.setItem(i + 8, filler);
        }
    }
    
    private void addActionButtons(Inventory inv, int size) {
        // Bouton "Ajouter un item"
        ItemStack addButton = createItem(Material.EMERALD, "&a&lAjouter un Item", Arrays.asList(
            "",
            "&7Glissez un item personnalisé",
            "&7dans un slot vide pour",
            "&7définir sa valeur économique.",
            "",
            "&eItems supportés:",
            "&7- ItemsAdder",
            "&7- Oraxen"
        ));
        inv.setItem(size - 5, addButton);
        
        // Bouton "Sauvegarder"
        ItemStack saveButton = createItem(Material.WRITABLE_BOOK, "&e&lSauvegarder", Arrays.asList(
            "",
            "&7Sauvegarder toutes les",
            "&7modifications effectuées."
        ));
        inv.setItem(size - 4, saveButton);
        
        // Bouton "Actualiser"
        ItemStack refreshButton = createItem(Material.ARROW, "&b&lActualiser", Arrays.asList(
            "",
            "&7Actualiser la liste des",
            "&7items économiques."
        ));
        inv.setItem(size - 6, refreshButton);
    }
    
    private void displayEconomyItems(Inventory inv, Player player) {
        Map<String, ItemValueManager.ItemValue> items = plugin.getItemValueManager().getAllItemValues();
        
        int slot = 10;
        for (Map.Entry<String, ItemValueManager.ItemValue> entry : items.entrySet()) {
            if (slot >= inv.getSize() - 18) break; // Éviter les bordures
            if ((slot + 1) % 9 == 0 || slot % 9 == 0) {
                slot += 2; // Sauter les bordures
            }
            
            String itemId = entry.getKey();
            ItemValueManager.ItemValue value = entry.getValue();
            
            // Essayer de créer l'item
            ItemStack item = null;
            if (plugin.getItemHook() != null) {
                item = plugin.getItemHook().getItemStack(itemId);
            }
            
            if (item == null) {
                item = new ItemStack(Material.BARRIER);
            }
            
            // Ajouter les informations
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
                lore.add("");
                lore.add(plugin.colorize("&6&lVALEUR ÉCONOMIQUE"));
                lore.add(plugin.colorize("&7ID: &f" + itemId));
                lore.add(plugin.colorize("&7Valeur: &a" + plugin.formatMoney(value.getValue())));
                lore.add(plugin.colorize("&7Vendable: " + (value.isSellable() ? "&a✓" : "&c✗")));
                lore.add(plugin.colorize("&7Achetable: " + (value.isBuyable() ? "&a✓" : "&c✗")));
                if (value.isBuyable()) {
                    lore.add(plugin.colorize("&7Prix achat: &e" + plugin.formatMoney(value.getBuyPrice())));
                }
                lore.add("");
                lore.add(plugin.colorize("&eClick: &fModifier"));
                lore.add(plugin.colorize("&eShift+Click: &fSupprimer"));
                
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            
            inv.setItem(slot, item);
            slot++;
        }
    }
    
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        
        if (!sessions.containsKey(player.getUniqueId())) return;
        
        GUISession session = sessions.get(player.getUniqueId());
        if (!event.getInventory().equals(session.getInventory())) return;
        
        // Empêcher de prendre les items de décoration
        if (event.getCurrentItem() != null) {
            Material type = event.getCurrentItem().getType();
            String fillerMaterial = plugin.getConfig().getString("gui.filler-item", "BLACK_STAINED_GLASS_PANE");
            
            if (type == Material.valueOf(fillerMaterial) || 
                type == Material.EMERALD || 
                type == Material.WRITABLE_BOOK ||
                type == Material.ARROW) {
                event.setCancelled(true);
                
                // Gérer les boutons
                if (type == Material.WRITABLE_BOOK) {
                    // Sauvegarder
                    plugin.getItemValueManager().saveItemValues();
                    player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                        "&aModifications sauvegardées!"));
                    player.closeInventory();
                    return;
                }
                
                if (type == Material.ARROW) {
                    // Actualiser
                    plugin.getItemValueManager().loadItemValues();
                    open(player);
                    return;
                }
                
                return;
            }
        }
        
        // Click sur un item économique existant
        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            String itemId = plugin.getItemValueManager().getItemId(event.getCurrentItem());
            
            if (itemId != null && plugin.getItemValueManager().hasValue(itemId)) {
                event.setCancelled(true);
                
                if (event.isShiftClick()) {
                    // Supprimer
                    plugin.getItemValueManager().removeItemValue(itemId);
                    player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                        "&cItem retiré de la liste économique."));
                    open(player);
                } else {
                    // Modifier
                    player.closeInventory();
                    new ItemValueInputGUI(plugin, player, itemId).start();
                }
                return;
            }
        }
        
        // Placer un nouvel item
        if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
            ItemStack cursor = event.getCursor();
            
            if (plugin.getItemHook() != null && plugin.getItemHook().isCustomItem(cursor)) {
                event.setCancelled(true);
                
                String itemId = plugin.getItemHook().getItemId(cursor);
                if (itemId != null) {
                    player.closeInventory();
                    new ItemValueInputGUI(plugin, player, itemId).start();
                } else {
                    player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                        plugin.getConfig().getString("messages.item-not-custom")));
                }
            } else {
                event.setCancelled(true);
                player.sendMessage(plugin.colorize(plugin.getConfig().getString("messages.prefix") + 
                    plugin.getConfig().getString("messages.item-not-custom")));
            }
        }
    }
    
    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        
        if (sessions.containsKey(player.getUniqueId())) {
            GUISession session = sessions.get(player.getUniqueId());
            if (event.getInventory().equals(session.getInventory())) {
                sessions.remove(player.getUniqueId());
            }
        }
    }
    
    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.colorize(name));
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore.add(plugin.colorize(line));
            }
            meta.setLore(coloredLore);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    private static class GUISession {
        private final Player player;
        private final Inventory inventory;
        
        public GUISession(Player player, Inventory inventory) {
            this.player = player;
            this.inventory = inventory;
        }
        
        public Player getPlayer() {
            return player;
        }
        
        public Inventory getInventory() {
            return inventory;
        }
    }
}
