package database.plugin.subject;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Store;
import database.main.Terminal;
import database.plugin.Plugin;

public class SubjectPlugin extends Plugin {
	public SubjectPlugin(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(store, terminal, graphicalUserInterface, administration, "subject");
		this.instanceList = new SubjectList();
	}

	public String[][] getCreateInformation() {
		String[][] createInformation = { { "name", "[A-ZÖÄÜ].*" }, { "tag", "[a-zöäüß]*" } };
		return createInformation;
	}

	public String[][] getShowInformation() {
		String[][] showInformation = { { "subject", "(average|" + ((SubjectList) instanceList).getTagsAsRegex() + ")" } };
		return showInformation;
	}

	public String[][] getChangeInformation() {
		String[][] changeInformation = { { "subject", ((SubjectList) instanceList).getTagsAsRegex() }, { "grade", "[0-9]{1,13}(\\.[0-9]*)?" }, { "maxgrade", "[0-9]{1,13}(\\.[0-9]*)?" } };
		return changeInformation;
	}
}
