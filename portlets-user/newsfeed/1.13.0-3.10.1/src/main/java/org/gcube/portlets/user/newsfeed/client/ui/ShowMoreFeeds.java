package org.gcube.portlets.user.newsfeed.client.ui;

import org.gcube.portlets.user.newsfeed.client.event.ShowMoreUpdatesEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class ShowMoreFeeds extends Composite {

	private static NewFeedsAvailableUiBinder uiBinder = GWT
			.create(NewFeedsAvailableUiBinder.class);

	interface NewFeedsAvailableUiBinder extends
	
	UiBinder<Widget, ShowMoreFeeds> {
	}
	
	private HandlerManager eventBus;
	
	@UiField HTML caption;
	@UiField HTMLPanel panel;


	public ShowMoreFeeds(HandlerManager eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		this.eventBus = eventBus;
		panel.getElement().getStyle().setMarginTop(10, Unit.PX);
		caption.addStyleName("new-feeds-show");		
		caption.getElement().getStyle().setBackgroundColor("transparent");
		caption.getElement().getStyle().setFontSize(14, Unit.PX);
		caption.setHTML("Show more feeds");
		//done after
		panel.getElement().getStyle().setVisibility(Visibility.HIDDEN);
	}	
	
	@UiHandler("caption")
	void onClick(ClickEvent e) {
		eventBus.fireEvent(new ShowMoreUpdatesEvent());
	}
}
