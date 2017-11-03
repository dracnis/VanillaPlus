package fr.soreth.VanillaPlus;

/**
 * Enumeration of used node for configuration files.
 *
 * @author Soreth.
 */
public enum Node {
	ABSORPTION		,	ACHIEVEMENT		,	ACTION			,	ALIAS			,	ALIASES			,	ALREADY			,	AMBIANT			,	AMOUNT			,	
	BASE			,
	CHANNEL			,	CLOSE			,	COMMAND			,	CURRENCY		,
	DEFAULT			,	DATA			,	DELAY			,	DESCRIPTION		,	DISPLAY			,	DURATION		,
	EFFECT			,	ENCHANT			,
	FAIL			,	FILL			,	FOOD			,	FORCE			,	FORMAT			,
	ID				,	ICON			,	ITEM			,
	JOIN			,
	LANG			,	LEAVE			,	LEVEL			,	LOCATION		,	LORE_PATH		,
	MATERIAL		,	MENU			,	MESSAGE			,	MESSAGE_FROM	,	MESSAGE_TO		,	MOVE			,
	NAME			,	NAME_PATH		,	NODE			,	NO_REQUIREMENT	,	NO_VIEW_ICON	,
	OPEN			,	OTHER_REQUIREMENT,	OWNED			,
	PARTICLE		,	PLAYER			,	PREFIX,
	RANDOM			,	REMOVE			,	REQUIREMENT		,	REWARD			,
	SATURATION		,	SELF			,	SET				,	SETTINGS		,	SUCCESS			,	SUFFIX			,	SWITCH			,
	TITLE			,	TYPE			,
	UNOWNED			,	USAGE			,	USE				,
	VALUE			,	VIEW_REQUIREMENT,	VOID			,
	WORLD			;

    /**
     * Get the node's name.
     *
     * @return Name.
     */
	public String get() {
		return this.name();
	}
    /**
     * Get the node's name list.
     *
     * @return Name + _LIST.
     */
	public String getList() {
		return get()+"_LIST";
	}
	public String getOther(){
		return get()+"_OTHER";
	}
}
