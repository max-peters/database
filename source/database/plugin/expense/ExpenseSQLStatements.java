package database.plugin.expense;

public class ExpenseSQLStatements {
	protected static final String	AUTOCOMPLETE_CATEGORY	= "SELECT category as first, name as second, count(category) as count FROM expense group by name, category;";
	protected static final String	AUTOCOMPLETE_NAME		= "SELECT name as first, '' as second, count(name) as count FROM expense group by name;";
	protected static final String	SHOW_ALL				= "SELECT * FROM expenseall";
	protected static final String	SHOW_ALL_LENGTH			= "SELECT MAX(LENGTH(name)) AS namelength, MAX(LENGTH(CAST(sum AS DECIMAL (18 , 2 )))) AS sumlength FROM expenseall;";
	protected static final String	SHOW_AVERAGE			= "SELECT * FROM expenseaverage;";
	protected static final String	SHOW_CATEGORY			= "SELECT * FROM expensecategories";
	protected static final String	SHOW_CATEGORY_LENGTH	= "SELECT MAX(LENGTH(category)) AS categorylength, MAX(LENGTH(CAST(sum AS DECIMAL (18 , 2 )))) AS sumlength, MAX(LENGTH(CAST(percentage AS DECIMAL (18 , 2 )))) AS percentagelength FROM expensecategories;";
	protected static final String	SHOW_MONTH				= "SELECT DATE_FORMAT(date,'%d/%m') AS date, name, value FROM expense WHERE (MONTH(date) = MONTH('??')) AND (YEAR(date) = YEAR('??')) order by expense.date;";
	protected static final String	SHOW_MONTHS				= "SELECT * FROM expensemonths;";
	protected static final String	SHOW_TOTAL				= "SELECT SUM(value) AS total FROM expense";
}