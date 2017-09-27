package fr.soreth.VanillaPlus;

/**
 * Enumeration of used error messages.
 *
 * @author Soreth.
 */
public enum Error {
	INVALID						("§cInvalid §8!"),
	LOGGER						("§8============ §c/§6!§c\\ ERROR /§6!§c\\ §8============"),
	MISSING						("§cMissing !"),
	MISSING_NODE				("§8Missing : §c"),
	SQL							("§cSQL Error See Console §8!");
	String error;
	private Error(String error){
		this.error = error;
	}
    /**
     * Get the error's message.
     *
     * @return Error's message.
     */
	public String getMessage() {
		return error;
	}
    /**
     * Add error to logger.
     *
     * @return The error as displayed in the logger.
     */
	public String add() {
		return ErrorLogger.addError(getMessage());
	}
    /**
     * Add error to logger with info.
     *
     * @param info The info to add with error.
     * @return The error as displayed in the logger.
     */
	public String add(String info) {
		return ErrorLogger.addError(getMessage()+info);
	}
}
