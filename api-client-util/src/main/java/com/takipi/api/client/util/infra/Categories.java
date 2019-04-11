package com.takipi.api.client.util.infra;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.takipi.common.util.CollectionUtil;

public class Categories {
	private static final String DEFAULT_CATEGORIES = "infra/categories.json";
	private static final Categories EMPTY_CATEGORIES = new Categories();

	private static volatile Categories instance = null;

	public enum CategoryType {
		app,
		infra
	}
	
	public static Categories defaultCategories() {
		if (instance == null) {
			synchronized (Categories.class) {
				if (instance == null) {
					Categories result;

					InputStream stream = null;

					try {
						ClassLoader classLoader = Categories.class.getClassLoader();

						stream = classLoader.getResourceAsStream(DEFAULT_CATEGORIES);

						if (stream == null) {
							return null;
						}

						result = (new Gson()).fromJson(IOUtils.toString(stream, Charset.defaultCharset()),
								Categories.class);
					} catch (Exception e) {
						result = EMPTY_CATEGORIES;
					} finally {
						IOUtils.closeQuietly(stream);
					}

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
					return Sets.newHashSet(category.labels);
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
		
		private boolean compare(Collection<String> a, Collection<String> b) {
						
			if (a == null) {
				return b == null;
			} 
			
			if (b == null) {
				return false;
			}
			
			if (a.size() != b.size()) {
				return false;
			}
			
			return a.containsAll(b);	
		}
		
		@Override
		public boolean equals(Object obj) {
			
			if (!(obj instanceof Category)) {
				return false;
			}
			
			Category other = (Category)obj;
			
			if (!Objects.equal(this.getType(), other.getType())) {
				return false;
			}

			if (!compare(names, other.names)) {
				return false;
			}
			
			if (!compare(labels, other.labels)) {
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

		result.categories = Lists.newArrayList(categories);
		result.categories.addAll(defaultCategories().categories);

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
					category.names = Lists.newArrayList(defaultCategory.names);
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

			if (Strings.isNullOrEmpty(label)) {
				continue;
			}

			if (b.labels.contains(label)) {
				return true;
			}
		}

		return false;
	}
}
