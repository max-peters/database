package database.plugin.accounting;

import java.util.LinkedList;

import database.services.stringUtility.Builder;
import database.services.stringUtility.StringUtility;

public class ElectricityWaterAccountingFormatter {
	public LinkedList<String> format(String verbraucher1, String verbraucher2, int jahr, double wasser_gesamtverbrauch1,
			double wasser_gesamtverbrauch2, double wasser_zähleranteil, double wasser_preisProKubikmeter,
			double abwasser_preisProKubikmeter, double niederschlagswasser_preisProQuadratmeter,
			double strom_gesamtpreis, double abschläge_verbraucher1, double strom_wasser_stadtwerke,
			double abwasser_stadtwerke) {
		Builder builder = new Builder();
		StringUtility su = new StringUtility();

		double wasser_kosten1 = wasser_gesamtverbrauch1 * wasser_preisProKubikmeter;
		double niederschlagswasser_kosten1 = 120 * niederschlagswasser_preisProQuadratmeter;
		double abwasser_kosten1 = wasser_gesamtverbrauch1 * abwasser_preisProKubikmeter;
		double wasser_gesamtkosten1 = (wasser_kosten1 + wasser_zähleranteil) * 1.07;
		double kosten1 = niederschlagswasser_kosten1 + wasser_gesamtkosten1 + abwasser_kosten1 + strom_gesamtpreis / 2;
		double wasser_kosten2 = wasser_gesamtverbrauch2 * wasser_preisProKubikmeter;
		double niederschlagswasser_kosten2 = 87 * niederschlagswasser_preisProQuadratmeter;
		double abwasser_kosten2 = wasser_gesamtverbrauch2 * abwasser_preisProKubikmeter;
		double wasser_gesamtkosten2 = (wasser_kosten2 + wasser_zähleranteil) * 1.07;
		double kosten2 = niederschlagswasser_kosten2 + wasser_gesamtkosten2 + abwasser_kosten2 + strom_gesamtpreis / 2;
		double ausgleich = abschläge_verbraucher1 - kosten1;
		double ausgleich2 = strom_wasser_stadtwerke + abwasser_stadtwerke - abschläge_verbraucher1 - kosten2;

		builder.append("Strom- und Wasserabrechnung für das Jahr " + jahr);
		builder.newLine();
		builder.append("---------------------------------------------");
		builder.newLine();
		builder.newLine();
		builder.append("Verbraucher: Haus " + verbraucher1);
		builder.newLine();
		builder.newLine();
		builder.append("Wasser - Verbrauch: " + su.formatDouble(wasser_gesamtverbrauch1, 2) + " m3 ("
				+ su.formatDouble(wasser_preisProKubikmeter, 2) + " €/m3)");
		builder.newLine();
		builder.append("Wasser - Kosten:" + su.preIncrementTo(su.formatDouble(wasser_kosten1, 2) + " €", 60, ' '));
		builder.newLine();
		builder.append("Wasser - 1/2 Zähleranteil:"
				+ su.preIncrementTo(su.formatDouble(wasser_zähleranteil, 2) + " €", 50, ' '));
		builder.newLine();
		builder.append("Wasser - 7% Mehrwertsteuer:"
				+ su.preIncrementTo(su.formatDouble((wasser_kosten1 + wasser_zähleranteil) * 0.07, 2) + " €", 49, ' '));
		builder.newLine();
		builder.newLine();
		builder.append("Abwasser - Verbrauch: " + su.formatDouble(wasser_gesamtverbrauch1, 2) + " m3 ("
				+ su.formatDouble(abwasser_preisProKubikmeter, 2) + " €/m3)");
		builder.newLine();
		builder.append("Abwasser - Kosten:" + su.preIncrementTo(su.formatDouble(abwasser_kosten1, 2) + " €", 58, ' '));
		builder.newLine();
		builder.newLine();
		builder.append("Niederschlagswasser - Grundfläche: 120 m2 ("
				+ su.formatDouble(niederschlagswasser_preisProQuadratmeter, 2) + " €/m2)");
		builder.newLine();
		builder.append("Niederschlagswasser - Kosten:"
				+ su.preIncrementTo(su.formatDouble(niederschlagswasser_kosten1, 2) + " €", 47, ' '));
		builder.newLine();
		builder.newLine();
		builder.append("Strom - Gesamtkosten: " + su.formatDouble(strom_gesamtpreis, 2) + " € (50% pro Verbraucher)");
		builder.newLine();
		builder.append(
				"Strom - Kosten:" + su.preIncrementTo(su.formatDouble(strom_gesamtpreis / 2, 2) + " €", 61, ' '));
		builder.newLine();
		builder.append(su.preIncrementTo("", 76, '-'));
		builder.newLine();
		builder.append("Gesamtkosten:" + su.preIncrementTo(su.formatDouble(kosten1, 2) + " €", 63, ' '));
		builder.newLine();
		builder.newLine();
		builder.append(su.preIncrementTo("", 76, '='));
		builder.newLine();
		builder.newLine();
		builder.append("Verbraucher: Haus " + verbraucher2);
		builder.newLine();
		builder.newLine();
		builder.append("Wasser - Verbrauch: " + su.formatDouble(wasser_gesamtverbrauch2, 2) + " m3 ("
				+ su.formatDouble(wasser_preisProKubikmeter, 2) + " €/m3)");
		builder.newLine();
		builder.append("Wasser - Kosten:" + su.preIncrementTo(su.formatDouble(wasser_kosten2, 2) + " €", 60, ' '));
		builder.newLine();
		builder.append("Wasser - 1/2 Zähleranteil:"
				+ su.preIncrementTo(su.formatDouble(wasser_zähleranteil, 2) + " €", 50, ' '));
		builder.newLine();
		builder.append("Wasser - 7% Mehrwertsteuer:"
				+ su.preIncrementTo(su.formatDouble((wasser_kosten2 + wasser_zähleranteil) * 0.07, 2) + " €", 49, ' '));
		builder.newLine();
		builder.newLine();
		builder.append("Abwasser - Verbrauch: " + su.formatDouble(wasser_gesamtverbrauch2, 2) + " m3 ("
				+ su.formatDouble(abwasser_preisProKubikmeter, 2) + " €/m3)");
		builder.newLine();
		builder.append("Abwasser - Kosten:" + su.preIncrementTo(su.formatDouble(abwasser_kosten2, 2) + " €", 58, ' '));
		builder.newLine();
		builder.newLine();
		builder.append("Niederschlagswasser - Grundfläche: 87 m2 ("
				+ su.formatDouble(niederschlagswasser_preisProQuadratmeter, 2) + " €/m2)");
		builder.newLine();
		builder.append("Niederschlagswasser - Kosten:"
				+ su.preIncrementTo(su.formatDouble(niederschlagswasser_kosten2, 2) + " €", 47, ' '));
		builder.newLine();
		builder.newLine();
		builder.append("Strom - Gesamtkosten: " + su.formatDouble(strom_gesamtpreis, 2) + " € (50% pro Verbraucher)");
		builder.newLine();
		builder.append(
				"Strom - Kosten:" + su.preIncrementTo(su.formatDouble(strom_gesamtpreis / 2, 2) + " €", 61, ' '));
		builder.newLine();
		builder.append(su.preIncrementTo("", 76, '-'));
		builder.newLine();
		builder.append("Gesamtkosten:" + su.preIncrementTo(su.formatDouble(kosten2, 2) + " €", 63, ' '));
		builder.newLine();
		builder.newLine();
		builder.newLine();
		builder.newLine();
		builder.newLine();
		builder.newLine();
		builder.newLine();
		builder.newLine();
		builder.newLine();
		builder.newLine();
		builder.newLine();
		builder.newLine();
		builder.newLine();
		builder.newLine();
		builder.newLine();
		builder.append("Abrechnung");
		builder.newLine();
		builder.append("----------");
		builder.newLine();
		builder.newLine();
		builder.append("Verbraucher: Haus " + verbraucher1);
		builder.newLine();
		builder.append("------------------" + su.postIncrementTo("", verbraucher1.length(), '-'));
		builder.newLine();
		builder.append(
				"bezahlte Abschläge:" + su.preIncrementTo(su.formatDouble(abschläge_verbraucher1, 2) + " €", 57, ' '));
		builder.newLine();
		builder.append("abzüglich Verbrauch:" + su.preIncrementTo("- " + su.formatDouble(kosten1, 2) + " €", 56, ' '));
		builder.newLine();
		builder.newLine();
		builder.append(su.preIncrementTo("", 76, '-'));
		builder.newLine();
		builder.newLine();
		if (ausgleich >= 0) {
			builder.append("Ausgleich: (Guthaben)" + su.preIncrementTo(su.formatDouble(ausgleich, 2) + " €", 55, ' '));
		}
		else {
			builder.append("Ausgleich: (Schulden)" + su.preIncrementTo(su.formatDouble(ausgleich, 2) + " €", 55, ' '));
		}
		builder.newLine();
		builder.newLine();
		builder.append(su.preIncrementTo("", 76, '='));
		builder.newLine();
		builder.newLine();
		builder.append("Verbraucher: Haus " + verbraucher2);
		builder.newLine();
		builder.append("------------------" + su.postIncrementTo("", verbraucher2.length(), '-'));
		builder.newLine();
		builder.append("Abrechnung der Stadtwerke - Strom und Wasser:"
				+ su.preIncrementTo(su.formatDouble(strom_wasser_stadtwerke, 2) + " €", 31, ' '));
		builder.newLine();
		builder.append("Abrechnung der Stadtwerke - Abwasser:"
				+ su.preIncrementTo(su.formatDouble(abwasser_stadtwerke, 2) + " €", 39, ' '));
		builder.newLine();
		builder.append(su.postIncrementTo("abzüglich gezahlte Abschläge von Verbraucher " + verbraucher1 + ":", 63, ' ')
				+ " - " + su.preIncrementTo(su.formatDouble(abschläge_verbraucher1, 2) + " €", 10, ' '));
		builder.newLine();
		builder.append("abzüglich eigener Verbrauch:                                    - "
				+ su.preIncrementTo(su.formatDouble(kosten2, 2) + " €", 10, ' '));
		builder.newLine();
		builder.newLine();
		builder.append(su.preIncrementTo("", 76, '-'));
		builder.newLine();
		builder.newLine();
		if (ausgleich2 >= 0) {
			builder.append("Ausgleich: (Guthaben)" + su.preIncrementTo(su.formatDouble(ausgleich2, 2) + " €", 55, ' '));
		}
		else {
			builder.append("Ausgleich: (Schulden)" + su.preIncrementTo(su.formatDouble(ausgleich2, 2) + " €", 55, ' '));
		}
		return builder.list;
	}
}
