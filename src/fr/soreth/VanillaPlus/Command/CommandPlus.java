package fr.soreth.VanillaPlus.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import fr.soreth.VanillaPlus.Node;
import fr.soreth.VanillaPlus.PlaceH;
import fr.soreth.VanillaPlus.IRequirement.Requirement;
import fr.soreth.VanillaPlus.Message.Message;
import fr.soreth.VanillaPlus.Message.MessageManager;
import fr.soreth.VanillaPlus.MComponent.MComponent;
import fr.soreth.VanillaPlus.Player.VPPlayer;
import fr.soreth.VanillaPlus.Player.VPSender;


public abstract class CommandPlus{
	public enum CommandResult { SUCCESS, CANCELED, CANCELED_OTHER, FAIL};
	private final Requirement requirement;
	private final String name;
	private final List<String>aliases;
	private final MComponent description, usage;
	protected final Message noRequirement;
	private final boolean takeRequirement;
	public CommandPlus(ConfigurationSection section, MessageManager manager){
		this(section, manager, section.getName());
	}
	public CommandPlus(ConfigurationSection section, MessageManager manager, String name){
		this.name = name;
		this.description		= manager.getComponentManager().get(section.getString(Node.DESCRIPTION.get()));
		this.usage				= manager.getComponentManager().get(section.getString(Node.USAGE.get()));
		this.requirement		= new Requirement(section.get(Node.REQUIREMENT.get()), manager.getComponentManager());
		this.takeRequirement	= section.getBoolean("TAKE_REQUIREMENT", true);
		if(section.contains(Node.NO_REQUIREMENT.get()))
			this.noRequirement = manager.get(section.getString(Node.NO_REQUIREMENT.get()));
		else
			this.noRequirement = null;
		List<String> aliases = section.getStringList(Node.ALIASES.get());
		this.aliases = new ArrayList<String>();
		if(aliases!=null){
			List<String>patchBukkit = new ArrayList<String>();
			patchBukkit.addAll(aliases);
			for(String s : patchBukkit)
				this.aliases.add(s.toLowerCase());
		}
	}
    /**
     * Get the description of this command.
     *
     * @return The description of this command.
     */
	public MComponent getDescription() {
		return description;
	}
    /**
     * Get the usage of this command.
     * @return The usage of this command.
     */
	public MComponent getUsage() {
		if(usage != null)
			usage.addReplacement(PlaceH.LABEL.get(), name);
		return usage; 
	}
    /**
     * Get the usage of this command.
     *
     * @return The usage of this command.
     */
	public void sendUsage(VPSender toSend, String label){
		if(usage == null)return;
		toSend.sendMessage(usage.addReplacement(PlaceH.LABEL.get(), label).getMessage());
	}
    /**
     * Get the aliases of this command.
     *
     * @return The aliases of this command.
     */
	public List<String> getAliases() {
		return aliases;
	}
    /**
     * Get the name of this command.
     *
     * @return The name of this command.
     */
	public String getName() {
		return name;
	}
    /**
     * Test if the player has the requirement.
     * Send noRequirement message if the player don't have
     * the requirement.
     *
     * @return true if player has requirement.
     */
	public boolean hasRequirement(VPSender sender){
		if(!(sender instanceof VPPlayer) || hasRequirementSilent(sender))
			return true;
		sendNoRequirement(sender);
		return false;
	}
    /**
     * Send noRequirement message.
     * If null send main noRequirement.
     *
     */
	public void sendNoRequirement(VPSender sender){
		if(noRequirement == null)
			CPManager.sendNoPerm(sender);
		else
	    	noRequirement.sendTo(sender);
	}
    /**
     * Test if the player has the requirement.
     * Send requirement message if the player don't have
     * the requirement.
     *
     * @return true if player has requirement.
     */
	public boolean hasRequirementSilent(VPSender vpSender){
		if(requirement == null)
			return true;
		return requirement.has(vpSender);
	}
    /**
     * Test if the given string is an alias.
     *
     * @return true if it is.
     */
	public boolean is(String name){
		if(aliases.contains(name))
			return true;
		if(this.name.equalsIgnoreCase(name))
			return true;
		return false;
	}
    /**
     * Take requirement if needed.
     *
     * @param sender player.
     */
	public void takeRequirement(VPSender sender) {
		if(takeRequirement && requirement != null && sender instanceof VPPlayer)
			requirement.take((VPPlayer) sender);
			
	}
	public abstract boolean onExecute(VPSender sender, String label, List<String> args);
	public abstract List<String> onTab(VPSender sender, String label, List<String> args);
}
