package com.takipi.common.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class IOUtil {
	public static void closeQuietly(final Closeable closeable) {
		try {
			if (closeable != null) {
				closeable.close();
			}
		} catch (final IOException ioe) {
			// ignore
		}
	}

	public static void closeQuietly(final InputStream input) {
		closeQuietly((Closeable) input);
	}
}
