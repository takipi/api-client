package com.takipi.api.client.observe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingObserver implements Observer {

	private static final Logger logger = LoggerFactory.getLogger(LoggingObserver.class);

	private final boolean verbose;

	private LoggingObserver(boolean verbose) {
		this.verbose = verbose;
	}

	@Override
	public void observe(Operation operation, String url, String request, String response, int responseCode, long time) {

		if (verbose) {
			logger.debug("{} {} with {} took {}ms - resp {} / {}.", operation, url, request, time, responseCode,
					response);
		} else {
			logger.debug("{} {} took {}ms - resp {}.", operation, url, time, responseCode);
		}
	}

	public static Observer create(boolean verbose) {
		return new LoggingObserver(verbose);
	}
}
