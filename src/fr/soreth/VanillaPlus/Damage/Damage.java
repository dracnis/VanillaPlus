package fr.soreth.VanillaPlus.Damage;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public abstract class Damage{
	protected double amount = 0;
	public Damage(ConfigurationSection section) {
		if(section == null)return;
		amount = section.getDouble(Node.AMOUNT.get(),0);
		if(amount <= 0)
			Error.INVALID.add();
	}
	public abstract boolean damage(VPPlayer player);
	public double getAmount() {
		return this.amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
}
