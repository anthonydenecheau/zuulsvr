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
	public List get() {
		List resources = new ArrayList();
		Map routes= zuulProperties.getRoutes();

		routes.forEach((k,v)-> {;
			ZuulProperties.ZuulRoute p = (ZuulProperties.ZuulRoute) v;
			resources.add(swaggerResource(p.getServiceId(),"/api/"+p.getId()+"/api-docs","2.0"));
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
