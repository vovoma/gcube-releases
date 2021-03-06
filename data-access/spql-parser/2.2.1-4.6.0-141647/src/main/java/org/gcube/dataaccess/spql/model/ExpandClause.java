/**
 * 
 */
package org.gcube.dataaccess.spql.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ExpandClause {
	
	protected List<String> datasources;
	
	public ExpandClause()
	{
		datasources = new ArrayList<String>();
	}

	/**
	 * @return the datasources
	 */
	public List<String> getDatasources() {
		return datasources;
	}

	/**
	 * @param datasources the datasources to set
	 */
	public void setDatasources(List<String> datasources) {
		this.datasources = datasources;
	}
	

}
