package com.blackducksoftware.integration.hub.rest.oauth.server;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.blackducksoftware.integration.hub.rest.oauth.TokenManager;

public class ClientRegistrationResource extends ServerResource {

	private final TokenManager tokenManager;

	public ClientRegistrationResource(final TokenManager tokenManager) {
		this.tokenManager = Objects.requireNonNull(tokenManager);
	}

	@Post
	public void accept(final JsonRepresentation entity) {
		final String clientId = entity.getJsonObject().getString("clientId");

		if (!StringUtils.isNotBlank(clientId)) {
			tokenManager.updateClientId(clientId);
		} else {
			getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "No client ID/hub URL provided");
		}
	}
}
