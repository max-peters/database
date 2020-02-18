package database.plugin.accounting;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import com.google.gson.Gson;

import database.main.UserCancelException;
import database.main.userInterface.ITerminal;
import database.main.userInterface.OutputType;
import database.main.userInterface.RequestType;
import database.main.userInterface.StringFormat;
import database.plugin.Command;
import database.plugin.Plugin;
import database.services.ServiceRegistry;
import database.services.writerReader.IWriterReader;

public class AccountingPlugin extends Plugin {
	List<Nachf�llung> nachf�llungen;
	Nachf�llung vorjahresbestand;

	public AccountingPlugin() {
		super("accounting", new StromWasserOutputHandler());
		nachf�llungen = new LinkedList<>();
		vorjahresbestand = new Nachf�llung(LocalDate.MIN, 1, 1);
	}

	@Command(tag = "start")
	public void start() throws InterruptedException, BadLocationException, NumberFormatException, UserCancelException,
			SQLException, IOException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		List<String> list = new LinkedList<>();
		list.add("Strom- und Wasserabrechnung");
		list.add("Heizkostenabrechnung");
		LinkedList<String> result = null;
		Path file;
		switch (terminal.checkRequest(list, "Abrechnung")) {
		case 0:
			result = electricityWaterAccountingRequest();
			file = Paths.get(System.getProperty("user.home") + "/Desktop" + "/Strom- und Wasserabrechnung.txt");
			break;
		case 1:
			result = heatingCostAccountingRequest();
			file = Paths.get(System.getProperty("user.home") + "/Desktop" + "/Heizkostenabrechnung.txt");
			break;
		default:
			return;
		}
		Files.write(file, result, Charset.forName("UTF-8"));
		IWriterReader writerReader = ServiceRegistry.Instance().get(IWriterReader.class);
		try {
			writerReader.write();
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		catch (TransformerException e) {
			e.printStackTrace();
		}
		terminal.printLine("Aufgabe abgeschlossen... dr�cke beliebige Taste zum Beenden...", OutputType.CLEAR,
				StringFormat.STANDARD);
		terminal.waitForInput();
		System.exit(0);
	}

	@Command(tag = "Strom- und Wasserabrechnung")
	public LinkedList<String> electricityWaterAccountingRequest() throws InterruptedException, BadLocationException,
			NumberFormatException, UserCancelException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		String verbraucher2 = "Pfrommer";
		int jahr = Integer.valueOf(terminal.request("Abrechnungsjahr", RequestType.INTEGER));
		String verbraucher1 = String.valueOf(terminal.request("Name - Verbraucher im Nebenhaus", RequestType.NAME));
		int verbrauch_eg_kw = Integer
				.valueOf(terminal.request(verbraucher1 + " - Kaltwasser - Verbrauch - EG", RequestType.INTEGER));
		int verbrauch_eg_ww = Integer
				.valueOf(terminal.request(verbraucher1 + " - Warmwasser - Verbrauch - EG", RequestType.INTEGER));
		int verbrauch_eg = verbrauch_eg_kw + verbrauch_eg_ww;
		int verbrauch_dg_kw = Integer
				.valueOf(terminal.request(verbraucher1 + " - Kaltwasser - Verbrauch - DG", RequestType.INTEGER));
		int verbrauch_dg_ww = Integer
				.valueOf(terminal.request(verbraucher1 + " - Warmwasser - Verbrauch - DG", RequestType.INTEGER));
		int verbrauch_dg = verbrauch_dg_kw + verbrauch_dg_ww;
		int verbrauch_keller = Integer
				.valueOf(terminal.request(verbraucher1 + " - Kaltasser - Verbrauch - Keller", RequestType.INTEGER));
		int gesamtverbrauch2_kw = Integer
				.valueOf(terminal.request(verbraucher2 + " - Kaltwasser - Verbrauch", RequestType.INTEGER));
		int gesamtverbrauch2_ww = Integer
				.valueOf(terminal.request(verbraucher2 + " - Warmwasser - Verbrauch", RequestType.INTEGER));
		int gesamtverbrauch2 = gesamtverbrauch2_kw + gesamtverbrauch2_ww;
		double z�hleranteil = Double.valueOf(
				terminal.request("Wasser - Z�hlermiete (Verrechnungspreis)", RequestType.DOUBLE).replaceAll(",", "\\."))
				/ 2;
		double wasser_preisProKubikmeter = Double
				.valueOf(terminal.request("Wasser - Preis pro Kubikmeter", RequestType.DOUBLE).replaceAll(",", "\\."));
		double strom_gesamtpreis = Double.valueOf(
				terminal.request("Strom - Gesamtkosten (laut Stadtwerke)", RequestType.DOUBLE).replaceAll(",", "\\."));
		double abwasser_preisProKubikmeter = Double.valueOf(
				terminal.request("Abwasser - Preis pro Kubikmeter", RequestType.DOUBLE).replaceAll(",", "\\."));
		double niederschlagswasser_preisProQuadratmeter = Double.valueOf(terminal
				.request("Niederschlagswasser - Preis pro Quadratmeter", RequestType.DOUBLE).replaceAll(",", "\\."));
		double abschl�ge = Double.valueOf(
				terminal.request("Abschl�ge - Verbraucher " + verbraucher1, RequestType.DOUBLE).replaceAll(",", "\\."));
		double strom_wasser_stadtwerke = Double
				.valueOf(terminal.request("Abrechnung der Stadtwerke - Strom und Wasser (�)", RequestType.DOUBLE)
						.replaceAll(",", "\\."));
		double abwasser_stadtwerke = Double.valueOf(terminal
				.request("Abrechnung der Stadtwerke - Abwasser (�)", RequestType.DOUBLE).replaceAll(",", "\\."));

