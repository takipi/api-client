package com.takipi.common.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;

public class IOUtil {
	public static <T> T readFromResource(String resource, Class<T> clazz) {
		return readFromResource(resource, clazz, new Gson());
	}

	public static <T> T readFromResource(String resource, Class<T> clazz, Gson gson) {
		T result = null;

		InputStream stream = null;

		try {
			stream = clazz.getResourceAsStream(resource);

			if (stream != null) {
				result = gson.fromJson(IOUtils.toString(stream, Charset.defaultCharset()), clazz);
			}
		} catch (Exception e) {
		} finally {
			closeQuietly(stream);
		}

		return result;
	}

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
