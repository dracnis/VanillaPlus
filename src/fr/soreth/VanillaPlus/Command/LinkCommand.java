package fr.soreth.VanillaPlus.Command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.soreth.VanillaPlus.VanillaPlusCore;

public class LinkCommand extends Command{

	CommandPlus commandPlus;
	protected LinkCommand(String name, String description, String usageMessage,
			List<String> aliases, CommandPlus command) {
		super(name, description, usageMessage, aliases);
		this.commandPlus = command;
	}
	@Override
	public boolean testPermissionSilent(CommandSender target) {
		if(commandPlus == null || !(target instanceof Player))
			return super.testPermissionSilent(target);
		return commandPlus.hasRequirementSilent(VanillaPlusCore.getPlayerManager().getPlayer((Player) target));
	}
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		return VanillaPlusCore.getCommandManager().execute(sender, label, args);
	}
	@Override
	public List<String> tabComplete(CommandSender sender, String label,
			String[] args) throws IllegalArgumentException {
		return VanillaPlusCore.getCommandManager().tabComplete(sender, label, args);
	}

}
