package fr.soreth.VanillaPlus.Heal;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.Error;
import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.Player.VPPlayer;

public class Heal{
	protected double amount = 0;
	public Heal(ConfigurationSection section) {
		if(section == null)return;
		amount = section.getDouble(Node.AMOUNT.get(),0);
		if(amount <= 0)
			Error.INVALID.add();
	}
	public void heal(VPPlayer player) {
		double max = player.getPlayer().getMaxHealth() - player.getPlayer().getHealth();
		player.getPlayer().setHealth(player.getPlayer().getHealth() +  (max < amount ? max : amount));
	}
	public double getAmount() {
		return this.amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
}
