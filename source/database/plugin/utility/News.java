package database.plugin.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class News {
	private String rank;
	// private int daysTillDecay;

	public News() {
		// daysTillDecay = getDaysTillDecay();
	}

	public String getCurrentRank() {
		return rank;
	}

	public void setRank() throws IOException {
		String line;
		URL url;
		URLConnection conn;
		BufferedReader in = null;
		try {
			url = new URL("http://www.lolking.net/summoner/euw/37588528");
			conn = url.openConnection();
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = in.readLine()) != null) {
				if (line.matches(".*<span class=\"tier-flag-.*\">[A-Z ]*</span>.*")) {
					rank = line.substring(line.indexOf('>') + 1, line.indexOf('<', line.indexOf('>')));
				}
				if (line.matches(".*League Points.*")) {
					rank += " - " + line.substring(line.indexOf('>') + 1, line.indexOf('<', line.indexOf('>')));
					break;
				}
			}
		}
		catch (IOException e) {
			rank = "404 page not found";
		}
		finally {
			in.close();
		}
	}
	// private int getDaysTillDecay() throws IOException {
	// String line;
	// int daysTillDecay = 0;
	//
	// return daysTillDecay;
	// }
}
