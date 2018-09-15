package database.plugin.stromWasser;

import java.time.LocalDate;

public class Nachf�llung {
	LocalDate datum;
	double literpreis;
	double menge;

	public Nachf�llung(LocalDate datum, double menge, double literpreis) {
		this.datum = datum;
		this.menge = menge;
		this.literpreis = literpreis;
	}

	public Nachf�llung(double preis, LocalDate datum, double menge) {
		this.datum = datum;
		this.menge = menge;
		this.literpreis = preis / menge;
	}

	double preis() {
		return literpreis * menge;
	}
}
