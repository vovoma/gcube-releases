package org.gcube.portlets.user.td.tablewidget.client.rows;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.rows.DuplicatesSession;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.tablewidget.client.properties.ColumnDataProperties;
import org.gcube.portlets.user.td.tablewidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.tablewidget.client.util.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoadResultBean;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class DuplicatesRowsDeletePanel extends FramedPanel implements
		MonitorDialogListener {
	private static final String WIDTH = "640px";
	private static final String HEIGHT = "520px";

	private TRId trId;
	private EventBus eventBus;

	private DuplicatesSession duplicatesSession;

	private ListLoader<ListLoadConfig, ListLoadResult<ColumnData>> loader;
	private Grid<ColumnData> grid;
	private CheckBoxSelectionModel<ColumnData> sm;

	private TextButton deleteButton;

	public DuplicatesRowsDeletePanel(TRId trId, EventBus eventBus) {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
		this.trId = trId;
		this.eventBus = eventBus;
		Log.debug("Create DuplicatesPanel(): [" + trId.toString() + "]");

		ColumnDataProperties props = GWT.create(ColumnDataProperties.class);

		ColumnConfig<ColumnData, String> labelCol = new ColumnConfig<ColumnData, String>(
				props.label());

		IdentityValueProvider<ColumnData> identity = new IdentityValueProvider<ColumnData>();
		sm = new CheckBoxSelectionModel<ColumnData>(identity);

		List<ColumnConfig<ColumnData, ?>> l = new ArrayList<ColumnConfig<ColumnData, ?>>();
		l.add(sm.getColumn());
		l.add(labelCol);
		ColumnModel<ColumnData> cm = new ColumnModel<ColumnData>(l);

		ListStore<ColumnData> store = new ListStore<ColumnData>(props.id());

		RpcProxy<ListLoadConfig, ListLoadResult<ColumnData>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<ColumnData>>() {

			public void load(ListLoadConfig loadConfig,
					final AsyncCallback<ListLoadResult<ColumnData>> callback) {
				loadData(loadConfig, callback);
			}
		};
		loader = new ListLoader<ListLoadConfig, ListLoadResult<ColumnData>>(
				proxy);

		loader.setRemoteSort(false);
		loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, ColumnData, ListLoadResult<ColumnData>>(
				store) {
		});

		grid = new Grid<ColumnData>(store, cm) {
			@Override
			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					public void execute() {
						loader.load();
					}
				});
			}
		};

		sm.setSelectionMode(SelectionMode.MULTI);
		grid.setLoader(loader);
		grid.setSelectionModel(sm);
		// grid.getView().setAutoExpandColumn(labelCol);
		grid.setHeight(360);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setAutoFill(true);
		grid.setBorders(false);
		grid.setLoadMask(true);
		grid.setColumnReordering(true);
		grid.setColumnResize(false);

		// Delete Button
		deleteButton = new TextButton("Delete");
		deleteButton
				.setIcon(ResourceBundle.INSTANCE.tableDuplicateRowsRemove());
		deleteButton.setIconAlign(IconAlign.RIGHT);
		deleteButton.setTitle("Delete Duplicates Rows");

		SelectHandler deleteHandler = new SelectHandler() {

			public void onSelect(SelectEvent event) {
				onDeleteDuplicate();

			}
		};
		deleteButton.addSelectHandler(deleteHandler);

		FieldLabel columnsLabel = new FieldLabel(null, "Columns");
		columnsLabel.getElement().applyStyles("font-weight:bold");
		HBoxLayoutContainer hBox = new HBoxLayoutContainer();
		hBox.add(deleteButton, new BoxLayoutData(new Margins(2, 5, 2, 5)));

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		v.setScrollMode(ScrollMode.AUTOY);
		v.setAdjustForScroll(true);
		v.add(columnsLabel, new VerticalLayoutData(-1, -1, new Margins(2, 1, 5,
				1)));
		v.add(grid, new VerticalLayoutData(1, -1, new Margins(0)));
		v.add(hBox, new VerticalLayoutData(-1, -1, new Margins(10, 0, 10, 0)));
		add(v, new VerticalLayoutData(1, -1, new Margins(0)));

	}

	protected ArrayList<ColumnData> getSelectedItems() {
		return new ArrayList<ColumnData>(grid.getSelectionModel()
				.getSelectedItems());

	}

	public void update(TRId trId) {
		this.trId = trId;
		loader.load();
	}

	protected void loadData(ListLoadConfig loadConfig,
			final AsyncCallback<ListLoadResult<ColumnData>> callback) {
		TDGWTServiceAsync.INSTANCE.getColumns(trId,
				new AsyncCallback<ArrayList<ColumnData>>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert("Error Locked",
										caught.getLocalizedMessage());
							} else {
								Log.error("load columns failure:"
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert("Error retrieving columns",
										"Error retrieving columns");
							}
						}
						callback.onFailure(caught);
					}

					public void onSuccess(ArrayList<ColumnData> result) {
						Log.trace("loaded " + result.size() + " ColumnData");
						callback.onSuccess(new ListLoadResultBean<ColumnData>(
								result));

					}

				});

	}

	protected void onDeleteDuplicate() {
		ArrayList<ColumnData> col = getSelectedItems();
		if (col == null || col.size() < 1) {
			UtilsGXT3.alert("Attention", "Attention no column selected!");
			return;
		}
		duplicatesSession = new DuplicatesSession(trId, col,
				DuplicatesSession.DuplicateOp.DELETE);

		TDGWTServiceAsync.INSTANCE.startDuplicates(duplicatesSession,
				new AsyncCallback<String>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert("Error Locked",
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert("Error Final",
											caught.getLocalizedMessage());
								} else {
									Log.debug("Delete Duplicates Error: "
											+ caught.getLocalizedMessage());
									UtilsGXT3.alert("Error Deleting Rows",
											caught.getLocalizedMessage());
								}
							}
						}
					}

					public void onSuccess(String taskId) {
						openMonitorDialog(taskId);
					}

				});

	}

	protected void close() {
		/*
		 * if (parent != null) { parent.close(); }
		 */
	}

	// /
	protected void openMonitorDialog(String taskId) {
		MonitorDialog monitorDialog = new MonitorDialog(taskId, eventBus);
		monitorDialog.addProgressDialogListener(this);
		monitorDialog.show();
	}

	@Override
	public void operationComplete(OperationResult operationResult) {
		ChangeTableWhy why = ChangeTableWhy.TABLEUPDATED;
		ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
				ChangeTableRequestType.DUPLICATES, operationResult.getTrId(),
				why);
		eventBus.fireEvent(changeTableRequestEvent);
		close();
	}

	@Override
	public void operationFailed(Throwable caught, String reason, String details) {
		UtilsGXT3.alert(reason, details);
		close();

	}

	@Override
	public void operationStopped(OperationResult operationResult,
			String reason, String details) {
		ChangeTableWhy why = ChangeTableWhy.TABLECURATION;
		ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
				ChangeTableRequestType.DUPLICATES, operationResult.getTrId(),
				why);
		eventBus.fireEvent(changeTableRequestEvent);
		close();

	}

	@Override
	public void operationAborted() {
		close();

	}

	@Override
	public void operationPutInBackground() {
		close();

	}

}
