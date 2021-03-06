package org.gcube.portlets.user.statisticalmanager.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gcube.common.homelibrary.home.Home;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalImage;
import org.gcube.contentmanager.storageclient.model.protocol.smp.SMPURLConnection;

public class StorageUtil {

	private static Logger logger = Logger.getLogger(StorageUtil.class);

	public static InputStream getStorageClientInputStream(String url)
			throws Exception {
		
		try {

			URL u = new URL(null, url, new URLStreamHandler() {

				@Override
				protected URLConnection openConnection(URL u)
						throws IOException {

					return new SMPURLConnection(u);
				}
			});
			return u.openConnection().getInputStream();

		} catch (Throwable e) {
			logger.error("Error in StorageUtil: " + e.getLocalizedMessage());
			e.printStackTrace();
			throw new Exception(e.getLocalizedMessage());
			
		}
	}

	public static String extractLocation(String url) {
		String[] loc = url.split("//");
		// logger.trace("url extracted: " + loc[1]);
		return loc[1];
	}

	public static String getFileName(String url) {
		String[] urlParam = url.split("\\?");
		String location = extractLocation(urlParam[0]);

		try {
			return location.split("/")[1];
		} catch (Exception e) {
			// e.printStackTrace();
			return location;
		}
	}

	/**
	 * @return
	 */
	public static Map<String, String> getFilesUrlFromFolderUrl(
			String serviceClass, String serviceName, String url,
			String username, String scope) throws Exception {

		Home home = HomeLibrary.getHomeManagerFactory().getHomeManager()
				.getHome(username);
		Map<String, String> map = new LinkedHashMap<String, String>();
		Workspace ws = home.getWorkspace();
		WorkspaceItem folderItem = ws.getItemByPath(url);
		// logger.trace("Type of workspace item is : "
		// + folderItem.getType().toString());
		WorkspaceFolder folder = (WorkspaceFolder) folderItem;
		List<WorkspaceItem> childrenList = folder.getChildren();
		for (WorkspaceItem item : childrenList) {
			ExternalImage file = (ExternalImage) item;
			String name = item.getName();
			String absoluteUrlFile = file.getPublicLink();

			map.put(name, absoluteUrlFile);
		}

		return map;
	}
	

}
