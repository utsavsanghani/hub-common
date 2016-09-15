package com.blackducksoftware.integration.hub.rest.oauth.server;

import org.apache.commons.lang3.StringUtils;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.resource.Get;

import com.blackducksoftware.integration.hub.rest.oauth.AuthorizationState;
import com.blackducksoftware.integration.hub.rest.oauth.TokenManager;

public class TokenAuthorizationResource extends OAuthServerResource {

	@Get
	public void authorize() {
		// Use state if provided
		final String next = getRequest().getResourceRef().getQueryAsForm(true).getFirstValue("next");
		final AuthorizationState state = new AuthorizationState(getQueryValue("state"));

		if (!StringUtils.isNotBlank(state.getReturnUrl()) && next != null) {
			state.setReturnUrl(next);
		} else if (getRequest().getReferrerRef() != null) {
			state.setReturnUrl(getRequest().getReferrerRef().toString());
		}
		final TokenManager tokenManager = getTokenManager();
		if (tokenManager != null) {
			final Reference authUrl = tokenManager.getOAuthAuthorizationUrl(state);
			getResponse().redirectSeeOther(authUrl);
		} else {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
