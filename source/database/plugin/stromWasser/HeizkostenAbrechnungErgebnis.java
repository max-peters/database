package database.plugin.stromWasser;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import database.services.stringUtility.Builder;
import database.services.stringUtility.StringUtility;

public class HeizkostenAbrechnungErgebnis extends Ergebnis {
	String verbraucher1;
	String verbraucher2;
	LocalDate abrechnungszeitraum_beginn;
	LocalDate abrechnungszeitraum_ende;
	Nachf�llung vorjahresbestand;
	LocalDate restbestand_datum;
	double restbestand_menge;
	double betriebsstrom_kosten;
	LocalDate brennerwartung_datum;
	double brennerwartung_kosten;
	LocalDate kaminreinigung_datum;
	double kaminreinigung_kosten;
	LocalDate immissionsmessung_datum;
	double immissionsmessung_kosten;
	double warmwasserverbrauch_kubikmeter;
	double solarmenge;
	double heizung_verbrauch1;
	double warmwasser_verbrauch1;
	double heizung_verbrauch2;

	public HeizkostenAbrechnungErgebnis(String verbraucher1, String verbraucher2, LocalDate abrechnungszeitraum_beginn,
			LocalDate abrechnungszeitraum_ende, Nachf�llung vorjahresbestand, LocalDate restbestand_datum,
			double restbestand_menge, double betriebsstrom_kosten, LocalDate brennerwartung_datum,
			double brennerwartung_kosten, LocalDate kaminreinigung_datum, double kaminreinigung_kosten,
			LocalDate immissionsmessung_datum, double immissionsmessung_kosten, double warmwasserverbrauch_kubikmeter,
			double solarmenge, double heizung_verbrauch1, double warmwasser_verbrauch1, double heizung_verbrauch2) {
		this.verbraucher1 = verbraucher1;
		this.verbraucher2 = verbraucher2;
		this.abrechnungszeitraum_beginn = abrechnungszeitraum_beginn;
		this.abrechnungszeitraum_ende = abrechnungszeitraum_ende;
		this.vorjahresbestand = vorjahresbestand;
		this.restbestand_datum = restbestand_datum;
		this.restbestand_menge = restbestand_menge;
		this.betriebsstrom_kosten = betriebsstrom_kosten;
		this.brennerwartung_datum = brennerwartung_datum;
		this.brennerwartung_kosten = brennerwartung_kosten;
		this.kaminreinigung_datum = kaminreinigung_datum;
		this.kaminreinigung_kosten = kaminreinigung_kosten;
		this.immissionsmessung_datum = immissionsmessung_datum;
		this.immissionsmessung_kosten = immissionsmessung_kosten;
		this.warmwasserverbrauch_kubikmeter = warmwasserverbrauch_kubikmeter;
		this.solarmenge = solarmenge;
		this.heizung_verbrauch1 = heizung_verbrauch1;
		this.warmwasser_verbrauch1 = warmwasser_verbrauch1;
		this.heizung_verbrauch2 = heizung_verbrauch2;
	}

