package database.plugin.outputHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import database.plugin.Command;
import database.plugin.IOutputHandler;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.database.SQLStatements;
import database.services.stringUtility.Builder;
import database.services.stringUtility.StringUtility;

public class SubjectOutputHandler implements IOutputHandler {
	@Command(tag = "average") public String getAverage() throws SQLException {
		IDatabase database = ServiceRegistry.Instance().get(IDatabase.class);
		StringUtility stringUtility = new StringUtility();
		ResultSet resultSet;
		double average = 0;
		resultSet = database.execute(SQLStatements.SUBJECT_SELECT_AVERAGE);
		if (resultSet.next()) {
			average = resultSet.getDouble("percent");
		}
		return "average percentage : " + stringUtility.formatDouble(average, 2) + "%";
	}

	@Override public String getInitialOutput() throws SQLException {
		return outputAll();
	}

	@Command(tag = "all") public String outputAll() throws SQLException {
		IDatabase database = ServiceRegistry.Instance().get(IDatabase.class);
		StringUtility stringUtility = new StringUtility();
		Builder builder = new Builder();
		ResultSet resultSet;
		int nameLength = 0;
		int scoreLength = 0;
		int maxPointsLength = 0;
		int percentLength = 0;
		int counterLength = 0;
		int gab = 4;
		resultSet = database.execute(SQLStatements.SUBJECT_SELECT_ALL);
		while (resultSet.next()) {
			int currentNameLength = resultSet.getString("name").length();
			int currentScoreLength = stringUtility.formatDouble(resultSet.getDouble("score"), 1).length();
			int currentMaxPointsLength = stringUtility.formatDouble(resultSet.getDouble("maxPoints"), 1).length();
			int currentPercentLength = stringUtility.formatDouble(resultSet.getDouble("percent"), 2).length();
			int currentCounterLenght = Integer.toString(resultSet.getInt("counter")).length();
			nameLength = currentNameLength > nameLength ? currentNameLength : nameLength;
			scoreLength = currentScoreLength > scoreLength ? currentScoreLength : scoreLength;
			maxPointsLength = currentMaxPointsLength > maxPointsLength ? currentMaxPointsLength : maxPointsLength;
			percentLength = currentPercentLength > percentLength ? currentPercentLength : percentLength;
			counterLength = currentCounterLenght > counterLength ? currentCounterLenght : counterLength;
		}
		resultSet.beforeFirst();
		while (resultSet.next()) {
			builder.append(" " + stringUtility.postIncrementTo(resultSet.getString("name"), nameLength + gab, ' '));
			builder.append("[" + stringUtility.preIncrementTo(stringUtility.formatDouble(resultSet.getDouble("score"), 1), scoreLength, ' ') + "/");
			builder.append(stringUtility.preIncrementTo(stringUtility.formatDouble(resultSet.getDouble("maxPoints"), 1), maxPointsLength, ' ') + " - ");
			builder.append(stringUtility.preIncrementTo(stringUtility.formatDouble(resultSet.getDouble("percent"), 2), percentLength, ' ') + "%] in [");
			builder.append(stringUtility.preIncrementTo(Integer.toString(resultSet.getInt("counter")), counterLength, ' ').replace(' ', '0') + "] ");
			if (resultSet.getInt("counter") == 1) {
				builder.append("Blatt");
			}
			else {
				builder.append("Blätter");
			}
			builder.newLine();
		}
		if (builder.isEmpty()) {
			builder.append("keine Blätter");
		}
		return builder.build();
	}
}