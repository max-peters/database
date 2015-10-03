package database.plugin.event.holiday;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import database.main.Administration;
import database.main.GraphicalUserInterface;
import database.main.PluginContainer;
import database.main.Terminal;
import database.plugin.event.EventPluginExtention;

public class HolidayPlugin extends EventPluginExtention {
	public HolidayPlugin(PluginContainer pluginContainer, Terminal terminal, GraphicalUserInterface graphicalUserInterface, Administration administration) throws IOException {
		super(pluginContainer, terminal, graphicalUserInterface, administration, "holiday", new HolidayList());
		getHolidays();
	}

	public void getHolidays() throws MalformedURLException, IOException, UnknownHostException {
		int lineCounter = 0;
		String line;
		String[] lines;
		Map<String, String> map;
		URL url = new URL("http://www.schulferien.org/Feiertage/Feiertage_Baden_Wuerttemberg.html");
		URLConnection conn = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		while ((line = in.readLine()) != null) {
			lineCounter++;
		}
		lines = new String[lineCounter];
		lineCounter = 0;
		in.close();
		conn = url.openConnection();
		in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		while ((line = in.readLine()) != null) {
			lines[lineCounter++] = line;
		}
		in.close();
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].matches(".*<a href=\"/Feiertage/feiertag_.*.html\" class=\"dash\">.*")) {
				map = new HashMap<String, String>();
				String temp = lines[i + 5].replace(" ", "");
				String date = temp.substring(0, temp.lastIndexOf(","));
				temp = lines[i + 1].replace("&ouml;", "ö").replace(":", " ");
				String name = temp.substring(14, temp.length() - 1);
				map.put("name", name);
				map.put("date", date);
				map.put("type", "holiday");
				create(map);
			}
		}
	}
}
