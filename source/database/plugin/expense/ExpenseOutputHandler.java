package database.plugin.expense;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.text.BadLocationException;
import database.main.UserCancelException;
import database.main.userInterface.ITerminal;
import database.plugin.Command;
import database.plugin.IOutputHandler;
import database.services.ServiceRegistry;
import database.services.database.IDatabase;
import database.services.database.SQLStatements;
import database.services.stringUtility.Builder;
import database.services.stringUtility.StringUtility;

public class ExpenseOutputHandler implements IOutputHandler {
	private IDatabase database;

	public ExpenseOutputHandler() throws SQLException {
		database = ServiceRegistry.Instance().get(IDatabase.class);
	}

	@Override public String getInitialOutput() {
		return " not implemented";
	}

	@Command(tag = "average") public String outputAverage() throws SQLException {
		StringUtility stringUtility = new StringUtility();
		Builder builder = new Builder();
		ResultSet resultSet;
		LocalDate firstDate = null;
		long dayCount = 0;
		long monthCount = 0;
		resultSet = database.execute(SQLStatements.EXPENSE_SELECT);
		if (resultSet.next()) {
			firstDate = resultSet.getDate("date").toLocalDate();
			dayCount = ChronoUnit.DAYS.between(firstDate, LocalDate.now().minusDays(LocalDate.now().getDayOfMonth()));
			monthCount = ChronoUnit.MONTHS.between(firstDate, LocalDate.now().minusDays(LocalDate.now().getDayOfMonth())) + 1;
		}
		resultSet = database.execute(SQLStatements.EXPENSE_SELECT_AVERAGE);
		if (resultSet.next()) {
			double sum = resultSet.getDouble("sum");
			double dayAverage = sum / dayCount;
			double monthAverage = sum / monthCount;
			String yearAverage = stringUtility.formatDouble((dayAverage * 365.25 + monthAverage * 12) / 2, 2) + "€";
			builder.append(" average value per");
			builder.newLine();
			builder.append(" - day   : " + stringUtility.preIncrementTo(stringUtility.formatDouble(dayAverage, 2) + "€", yearAverage.length(), ' '));
			builder.newLine();
			builder.append(" - month : " + stringUtility.preIncrementTo(stringUtility.formatDouble(monthAverage, 2) + "€", yearAverage.length(), ' '));
			builder.newLine();
			builder.append(" - year  : " + yearAverage);
			builder.newLine();
		}
		resultSet.close();
		return builder.build();
	}

	@Command(tag = "category") public String outputCategory() throws SQLException {
		int maxCategoryLength = 0;
		int maxSumLength = 0;
		int maxPercentageLength = 0;
		int gap = 5;
		StringUtility stringUtility = new StringUtility();
		Builder builder = new Builder();
		ResultSet resultSet;
		resultSet = database.execute(SQLStatements.EXPENSE_SELECT_CATEGORY_LENGTH);
		if (resultSet.next()) {
			maxCategoryLength = resultSet.getInt("categorylength");
			maxSumLength = resultSet.getInt("sumlength");
			maxPercentageLength = resultSet.getInt("percentagelength");
		}
		resultSet = database.execute(SQLStatements.EXPENSE_SELECT_CATEGORY);
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
		resultSet = database.execute(SQLStatements.EXPENSE_SELECT_TOTAL);
		if (resultSet.next()) {
			int length = 3 + maxCategoryLength + maxSumLength + 2 * gap;
			builder.append(stringUtility.preIncrementTo(stringUtility.formatDouble(resultSet.getDouble("total"), 2) + "€", length, ' '));
		}
		resultSet.close();
		return builder.build();
	}

	@Command(tag = "months") public String outputMonth() throws SQLException {
		StringUtility stringUtility = new StringUtility();
		ResultSet resultSet;
		String month = "";
		Map<String, Integer> map = new HashMap<>();
		List<List<String>> returnList = new ArrayList<>();
		List<String> list = null;
		resultSet = database.execute(SQLStatements.EXPENSE_SELECT_MONTHS);
		while (resultSet.next()) {
			map.put(resultSet.getString("month"), stringUtility.formatDouble(resultSet.getDouble("value"), 2).length());
		}
		resultSet = database.execute(SQLStatements.EXPENSE_SELECT_MONTHS);
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

	@Command(tag = "all") public String printAll() throws SQLException {
		int maxNameLength = 0;
		int maxSumLength = 0;
		int gap = 5;
		String lastCategory = "";
		StringUtility stringUtility = new StringUtility();
		Builder builder = new Builder();
		ResultSet resultSet;
		resultSet = database.execute(SQLStatements.EXPENSE_SELECT_ALL_LENGTH);
		if (resultSet.next()) {
			maxNameLength = resultSet.getInt("namelength");
			maxSumLength = resultSet.getInt("sumlength");
		}
		resultSet = database.execute(SQLStatements.EXPENSE_SELECT_ALL);
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

	@Command(tag = "month") public String printCurrent() throws InterruptedException, BadLocationException, UserCancelException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
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
		resultSet = database.execute(SQLStatements.EXPENSE_SELECT_MONTH.replace("??", date));
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