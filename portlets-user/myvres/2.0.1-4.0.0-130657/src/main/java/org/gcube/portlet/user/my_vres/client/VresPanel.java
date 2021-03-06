package org.gcube.portlet.user.my_vres.client;


import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.gcube.portlet.user.my_vres.client.widgets.ClickableVRE;
import org.gcube.portlet.user.my_vres.shared.VRE;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
/**
 * 
 * @author Massimiliano Assante - ISTI CNR
 *
 */
public class VresPanel extends Composite {
	public static final String loading = GWT.getModuleBaseURL() + "../images/loading.gif";
	private static final String ADD_OTHER = "Add More";
	private final MyVREsServiceAsync myVREsService = GWT.create(MyVREsService.class);
	private FlowPanel flowPanel;
	private VerticalPanel mainPanel = new VerticalPanel();
	private SimplePanel catPanel = new SimplePanel();
	private HorizontalPanel imagesPanel = new HorizontalPanel();
	private Image loadingImage = new Image(loading);
	private LinkedHashMap<String, ArrayList<VRE>> cachedVREs = null;

	boolean hasVres = false;

	public VresPanel() {
		super();		
		loadingImage.getElement().getStyle().setMarginTop(59, Unit.PX);
		mainPanel.setHorizontalAlignment(HasAlignment.ALIGN_CENTER);
		mainPanel.setWidth("100%");
		mainPanel.setStyleName("mainPanel");
		this.flowPanel = new FlowPanel();
		flowPanel.setWidth("100%");
		flowPanel.setStyleName("flowPanel");
		catPanel.setWidth("95%");
		loadVREs();
		initWidget(mainPanel);
	}

	private void loadVREs() {
		mainPanel.add(loadingImage);
		myVREsService.getUserVREs(new AsyncCallback<LinkedHashMap<String,ArrayList<VRE>>>() {

			@Override
			public void onFailure(Throwable caught) {
				flowPanel.add(new HTML("Could not fetch personal VREs: " + caught.getMessage()));							
			}

			@Override
			public void onSuccess(LinkedHashMap<String, ArrayList<VRE>> result) {
				cachedVREs = result;
				showIconView();

			}
		});


	}

	private void showIconView() {
		mainPanel.clear();		
		mainPanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		for (String cat : cachedVREs.keySet()) {
			if (! cachedVREs.get(cat).isEmpty()) {
				SimplePanel catPanel = new SimplePanel();
				catPanel.setStyleName("category-panel");
				HTML categ = new HTML(cat);
				categ.setStyleName("category-name");
				catPanel.add(categ);
				mainPanel.add(catPanel);
				FlowPanel flowPanel = new FlowPanel();
				flowPanel.setWidth("100%");
				flowPanel.setStyleName("flowPanel");
				for (VRE vre: cachedVREs.get(cat)) {
					ClickableVRE vreButton = new ClickableVRE(vre, myVREsService);
					flowPanel.add(vreButton);
				}
				
				mainPanel.add(flowPanel);
			}
		}

		if (! hasAtLeastOneVRE(cachedVREs)) {
			mainPanel.add(new NoVresPanel());
			imagesPanel.clear();
		} else {
			addMoreVREsButton();
		}

	}
	
	private void addMoreVREsButton() {
		SimplePanel catPanel = new SimplePanel();
		catPanel.setStyleName("category-panel");
		HTML categ = new HTML(ADD_OTHER);
		categ.setStyleName("category-name");
		catPanel.add(categ);
		
		mainPanel.add(catPanel);
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.setWidth("100%");
		flowPanel.setStyleName("flowPanel");
		
		ClickableVRE vreButton = new ClickableVRE(myVREsService);
		flowPanel.add(vreButton);
		
		mainPanel.add(flowPanel);
	}

	private boolean hasAtLeastOneVRE(LinkedHashMap<String, ArrayList<VRE>> cachedVREs) {
		for (String cat : cachedVREs.keySet()) 
			if (! cachedVREs.get(cat).isEmpty()) 
				return true;
		return false;
	}



}
