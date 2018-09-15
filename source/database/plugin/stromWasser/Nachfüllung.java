package database.plugin.stromWasser;

import java.time.LocalDate;

public class Nachfüllung {
	LocalDate datum;
	double literpreis;
	double menge;

	public Nachfüllung(LocalDate datum, double menge, double literpreis) {
		this.datum = datum;
		this.menge = menge;
		this.literpreis = literpreis;
	}

	public Nachfüllung(double preis, LocalDate datum, double menge) {
		this.datum = datum;
		this.menge = menge;
		this.literpreis = preis / menge;
	}

	double preis() {
		return literpreis * menge;
	}
}
