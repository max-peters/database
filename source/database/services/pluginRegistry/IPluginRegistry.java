package database.services.pluginRegistry;

import java.sql.SQLException;
import javax.swing.text.BadLocationException;
import database.plugin.Plugin;

public interface IPluginRegistry {
	public <T> T getPlugin(Class<T> type);

	public Plugin getPlugin(String identity);

	public String getPluginNameTagsAsRegex(String... args);

	public void initialOutput() throws BadLocationException, SQLException;

	public void register(Plugin plugin);
}
