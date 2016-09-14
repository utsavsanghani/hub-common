package com.blackducksoftware.integration.hub.rest.oauth;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.restlet.data.Reference;
import org.restlet.ext.oauth.AccessTokenClientResource;
import org.restlet.ext.oauth.GrantType;
import org.restlet.ext.oauth.OAuthException;
import org.restlet.ext.oauth.OAuthParameters;
import org.restlet.ext.oauth.ResponseType;
import org.restlet.ext.oauth.internal.Token;

public class TokenManager {

	private final OAuthConfiguration configuration;
	// Internal storage for tokens - done in-memory as a simple example
	private Token userToken = null;
	private Token clientToken = null;

	public TokenManager(final OAuthConfiguration configuration) {
		this.configuration = configuration;
	}

	public OAuthConfiguration getConfiguration() {
		return configuration;
	}

	public boolean authenticationRequired() {
		return userToken == null;
	}

	public void updateClientId(final String clientId) {
	}

	public Reference getOAuthAuthorizationUrl(final AuthenticationState state) {
		final Reference reference = new Reference(configuration.getLinks().getAuthorizeUrl());

		final OAuthParameters parameters = new OAuthParameters();
		parameters.responseType(ResponseType.code);
		parameters.add(OAuthParameters.CLIENT_ID, configuration.getClientId());
		parameters.redirectURI(configuration.getAuthResponseUrl());
		parameters.scope(new String[] { "read" });

		if (state != null) {
			final String stateUrlValue = state.encode();

			if (StringUtils.isNotBlank(stateUrlValue)) {
				parameters.state(stateUrlValue);
			}
		}

		return parameters.toReference(reference.toString());
	}

	private AccessTokenClientResource getTokenResource() {
		final Reference reference = new Reference(configuration.getLinks().getTokenUrl());

		final AccessTokenClientResource tokenResource = new AccessTokenClientResource(reference);
		// Client ID here and not on OAuthParams so that it can auto-add to
		// parameters internally. null auth so it does
		// NPE trying to format challenge response
		tokenResource.setClientCredentials(configuration.getClientId(), null);
		tokenResource.setAuthenticationMethod(null);

		return tokenResource;
	}

	private OAuthParameters getAccessTokenParameters(final String code) {
		final OAuthParameters parameters = new OAuthParameters();
		parameters.grantType(GrantType.authorization_code);
		// TODO check if this is the correct URL
		parameters.redirectURI(configuration.getLinks().getAuthorizeUrl());
		parameters.code(code);

		return parameters;
	}

	private OAuthParameters getClientTokenParameters() {
		final OAuthParameters parameters = new OAuthParameters();
		parameters.grantType(GrantType.client_credentials);
		parameters.scope(new String[] { "read", "write" });

		return parameters;
	}

	private OAuthParameters getRefreshTokenParameters(final String refreshToken) {
		final OAuthParameters parameters = new OAuthParameters();
		parameters.grantType(GrantType.refresh_token);
		parameters.refreshToken(refreshToken);

		return parameters;
	}

	public void exchangeForToken(final String authorizationCode) throws IOException {
		final AccessTokenClientResource tokenResource = getTokenResource();
		try {
			userToken = tokenResource.requestToken(getAccessTokenParameters(authorizationCode));
		} catch (JSONException | OAuthException e) {
			throw new IOException(e);
		}
	}

	public void refreshToken(final AccessType accessType) throws IOException {
		if (AccessType.USER.equals(accessType)) {
			refreshUserAccessToken();
		} else if (AccessType.CLIENT.equals(accessType)) {
			refreshClientAccessToken();
		}
	}

	public TokenClientResource createClientResource(final Reference reference, final AccessType accessType)
			throws IOException {
		final Token token = getToken(accessType);
		return new TokenClientResource(reference, token);
	}

	private Token getToken(final AccessType accessType) throws IOException {
		Token result = null;

		if (AccessType.USER.equals(accessType)) {
			if (userToken == null) {
				throw new IllegalStateException("User token not populated");
			} else {
				result = userToken;
			}
		} else if (AccessType.CLIENT.equals(accessType)) {
			if (clientToken == null) {
				refreshClientAccessToken();
			}
			result = clientToken;
		}

		return result;
	}

	private void refreshUserAccessToken() throws IOException {
		if (userToken != null) {
			final AccessTokenClientResource tokenResource = getTokenResource();
			try {
				userToken = tokenResource.requestToken(getRefreshTokenParameters(userToken.getRefreshToken()));
			} catch (JSONException | OAuthException e) {
				throw new IOException(e);
			}
		} else {
			throw new IllegalStateException("No token present to refresh");
		}
	}

	private void refreshClientAccessToken() throws IOException {
		final AccessTokenClientResource tokenResource = getTokenResource();
		try {
			clientToken = tokenResource.requestToken(getClientTokenParameters());
		} catch (JSONException | OAuthException e) {
			throw new IOException(e);
		}
	}
}
