package org.gcube.portlets.user.td.expressionwidget.client.properties;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface ReplaceElementProperties extends PropertyAccess<ReplaceElement> {

	@Path("id")
	ModelKeyProvider<ReplaceElement> id();

	LabelProvider<ReplaceElement> label();

}
