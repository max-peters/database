package database.plugin.subject;

import java.util.HashMap;
import java.util.Map;
import database.plugin.Instance;

public class Subject extends Instance {
	public int		worksheetCounter;
	public Double	maxPoints;
	public Double	score;
	public String	name;
	public String	tag;

	public Subject(Map<String, String> parameter) {
		this.maxPoints = Double.valueOf(parameter.get("maxPoints"));
		this.name = parameter.get("name");
		this.score = Double.valueOf(parameter.get("score"));
		this.worksheetCounter = Integer.valueOf(parameter.get("counter"));
		this.tag = parameter.get("tag");
	}

	protected double calcPercent() {
		return score / maxPoints * 100;
	}

	protected void setGrade(double newScore, double newMaxPoint) {
		worksheetCounter++;
		maxPoints += newMaxPoint;
		score += newScore;
	}

	@Override public Map<String, String> getParameter() {
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("counter", String.valueOf(worksheetCounter));
		parameter.put("name", name);
		parameter.put("tag", tag);
		parameter.put("score", score.toString());
		parameter.put("maxPoints", maxPoints.toString());
		return parameter;
	}

	@Override public boolean equals(Object object) {
		Subject subject;
		if (object != null && object.getClass().equals(this.getClass())) {
			subject = (Subject) object;
			if (worksheetCounter == subject.worksheetCounter&& name.equals(subject.name) && tag.equals(subject.tag) && score.equals(subject.score)
				&& maxPoints.equals(subject.maxPoints)) {
				return true;
			}
		}
		return false;
	}
}