package database.main;

import java.lang.reflect.InvocationTargetException;
import javax.swing.JOptionPane;
import database.main.userInterface.GraphicalUserInterface;
import database.main.userInterface.Terminal;
import database.plugin.Storage;
import database.plugin.backup.BackupService;
import database.plugin.event.EventPlugin;
import database.plugin.event.appointment.AppointmentPlugin;
import database.plugin.event.birthday.BirthdayPlugin;
import database.plugin.event.day.DayPlugin;
import database.plugin.event.holiday.HolidayPlugin;
import database.plugin.event.multiDayAppointment.MultiDayAppointmentPlugin;
import database.plugin.event.weeklyAppointment.WeeklyAppointmentPlugin;
import database.plugin.expense.ExpensePlugin;
import database.plugin.refilling.RefillingPlugin;
import database.plugin.repetitiveExpense.RepetitiveExpensePlugin;
import database.plugin.settings.Settings;
import database.plugin.subject.SubjectPlugin;
import database.plugin.task.TaskPlugin;
import database.plugin.utility.UtilityPlugin;

public class Main {
	public static void main(String[] args) {
		Thread guiThread;
		try {
			GraphicalUserInterface graphicalUserInterface = new GraphicalUserInterface();
			guiThread = new Thread(() -> {
				try {
					graphicalUserInterface.initialise();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			});
			guiThread.start();
			Storage storage = new Storage();
			PluginContainer pluginContainer = new PluginContainer(storage);
			Terminal terminal = new Terminal(graphicalUserInterface);
			BackupService backupService = new BackupService();
			WriterReader writerReader = new WriterReader();
			Administration administration = new Administration();
			Settings settings = new Settings();
			SubjectPlugin subjectPlugin = new SubjectPlugin();
			ExpensePlugin expensePlugin = new ExpensePlugin();
			RepetitiveExpensePlugin repetitiveExpensePlugin = new RepetitiveExpensePlugin();
			RefillingPlugin refillingPlugin = new RefillingPlugin();
			TaskPlugin taskPlugin = new TaskPlugin();
			DayPlugin dayPlugin = new DayPlugin();
			BirthdayPlugin birthdayPlugin = new BirthdayPlugin();
			HolidayPlugin holidayPlugin = new HolidayPlugin();
			AppointmentPlugin appointmentPlugin = new AppointmentPlugin();
			WeeklyAppointmentPlugin weeklyAppointmentPlugin = new WeeklyAppointmentPlugin();
			MultiDayAppointmentPlugin multiDayAppointmentPlugin = new MultiDayAppointmentPlugin();
			EventPlugin eventPlugin = new EventPlugin();
			UtilityPlugin utilityPlugin = new UtilityPlugin();
			pluginContainer.addPlugin(settings);
			pluginContainer.addPlugin(utilityPlugin);
			pluginContainer.addPlugin(subjectPlugin);
			pluginContainer.addPlugin(expensePlugin);
			pluginContainer.addPlugin(repetitiveExpensePlugin);
			pluginContainer.addPlugin(refillingPlugin);
			pluginContainer.addPlugin(taskPlugin);
			pluginContainer.addPlugin(eventPlugin);
			pluginContainer.addPlugin(dayPlugin);
			pluginContainer.addPlugin(birthdayPlugin);
			pluginContainer.addPlugin(holidayPlugin);
			pluginContainer.addPlugin(appointmentPlugin);
			pluginContainer.addPlugin(weeklyAppointmentPlugin);
			pluginContainer.addPlugin(multiDayAppointmentPlugin);
			writerReader.read(pluginContainer);
			holidayPlugin.updateHolidays(terminal);
			repetitiveExpensePlugin.createExpense(terminal, expensePlugin, backupService);
			guiThread.join();
			graphicalUserInterface.setVisible(true);
			terminal.update(pluginContainer);
			administration.request(terminal, backupService, writerReader, pluginContainer);
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