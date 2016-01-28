package database.plugin.subject;

import java.util.HashMap;
import java.util.Map;
import database.plugin.Instance;

public class Subject extends Instance {
	public Double	maxPoints;
	public String	name;
	public Double	score;
	public String	tag;
	public int		worksheetCounter;

	public Subject(Map<String, String> parameter) {
		maxPoints = Double.valueOf(parameter.get("maxPoints"));
		name = parameter.get("name");
		score = Double.valueOf(parameter.get("score"));
		worksheetCounter = Integer.valueOf(parameter.get("counter"));
		tag = parameter.get("tag");
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

	@Override public Map<String, String> getParameter() {
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("counter", String.valueOf(worksheetCounter));
		parameter.put("name", name);
		parameter.put("tag", tag);
		parameter.put("score", score.toString());
		parameter.put("maxPoints", maxPoints.toString());
		return parameter;
	}

	protected double calcPercent() {
		return score / maxPoints * 100;
	}

	protected void setGrade(double newScore, double newMaxPoint) {
		worksheetCounter++;
		maxPoints += newMaxPoint;
		score += newScore;
	}
}