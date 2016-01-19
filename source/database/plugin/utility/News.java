package database.plugin.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class News {
	private String rank;

	public News() throws IOException {
		setRank();
	}

	private void setRank() throws IOException {
		String line;
		URL url = new URL("http://www.lolking.net/summoner/euw/37588528");
		URLConnection conn = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		while ((line = in.readLine()) != null) {
			if (line.matches(".*<span class=\"tier-flag-.*\">[A-Z ]*</span>.*")) {
				rank = line.substring(line.indexOf('>') + 1, line.indexOf('<', line.indexOf('>')));
			}
			if (line.matches(".*League Points.*")) {
				rank += " - " + line.substring(line.indexOf('>') + 1, line.indexOf('<', line.indexOf('>')));
				break;
			}
		}
		in.close();
	}

	public String getCurrentRank() {
		return rank;
	}
}
