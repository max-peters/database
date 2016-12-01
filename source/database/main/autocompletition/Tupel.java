package database.main.autocompletition;

public class Tupel<T1, T2> {
	public T1	first;
	public T2	second;

	public Tupel(T1 first, T2 second) {
		this.first = first;
		this.second = second;
	}

	@Override public boolean equals(Object object) {
		Tupel<?, ?> tupel;
		if (object != null && object instanceof Tupel) {
			tupel = (Tupel<?, ?>) object;
			if (tupel.first.equals(first) && tupel.second.equals(second)) {
				return true;
			}
		}
		return false;
	}

	@Override public int hashCode() {
		return toString().hashCode();
	}

	@Override public String toString() {
		return "(" + first.toString() + ", " + second.toString() + ")";
	}
}
