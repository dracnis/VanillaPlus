package fr.soreth.VanillaPlus.Event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.soreth.VanillaPlus.Player.Currency;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public class VPPCurrencyChangeEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final VPPlayer player;
    private final Currency currency;
    private final double amount;
    public VPPCurrencyChangeEvent(VPPlayer player, Currency currency, double amount) {
    	this.player = player;
    	this.currency = currency;
    	this.amount = amount;
    }
    public VPPlayer getPlayer(){
    	return player;
    }
    public Currency getCurency(){
    	return currency;
    }
    public double getAmount(){
    	return amount;
    }
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
