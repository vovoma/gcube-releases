package org.gcube.data_catalogue.grsf_publish_ws.utils.groups;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Stock types
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
public enum Stock_Type {
	
	Assessment_Unit("Assessment Unit"),// for stock
	Resource("Resource"); // for stock
	
	private String subGroupNameOrig;

	private Stock_Type(String origName) {
		this.subGroupNameOrig = origName;
	}

	/**
	 * Return the original name
	 * @return
	 */
	public String getOrigName(){
		return subGroupNameOrig;
	}

	/**
	 * Get the json value
	 * @return
	 */
	@JsonValue
	public String onSerialize(){
		return subGroupNameOrig.toLowerCase();
	}

	/**
	 * Get back an enum from json string
	 * @param typeString
	 * @return
	 */
	@JsonCreator
	public static Stock_Type onDeserialize(String typeString) {
		if(typeString != null) {
			for(Stock_Type type : Stock_Type.values()) {
				if(type.getOrigName().equalsIgnoreCase(typeString.trim()))
					return type;
			}
		}
		return null;
	}
	
	/**
	 * Used by reflection
	 */
	@Override
	public String toString() {
		return getOrigName();
	}

}
