package com.blackducksoftware.integration.hub.rest.oauth.server;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public abstract class AbstractOAuthApplication extends Application {

	public AbstractOAuthApplication() {
		super();
	}

	@Override
	public Restlet createInboundRoot() {
		final Router router = new Router(getContext());
		// router.setFinderClass(getFinderClass());

		router.attach(ServerConstants.REGISTRATION, ClientRegistrationResource.class);
		router.attach(ServerConstants.CALLBACK, TokenAuthResponseResource.class);
		router.attach(ServerConstants.AUTH_GRANT, TokenAuthorizationResource.class);
		additionalRouterConfig(router);
		return router;
	}

	public abstract void additionalRouterConfig(final Router router);
}
