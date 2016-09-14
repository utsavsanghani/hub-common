package com.blackducksoftware.integration.hub.rest.oauth.server;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.blackducksoftware.integration.hub.rest.oauth.AuthenticationState;
import com.blackducksoftware.integration.hub.rest.oauth.TokenManager;

public class TokenAuthenticateResource extends ServerResource {

	private final TokenManager tokenManager;

	public TokenAuthenticateResource(final TokenManager tokenManager) {
		this.tokenManager = Objects.requireNonNull(tokenManager);
	}

	@Get
	public void authenticate() {
		// Use state if provided
		final String next = getRequest().getResourceRef().getQueryAsForm(true).getFirstValue("next");
		final AuthenticationState state = new AuthenticationState(getQueryValue("state"));

		if (!StringUtils.isNotBlank(state.getReturnUrl()) && next != null) {
			state.setReturnUrl(next);
		} else if (getRequest().getReferrerRef() != null) {
			state.setReturnUrl(getRequest().getReferrerRef().toString());
		}

		if (tokenManager != null) {
			final Reference authUrl = tokenManager.getOAuthAuthorizationUrl(state);
			getResponse().redirectSeeOther(authUrl);
		} else {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
