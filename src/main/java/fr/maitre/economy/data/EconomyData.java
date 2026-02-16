package fr.maitre.economy.data;

import java.util.UUID;

public class EconomyData {
    
    private final UUID uuid;
    private double balance;
    private long lastUpdated;
    
    public EconomyData(UUID uuid, double balance) {
        this.uuid = uuid;
        this.balance = balance;
        this.lastUpdated = System.currentTimeMillis();
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance) {
        this.balance = balance;
        this.lastUpdated = System.currentTimeMillis();
    }
    
    public void addBalance(double amount) {
        this.balance += amount;
        this.lastUpdated = System.currentTimeMillis();
    }
    
    public void removeBalance(double amount) {
        this.balance -= amount;
        this.lastUpdated = System.currentTimeMillis();
    }
    
    public long getLastUpdated() {
        return lastUpdated;
    }
}
