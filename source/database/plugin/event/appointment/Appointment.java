package database.plugin.event.appointment;

import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;
import database.main.date.Date;
import database.plugin.event.Event;

public class Appointment extends Event {
	String attribute;

	public Appointment(String name, Date date, String attribute) {
		super(name, date);
		this.attribute = attribute;
	}

	@Override public Map<String, String> getParameter() {
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.putAll(super.getParameter());
		parameter.put("attribute", attribute);
		return parameter;
	}

	@Override public void insertParameter(Element element) {
		super.insertParameter(element);
		element.setAttribute("attribute", attribute);
	}

	@Override public Date updateYear() {
		return date;
	}

	@Override protected String appendToOutput() {
		if (attribute.isEmpty()) {
			return "";
		}
		else {
			return "[" + attribute + "]";
		}
	}
}
