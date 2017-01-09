package database.plugin.expense;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.main.UserCancelException;
import database.main.userInterface.ITerminal;
import database.main.userInterface.StringUtility;
import database.plugin.Command;
import database.plugin.OutputFormatter;
import database.services.database.IDatabase;
import database.services.stringUtility.Builder;

public class ExpenseOutputFormatter implements OutputFormatter<Expense> {
	@Override public String getInitialOutput(Iterable<Expense> iterable) {
		return "not implemented";
	}

	@Command(tag = "average") public String outputAverage(IDatabase database) throws SQLException {
		StringUtility stringUtility = new StringUtility();
		Builder builder = new Builder();
		ResultSet resultSet;
		resultSet = database.execute(ExpenseSQLStatements.SHOW_AVERAGE);
		if (resultSet.next()) {
			double dayAverage = resultSet.getDouble("dayaverage");
			double monthAverage = resultSet.getDouble("monthaverage");
			String yearAverage = stringUtility.formatDouble(((dayAverage * 365.25) + (monthAverage * 12)) / 2, 2) + "€";
			builder.append(" average value per");
			builder.newLine();
			builder.append("  - day   : " + stringUtility.preIncrementTo(stringUtility.formatDouble(dayAverage, 2) + "€", yearAverage.length(), ' '));
			builder.newLine();
			builder.append("  - month : " + stringUtility.preIncrementTo(stringUtility.formatDouble(monthAverage, 2) + "€", yearAverage.length(), ' '));
			builder.newLine();
			builder.append("  - year  : " + yearAverage);
			builder.newLine();
		}
		resultSet.close();
		return builder.build();
	}

	@Command(tag = "category") public String outputCategory(IDatabase database) throws SQLException {
		int maxCategoryLength = 0;
		int maxSumLength = 0;
		int maxPercentageLength = 0;
		int gap = 5;
		StringUtility stringUtility = new StringUtility();
		Builder builder = new Builder();
		ResultSet resultSet;
		resultSet = database.execute(ExpenseSQLStatements.SHOW_CATEGORY_LENGTH);
		if (resultSet.next()) {
			maxCategoryLength = resultSet.getInt("categorylength");
			maxSumLength = resultSet.getInt("sumlength");
			maxPercentageLength = resultSet.getInt("percentagelength");
		}
		resultSet = database.execute(ExpenseSQLStatements.SHOW_CATEGORY);
		while (resultSet.next()) {
			builder.append(" - " + stringUtility.postIncrementTo(resultSet.getString("category"), maxCategoryLength + gap, ' '));
			builder.append(stringUtility.preIncrementTo(stringUtility.formatDouble(resultSet.getDouble("sum"), 2) + "€", maxSumLength + gap, ' '));
			builder.append(stringUtility.preIncrementTo(stringUtility.formatDouble(resultSet.getDouble("percentage"), 2) + "%", maxPercentageLength + gap, ' '));
			builder.newLine();
		}
		for (int i = 0; i < builder.getLongestLine(); i++) {
			builder.append("-");
		}
		builder.newLine();
		resultSet = database.execute(ExpenseSQLStatements.SHOW_TOTAL);
		if (resultSet.next()) {
			int length = 3 + maxCategoryLength + maxSumLength + 2 * gap;
			builder.append(stringUtility.preIncrementTo(stringUtility.formatDouble(resultSet.getDouble("total"), 2) + "€", length, ' '));
		}
		resultSet.close();
		return builder.build();
	}

	@Command(tag = "months") public String outputMonth(IDatabase database) throws SQLException {
		StringUtility stringUtility = new StringUtility();
		ResultSet resultSet;
		String month = "";
		Map<String, Integer> map = new HashMap<>();
		List<List<String>> returnList = new ArrayList<>();
		List<String> list = null;
		resultSet = database.execute(ExpenseSQLStatements.SHOW_MONTHS);
		while (resultSet.next()) {
			map.put(resultSet.getString("month"), stringUtility.formatDouble(resultSet.getDouble("value"), 2).length());
		}
		resultSet = database.execute(ExpenseSQLStatements.SHOW_MONTHS);
		while (resultSet.next()) {
			if (!resultSet.getString("month").regionMatches(3, month, 3, 2)) {
				list = new ArrayList<>();
				returnList.add(list);
				month = resultSet.getString("month");
			}
			list.add(" "	+ resultSet.getString("month") + " : "
						+ stringUtility.preIncrementTo(stringUtility.formatDouble(resultSet.getDouble("value"), 2), map.get(month), ' ') + "€");
		}
		while (returnList.get(0).size() < 12) {
			returnList.get(0).add(0, "");
		}
		resultSet.close();
		return stringUtility.arrangeInCollums(returnList, 3);
	}

	@Command(tag = "all") public String printAll(IDatabase database) throws SQLException {
		int maxNameLength = 0;
		int maxSumLength = 0;
		int gap = 5;
		String lastCategory = "";
		StringUtility stringUtility = new StringUtility();
		Builder builder = new Builder();
		ResultSet resultSet;
		resultSet = database.execute(ExpenseSQLStatements.SHOW_ALL_LENGTH);
		if (resultSet.next()) {
			maxNameLength = resultSet.getInt("namelength");
			maxSumLength = resultSet.getInt("sumlength");
		}
		resultSet = database.execute(ExpenseSQLStatements.SHOW_ALL);
		while (resultSet.next()) {
			if (!resultSet.getString("category").equals(lastCategory)) {
				lastCategory = resultSet.getString("category");
				builder.append(" " + resultSet.getString("category") + ":");
				builder.newLine();
			}
			builder.append("  - " + stringUtility.postIncrementTo(resultSet.getString("name"), maxNameLength + gap, ' '));
			builder.append(stringUtility.preIncrementTo(stringUtility.formatDouble(resultSet.getDouble("sum"), 2), maxSumLength + gap, ' '));
			builder.append("€");
			builder.newLine();
		}
		resultSet.close();
		return builder.build();
	}

	@Command(tag = "month") public String printCurrent(IDatabase database, ITerminal terminal)	throws InterruptedException, BadLocationException, UserCancelException,
																								SQLException {
		StringUtility stringUtility = new StringUtility();
		Builder builder = new Builder();
		ResultSet resultSet;
		String date = "";
		int gap = 73;
		boolean retry = true;
		while (retry) {
			date = terminal.request("enter month and year", ".*");
			date = stringUtility.parseDateString(date.isEmpty() ? LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.uuuu")) : "01." + date);
			if (!stringUtility.testDateString(date)) {
				terminal.errorMessage();
			}
			else {
				retry = false;
			}
		}
		date = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.uuuu")).format(DateTimeFormatter.ofPattern("uuuu-MM-dd"));
		resultSet = database.execute(ExpenseSQLStatements.SHOW_MONTH.replace("??", date));
		while (resultSet.next()) {
			builder.append(" \u2022 " + resultSet.getString("date") + " - ");
			builder.append(resultSet.getString("name"));
			builder.append(stringUtility.preIncrementTo(stringUtility.formatDouble(resultSet.getDouble("value"), 2), gap - resultSet.getString("name").length(), ' '));
			builder.append("€");
			builder.newLine();
		}
		resultSet.close();
		return builder.build();
	}
}