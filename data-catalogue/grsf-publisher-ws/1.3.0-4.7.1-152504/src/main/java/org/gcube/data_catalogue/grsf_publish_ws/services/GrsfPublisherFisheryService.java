package org.gcube.data_catalogue.grsf_publish_ws.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.utils.Caller;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.Common;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.DeleteProductBean;
import org.gcube.data_catalogue.grsf_publish_ws.json.input.FisheryRecord;
import org.gcube.data_catalogue.grsf_publish_ws.json.output.ResponseBean;
import org.gcube.data_catalogue.grsf_publish_ws.json.output.ResponseCreationBean;
import org.gcube.data_catalogue.grsf_publish_ws.utils.HelperMethods;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Product_Type;
import org.gcube.data_catalogue.grsf_publish_ws.utils.groups.Sources;
import org.gcube.datacatalogue.ckanutillibrary.server.DataCatalogue;
import org.gcube.datacatalogue.ckanutillibrary.shared.ResourceBean;
import org.slf4j.LoggerFactory;

import eu.trentorise.opendata.jackan.model.CkanDataset;

/**
 * Fishery web service methods.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
@Path("{source:firms|FIRMS|ram|RAM|grsf|GRSF|FishSource|fishsource}/fishery/")
public class GrsfPublisherFisheryService {

	// the context
	@Context ServletContext contextServlet;

	// Logger
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(GrsfPublisherFisheryService.class);

	@GET
	@Path("hello")
	@Produces(MediaType.TEXT_PLAIN)
	public Response hello(){
		return Response.ok("Hello.. Fishery service is here").build();
	}

	@GET
	@Path("get-licenses")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getLicenses(){
		Status status = Status.OK;
		Map<String, String> licenses = CommonServiceUtils.getLicenses();
		if(licenses == null)
			status = Status.INTERNAL_SERVER_ERROR;
		return Response.status(status).entity(licenses).build();
	}

	@POST
	@Path("publish-product")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response publishFishery(
			@NotNull(message="record cannot be null") 
			@Valid FisheryRecord record,
			@PathParam("source") String source) 
					throws ValidationException{

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String context = ScopeProvider.instance.get();
		String token = SecurityTokenProvider.instance.get();

		logger.info("Incoming request for creating a fishery record = " + record + ".\nRequest comes from user " + username + " in context " + context);

		ResponseCreationBean responseBean = new ResponseCreationBean();
		Status status = Status.INTERNAL_SERVER_ERROR;
		String id = ""; // id of the created record, if everything went ok

		try{

			// Cast the source to the accepted ones
			Sources sourceInPath = Sources.onDeserialize(source);

			DataCatalogue catalogue = HelperMethods.getDataCatalogueRunningInstance(context);
			if(catalogue == null){
				throw new Exception("There was a problem while serving your request. No catalogue instance was found!");
			}else{

				String apiKey = catalogue.getApiKeyFromUsername(username);
				String organization =  HelperMethods.retrieveOrgNameFromScope(context); //"grsf_admin";
				CommonServiceUtils.hasAdminRole(username, catalogue, apiKey, organization);

				// retrieve the user's email and fullname
				String authorMail = HelperMethods.getUserEmail(context, token);
				String authorFullname = HelperMethods.getUserFullname(context, token);

				if(authorMail == null || authorFullname == null){
					throw new Exception("Sorry but there was not possible to retrieve your fullname/email!");
				}

				// The name of the product will be the uuid of the kb. The title will be the fishery's fishery_name. Fishery has also the constraint that
				// fishing area and jurisdiction area cannot be empty at the same time
				String futureName = record.getUuid();
				String futureTitle = record.getFisheryName();

				// check name
				CommonServiceUtils.checkName(futureName, catalogue);

				Map<String, List<String>> customFields = record.getExtrasFields();
				Set<String> tags = new HashSet<String>();						
				Set<String> groups = new HashSet<String>();
				List<ResourceBean> resources = record.getExtrasResources();

				// validate end set sources
				CommonServiceUtils.validateRecordAndMapFields(apiKey, context, contextServlet, sourceInPath, record, Product_Type.FISHERY, tags, customFields, groups, resources, username, futureTitle);

				// check the license id
				String license = null;
				if(record.getLicense() == null || record.getLicense().isEmpty())
					license = CommonServiceUtils.DEFAULT_LICENSE;
				else
					if(HelperMethods.existsLicenseId(record.getLicense(), catalogue))
						license = record.getLicense();
					else throw new Exception("Please check the license id!"); 

				long version = record.getVersion() == null ? 1 : record.getVersion();

				// set the visibility of the datatest according the context
				boolean publicDataset = context.equals((String)contextServlet.getInitParameter(HelperMethods.PUBLIC_CONTEX_KEY));

				// convert extras' keys to keys with namespace
				Map<String, String> namespaces = HelperMethods.getFieldToFieldNameSpaceMapping(HelperMethods.GENERIC_RESOURCE_NAME_MAP_KEY_NAMESPACES_FISHERY);

				if(namespaces == null)
					throw new Exception("Failed to retrieve the namespaces for the key fields!");

				customFields = HelperMethods.replaceFieldsKey(customFields, namespaces);

				logger.info("Invoking create method..");

				// create the product 
				id = catalogue.createCKanDatasetMultipleCustomFields(
						apiKey, 
						futureTitle, 
						futureName,
						organization, 
						authorFullname, 
						authorMail, 
						record.getMaintainer() == null? authorFullname : record.getMaintainer(), 
								record.getMaintainerContact() == null? authorMail : record.getMaintainerContact(), 
										version, 
										null, 
										license, 
										new ArrayList<String>(tags), 
										customFields, 
										resources, 
										publicDataset); 

				// post actions
				if(id != null){

					logger.info("Created record with identifier " + id);
					String description = "Short Name: " + record.getShortName();
					
					if(sourceInPath.equals(Sources.GRSF))
						description += ", GRSF Semantic Identifier: " + record.getFisheryId();
					
					CommonServiceUtils.actionsPostCreateOrUpdate(
							id, futureName, record, apiKey, username, organization, 
							null, responseBean, catalogue, namespaces, groups, context, token, futureTitle, authorFullname, 
							contextServlet, false, description);
					status = Status.CREATED;

				}else{
					throw new Exception("There was an error during the product generation, sorry");
				}
			}
		}catch(Exception e){
			logger.error("Failed to create fishery record" + e);
			status = Status.INTERNAL_SERVER_ERROR;
			responseBean.setError(e.getMessage());
		}

		return Response.status(status).entity(responseBean).build();
	} 

	@DELETE
	@Path("delete-product")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteFishery(
			@NotNull(message="input value is missing") 
			@Valid DeleteProductBean recordToDelete,
			@PathParam("source") String source) throws ValidationException{

		// retrieve context and username
		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String context = ScopeProvider.instance.get();

		ResponseCreationBean responseBean = new ResponseCreationBean();
		Status status = Status.INTERNAL_SERVER_ERROR;

		// check it is a fishery ...
		logger.info("Received call to delete product with id " + recordToDelete.getId() + ", checking if it is a fishery");
		try{

			DataCatalogue catalogue = HelperMethods.getDataCatalogueRunningInstance(context);

			// Cast the source to the accepted ones
			Sources sourceInPath = Sources.onDeserialize(source);
			logger.debug("The request is to delete a fishery object of source " + sourceInPath);

			// retrieve the catalogue instance
			String apiKey = catalogue.getApiKeyFromUsername(username);
			CkanDataset fisheryInCkan = catalogue.getDataset(recordToDelete.getId(), apiKey);

			if(fisheryInCkan == null){
				status = Status.NOT_FOUND;
				throw new Exception("There was a problem while serving your request. This item was not found");
			}

			// check it is in the right source and it is a fishery
			String grsfTypeValue = fisheryInCkan.getExtrasAsHashMap().get(Common.GRSF_DOMAIN_KEY);
			String systemTypeValue = fisheryInCkan.getExtrasAsHashMap().get(CommonServiceUtils.SYSTEM_TYPE);

			if(systemTypeValue.equalsIgnoreCase(source) && Product_Type.FISHERY.getOrigName().equals(grsfTypeValue)){

				logger.debug("Ok, this is a fishery of the right source, removing it");
				boolean deleted = catalogue.deleteProduct(fisheryInCkan.getId(), apiKey, true);

				if(deleted){
					logger.info("Fishery DELETED AND PURGED!");
					status = Status.OK;
					responseBean.setId(fisheryInCkan.getId());
				}
				else{
					status = Status.INTERNAL_SERVER_ERROR;
					throw new Exception("Request failed, sorry. Unable to delete/purge the fishery");
				}
			}else{
				status = Status.BAD_REQUEST;
				throw new Exception("The id you are using doesn't belong to a Fishery item having source " + source + "!");
			}
		}catch(Exception e){
			logger.error("Failed to delete this", e);
			responseBean.setError(e.getMessage());
		}

		return Response.status(status).entity(responseBean).build();
	}

	@GET
	@Path("get-fisheries-ids")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFisheriesIds(
			@PathParam("source") String source){

		logger.info("Received call to get fisheries with source " + source);

		Caller caller = AuthorizationProvider.instance.get();
		String context = ScopeProvider.instance.get();
		String username = caller.getClient().getId();
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.INTERNAL_SERVER_ERROR;
		List<String> datasetsIds = new ArrayList<String>();

		try{

			// Cast the source to the accepted ones
			Sources sourceInPath = Sources.onDeserialize(source);

			DataCatalogue catalogue = HelperMethods.getDataCatalogueRunningInstance(context);
			if(catalogue == null){
				throw new Exception("There was a problem while serving your request");
			}

			// if it is a request for GRSF records, we have Fishery - Stock groups, so it is easy.
			// For other cases, records needs to be parsed
			if(sourceInPath.equals(Sources.GRSF))
				datasetsIds = HelperMethods.getProductsInGroup(source + "-" + "fishery", catalogue);
			else{
				List<String> fullGroupListIds = HelperMethods.getProductsInGroup(source, catalogue);
				String apiKey = catalogue.getApiKeyFromUsername(username);
				for (String id : fullGroupListIds) {
					CkanDataset dataset = catalogue.getDataset(id, apiKey);
					if(dataset != null){
						String grsfType = dataset.getExtrasAsHashMap().get(Common.GRSF_DOMAIN_KEY);
						if(grsfType.equals(Product_Type.FISHERY.getOrigName()))
							datasetsIds.add(id);
					}
				}
			}
			status = Status.OK;
			responseBean.setResult(datasetsIds);
			responseBean.setSuccess(true);

		}catch(Exception e){
			logger.error("Failed to fetch this list of ids ", e);
			responseBean.setMessage(e.getMessage());
		}

		return Response.status(status).entity(responseBean).build();
	}

	@GET
	@Path("get-catalogue-id-and-url-from-name")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCatalogueIdAndUrlFromKBID(
			@QueryParam("name") String name){

		String context = ScopeProvider.instance.get();
		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		ResponseBean responseBean = new ResponseBean();
		Status status = Status.INTERNAL_SERVER_ERROR;

		logger.info("Received call to get the catalogue identifier for the product with name " + name);

		try{
			DataCatalogue catalogue = HelperMethods.getDataCatalogueRunningInstance(context);
			if(catalogue == null){
				throw new Exception("There was a problem while serving your request");
			}
			CkanDataset dataset = catalogue.getDataset(name, catalogue.getApiKeyFromUsername(username));
			if(dataset != null){
				Map<String, String> result = new HashMap<String, String>();
				result.put("id", dataset.getId());
				result.put("url", catalogue.getUnencryptedUrlFromDatasetIdOrName(dataset.getId()));
				responseBean.setResult(result);
				responseBean.setSuccess(true);
				status = Status.OK;
			}else{
				responseBean.setMessage("Unable to retrieve a catalogue item with name " + name);
			}
		}catch(Exception e){
			logger.error("Failed to retrieve this product", e);
			responseBean.setMessage(e.getMessage());
		}
		return Response.status(status).entity(responseBean).build();
	}

	@POST
	@Path("update-product")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateFishery(
			@NotNull(message="record cannot be null") 
			@Valid FisheryRecord record,
			@PathParam("source") String source) 
					throws ValidationException{

		Caller caller = AuthorizationProvider.instance.get();
		String username = caller.getClient().getId();
		String context = ScopeProvider.instance.get();
		String token = SecurityTokenProvider.instance.get();

		logger.info("Incoming request for updating a fishery record = " + record + ". Request comes from user " + username + " in context " + context);

		ResponseCreationBean responseBean = new ResponseCreationBean();
		Status status = Status.INTERNAL_SERVER_ERROR;

		// catalog id must be reported
		String catalogId = record.getCatalogId();

		try{

			if(catalogId == null || catalogId.isEmpty()){
				status = Status.BAD_REQUEST;
				throw new Exception("Please specify the 'catalog_id' property");
			}

			DataCatalogue catalogue = HelperMethods.getDataCatalogueRunningInstance(context);

			if(catalogue == null){
				throw new Exception("There was a problem while serving your request. No catalogue instance was found in this context!");
			}else{

				// get already published record and modify it
				String apiKey = catalogue.getApiKeyFromUsername(username);
				CkanDataset recordPublished = catalogue.getDataset(catalogId, apiKey);

				if(recordPublished == null)
					throw new Exception("A record with catalogue id " + catalogId + " does not exist!");

				// retrieve the user's email and fullname
				String authorMail = HelperMethods.getUserEmail(context, token);
				String authorFullname = HelperMethods.getUserFullname(context, token);

				if(authorMail == null || authorFullname == null){
					logger.debug("Author fullname or mail missing, cannot continue");
					throw new Exception("Sorry but there was not possible to retrieve your fullname/email!");
				}


				String organization =  HelperMethods.retrieveOrgNameFromScope(context); //"grsf_admin";

				// check he/she has admin role
				CommonServiceUtils.hasAdminRole(username, catalogue, apiKey, organization);

				// name, title, product url and  are going to remain unchanged (so we keep them from the publisher record);
				String name = recordPublished.getName();
				String title = recordPublished.getTitle();

				// Cast the source to the accepted ones
				Sources sourceInPath = Sources.onDeserialize(source);

				// load infos
				Map<String, List<String>> customFields = record.getExtrasFields();
				Set<String> tags = new HashSet<String>();						
				Set<String> groups = new HashSet<String>();
				List<ResourceBean> resources = record.getExtrasResources();

				// validate end set sources
				CommonServiceUtils.validateRecordAndMapFields(
						apiKey,
						context, 
						contextServlet, 
						sourceInPath, 
						record, 
						Product_Type.FISHERY, 
						tags, 
						customFields, 
						groups, 
						resources, 
						username, 
						title);

				// check the license id
				String license = null;
				if(record.getLicense() == null || record.getLicense().isEmpty())
					license = CommonServiceUtils.DEFAULT_LICENSE;
				else
					if(HelperMethods.existsLicenseId(record.getLicense(), catalogue))
						license = record.getLicense();
					else throw new Exception("Please check the license id!"); 

				long version = record.getVersion() == null ? 1 : record.getVersion();

				// set the visibility of the datatest according the context
				boolean publicDataset = context.equals((String)contextServlet.getInitParameter(HelperMethods.PUBLIC_CONTEX_KEY));

				// add the SYSTEM_TYPE
				customFields.put(CommonServiceUtils.SYSTEM_TYPE, Arrays.asList(sourceInPath.getOrigName()));

				// convert extras' keys to keys with namespace
				Map<String, String> namespaces = HelperMethods.getFieldToFieldNameSpaceMapping(HelperMethods.GENERIC_RESOURCE_NAME_MAP_KEY_NAMESPACES_FISHERY);

				if(namespaces == null)
					throw new Exception("Failed to retrieve the namespaces for the key fields!");

				// retrieve the url
				String modifiedUUIDKey = namespaces.containsKey(CommonServiceUtils.ITEM_URL_FIELD) ? namespaces.get(CommonServiceUtils.ITEM_URL_FIELD) : CommonServiceUtils.ITEM_URL_FIELD;
				String itemUrl = recordPublished.getExtrasAsHashMap().get(modifiedUUIDKey);
				customFields.put(CommonServiceUtils.ITEM_URL_FIELD, Arrays.asList(itemUrl));

				// replace fields
				customFields = HelperMethods.replaceFieldsKey(customFields, namespaces);

				logger.info("Invoking update method..");

				// update the product 
				String id = catalogue.updateCKanDataset(
						apiKey, 
						catalogId, 
						title, name, 
						organization, 
						authorFullname, 
						authorMail, 
						record.getMaintainer(), 
						record.getMaintainerContact(), 
						version, 
						HelperMethods.removeHTML(record.getDescription()),
						license, 
						new ArrayList<String>(tags), 
						null, // remove any previous group 
						customFields, 
						resources, 
						publicDataset);

				if(id != null){
					logger.info("Item updated!");
					CommonServiceUtils.actionsPostCreateOrUpdate(
							id, name, record, apiKey, username, organization, 
							itemUrl, responseBean, catalogue, namespaces, groups, context, token, title, authorFullname, 
							contextServlet, false, null);
					status = Status.OK;
				}else{
					throw new Exception("There was an error during the item updated, sorry");
				}
			}
		}catch(Exception e){
			logger.error("Failed to update fishery record" + e);
			status = Status.INTERNAL_SERVER_ERROR;
			responseBean.setError(e.getMessage());
		}
		return Response.status(status).entity(responseBean).build();
	}
}