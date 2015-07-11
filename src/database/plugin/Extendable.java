package database.plugin;

import java.util.ArrayList;

import database.plugin.event.EventExtention;

public abstract interface Extendable {
	public ArrayList<EventExtention>	extentionList	= new ArrayList<EventExtention>();

	public abstract void initialise();

	public default ArrayList<EventExtention> getExtentions() {
		return extentionList;
	}
}
