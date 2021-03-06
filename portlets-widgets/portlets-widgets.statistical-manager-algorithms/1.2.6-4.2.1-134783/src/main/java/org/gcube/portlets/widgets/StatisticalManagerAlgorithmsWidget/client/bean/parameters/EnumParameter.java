/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author ceras
 *
 */
public class EnumParameter extends Parameter implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6893909305820759313L;
	List<String> values = new ArrayList<String>();
	String defaultValue;
	String value;
	

	/**
	 * 
	 */
	public EnumParameter() {
		super();
		this.typology = ParameterTypology.ENUM;
	}
	
	/**
	 * @param type
	 * @param defaultValue
	 * @param value
	 */
	public EnumParameter(String name, String description, List<String> values, String defaultValue) {
		super(name, ParameterTypology.ENUM, description);
		this.values = values;
		this.defaultValue = defaultValue;
	}


	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
	
	/**
	 * @param defaultValue the defaultValue to set
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	/**
	 * @param values the values to set
	 */
	public void setValues(List<String> values) {
		this.values = values;
	}
	
	/**
	 * @return the values
	 */
	public List<String> getValues() {
		return values;
	}
	
	public void addValue(String value) {
		this.values.add(value);
	}
	
	/**
	 * @return the value
	 */
	@Override
	public String getValue() {
		return value;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.bean.Parameter#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String value) {
		this.value = value;
	}

}
