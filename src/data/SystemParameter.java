package data;

import javafx.beans.property.SimpleStringProperty;

public class SystemParameter {
	
	private final SimpleStringProperty parameter;
	private final SimpleStringProperty value;
	
	public SystemParameter(String param, String val) {
		this.parameter = new SimpleStringProperty(param);
		this.value = new SimpleStringProperty(val);
	}
	
	public String getParameter() {
		return parameter.get();
	}
	
	public String getValue() {
		return value.get();
	}

}
