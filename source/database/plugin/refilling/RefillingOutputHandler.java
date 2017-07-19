package database.plugin.refilling;

import java.sql.ResultSet;
import java.sql.SQLException;

import database.plugin.Command;
import database.plugin.IOutputHandler;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.database.SQLStatements;
import database.services.stringUtility.Builder;
import database.services.stringUtility.StringUtility;

public class RefillingOutputHandler implements IOutputHandler {
	@Override
	public String getInitialOutput() throws SQLException {
		IDatabase database = ServiceRegistry.Instance().get(IDatabase.class);
		StringUtility stringUtility = new StringUtility();
		Builder builder = new Builder();
		ResultSet resultSet = database.execute(SQLStatements.REFILLING_SELECT_INITIAL_OUTPUT);
		if (resultSet.next()) {
			builder.append(" [" + resultSet.getInt("count") + "] ");
			builder.append(
					"distance: " + "[" + stringUtility.formatDouble(resultSet.getDouble("distance"), 1) + " km" + "] ");
			builder.append("refuelAmount: " + "[" + stringUtility.formatDouble(resultSet.getDouble("refuelAmount"), 1)
					+ " l" + "] ");
			builder.append("averageConsumption: " + "[" + stringUtility.formatDouble(resultSet.getDouble("average"), 1)
					+ " l/km" + "]");
		}
		resultSet.close();
		return builder.build();
	}

	@Command(tag = "all")
	public String outputAll() throws SQLException {
		IDatabase database = ServiceRegistry.Instance().get(IDatabase.class);
		int maxDistanceLength = 0;
		int maxRefuelAmountLength = 0;
		int maxConsumptionLength = 0;
		StringUtility stringUtility = new StringUtility();
		Builder builder = new Builder();
		ResultSet resultSet;
		resultSet = database.execute(SQLStatements.REFILLING_SELECT_ALL);
		while (resultSet.next()) {
			int currentDistanceLength = stringUtility.formatDouble(resultSet.getDouble("distance"), 1).length();
			int currentRefuelAmountLength = stringUtility.formatDouble(resultSet.getDouble("refuelAmount"), 1).length();
			int currentConsumptionLength = stringUtility.formatDouble(resultSet.getDouble("average"), 1).length();
			if (currentDistanceLength > maxDistanceLength) {
				maxDistanceLength = currentDistanceLength;
			}
			if (currentRefuelAmountLength > maxRefuelAmountLength) {
				maxRefuelAmountLength = currentRefuelAmountLength;
			}
			if (currentConsumptionLength > maxConsumptionLength) {
				maxConsumptionLength = currentConsumptionLength;
			}
		}
		resultSet = database.execute(SQLStatements.REFILLING_SELECT_ALL);
		while (resultSet.next()) {
			String incrementedDistance = stringUtility.preIncrementTo(
					stringUtility.formatDouble(resultSet.getDouble("distance"), 1), maxDistanceLength, ' ');
			String incrementedRefuelAmount = stringUtility.preIncrementTo(
					stringUtility.formatDouble(resultSet.getDouble("refuelAmount"), 1), maxRefuelAmountLength, ' ');
			String incrementedConsumption = stringUtility.preIncrementTo(
					stringUtility.formatDouble(resultSet.getDouble("average"), 1), maxConsumptionLength, ' ');
			builder.append(" \u25AA distance: [" + incrementedDistance + " km] ");
			builder.append("refuelAmount: [" + incrementedRefuelAmount + " l] ");
			builder.append("averageConsumption: [" + incrementedConsumption + " l/km]");
			builder.newLine();
		}
		resultSet.close();
		return builder.build();
	}
}