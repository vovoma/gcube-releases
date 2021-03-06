package org.gcube.portlets.widgets.ckandatapublisherwidget.client;

import java.util.List;

import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.DatasetMetadataBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.GroupBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.LicensesBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.MetaDataProfileBean;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.ResourceElementBean;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * CKAN publisher services.
 * @author Costantino Perciante at ISTI-CNR 
 * (costantino.perciante@isti.cnr.it)
 */
@RemoteServiceRelativePath("ckanservices")
public interface CKanPublisherService extends RemoteService {

	/**
	 * Retrieve the list of licenses to show to the user.
	 * @return a LicenseBean on success, <b>null</b> on error.
	 */
	LicensesBean getLicenses();

	/**
	 * Retrieve the list of profiles for a given organization name .
	 * @return a List<MetaDataProfileBean> on success, <b>null</b> on error.
	 */
	List<MetaDataProfileBean> getProfiles(String orgName);

	/**
	 * Retrieve a partially filled bean given a folder id and its owner.
	 * @param folderId
	 * @return @return a DatasetMetadataBean on success, <b>null</b> on error.
	 */
	DatasetMetadataBean getDatasetBean(String folderId);

	/**
	 * Try to create such dataset starting from the information contained into the toCreate bean.
	 * @param toCreate
	 * @return the sent bean filled with the needed information
	 */
	DatasetMetadataBean createCKanDataset(DatasetMetadataBean toCreate);

	/**
	 * Add this resource to the dataset whose id is datasetId
	 * @param resource
	 * @param datasetId
	 */
	ResourceElementBean addResourceToDataset(ResourceElementBean resource, String datasetId);

	/**
	 * Delete this resource from the dataset with id datasetId
	 * @param resource
	 * @param datasetId
	 * @return <b>true</b> on success, false otherwise
	 */
	boolean deleteResourceFromDataset(ResourceElementBean resource);

	/**
	 * Given the title the user wants to give to the new product to create, a check is performed
	 * to understand if a dataset with the proposed title (and so the id generated at server side) already exists
	 * @param title
	 * @return true if it exists, false otherwise
	 */
	boolean datasetIdAlreadyExists(String title);

	/**
	 * Retrieve the list of groups the user can choose to associate this product with.
	 * @return a list of groups' beans
	 */
	List<GroupBean> getUserGroups();

	//	/**
	//	 * Return a tree object representing the whole folder hierarchy
	//	 * @param folderId
	//	 * @return ResourceElementBean
	//	 */
	//	ResourceElementBean getTreeFolder(String folderId);
}
