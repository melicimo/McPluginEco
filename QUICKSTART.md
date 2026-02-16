# 🚀 Guide de Démarrage Rapide - EconomyPlugin

## ⚡ Installation en 5 Minutes

### Étape 1: Installation
```bash
1. Télécharger EconomyPlugin.jar
2. Placer dans plugins/
3. Redémarrer le serveur
```

### Étape 2: Première Configuration
```bash
1. Éditer plugins/EconomyPlugin/config.yml
2. Changer la monnaie (€, $, etc.)
3. Définir le solde de départ
4. Sauvegarder et /eco reload
```

### Étape 3: Tester
```
/money                    - Voir votre solde
/pay <joueur> 100        - Envoyer de l'argent
/baltop                  - Voir le classement
```

---

## 🎨 Avec ItemsAdder (Recommandé)

### Installation ItemsAdder
1. Installer ItemsAdder 3.6.1+
2. Copier `examples/itemsadder-economy-items.yml` vers `plugins/ItemsAdder/data/items_packs/economy/`
3. Créer les textures dans `plugins/ItemsAdder/data/resource_pack/assets/custom/textures/item/`
4. Exécuter `/iazip`

### Configuration EconomyPlugin
```yaml
# config.yml
wallet-display:
  enabled: true
  wallet-icon: "itemsadder:custom:wallet"
  chat-image: ":wallet:"

withdraw:
  money-item: "itemsadder:custom:money_note"
```

### Ajouter des Items Économiques
```
1. /economyitems                                 - Ouvrir le GUI
2. Glisser un item ItemsAdder dans le GUI
3. Définir la valeur (ex: 100€)
4. Activer vendable/achetable
5. Cliquer sur "Valider"
```

---

## 🎯 Avec Oraxen (Alternative)

### Installation Oraxen
1. Installer Oraxen 1.171.0+
2. Copier `examples/oraxen-economy-items.yml` vers `plugins/Oraxen/items/`
3. Créer les textures dans `plugins/Oraxen/pack/textures/items/`
4. Exécuter `/oraxen reload all`

### Configuration EconomyPlugin
```yaml
# config.yml
wallet-display:
  enabled: true
  wallet-icon: "oraxen:wallet"

withdraw:
  money-item: "oraxen:money_note"
```

---

## 📦 Exemples de Configuration

### Économie Simple (Restaurant)
```yaml
settings:
  starting-balance: 500
  currency-symbol: "🍔"
  currency-name: "Burger"
  currency-name-plural: "Burgers"

economy-items:
  items:
    "itemsadder:food:hamburger":
      value: 10
      sellable: true
      buyable: true
      buy-price: 15
```

### Économie RP (Serveur Ville)
```yaml
settings:
  starting-balance: 5000
  currency-symbol: "$"
  currency-name: "Dollar"
  currency-name-plural: "Dollars"

economy-items:
  items:
    "itemsadder:drugs:weed":
      value: 500
      sellable: true
      buyable: false
    "itemsadder:work:diamond_ore":
      value: 1000
      sellable: true
      buyable: false
```

### Économie Fantasy
```yaml
settings:
  starting-balance: 1000
  currency-symbol: "⚜"
  currency-name: "Pièce d'or"
  currency-name-plural: "Pièces d'or"

economy-items:
  items:
    "oraxen:ruby":
      value: 500
      sellable: true
      buyable: true
      buy-price: 600
    "oraxen:sapphire":
      value: 1500
      sellable: true
      buyable: true
      buy-price: 1800
```

---

## 🎮 Scénarios d'Utilisation

### Scénario 1: Shop de Ressources
```
1. Créer des items de ressources dans ItemsAdder/Oraxen
2. Les ajouter dans le GUI (/economyitems)
3. Définir les prix de vente
4. Les joueurs minent et vendent leurs ressources
5. Utiliser l'argent pour acheter d'autres items
```

### Scénario 2: Système de Salaire
```
1. Donner un salaire quotidien: /eco give <joueur> 1000
2. Utiliser un plugin de jobs pour automatiser
3. Les joueurs gagnent de l'argent en travaillant
4. Ils peuvent payer d'autres joueurs avec /pay
```

### Scénario 3: Casino/Jeux
```
1. Créer des items de jeu (jetons)
2. Les joueurs achètent des jetons avec leur argent
3. Utiliser /withdraw pour retirer de l'argent physique
4. Créer des mini-jeux avec récompenses
```

---

## 💡 Astuces Avancées

### Créer un Image de Portefeuille Personnalisée

**ItemsAdder:**
```yaml
specific_properties:
  font:
    unicode: ":wallet:"
    scale_ratio: 2.0        # Taille de l'image
    y_position: 8           # Position verticale
```

**Dans config.yml:**
```yaml
wallet-display:
  chat-image: ":wallet:   "  # Espacer pour mieux voir
```

### Optimiser les Performances
```yaml
# Sauvegarder moins souvent si beaucoup de joueurs
# Dans le code, modifier la fréquence de sauvegarde automatique
```

### Multi-devises (Avancé)
```
1. Créer plusieurs types de pièces
2. Chaque pièce a sa propre valeur
3. Les joueurs peuvent échanger entre elles
```

---

## ❓ FAQ Rapide

**Q: Les joueurs commencent avec combien d'argent?**
A: Configurable dans `settings.starting-balance` (défaut: 1000€)

**Q: Comment ajouter une image de portefeuille?**
A: Utiliser ItemsAdder ou Oraxen + configurer `wallet-display.chat-image`

**Q: Les items custom ne fonctionnent pas?**
A: Vérifier que ItemsAdder/Oraxen est installé et à jour. Utiliser `/iazip` ou `/oraxen reload`

**Q: Comment reset l'économie d'un joueur?**
A: `/eco reset <joueur>` pour remettre au solde de départ

**Q: Peut-on avoir des valeurs négatives?**
A: Oui, configurez `settings.min-balance` à un nombre négatif

**Q: Les changements du GUI ne se sauvegardent pas?**
A: Cliquer sur le bouton "Sauvegarder" dans le GUI ou faire `/eco reload`

---

## 🔧 Dépannage Rapide

### Problème: Items custom non détectés
```
Solution:
1. Vérifier que ItemsAdder/Oraxen est chargé
2. Regarder les logs au démarrage du serveur
3. Tester avec /ia give ou /oraxen give
```

### Problème: Portefeuille sans image
```
Solution:
1. Vérifier que l'item existe dans ItemsAdder/Oraxen
2. Exécuter /iazip ou /oraxen reload
3. Vérifier le format dans config.yml
```

### Problème: Erreurs de compilation
```
Solution:
1. Utiliser Java 21
2. Vérifier que Maven est installé
3. Supprimer le dossier target/ et recompiler
```

---

## 📞 Support

Pour plus d'aide:
1. Consulter README.md complet
2. Vérifier les fichiers d'exemples dans examples/
3. Regarder les logs du serveur

---

**Bon jeu! 🎮**
