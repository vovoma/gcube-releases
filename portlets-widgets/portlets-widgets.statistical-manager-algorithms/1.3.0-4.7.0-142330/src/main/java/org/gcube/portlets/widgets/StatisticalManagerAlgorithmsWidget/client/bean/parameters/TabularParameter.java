/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * @author ceras
 *
 */
public class TabularParameter extends Parameter implements Serializable {

	private static final long serialVersionUID = 8038591467145151553L;
	private String tableName;
	private List<String> templates = new ArrayList<String>();

	/**
	 * 
	 */
	public TabularParameter() {
		super();
		this.typology = ParameterTypology.TABULAR;
	}

	/**
	 * @param defaultValue
	 * @param value
	 */
	public TabularParameter(String name, String description) {
		super(name, ParameterTypology.TABULAR, description);
	}
	
	/**
	 * @param defaultValue
	 * @param value
	 */
	public TabularParameter(String name, String description, List<String> templates) {
		super(name, ParameterTypology.TABULAR, description);
		this.templates = templates;
	}
	
	
	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.bean.Parameter#setValue()
	 */
	@Override
	public void setValue(String value) {
		this.setTableName(value);
	}
	
	/**
	 * @param templates the templates to set
	 */
	public void setTemplates(List<String> templates) {
		this.templates = templates;
	}
	
	/**
	 * @return the templates
	 */
	public List<String> getTemplates() {
		return templates;
	}
	
	public void addTemplate(String template) {
		templates.add(template);
	}
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.bean.Parameter#getValue()
	 */
	@Override
	public String getValue() {
		return getTableName();
	}
}
