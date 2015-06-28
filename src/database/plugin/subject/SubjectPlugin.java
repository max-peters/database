package database.plugin.subject;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.Plugin;
import database.main.Store;
import database.main.Terminal;

public class SubjectPlugin extends Plugin {
	public SubjectPlugin(Store store, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) {
		super(store, terminal, graphicalUserInterface, administration, "subject");
		this.instanceList = new SubjectList();
	}

	protected String[][] getCreateInformation() {
		String[][] createInformation = { { "name", "[A-ZÖÄÜ].*" }, { "tag", "[a-zöäü].*" } };
		return createInformation;
	}

	protected String[][] getShowInformation() {
		String[][] showInformation = { { "subject", "(average|" + ((SubjectList) instanceList).getTagsAsRegex() + ")" } };
		return showInformation;
	}

	protected String[][] getChangeInformation() {
		String[][] changeInformation = { { "subject", ((SubjectList) instanceList).getTagsAsRegex() }, { "grade", "[0-9]{1,13}(\\.[0-9]*)?" }, { "maxgrade", "[0-9]{1,13}(\\.[0-9]*)?" } };
		return changeInformation;
	}

	protected void change(String[] parameter) {
		Subject toChange = ((SubjectList) instanceList).getSubject(parameter[0]);
		toChange.calcGrade(Double.parseDouble(parameter[1]), Double.parseDouble(parameter[2]));
	}
}
