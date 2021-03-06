package org.gcube.portlets.user.td.expressionwidget.client.operation;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public interface OperationProperties extends PropertyAccess<Operation> {

	@Path("id")
	ModelKeyProvider<Operation> id();

	LabelProvider<Operation> label();

}