	public Nachf�llung print(List<Nachf�llung> nachf�llungen) {
		Builder builder = new Builder();
		StringUtility su = new StringUtility();
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.uuuu");
		double bestand_preis = vorjahresbestand.literpreis * vorjahresbestand.menge;
		double bestand_menge = vorjahresbestand.menge;
		double nebenkosten = betriebsstrom_kosten + brennerwartung_kosten + kaminreinigung_kosten
				+ immissionsmessung_kosten;
		builder.append("Heizkostenabrechnung f�r den Zeitraum " + abrechnungszeitraum_beginn.format(df) + " - "
				+ abrechnungszeitraum_ende.format(df));
		builder.newLine();
		builder.append("-------------------------------------------------------------");
		builder.newLine();
		builder.newLine();
		builder.append("|------------------|-------------|-------------|-------------|-------------|");
		builder.newLine();
		builder.append(su.postIncrementTo("| Messung", 19, ' '));
		builder.append(su.postIncrementTo("| Datum", 14, ' '));
		builder.append(su.postIncrementTo("| Menge", 14, ' '));
		builder.append(su.postIncrementTo("| Literpreis", 14, ' '));
		builder.append(su.postIncrementTo("| Preis", 14, ' ') + "|");
		builder.newLine();
		builder.append("|------------------|-------------|-------------|-------------|-------------|");
		builder.newLine();
		printNachf�llung(builder, vorjahresbestand, "Vorjahresbestand");
		int x = 1;
		for (Nachf�llung nachf�llung : nachf�llungen) {
			if (nachf�llung.datum.isAfter(abrechnungszeitraum_beginn)
					&& nachf�llung.datum.isBefore(abrechnungszeitraum_ende)) {
				printNachf�llung(builder, nachf�llung, x++ + ". Nachf�llung");
				bestand_preis += nachf�llung.literpreis * nachf�llung.menge;
				bestand_menge += nachf�llung.menge;
			}
		}
		builder.append("|------------------|-------------|-------------|-------------|-------------|");
		builder.newLine();

		double tmp = restbestand_menge;
		int q = nachf�llungen.size() - 1;
		while (tmp > 0) {
			tmp -= nachf�llungen.get(q).menge;
			if (tmp > 0) {
				printNachf�llung(builder, nachf�llungen.get(q), "Kauf");
				q--;
			}
		}

		double preis_restbestand = (nachf�llungen.get(q).menge + tmp) * nachf�llungen.get(q).literpreis;
		for (int i = q + 1; i < nachf�llungen.size(); i++) {
			preis_restbestand += nachf�llungen.get(i).menge * nachf�llungen.get(i).literpreis;
		}
		printNachf�llung(builder, new Nachf�llung(nachf�llungen.get(q).datum, (nachf�llungen.get(q).menge + tmp),
				nachf�llungen.get(q).literpreis), "Kauf");
		builder.append("|------------------|-------------|-------------|-------------|-------------|");
		builder.newLine();
		printNachf�llung(builder, new Nachf�llung(preis_restbestand, restbestand_datum, restbestand_menge),
				"- Restbestand");

		double brennstoffkosten = bestand_preis - preis_restbestand;
		builder.append("|------------------|-------------|-------------|-------------|-------------|");
		builder.newLine();
		builder.append(su.postIncrementTo("Brennstoffverbrauch", 35, ' '));
		builder.append(su.postIncrementTo(su.formatDouble(bestand_menge - restbestand_menge, 2) + " l", 28, ' '));
		builder.append(su.formatDouble(brennstoffkosten, 2) + " �");
		builder.newLine();
		builder.newLine();
		builder.newLine();
		builder.append("Betriebsstrom - Abrechnungszeitraum: " + abrechnungszeitraum_beginn.format(df) + " - "
				+ abrechnungszeitraum_ende.format(df));
		builder.newLine();
		builder.append("Betriebsstrom - Kosten:");
		builder.append(su.preIncrementTo(su.formatDouble(betriebsstrom_kosten, 2) + " �", 53, ' '));
		builder.newLine();
		builder.newLine();
		builder.append("Brennerwartung - Datum: " + brennerwartung_datum.format(df));
		builder.newLine();
		builder.append("Brennerwartung - Kosten:");
		builder.append(su.preIncrementTo(su.formatDouble(brennerwartung_kosten, 2) + " �", 52, ' '));
		builder.newLine();
		builder.newLine();
		builder.append("Kaminreinigung - Datum: " + kaminreinigung_datum.format(df));
		builder.newLine();
		builder.append("Kaminreinigung - Kosten:");
		builder.append(su.preIncrementTo(su.formatDouble(kaminreinigung_kosten, 2) + " �", 52, ' '));
		builder.newLine();
		builder.newLine();
		builder.append("Immissionsmessung - Datum: " + immissionsmessung_datum.format(df));
		builder.newLine();
		builder.append("Immissionsmessung - Kosten:");
		builder.append(su.preIncrementTo(su.formatDouble(immissionsmessung_kosten, 2) + " �", 49, ' '));
		builder.newLine();
		builder.newLine();
		builder.append(su.preIncrementTo("", 76, '-'));
		builder.newLine();
		builder.append("Gesamtbetrag f�r Nebenkosten:");
		builder.append(su.preIncrementTo(su.formatDouble(nebenkosten, 2) + " �", 47, ' '));
		builder.newLine();
		builder.newLine();
		builder.append("Gesamtbetrag f�r Brennstoff und Nebenkosten:");
		builder.append(su.preIncrementTo(su.formatDouble(nebenkosten + brennstoffkosten, 2) + " �", 32, ' '));
		builder.newLine();
		builder.newLine();
		builder.append(su.preIncrementTo("", 76, '='));
		builder.newLine();
		builder.newLine();
		builder.append("Mittlere Brauchwassertemperatur: 50 �C");
		builder.newLine();
		builder.append("Heizwert: 10 KWh");
		builder.newLine();
		double gesamtkosten_liter = (nebenkosten + brennstoffkosten) / (bestand_menge - restbestand_menge);
		double brennstoffkosten_liter = (brennstoffkosten) / (bestand_menge - restbestand_menge);
		builder.append("Brennstoffkosten pro Liter: " + su.formatDouble(brennstoffkosten_liter, 4) + " �/l");
		builder.newLine();
		builder.append("Gesamtkosten pro Liter: " + su.formatDouble(gesamtkosten_liter, 4) + " �/l");
		builder.newLine();
		builder.newLine();
		builder.append("Warmwasser - Gesamtverbrauch: " + warmwasserverbrauch_kubikmeter + " m3");
		builder.newLine();
		builder.append("Warmwasser - �lmenge zur Bereitung: " + su.formatDouble(10 * warmwasserverbrauch_kubikmeter, 0)
				+ " l");
		builder.append(" (" + su.formatDouble(gesamtkosten_liter, 4) + " �/l)");
		builder.newLine();
		builder.append("Warmwasser - Kosten:");
		builder.append(su.preIncrementTo(
				su.formatDouble(10 * warmwasserverbrauch_kubikmeter * gesamtkosten_liter, 2) + " �", 56, ' '));
		builder.newLine();
		builder.newLine();
		builder.append("Solarmenge - Erzeugung: " + solarmenge + " KWh");
		builder.newLine();
		builder.append("Solarmenge - Entsprechung in �l: " + solarmenge / 10 + " l");
		builder.append(" (" + su.formatDouble(brennstoffkosten_liter, 4) + " �/l)");
		builder.newLine();
		builder.append("Solarmenge - Erl�s:");
		builder.append(
				su.preIncrementTo("- " + su.formatDouble(solarmenge / 10 * brennstoffkosten_liter, 2) + " �", 57, ' '));
		builder.newLine();
		builder.newLine();
		builder.append(su.preIncrementTo("", 76, '-'));
		builder.newLine();
		double warmwasser_tats�chlichekosten = (10 * warmwasserverbrauch_kubikmeter * gesamtkosten_liter)
				- (solarmenge / 10 * brennstoffkosten_liter);
		builder.append("Tats�chliche Warmwasserkosten:");
		builder.append(su.preIncrementTo(su.formatDouble(warmwasser_tats�chlichekosten, 2) + " �", 46, ' '));
		builder.newLine();
		builder.newLine();
		builder.newLine();
		builder.append("Gesamtkostenaufteilung");
		builder.newLine();
		builder.append("----------------------");
		builder.newLine();
		builder.append("Zentralheizung - Gesamtverbrauch: " + (heizung_verbrauch1 + heizung_verbrauch2) + " KWh");
		builder.newLine();
		builder.append("Zentralheizung - Gesamtkosten:");
		builder.append(su.preIncrementTo(
				su.formatDouble((nebenkosten + brennstoffkosten - warmwasser_tats�chlichekosten), 2) + " �", 46, ' '));
		builder.newLine();
		builder.append("Zentralheizung - Anteil Grundkosten (30%):");
		builder.append(su.preIncrementTo(
				su.formatDouble((nebenkosten + brennstoffkosten - warmwasser_tats�chlichekosten) * 0.3, 2) + " �", 34,
				' '));
		builder.newLine();
		builder.append("Zentralheizung - Anteil Verbrauchskosten (70%):");
		builder.append(su.preIncrementTo(
				su.formatDouble((nebenkosten + brennstoffkosten - warmwasser_tats�chlichekosten) * 0.7, 2) + " �", 29,
				' '));
		builder.newLine();
		builder.newLine();
		builder.append("Warmwasser - Gesamtverbrauch: " + warmwasserverbrauch_kubikmeter + " m3");
		builder.newLine();
		builder.append("Warmwasser - Gesamtkosten: ");
		builder.append(su.preIncrementTo(
				su.formatDouble((10 * warmwasserverbrauch_kubikmeter * gesamtkosten_liter), 2) + " �", 49, ' '));
		builder.newLine();
		builder.append("Warmwasser - Anteil Grundkosten (30%):");
		builder.append(su.preIncrementTo(
				su.formatDouble((10 * warmwasserverbrauch_kubikmeter * gesamtkosten_liter) * 0.3, 2) + " �", 38, ' '));
		builder.newLine();
		builder.append("Warmwasser - Anteil Verbrauchskosten (70%):");
		builder.append(su.preIncrementTo(
				su.formatDouble((10 * warmwasserverbrauch_kubikmeter * gesamtkosten_liter) * 0.7, 2) + " �", 33, ' '));
		builder.newLine();
		builder.newLine();
		builder.newLine();
		builder.append("Abrechnung Verbraucher Haus " + verbraucher1);
		builder.newLine();
		builder.append("----------------------------" + su.postIncrementTo("", verbraucher1.length(), '-'));
		builder.newLine();
		builder.append("Verbrauch - Heizung: " + heizung_verbrauch1 + " KWh");
		builder.newLine();
		builder.append("Verbrauch - Warmwasser: " + warmwasser_verbrauch1 + " m3");
		builder.newLine();
		builder.newLine();
		builder.append("Gesamtbetrag   / Gesamteinheiten   * Anteil Einheiten");
		builder.newLine();
		builder.newLine();
		builder.append("Zentralheizung - Grundkosten:");
		builder.newLine();
		builder.append(su.postIncrementTo(
				su.formatDouble((nebenkosten + brennstoffkosten - warmwasser_tats�chlichekosten) * 0.3, 2) + " �", 15,
				' ') + "/ 242 m2            * 108 m2             =");
		double grundkostenkosten_zentralheizung = ((nebenkosten + brennstoffkosten - warmwasser_tats�chlichekosten)
				* 0.3) / 242 * 108;
		builder.append(su.preIncrementTo(su.formatDouble(grundkostenkosten_zentralheizung, 2) + " �", 19, ' '));
		builder.newLine();
		builder.append("Zentralheizung - Verbrauchskosten:");
		builder.newLine();
		builder.append(su.postIncrementTo(
				su.formatDouble((nebenkosten + brennstoffkosten - warmwasser_tats�chlichekosten) * 0.7, 2) + " �", 15,
				' ') + "/ "
				+ su.postIncrementTo(su.formatDouble(heizung_verbrauch1 + heizung_verbrauch2, 2) + " KWh", 18, ' ')
				+ "* " + su.postIncrementTo(su.formatDouble(heizung_verbrauch1, 2) + " KWh", 18, ' ') + " =");
		double verbrauchskosten_zentralheizung = ((nebenkosten + brennstoffkosten - warmwasser_tats�chlichekosten)
				* 0.7) / (heizung_verbrauch1 + heizung_verbrauch2) * heizung_verbrauch1;
		builder.append(su.preIncrementTo(su.formatDouble(verbrauchskosten_zentralheizung, 2) + " �", 19, ' '));
		builder.newLine();
		builder.newLine();
		builder.append("Warmwasser - Grundkosten:");
		builder.newLine();
		builder.append(su.postIncrementTo(
				su.formatDouble((10 * warmwasserverbrauch_kubikmeter * gesamtkosten_liter) * 0.3, 2) + " �", 15, ' ')
				+ "/ 242 m2            * 108 m2             =");
		double grundkosten_warmwasser = ((10 * warmwasserverbrauch_kubikmeter * gesamtkosten_liter) * 0.3) / 242 * 108;
		builder.append(su.preIncrementTo(su.formatDouble(grundkosten_warmwasser, 2) + " �", 19, ' '));
		builder.newLine();
		builder.append("Warmwasser - Verbrauchskosten:");
		builder.newLine();
		builder.append(su.postIncrementTo(
				su.formatDouble((10 * warmwasserverbrauch_kubikmeter * gesamtkosten_liter) * 0.7, 2) + " �", 15, ' ')
				+ "/ " + su.postIncrementTo(su.formatDouble(warmwasserverbrauch_kubikmeter, 2) + " m3", 18, ' ') + "* "
				+ su.postIncrementTo(su.formatDouble(warmwasser_verbrauch1, 2) + " m3", 18, ' ') + " =");
		double verbrauchskosten_warmwasser = ((10 * warmwasserverbrauch_kubikmeter * gesamtkosten_liter) * 0.7)
				/ warmwasserverbrauch_kubikmeter * warmwasser_verbrauch1;
		builder.append(su.preIncrementTo(su.formatDouble(verbrauchskosten_warmwasser, 2) + " �", 19, ' '));
		builder.newLine();
		builder.newLine();
		builder.append(su.preIncrementTo("", 76, '-'));
		builder.newLine();
		double kosten_verbraucher1 = verbrauchskosten_warmwasser + verbrauchskosten_zentralheizung
				+ grundkosten_warmwasser + grundkostenkosten_zentralheizung;
		builder.append("Kosten:" + su.preIncrementTo(su.formatDouble(kosten_verbraucher1, 2) + " �", 69, ' '));
		builder.newLine();
		builder.newLine();

		double ausgleich = (nebenkosten + brennstoffkosten) / 2 - kosten_verbraucher1;

		builder.newLine();
		if (ausgleich >= 0) {
			builder.append("Ausgleich (1/2 Gesamtkosten - eigene Kosten): (Guthaben)"
					+ su.preIncrementTo(su.formatDouble(ausgleich, 2) + " �", 20, ' '));
		}
		else {
			builder.append("Ausgleich (1/2 Gesamtkosten - eigene Kosten): (Schulden)"
					+ su.preIncrementTo(su.formatDouble(ausgleich, 2) + " �", 20, ' '));
		}

		builder.newLine();
		builder.newLine();
		builder.newLine();
		builder.append("Abrechnung Verbraucher Haus " + verbraucher2);
		builder.newLine();
		builder.append("----------------------------" + su.postIncrementTo("", verbraucher2.length(), '-'));
		builder.newLine();
		builder.append("Verbrauch - Heizung: " + heizung_verbrauch2 + " KWh");
		builder.newLine();
		builder.newLine();
		builder.append("Gesamtbetrag   / Gesamteinheiten   * Anteil Einheiten");
		builder.newLine();
		builder.newLine();
		builder.append("Zentralheizung - Grundkosten:");
		builder.newLine();
		builder.append(su.postIncrementTo(
				su.formatDouble((nebenkosten + brennstoffkosten - warmwasser_tats�chlichekosten) * 0.3, 2) + " �", 15,
				' ') + "/ 242 m2            * 134 m2             =");
		double grundkostenkosten_zentralheizung2 = ((nebenkosten + brennstoffkosten - warmwasser_tats�chlichekosten)
				* 0.3) / 242 * 134;
		builder.append(su.preIncrementTo(su.formatDouble(grundkostenkosten_zentralheizung2, 2) + " �", 19, ' '));
		builder.newLine();
		builder.append("Zentralheizung - Verbrauchskosten:");
		builder.newLine();
		builder.append(su.postIncrementTo(
				su.formatDouble((nebenkosten + brennstoffkosten - warmwasser_tats�chlichekosten) * 0.7, 2) + " �", 15,
				' ') + "/ "
				+ su.postIncrementTo(su.formatDouble(heizung_verbrauch1 + heizung_verbrauch2, 2) + " KWh", 18, ' ')
				+ "* " + su.postIncrementTo(su.formatDouble(heizung_verbrauch2, 2) + " KWh", 18, ' ') + " =");
		double verbrauchskosten_zentralheizung2 = ((nebenkosten + brennstoffkosten - warmwasser_tats�chlichekosten)
				* 0.7) / (heizung_verbrauch1 + heizung_verbrauch2) * heizung_verbrauch2;
		builder.append(su.preIncrementTo(su.formatDouble(verbrauchskosten_zentralheizung2, 2) + " �", 19, ' '));
		builder.newLine();
		builder.newLine();
		builder.append("tats�chliche Warmwasserkosten:"
				+ su.preIncrementTo(su.formatDouble(warmwasser_tats�chlichekosten, 2), 44, ' ') + " �");
		builder.newLine();
		builder.append("abz�glich Warmwasserkosten Haus " + verbraucher1 + ":" + su.preIncrementTo(
				"- " + su.formatDouble(grundkosten_warmwasser + verbrauchskosten_warmwasser, 2), 37, ' ') + " �");
		builder.newLine();
		builder.newLine();
		builder.append(su.preIncrementTo("", 76, '-'));
		builder.newLine();
		double kosten_verbraucher2 = grundkostenkosten_zentralheizung2 + verbrauchskosten_zentralheizung2
				+ warmwasser_tats�chlichekosten - grundkosten_warmwasser - verbrauchskosten_warmwasser;
		builder.append("Kosten:" + su.preIncrementTo(su.formatDouble(kosten_verbraucher2, 2) + " �", 69, ' '));
		builder.newLine();
		builder.newLine();

		double ausgleich2 = (nebenkosten + brennstoffkosten) / 2 - kosten_verbraucher2;

		builder.newLine();
		if (ausgleich2 >= 0) {
			builder.append("Ausgleich (1/2 Gesamtkosten - eigene Kosten): (Guthaben)"
					+ su.preIncrementTo(su.formatDouble(ausgleich2, 2) + " �", 20, ' '));
		}
		else {
			builder.append("Ausgleich (1/2 Gesamtkosten - eigene Kosten): (Schulden)"
					+ su.preIncrementTo(su.formatDouble(ausgleich2, 2) + " �", 20, ' '));
		}

		print = builder.list;
		return new Nachf�llung(preis_restbestand, restbestand_datum, restbestand_menge);
	}

	public void printNachf�llung(Builder builder, Nachf�llung nachf�llung, String name) {
		StringUtility su = new StringUtility();
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.uuuu");
		builder.append("| " + su.postIncrementTo(name, 17, ' ') + "| ");
		builder.append(su.postIncrementTo(nachf�llung.datum.format(df), 12, ' ') + "| ");
		builder.append(su.postIncrementTo(su.formatDouble(nachf�llung.menge, 2) + " l", 12, ' ') + "| ");
		builder.append(su.postIncrementTo(su.formatDouble(nachf�llung.literpreis, 4) + " �/l", 12, ' ') + "| ");
		builder.append(su.postIncrementTo(su.formatDouble(nachf�llung.preis(), 2) + " �", 12, ' ') + "| ");
		builder.newLine();
	}
}
