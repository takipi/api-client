package com.takipi.api.client.util.infra;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.takipi.common.util.CollectionUtil;
import com.takipi.common.util.IOUtil;
import com.takipi.common.util.StringUtil;

public class Categories {

	private static final String DEFAULT_CATEGORIES = "/" + "infra/categories.json";
	private static final Categories EMPTY_CATEGORIES = new Categories();

	private static volatile Categories instance = null;

	public enum CategoryType {
		app, infra
	}

	public static Categories defaultCategories() {
		if (instance == null) {
			synchronized (Categories.class) {
				if (instance == null) {
					Categories result = IOUtil.readFromResource(DEFAULT_CATEGORIES, Categories.class, new Gson());

					instance = ((result != null) ? result : EMPTY_CATEGORIES);
				}
			}
		}

		return instance;
	}

	public List<Category> categories;

	public Set<String> getCategories(String className, CategoryType type) {
		if (CollectionUtil.safeIsEmpty(categories)) {
			return Collections.emptySet();
		}

		for (Category category : categories) {
			if ((CollectionUtil.safeIsEmpty(category.names)) || (CollectionUtil.safeIsEmpty(category.labels))) {
				continue;
			}

			if ((type != null) && (type != category.getType())) {
				continue;
			}

			for (String name : category.names) {
				if (className.startsWith(name)) {
					return new HashSet<>(category.labels);
				}
			}
		}

		return Collections.emptySet();
	}

	public static class Category {

		public List<String> names;
		public List<String> labels;
		public CategoryType type;

		public CategoryType getType() {
			if (type == null) {
				return CategoryType.infra;
			}

			return type;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			}

			if (!(o instanceof Category)) {
				return false;
			}

			Category other = (Category) o;

			if (this.getType() != other.getType()) {
				return false;
			}

			if (!CollectionUtil.equalCollections(names, other.names)) {
				return false;
			}

			if (!CollectionUtil.equalCollections(labels, other.labels)) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			if (CollectionUtil.safeIsEmpty(names)) {
				return super.hashCode();
			}

			return String.join(",", names).hashCode();
		}
	}

	public static Categories expandWithDefaultCategories(Collection<Category> categories) {
		if (CollectionUtil.safeIsEmpty(categories)) {
			return defaultCategories();
		}

		Categories result = new Categories();

		result.categories = new ArrayList<>(categories);

		List<Category> defaultCategories = defaultCategories().categories;

		if (defaultCategories != null) {
			result.categories.addAll(defaultCategories);
		}

		return result;
	}

	public static void fillMissingCategoryNames(Collection<Category> categories) {
		if (CollectionUtil.safeIsEmpty(categories)) {
			return;
		}

		for (Category category : categories) {
			if (!CollectionUtil.safeIsEmpty(category.names)) {
				continue;
			}

			for (Category defaultCategory : defaultCategories().categories) {
				if (doCategoriesMatch(category, defaultCategory)) {
					category.names = new ArrayList<>(defaultCategory.names);
					break;
				}
			}
		}
	}

	private static boolean doCategoriesMatch(Category a, Category b) {
		if ((CollectionUtil.safeIsEmpty(a.labels)) || (CollectionUtil.safeIsEmpty(b.labels))) {
			return false;
		}

		for (String label : a.labels) {

			if (StringUtil.isNullOrEmpty(label)) {
				continue;
			}

			if (b.labels.contains(label)) {
				return true;
			}
		}

		return false;
	}
}
