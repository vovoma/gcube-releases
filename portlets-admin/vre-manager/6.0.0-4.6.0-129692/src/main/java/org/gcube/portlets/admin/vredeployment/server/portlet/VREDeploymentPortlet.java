
package org.gcube.portlets.admin.vredeployment.server.portlet;

import javax.portlet.GenericPortlet;
import javax.portlet.ActionRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderResponse;
import javax.portlet.PortletException;
import java.io.IOException;
import javax.portlet.PortletRequestDispatcher;

import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * VREDeploymentPortlet Portlet Class
 * @author massi
 */
public class VREDeploymentPortlet extends GenericPortlet {

	public void doView(RenderRequest request, RenderResponse response)
			throws PortletException, IOException {

		ScopeHelper.setContext(request);
		
	    PortletRequestDispatcher dispatcher =
	        getPortletContext().getRequestDispatcher("/WEB-INF/jsp/VREDeploymentPortlet_view.jsp");
	    dispatcher.include(request, response);
		
	}


	public void processAction(ActionRequest request, ActionResponse response)
			throws PortletException, IOException {

	}

}
