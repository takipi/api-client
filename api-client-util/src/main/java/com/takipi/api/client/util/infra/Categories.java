package com.takipi.api.client.util.infra;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.Pair;

public class Categories {
	private static final String DEFAULT_CATEGORIES = "infra/categories.json";
	private static final Categories EMPTY_CATEGORIES = new Categories();

	private static boolean initialized;
	private static volatile Categories instance = null;

	public static Categories defaultCategories() {
		if ((instance == null) && (!initialized)) {
			synchronized (Categories.class) {
				if ((instance == null) && (!initialized)) {
					initialized = true;

					InputStream stream = null;

					try {
						ClassLoader classLoader = Categories.class.getClassLoader();

						stream = classLoader.getResourceAsStream(DEFAULT_CATEGORIES);

						if (stream == null) {
							return null;
						}

						instance = (new Gson()).fromJson(IOUtils.toString(stream, Charset.defaultCharset()),
								Categories.class);
					} catch (Exception e) {
						instance = EMPTY_CATEGORIES;
					} finally {
						IOUtils.closeQuietly(stream);
					}
				}
			}
		}

		return instance;
	}

	public List<Category> categories;

	public Set<String> getCategories(String className) {
		if (CollectionUtil.safeIsEmpty(categories)) {
			return Collections.emptySet();
		}

		for (Category category : categories) {
			if ((CollectionUtil.safeIsEmpty(category.names)) || (CollectionUtil.safeIsEmpty(category.labels))) {
				continue;
			}

			for (String name : category.names) {
				if (className.startsWith(name)) {
					return Sets.newHashSet(category.labels);
				}
			}
		}

		return Collections.emptySet();
	}

	public static class Category {
		public List<String> names;
		public List<String> labels;
	}

	public static Categories from(List<Pair<String, String>> namespaceToLabel) {
		List<Category> categories = Lists.newArrayListWithExpectedSize(namespaceToLabel.size());

		for (Pair<String, String> entry : namespaceToLabel) {
			Category category = new Category();
			category.names = Collections.singletonList(entry.getFirst());
			category.labels = Collections.singletonList(entry.getSecond());

			categories.add(category);
		}

		Categories result = new Categories();
		result.categories = categories;

		return result;
	}
}