		ElectricityWaterAccountingFormatter formatter = new ElectricityWaterAccountingFormatter();
		return formatter.format(verbraucher1, verbraucher2, jahr, verbrauch_eg + verbrauch_dg + verbrauch_keller,
				gesamtverbrauch2, z�hleranteil, wasser_preisProKubikmeter, abwasser_preisProKubikmeter,
				niederschlagswasser_preisProQuadratmeter, strom_gesamtpreis, abschl�ge, strom_wasser_stadtwerke,
				abwasser_stadtwerke);
	}

	@Command(tag = "Heizkostenabrechnung")
	public LinkedList<String> heatingCostAccountingRequest() throws InterruptedException, BadLocationException,
			NumberFormatException, UserCancelException, SQLException {
		ITerminal terminal = ServiceRegistry.Instance().get(ITerminal.class);
		String verbraucher1 = String.valueOf(terminal.request("Name - Verbraucher im Nebenhaus", RequestType.NAME));
		String verbraucher2 = "Pfrommer";
		LocalDate abrechnungszeitraum_beginn = LocalDate.parse(
				terminal.request("Abrechnungszeitraum - Beginn", RequestType.DATE),
				DateTimeFormatter.ofPattern("dd.MM.uuuu"));
		LocalDate abrechnungszeitraum_ende = LocalDate.parse(
				terminal.request("Abrechnungszeitraum - Ende", RequestType.DATE),
				DateTimeFormatter.ofPattern("dd.MM.uuuu"));
//		LocalDate vorjahresbestand_datum = LocalDate.parse(
//				terminal.request("Vorjahresbestand - Datum", RequestType.DATE),
//				DateTimeFormatter.ofPattern("dd.MM.uuuu"));
//		double vorjahresbestand_menge = Double
//				.valueOf(terminal.request("Vorjahresbestand - Menge", RequestType.DOUBLE).replaceAll(",", "\\."));
//		double vorjahresbestand_preis = Double
//				.valueOf(terminal.request("Vorjahresbestand - Preis", RequestType.DOUBLE).replaceAll(",", "\\."));
//		Nachf�llung vorjahresbestand = new Nachf�llung(vorjahresbestand_preis, vorjahresbestand_datum,
//				vorjahresbestand_menge);
		int anzahl_nachf�llungen = Integer.valueOf(terminal.request("Anzahl Nachf�llungen", RequestType.INTEGER));
		for (int i = 0; i < anzahl_nachf�llungen; i++) {
			LocalDate nachf�llung_datum = LocalDate.parse(
					terminal.request((i + 1) + ". Nachf�llung - Datum", RequestType.DATE),
					DateTimeFormatter.ofPattern("dd.MM.uuuu"));
			double nachf�llung_menge = Double.valueOf(
					terminal.request((i + 1) + ". Nachf�llung - Menge", RequestType.DOUBLE).replaceAll(",", "\\."));
			double nachf�llung_literpreis = Double.valueOf(terminal
					.request((i + 1) + ". Nachf�llung - Literpreis", RequestType.DOUBLE).replaceAll(",", "\\."));
			nachf�llungen.add(new Nachf�llung(nachf�llung_datum, nachf�llung_menge, nachf�llung_literpreis));
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
				terminal.request("Warmwasser - Verbrauch (Kubikmeter)", RequestType.DOUBLE).replaceAll(",", "\\."));
		double solarmenge = Double.valueOf(
				terminal.request("Solarmenge - Erzeugung (Kilowattstunde)", RequestType.DOUBLE).replaceAll(",", "\\."));
		double heizung_verbrauch1_eg = Double.valueOf(
				terminal.request(verbraucher1 + " - Heizung EG - Verbrauch (Kilowattstunde)", RequestType.DOUBLE)
						.replaceAll(",", "\\."));
		double heizung_verbrauch1_dg = Double.valueOf(
				terminal.request(verbraucher1 + " - Heizung DG - Verbrauch (Kilowattstunde)", RequestType.DOUBLE)
						.replaceAll(",", "\\."));
		double heizung_verbrauch1 = heizung_verbrauch1_eg + heizung_verbrauch1_dg;
		double warmwasser_verbrauch1_eg = Double.valueOf(
				terminal.request(verbraucher1 + " - Warmwasser EG - Verbrauch (Kubikmeter)", RequestType.DOUBLE)
						.replaceAll(",", "\\."));
		double warmwasser_verbrauch1_dg = Double.valueOf(
				terminal.request(verbraucher1 + " - Warmwasser DG - Verbrauch (Kubikmeter)", RequestType.DOUBLE)
						.replaceAll(",", "\\."));
		double warmwasser_verbrauch1 = warmwasser_verbrauch1_eg + warmwasser_verbrauch1_dg;
		double heizung_verbrauch2 = Double
				.valueOf(terminal.request(verbraucher2 + " - Heizung - Verbrauch (Kilowattstunde)", RequestType.DOUBLE)
						.replaceAll(",", "\\."));

		List<Nachf�llung> printRefillings = new LinkedList<Nachf�llung>();
		double tmp = restbestand_menge;
		int q = nachf�llungen.size() - 1;
		while (tmp > 0) {
			tmp -= nachf�llungen.get(q).menge;
			if (tmp > 0) {
				printRefillings.add(nachf�llungen.get(q));
				q--;
			}
		}
		double preis_restbestand = (nachf�llungen.get(q).menge + tmp) * nachf�llungen.get(q).literpreis;
		for (int i = q + 1; i < nachf�llungen.size(); i++) {
			preis_restbestand += nachf�llungen.get(i).menge * nachf�llungen.get(i).literpreis;
		}
		printRefillings.add(new Nachf�llung(nachf�llungen.get(q).datum, (nachf�llungen.get(q).menge + tmp),
				nachf�llungen.get(q).literpreis));
		Nachf�llung restbestand = new Nachf�llung(preis_restbestand, restbestand_datum, restbestand_menge);

		HeatingCostAccountingFormatter formatter = new HeatingCostAccountingFormatter();
		LinkedList<String> result = formatter.format(verbraucher1, verbraucher2, abrechnungszeitraum_beginn,
				abrechnungszeitraum_ende, vorjahresbestand, restbestand, printRefillings, nachf�llungen,
				betriebsstrom_kosten, brennerwartung_datum, brennerwartung_kosten, kaminreinigung_datum,
				kaminreinigung_kosten, immissionsmessung_datum, immissionsmessung_kosten,
				warmwasserverbrauch_kubikmeter, solarmenge, heizung_verbrauch1, warmwasser_verbrauch1,
				heizung_verbrauch2);

		vorjahresbestand = restbestand;
		return result;
	}

	@Override
	public void write() {
		IWriterReader writerReader = ServiceRegistry.Instance().get(IWriterReader.class);
		Gson gson = new Gson();
		for (Nachf�llung nachf�llung : nachf�llungen) {
			writerReader.add(identity, "nachf�llung", gson.toJson(nachf�llung));
		}
		writerReader.add(identity, "vorjahresbestand", gson.toJson(vorjahresbestand));
	}

	@Override
	public void read(Node node) throws ParserConfigurationException, DOMException {
		Gson gson = new Gson();
		if (node.getNodeName().equals("nachf�llung")) {
			nachf�llungen.add(gson.fromJson(node.getTextContent(), Nachf�llung.class));
		}
		else if (node.getNodeName().equals("vorjahresbestand")) {
			vorjahresbestand = gson.fromJson(node.getTextContent(), Nachf�llung.class);
		}
	}
}
