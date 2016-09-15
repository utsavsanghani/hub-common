package com.blackducksoftware.integration.hub.rest.oauth.server;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.blackducksoftware.integration.hub.rest.oauth.AccessType;
import com.blackducksoftware.integration.hub.rest.oauth.AuthorizationState;
import com.blackducksoftware.integration.hub.rest.oauth.TokenClientResource;
import com.blackducksoftware.integration.hub.rest.oauth.TokenManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class TokenAuthResponseResource extends ServerResource {
	private final TokenManager tokenManager;

	public TokenAuthResponseResource(final TokenManager tokenManager) {
		this.tokenManager = tokenManager;
	}

	@Get
	public void accept() {
		final String authorizationCode = getQuery().getFirstValue("code");
		final String urlState = getQuery().getFirstValue("state");

		final AuthorizationState state = new AuthorizationState(urlState);
		final Reference redirectTo;

		if (!StringUtils.isNotBlank(state.getReturnUrl())) {
			redirectTo = new Reference(state.getReturnUrl());
		} else {
			redirectTo = new Reference(tokenManager.getConfiguration().getLocalBaseUrl());
		}

		try {
			tokenManager.exchangeForToken(authorizationCode);

			// Update authorization status
			// TODO figure this one out....
			final Reference extensionRef = new Reference("");// configurationService.getHubConfiguration().get().getExtensionUri();
			final TokenClientResource resource = tokenManager.createClientResource(extensionRef, AccessType.CLIENT);
			try {
				updateAuthenticated(resource);
			} catch (final ResourceException e) {
				if (Status.CLIENT_ERROR_UNAUTHORIZED.equals(e.getStatus())) {
					// Try one more time, after refreshing tokens
					tokenManager.refreshToken(AccessType.CLIENT);
					updateAuthenticated(resource);
				} else {
					throw e;
				}
			}
			getResponse().redirectSeeOther(redirectTo);
		} catch (final IOException e) {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL, e);
		}
	}

	private void updateAuthenticated(final ClientResource resource) throws IOException {
		final Representation rep = resource.get();
		final JsonParser parser = new JsonParser();
		try {
			final JsonElement json = parser.parse(rep.getText());
			json.getAsJsonObject().add("authenticated", new JsonPrimitive(true));

			resource.put(new JsonRepresentation(json.toString()));
		} catch (final IOException e) {
			throw e;
		}
	}
}
