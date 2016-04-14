package com.blackducksoftware.integration.hub.logging;

public class NullLogger implements IntLogger {

	@Override
	public void info(final String txt) {

	}

	@Override
	public void error(final Throwable e) {

	}

	@Override
	public void error(final String txt, final Throwable t) {

	}

	@Override
	public void error(final String txt) {

	}

	@Override
	public void warn(final String txt) {

	}

	@Override
	public void trace(final String txt) {

	}

	@Override
	public void trace(final String txt, final Throwable t) {

	}

	@Override
	public void debug(final String txt) {

	}

	@Override
	public void debug(final String txt, final Throwable t) {

	}

	@Override
	public void setLogLevel(final LogLevel logLevel) {

	}

	@Override
	public LogLevel getLogLevel() {
		return null;
	}

}
