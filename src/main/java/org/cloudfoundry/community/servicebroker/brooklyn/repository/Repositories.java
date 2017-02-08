package org.cloudfoundry.community.servicebroker.brooklyn.repository;

import javax.ws.rs.core.Response;

import org.apache.brooklyn.rest.client.BrooklynApi;
import org.apache.brooklyn.rest.domain.ApplicationSummary;
import org.apache.brooklyn.rest.domain.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO Consider using @Async on calls to the REST api
public class Repositories {
    
    private static final Logger LOG = LoggerFactory.getLogger(Repositories.class);

	public static void createRepositories(BrooklynApi brooklynApi) {
		String name = "service-broker-records";
		String bindingRepoName = "service-instance-binding-repository";
		String instanceRepoName = "service-instance-repository";
		
		if(!repositoryExists(brooklynApi, name)){
			Response response = brooklynApi.getApplicationApi().createFromForm(
				String.format("{ \"name\": \"%s\","
						+ "\"services\": [{"
						+ "\"type\": \"org.apache.brooklyn.entity.stock.BasicApplication\","
						+ "\"brooklyn.children\":[{\"name\": \"%s\", \"type\": \"org.apache.brooklyn.entity.stock.BasicEntity\"},"
						+ "{\"name\": \"%s\", \"type\": \"org.apache.brooklyn.entity.stock.BasicEntity\"}]"
						+ "}]"
						+ "}", name, bindingRepoName, instanceRepoName));
			TaskSummary entity = BrooklynApi.getEntity(response, TaskSummary.class);
			LOG.info("[entity={}]",  entity);
		}
	}
	
	private static boolean repositoryExists(BrooklynApi brooklynApi, String name) {
		try {
			ApplicationSummary repo = brooklynApi.getApplicationApi().get(name);
			LOG.info("Got repo [name={}, status={}]", name, repo.getStatus());
			return true;
		} catch (Exception e) {
		    LOG.error("Could not find repo: {}", name);
			return false;
		}
	}

}
