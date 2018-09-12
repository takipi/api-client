package com.takipi.common.api.util;

import com.google.common.base.Strings;
import com.takipi.common.api.consts.ApiConstants;

public class ValidationUtil {
	public static boolean isLegalServiceId(String serviceId) {
		return isLegalId(serviceId, ApiConstants.SERVICES_PREFIX);
	}

	public static boolean isLegalViewId(String viewId) {
		return isLegalId(viewId, ApiConstants.VIEWS_PREFIX);
	}

	public static boolean isLegalEventId(String eventId) {
		return ((isLegalId(eventId, ApiConstants.PACK_PREFIX)) || (isInteger(eventId)));
	}

	public static boolean isLegalId(String id, String prefix) {
		if (Strings.isNullOrEmpty(id)) {
			return false;
		}

		if (!id.startsWith(prefix)) {
			return false;
		}

		return isInteger(id.substring(prefix.length()));
	}

	private static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	// These values match valid expected values of the Api Server.
	//
	public enum GraphType {
		view, entrypoint, event
	}

	// These values match valid expected values of the Api Server.
	//
	public enum VolumeType {
		hits, invocations, all,
	}
}
