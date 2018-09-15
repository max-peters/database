package database.plugin.stromWasser;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import com.google.gson.Gson;

import database.main.UserCancelException;
import database.main.userInterface.ITerminal;
import database.main.userInterface.RequestType;
import database.plugin.Command;
import database.plugin.Plugin;
import database.services.ServiceRegistry;
import database.services.writerReader.IWriterReader;

public class StromWasserAbrechnungPlugin extends Plugin {
	List<Nachfüllung> nachfüllungen;
	StromWasserErgebnis erg;
	HeizkostenAbrechnungErgebnis ergebnisHeiz;

	public StromWasserAbrechnungPlugin() {
		super("abrechnung", new StromWasserOutputHandler());
		nachfüllungen = new LinkedList<>();
	}

	@Command(tag = "start")
	public void start() throws InterruptedException, BadLocationException, NumberFormatException, UserCancelException,
			SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		List<String> list = new LinkedList<>();
		list.add("Strom- und Wasserabrechnung");
		list.add("Heizkostenabrechnung");
		switch (terminal.checkRequest(list, "Menü")) {
			case 0:
				stromwasserRequest();
				break;
			case 1:
				heizkostenRequest();
				break;
		}
	}

	@Command(tag = "strom- und wasserabrechnung")
	public void stromwasserRequest() throws InterruptedException, BadLocationException, NumberFormatException,
			UserCancelException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		terminal.update();
		int jahr = Integer.valueOf(terminal.request("Abrechnungsjahr", RequestType.INTEGER));
		String verbraucher1 = String.valueOf(terminal.request("Name von Verbraucher 1", RequestType.NAME));
		int verbrauch_eg = Integer.valueOf(terminal.request("Wasser - Verbrauch - EG", RequestType.INTEGER));
		int verbrauch_dg = Integer.valueOf(terminal.request("Wasser - Verbrauch - DG", RequestType.INTEGER));
		int verbrauch_keller = Integer.valueOf(terminal.request("Wasser - Verbrauch - Keller", RequestType.INTEGER));
		int gesamtverbrauch1 = verbrauch_eg + verbrauch_dg + verbrauch_keller;
		double zähleranteil = Double
				.valueOf(terminal.request("Wasser - Halber Zähleranteil", RequestType.DOUBLE).replaceAll(",", "\\."));
		double wasser_preisProKubikmeter = Double
				.valueOf(terminal.request("Wasser - Preis pro Kubikmeter", RequestType.DOUBLE).replaceAll(",", "\\."));
		double summe_verbraucher1 = gesamtverbrauch1 * wasser_preisProKubikmeter + zähleranteil;
		summe_verbraucher1 *= 1.07;
		double abwasser_preisProKubikmeter = Double.valueOf(
				terminal.request("Abwasser - Preis pro Kubikmeter", RequestType.DOUBLE).replaceAll(",", "\\."));
		summe_verbraucher1 += gesamtverbrauch1 * abwasser_preisProKubikmeter;
		double niederschlagswasser_preisProQuadratmeter = Double.valueOf(terminal
				.request("Niederschlagswasser - Preis pro Quadratmeter", RequestType.DOUBLE).replaceAll(",", "\\."));
		summe_verbraucher1 += 120 * niederschlagswasser_preisProQuadratmeter;
		double strom_gesamtpreis = Double
				.valueOf(terminal.request("Strom - Gesamtverbrauch", RequestType.DOUBLE).replaceAll(",", "\\."));
		summe_verbraucher1 += 0.5 * strom_gesamtpreis;
		String verbraucher2 = String.valueOf(terminal.request("Name von Verbraucher 2", RequestType.NAME));
		int gesamtverbrauch2 = Integer.valueOf(terminal.request("Wasser - Verbrauch - Gesamt", RequestType.INTEGER));
		double summe_verbraucher2 = gesamtverbrauch2 * wasser_preisProKubikmeter + zähleranteil;
		summe_verbraucher2 *= 1.07;
		summe_verbraucher2 += gesamtverbrauch2 * abwasser_preisProKubikmeter;
		summe_verbraucher2 += 87 * niederschlagswasser_preisProQuadratmeter;
		summe_verbraucher2 += 0.5 * strom_gesamtpreis;
		double abschläge = Double.valueOf(
				terminal.request("Abschläge - Verbraucher " + verbraucher1, RequestType.DOUBLE).replaceAll(",", "\\."));
		double gesamtkosten_stadtwerke = Double.valueOf(terminal
				.request("Gesamtkosten laut Abrechnung der Stadtwerke", RequestType.DOUBLE).replaceAll(",", "\\."));
		StromWasserErgebnis ergebnis = new StromWasserErgebnis(verbraucher1, verbraucher2, jahr, gesamtverbrauch1,
				gesamtverbrauch2, zähleranteil, wasser_preisProKubikmeter, abwasser_preisProKubikmeter,
				niederschlagswasser_preisProQuadratmeter, strom_gesamtpreis, abschläge, gesamtkosten_stadtwerke);
		erg = ergebnis;
	}

	@Command(tag = "heizkostenabrechnung")
	public void heizkostenRequest() throws InterruptedException, BadLocationException, NumberFormatException,
			UserCancelException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		String verbraucher1 = String.valueOf(terminal.request("Verbraucher Eins", RequestType.NAME));
		String verbraucher2 = String.valueOf(terminal.request("Verbraucher Zwei", RequestType.NAME));
		LocalDate beginn = LocalDate.parse(terminal.request("Abrechnungszeitraum - Beginn", RequestType.DATE),
				DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		LocalDate ende = LocalDate.parse(terminal.request("Abrechnungszeitraum - Ende", RequestType.DATE),
				DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		LocalDate vorjahresbestand_datum = LocalDate.parse(
				terminal.request("Vorjahresbestand - Datum", RequestType.DATE),
				DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		double vorjahresbestand_menge = Double
				.valueOf(terminal.request("Vorjahresbestand - Menge", RequestType.DOUBLE).replaceAll(",", "\\."));
		double vorjahresbestand_preis = Double
				.valueOf(terminal.request("Vorjahresbestand - Kosten", RequestType.DOUBLE).replaceAll(",", "\\."));
		Nachfüllung vorjahresbestand = new Nachfüllung(vorjahresbestand_preis, vorjahresbestand_datum,
				vorjahresbestand_menge);
		int anzahl_nachfüllungen = Integer.valueOf(terminal.request("Anzahl Nachfüllungen", RequestType.INTEGER));
		for (int i = 0; i < anzahl_nachfüllungen; i++) {
			LocalDate nachfüllung_datum = LocalDate.parse(
					terminal.request((i + 1) + ". Nachfüllung - Datum", RequestType.DATE),
					DateTimeFormatter.ofPattern("dd.MM.uuuu"));
			double nachfüllung_menge = Double.valueOf(
					terminal.request((i + 1) + ". Nachfüllung - Menge", RequestType.DOUBLE).replaceAll(",", "\\."));
			double nachfüllung_literpreis = Double.valueOf(terminal
					.request((i + 1) + ". Nachfüllung - Literpreis", RequestType.DOUBLE).replaceAll(",", "\\."));
			nachfüllungen.add(new Nachfüllung(nachfüllung_datum, nachfüllung_menge, nachfüllung_literpreis));
		}
		LocalDate restbestand_datum = LocalDate.parse(terminal.request("Restbestand - Ablesedatum", RequestType.DATE),
				DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		double restbestand_menge = Double
				.valueOf(terminal.request("Restbestand - Menge", RequestType.DOUBLE).replaceAll(",", "\\."));
		double betriebsstrom_kosten = Double.valueOf(
				terminal.request("Nebenkosten - Betriebsstrom - Kosten", RequestType.DOUBLE).replaceAll(",", "\\."));
		LocalDate brennerwartung_datum = LocalDate.parse(
				terminal.request("Nebenkosten - Brennerwartung - Datum", RequestType.DATE),
				DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		double brennerwartung_kosten = Double.valueOf(
				terminal.request("Nebenkosten - Brennerwartung - Kosten", RequestType.DOUBLE).replaceAll(",", "\\."));
		LocalDate kaminreinigung_datum = LocalDate.parse(
				terminal.request("Nebenkosten - Kaminreinigung - Datum", RequestType.DATE),
				DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		double kaminreinigung_kosten = Double.valueOf(
				terminal.request("Nebenkosten - Kaminreinigung - Kosten", RequestType.DOUBLE).replaceAll(",", "\\."));
		LocalDate immissionsmessung_datum = LocalDate.parse(
				terminal.request("Nebenkosten - Immissionsmessung - Datum", RequestType.DATE),
				DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		double immissionsmessung_kosten = Double.valueOf(terminal
				.request("Nebenkosten - Immissionsmessung - Kosten", RequestType.DOUBLE).replaceAll(",", "\\."));
		double warmwasserverbrauch_kubikmeter = Double.valueOf(
				terminal.request("Warmwasser - Verbrauch - Kubikmeter", RequestType.DOUBLE).replaceAll(",", "\\."));
		double solarmenge = Double.valueOf(
				terminal.request("Solarmenge - Erzeugung - Kilowattstunde", RequestType.DOUBLE).replaceAll(",", "\\."));
		double heizung_verbrauch1 = Double
				.valueOf(terminal.request(verbraucher1 + "Heizung - Verbrauch - Kilowattstunde", RequestType.DOUBLE)
						.replaceAll(",", "\\."));
		double warmwasser_verbrauch1 = Double
				.valueOf(terminal.request(verbraucher1 + "Warmwasser - Verbrauch - Kubikmeter", RequestType.DOUBLE)
						.replaceAll(",", "\\."));
		double heizung_verbrauch2 = Double
				.valueOf(terminal.request(verbraucher2 + "Heizung - Verbrauch - Kilowattstunde", RequestType.DOUBLE)
						.replaceAll(",", "\\."));
		HeizkostenAbrechnungErgebnis ergebnis = new HeizkostenAbrechnungErgebnis(verbraucher1, verbraucher2, beginn,
				ende, vorjahresbestand, restbestand_datum, restbestand_menge, betriebsstrom_kosten,
				brennerwartung_datum, brennerwartung_kosten, kaminreinigung_datum, kaminreinigung_kosten,
				immissionsmessung_datum, immissionsmessung_kosten, warmwasserverbrauch_kubikmeter, solarmenge,
				heizung_verbrauch1, warmwasser_verbrauch1, heizung_verbrauch2);
		ergebnisHeiz = ergebnis;
	}

	@Override
	public void write() {
		IWriterReader writerReader = ServiceRegistry.Instance().get(IWriterReader.class);
		Gson gson = new Gson();
		writerReader.add(identity, "display", String.valueOf(display));
		for (Nachfüllung nachfüllung : nachfüllungen) {
			writerReader.add(identity, "nachfüllung", gson.toJson(nachfüllung));
		}
		writerReader.add(identity, "ergebnis", gson.toJson(erg));
		writerReader.add(identity, "ergebnisHeiz", gson.toJson(ergebnisHeiz));
	}

	@Override
	public void read(Node node) throws ParserConfigurationException, DOMException {
		Gson gson = new Gson();
		if (node.getNodeName().equals("display")) {
			display = Boolean.valueOf(node.getTextContent());
		}
		else if (node.getNodeName().equals("nachfüllung")) {
			nachfüllungen.add(gson.fromJson(node.getTextContent(), Nachfüllung.class));
		}
		else if (node.getNodeName().equals("ergebnis")) {
			erg = gson.fromJson(node.getTextContent(), StromWasserErgebnis.class);
			// erg.print();
		}
		else if (node.getNodeName().equals("ergebnisHeiz")) {
			ergebnisHeiz = gson.fromJson(node.getTextContent(), HeizkostenAbrechnungErgebnis.class);
			ergebnisHeiz.print(nachfüllungen);
		}
	}
}
