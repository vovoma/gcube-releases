package org.gcube.data.spd.catalogueoflife;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;
import java.util.List;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.spd.catalogueoflife.capabilities.ExpansionCapabilityImpl;
import org.gcube.data.spd.model.exceptions.StreamException;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestExpansionCapability {
	static Logger logger = LoggerFactory.getLogger(TestExpansionCapability.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		CatalogueOfLifePlugin a = new CatalogueOfLifePlugin();
		
		SimpleQuery query = queryFor(ServiceEndpoint.class);

		query.addCondition("$resource/Profile/Category/text() eq 'BiodiversityRepository' and $resource/Profile/Name eq 'CatalogueOfLife' ");
		ScopeProvider.instance.set("/gcube/devsec");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);

		List<ServiceEndpoint> resources = client.submit(query);

		System.out.println(resources.size());
		
		if(resources.size() != 0) {	   
			try {
				a.initialize(resources.get(0));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		ExpansionCapabilityImpl b = new ExpansionCapabilityImpl();
		b.getSynonyms(new ObjectWriter<String>() {
			
			@Override
			public boolean write(StreamException error) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean write(String t) {
					System.out.println(t);
				return false;
			}
			
			@Override
			public boolean isAlive() {
//				System.out.println("isAlive");
				return true;
			}
		}, "rosa");

	}

}
