package com.blackducksoftware.integration.hub.rest.oauth.server;

import org.restlet.resource.ServerResource;

import com.blackducksoftware.integration.hub.rest.oauth.TokenManager;

public class OAuthServerResource extends ServerResource {

	public TokenManager getTokenManager() {
		return (TokenManager) getContext().getAttributes().get(TokenManager.CONTEXT_ATTRIBUTE_KEY);
	}
}
