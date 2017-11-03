package fr.soreth.VanillaPlus;

/**
 * Enumeration of used placeholder.
 *
 * @author Soreth.
 */
public enum PlaceH {
	COMMAND			("command"),
	CURRENCY		("currency"),
	DESCRIPTION		("description"),
	MESSAGE			("message"),
	HELP			("help"),
	LABEL			("label"),
	NAME			("name"),
	RECEIVER		("receiver"),
	SENDER			("sender");
	String node;
	private PlaceH(String node){
		this.node = node;
	}
    /**
     * Get the placeHolder.
     *
     * @return PlaceHolder.
     */
	public String get() {
		return node;
	}
}
