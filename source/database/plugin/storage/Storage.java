package database.plugin.storage;

import java.io.IOException;
import java.util.ArrayList;

import jdk.nashorn.internal.objects.annotations.Getter;
import database.main.Administration;
import database.main.PluginContainer;
import database.plugin.Command;
import database.plugin.Instance;
import database.plugin.InstancePlugin;
import database.plugin.Plugin;
import database.plugin.Writeable;

public class Storage extends Plugin implements Writeable {
	private ArrayList<String>	storage;
	private PluginContainer		pluginContainer;

	public Storage(PluginContainer pluginContainer, Administration administration) {
		super("storage", administration);
		this.pluginContainer = pluginContainer;
		this.storage = new ArrayList<String>();
	}

	@Getter public ArrayList<String> getStorage() {
		return storage;
	}

	@Override public void conduct(String command) throws InterruptedException, IOException {
		store();
	}

	@Command(tag = "store") public void store() throws InterruptedException {
		Plugin plugin = pluginContainer.getPlugin(request(new String[][] { { "store", pluginContainer.getPluginNameTagsAsRegesx() } })[0][1]);
		InstancePlugin instancePlugin;
		String parameter;
		if (plugin != null && plugin instanceof InstancePlugin) {
			instancePlugin = (InstancePlugin) plugin;
		}
		else {
			administration.errorMessage();
			return;
		}
		for (Instance instance : instancePlugin.getList()) {
			parameter = "";
			for (int i = 0; i < instance.getParameter().length; i++) {
				parameter = parameter + instance.getParameter()[i][0] + ": " + instance.getParameter()[i][1];
				if (i + 1 < instance.getParameter().length) {
					parameter = parameter + ",";
				}
			}
			storage.add(parameter);
		}
		instancePlugin.getList().clear();
		instancePlugin.update();
	}

	@Override public ArrayList<String> write() {
		return storage;
	}

	@Override public void read(String line) {
		storage.add(line);
	}
}
