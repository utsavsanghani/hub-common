package com.blackducksoftware.integration.hub.rest.oauth.server;

import org.restlet.routing.Router;

import com.blackducksoftware.integration.hub.rest.oauth.TokenManager;

public class DefaultOAuthApplication extends AbstractOAuthApplication {

	public DefaultOAuthApplication(final TokenManager tokenManager) {
		super(tokenManager);
	}

	@Override
	public void additionalRouterConfig(final Router router) {
		// no additional configuration
	}
}
