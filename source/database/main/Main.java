package database.main;

import java.lang.reflect.InvocationTargetException;
import javax.swing.JOptionPane;
import database.main.userInterface.GraphicalUserInterface;
import database.main.userInterface.Terminal;
import database.plugin.Backup;
import database.plugin.Storage;
import database.plugin.event.EventPlugin;
import database.plugin.event.appointment.AppointmentPlugin;
import database.plugin.event.birthday.BirthdayPlugin;
import database.plugin.event.day.DayPlugin;
import database.plugin.event.holiday.HolidayPlugin;
import database.plugin.expense.ExpensePlugin;
import database.plugin.monthlyExpense.MonthlyExpensePlugin;
import database.plugin.refilling.RefillingPlugin;
import database.plugin.settings.Settings;
import database.plugin.subject.SubjectPlugin;
import database.plugin.task.TaskPlugin;
import database.plugin.utility.UtilityPlugin;

public class Main {
	public static void main(String[] args) {
		try {
			PluginContainer pluginContainer = new PluginContainer();
			GraphicalUserInterface graphicalUserInterface = new GraphicalUserInterface();
			Storage storage = new Storage();
			new Terminal(graphicalUserInterface, pluginContainer);
			WriterReader writerReader = new WriterReader(pluginContainer, storage);
			Backup backup = new Backup(writerReader, pluginContainer, storage);
			Administration administration = new Administration(pluginContainer, writerReader, backup);
			Settings settings = new Settings(backup);
			SubjectPlugin subjectPlugin = new SubjectPlugin(storage, backup);
			ExpensePlugin expensePlugin = new ExpensePlugin(storage, backup);
			MonthlyExpensePlugin monthlyExpensePlugin = new MonthlyExpensePlugin(expensePlugin, storage, backup);
			RefillingPlugin refillingPlugin = new RefillingPlugin(expensePlugin, storage, backup);
			TaskPlugin taskPlugin = new TaskPlugin(storage, backup);
			DayPlugin dayPlugin = new DayPlugin(storage, backup);
			BirthdayPlugin birthdayPlugin = new BirthdayPlugin(storage, backup);
			HolidayPlugin holidayPlugin = new HolidayPlugin(storage, backup);
			AppointmentPlugin appointmentPlugin = new AppointmentPlugin(storage, backup);
			EventPlugin eventPlugin = new EventPlugin(dayPlugin, birthdayPlugin, holidayPlugin, appointmentPlugin, settings, backup);
			UtilityPlugin utilityPlugin = new UtilityPlugin(backup, writerReader);
			pluginContainer.addPlugin(settings);
			pluginContainer.addPlugin(utilityPlugin);
			pluginContainer.addPlugin(subjectPlugin);
			pluginContainer.addPlugin(expensePlugin);
			pluginContainer.addPlugin(monthlyExpensePlugin);
			pluginContainer.addPlugin(refillingPlugin);
			pluginContainer.addPlugin(taskPlugin);
			pluginContainer.addPlugin(eventPlugin);
			pluginContainer.addPlugin(dayPlugin);
			pluginContainer.addPlugin(birthdayPlugin);
			pluginContainer.addPlugin(holidayPlugin);
			pluginContainer.addPlugin(appointmentPlugin);
			writerReader.read();
			holidayPlugin.updateHolidays();
			graphicalUserInterface.setVisible(true);
			Terminal.initialOutput();
			Terminal.printCollectedLines();
			administration.request();
		}
		catch (Throwable e) {
			String stackTrace = "";
			if (e instanceof InvocationTargetException) {
				e = e.getCause();
			}
			for (StackTraceElement element : e.getStackTrace()) {
				stackTrace = stackTrace + System.getProperty("line.separator") + element;
			}
			JOptionPane.showMessageDialog(null, stackTrace, e.getClass().getName(), JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(0);
		}
	}
}