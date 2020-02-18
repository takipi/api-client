package com.takipi.api.client.util.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.ObjectUtil;

public class GroupSettings {
	public static final String REGEX_ESCAPE = "/";
	public static final String CATEGORY_PREFIX = "-";

	public static class GroupFilter {
		public final Collection<String> values;
		public final Collection<Pattern> patterns;

		private GroupFilter(Collection<String> values, Collection<Pattern> patterns) {
			this.values = new ArrayList<>(values);
			this.patterns = new ArrayList<>(patterns);
		}

		public boolean isEmpty() {
			return ((CollectionUtil.safeIsEmpty(values)) && (CollectionUtil.safeIsEmpty(patterns)));
		}

		public boolean filter(String s) {

			for (String value : values) {

				if (s.contains(value)) {
					return false;
				}
			}

			for (Pattern pattern : patterns) {

				if (pattern.matcher(s).find()) {
					return false;
				}
			}

			return true;
		}

		public static GroupFilter from(Collection<String> values) {
			List<String> filterValues = new ArrayList<>();
			List<Pattern> filterPatterns = new ArrayList<>();

			for (String value : values) {

				if (isRegexValue(value)) {
					String regex = value.substring(1, value.length() - 1);
					filterPatterns.add(Pattern.compile(regex));
				} else {
					filterValues.add(value);
				}
			}

			return GroupFilter.from(filterValues, filterPatterns);
		}

		public static GroupFilter from(Collection<String> values, Collection<Pattern> patterns) {
			return new GroupFilter(values, patterns);
		}

		private static boolean isRegexValue(String value) {
			return ((value.length() > 2) && (value.startsWith(REGEX_ESCAPE)) && (value.endsWith(REGEX_ESCAPE)));
		}
	}

	public static class Group {

		public String name;
		public List<String> values;

		public GroupFilter getFilter() {
			return GroupFilter.from(getValues());
		}

		public Collection<String> getValues() {
			if (CollectionUtil.safeIsEmpty(values)) {
				return Collections.emptyList();
			}

			List<String> result = new ArrayList<>();

			for (String value : values) {
				result.addAll(Arrays.asList(value.split(ServiceSettingsData.ARRAY_SEPERATOR)));
			}

			return result;
		}

		public String toGroupName() {
			if (!isGroup(name)) {
				return GroupSettings.toGroupName(name);
			}

			return name;
		}
	}

	public List<Group> groups;

	public Collection<Group> getGroups() {
		if (groups == null) {
			return Collections.emptyList();
		}

		return groups;
	}

	public static String fromGroupName(String value) {
		if ((value == null) || (!isGroup(value))) {
			return value;
		}

		return value.substring(CATEGORY_PREFIX.length());
	}

	public static String toGroupName(String value) {
		if (!isGroup(value)) {
			return CATEGORY_PREFIX + value;
		} else {
			return value;
		}
	}

	public static boolean isGroup(String name) {
		return name.startsWith(CATEGORY_PREFIX);
	}

	public GroupFilter getExpandedFilter(Collection<String> values) {
		return GroupFilter.from(expandList(values));
	}

	public Collection<String> expandList(Collection<String> values) {
		List<String> result = new ArrayList<>();

		for (String value : values) {
			if (GroupSettings.isGroup(value)) {
				result.addAll(getGroupValues(value));
			} else {
				result.add(value);
			}
		}

		return result;
	}

	public GroupFilter getAllGroupFilter() {
		return GroupFilter.from(expandList(getAllGroupValues()));
	}

	public Collection<String> getAllGroupNames(boolean includePrefix) {
		if (groups == null) {
			return Collections.emptyList();
		}

		List<String> result = new ArrayList<>();

		for (Group group : groups) {
			if (includePrefix) {
				result.add(toGroupName(group.name));

			} else {
				result.add(group.name);
			}
		}

		return result;
	}

	public Collection<String> getAllGroupValues() {
		if (groups == null) {
			return Collections.emptyList();
		}

		List<String> result = new ArrayList<>();

		for (Group group : groups) {
			result.addAll(group.getValues());
		}

		return result;
	}

	public Group getGroup(String groupName) {
		if (groups == null) {
			return null;
		}

		String value;
		int index = groupName.indexOf(CATEGORY_PREFIX);

		if (index >= 0) {
			value = groupName.substring(index + 1);
		} else {
			value = groupName;
		}

		for (Group group : groups) {
			if (ObjectUtil.equal(group.name, value)) {
				return group;
			}
		}

		return null;
	}

	public Collection<String> getGroupValues(String groupName) {
		Group group = getGroup(groupName);

		if (group == null) {
			return Collections.emptyList();
		}

		return group.getValues();
	}
}
