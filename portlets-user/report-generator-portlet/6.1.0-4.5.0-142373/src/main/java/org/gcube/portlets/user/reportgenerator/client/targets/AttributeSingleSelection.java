package org.gcube.portlets.user.reportgenerator.client.targets;


import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.d4sreporting.common.shared.Attribute;
import org.gcube.portlets.d4sreporting.common.shared.AttributeArea;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
/**
 * <code> AttributeArea </code> class 
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
public class AttributeSingleSelection extends Composite {

	private final String RADIO_NAME = "radio_unique"+(int)(Math.random()*10000); 
	private HorizontalPanel myPanel;
	private String attrName;
	private RadioButton[] boxes;
	private List<Metadata> metas;
	/**
	 * Coming form a template constructor 
	 */
	public AttributeSingleSelection(final Presenter presenter, int left, int top, int width,  final int height, String textToDisplay, boolean displayBlock) {
		myPanel = new HorizontalPanel();
		myPanel.setTitle("Attribute Area");
		myPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		myPanel.setPixelSize(width, 20);
		myPanel.addStyleName("attributeArea");

		attrName = getAttributeName(textToDisplay);
		HTML attrNameLabel = new HTML(attrName);
		attrNameLabel.setStyleName("attribute-name");
		attrNameLabel.getElement().getStyle().setMarginRight(5, Unit.PX);
		ComplexPanel boxesPanel = null;
		if (displayBlock) {
			boxesPanel = new VerticalPanel(); //use a vertical panel when display block is requested
			attrNameLabel.getElement().getStyle().setPaddingBottom(5, Unit.PX);
		}
		else {
			boxesPanel = new FlowPanel();
		}		
		boxesPanel.add(attrNameLabel);
		myPanel.add(boxesPanel);
		myPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		boxes = getRadioboxes(textToDisplay);
		for (int i = 0; i < boxes.length; i++) {
			boxesPanel.add(boxes[i]);
		}
		initWidget(myPanel);
	}
	/**
	 * Coming form a report constructor 
	 */
	public AttributeSingleSelection(final Presenter presenter, int left, int top, int width,  final int height, AttributeArea sata, boolean displayBlock) {
		myPanel = new HorizontalPanel();
		myPanel.setTitle("Attribute Area");
		myPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		myPanel.setPixelSize(width, 20);
		myPanel.addStyleName("attributeArea");

		attrName = sata.getAttrName();
		
		HTML attrNameLabel = new HTML(attrName);
		attrNameLabel.setStyleName("attribute-name");
		attrNameLabel.getElement().getStyle().setMarginRight(5, Unit.PX);
		ComplexPanel boxesPanel = null;
		if (displayBlock) {
			boxesPanel = new VerticalPanel(); //use a vertical panel when display block is requested	
			attrNameLabel.getElement().getStyle().setPaddingBottom(5, Unit.PX);
		}
		else {
			boxesPanel = new FlowPanel();
		}		
		boxesPanel.add(attrNameLabel);
		myPanel.add(boxesPanel);
		myPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		int values = sata.getValues().size(); 
		boxes = new RadioButton[values];
		int j = 0;
		for (Attribute attr: sata.getValues()) {
			RadioButton toAdd = new RadioButton(RADIO_NAME, attr.getName());
			toAdd.setStyleName("checkAttribute");
			toAdd.setValue(attr.getValue());
			toAdd.setTitle(attr.getOptionalValue());
			boxes[j] = toAdd;
			j++;
		}
		
		//adding it to the panel		
		for (int i = 0; i < boxes.length; i++) {
			boxesPanel.add(boxes[i]);
		}
		initWidget(myPanel);
	}
	/**
	 * 
	 * @param toParse
	 * @return
	 */
	private String getAttributeName(String toParse) {
		if (toParse == null)
			return "";
		String toReturn = "";
		try {
			toReturn = toParse.substring(0, toParse.indexOf(":"));
		} catch (StringIndexOutOfBoundsException e) {
			//GWT.log("Could not find : returning empty");
		}
		return toReturn;
	}
	/**
	 * 
	 * @param toParse
	 * @return
	 */
	private RadioButton[] getRadioboxes(String toParse) {
		String toSplit = toParse.substring(toParse.indexOf(":")+1, toParse.length());
		String[] values = toSplit.split("\\|");
//		GWT.log("toSplit" + toSplit);
//		GWT.log("values" + values.length);
		RadioButton[] boxes = new RadioButton[values.length];
		for (int i = 0; i < values.length; i++) {
			boxes[i] = new RadioButton(RADIO_NAME, values[i].trim());
			boxes[i].setStyleName("checkAttribute");
		}
		return boxes;
	}
	/**
	 * 
	 * @return
	 */
	public ComponentType getType() {
		return ComponentType.ATTRIBUTE_UNIQUE;
	}
	/**
	 * 
	 * @return
	 */
	public RadioButton[] getBoxes() {
		return boxes;
	}
	/**
	 * 
	 * @return
	 */
	public String getAttrName() {
		return attrName;
	}
	
	public AttributeArea getSerializable() {
		ArrayList<Attribute> singlevalues = new ArrayList<Attribute>();
		for (RadioButton box : getBoxes()) {
			singlevalues.add(new Attribute(box.getText().trim(), box.getValue(), box.getTitle()));
		}	
		return new AttributeArea(getAttrName().trim(), singlevalues);
	}
	public List<Metadata> getMetadata() {
		return metas;
	}

	public void setMetadata(List<Metadata> metas) {
		this.metas = metas;
	}
	
	public void reset() {
		for (RadioButton radio : boxes) {
			if (radio.getText().compareTo("Not applicable") == 0) {
				radio.setValue(true);
			}
			else
				radio.setValue(false);
		}
	}
}

