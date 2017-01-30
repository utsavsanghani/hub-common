package com.blackducksoftware.integration.hub.swagger;

public class DataService {
	private ApiServicesFactory apiFactory;

	public DataService(ApiServicesFactory apiFactory) {
		this.apiFactory = apiFactory;
	}

	public ApiServicesFactory getFactory() {
		return apiFactory;
	}
}
