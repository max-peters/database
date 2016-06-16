package database.plugin.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.json.JSONObject;

public class News {
	public boolean	connection;
	private int		daysTillDecay;
	private String	rank;

	public int getDaysTillDecay() {
		return daysTillDecay;
	}

	public String getRank() {
		return rank;
	}

	public void setDaysTillDecay() throws IOException {
		String data = URLEncoder.encode("region", "UTF-8")+ "=" + URLEncoder.encode("EUW", "UTF-8") + "&" + URLEncoder.encode("summoner", "UTF-8") + "="
						+ URLEncoder.encode("Brundlefliege", "UTF-8");
		URL url = new URL("http://decayoflegends.com/files/check.php");
		URLConnection conn = url.openConnection();
		conn.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(data);
		wr.flush();
		StringBuilder sb = new StringBuilder();
		int cp;
		InputStreamReader isr = new InputStreamReader(conn.getInputStream());
		while ((cp = isr.read()) != -1) {
			sb.append((char) cp);
		}
		wr.close();
		daysTillDecay = Integer.valueOf(new JSONObject(sb.toString()).get("days").toString());
	}

	public void setRank() throws IOException {
		String line;
		URL url;
		URLConnection conn;
		BufferedReader in = null;
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
		in.close();
	}
}
