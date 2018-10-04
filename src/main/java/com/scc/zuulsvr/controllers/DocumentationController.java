package com.scc.zuulsvr.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties.ZuulRoute;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

@Component
@Primary
@EnableAutoConfiguration
public class DocumentationController implements SwaggerResourcesProvider {

	@Autowired
	private ZuulProperties zuulProperties ;

	@Override
	public List<SwaggerResource> get() {
		List<SwaggerResource> resources = new ArrayList<SwaggerResource>();
		Map<String, ZuulRoute> routes= zuulProperties.getRoutes();

		routes.forEach((k,v)-> {;
			ZuulProperties.ZuulRoute p = (ZuulProperties.ZuulRoute) v;
			// TODO : automate version (via config.)
			if (p.getServiceId().equals("dogservice")) {
				resources.add(swaggerResource(p.getServiceId()+"-v1","/api/"+p.getId()+"/api-docs?group=dogservice-1.0","2.0"));
				resources.add(swaggerResource(p.getServiceId()+"-v2","/api/"+p.getId()+"/api-docs?group=dogservice-2.0","2.0"));
			} else
				resources.add(swaggerResource(p.getServiceId()+"-v1","/api/"+p.getId()+"/api-docs","2.0"));
		});
		
		return resources;
	}

	private SwaggerResource swaggerResource(String name, String location, String version) {
		SwaggerResource swaggerResource = new SwaggerResource();
		swaggerResource.setName(name);
		swaggerResource.setLocation(location);
		swaggerResource.setSwaggerVersion(version);
		return swaggerResource;
	}

}
