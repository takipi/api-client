package com.takipi.api.client.util.infra;

import java.util.Collection;
import java.util.HashSet;

import com.takipi.api.client.ApiClient;
import com.takipi.api.client.data.event.Location;
import com.takipi.api.client.result.event.EventResult;
import com.takipi.api.client.util.infra.Categories.CategoryType;
import com.takipi.common.util.CollectionUtil;

public abstract class Categorizer {
	protected abstract CategoryType getType();

	protected abstract boolean validateEvent(EventResult event);

	protected abstract Collection<String> getTierLabels(EventResult event, Categories categories);

	private void updateEventLabels(EventResult event, Collection<String> tierLabels, Collection<String> existingLabels,
			Collection<String> labelsToAdd, Collection<String> labelsToRemove, String serviceId, String categoryId,
			ApiClient apiClient) {

		if (CollectionUtil.safeIsEmpty(tierLabels)) {
			return;
		}

		for (String tierLabel : tierLabels) {
			String labelName = InfraUtil.toTierLabelName(tierLabel, getType());

			labelsToRemove.remove(labelName);

			if (CollectionUtil.safeContains(event.labels, labelName)) {
				continue;
			}

			labelsToAdd.add(labelName);

			if (existingLabels.add(tierLabel)) {
				InfraUtil.validateTierView(tierLabel, getType(), categoryId, serviceId, apiClient);
			}
		}
	}

	private void removeExistingTierLabels(EventResult event, Collection<String> labelsToRemove) {
		if (CollectionUtil.safeIsEmpty(event.labels)) {
			return;
		}

		for (String label : event.labels) {
			if (label.endsWith(InfraUtil.getTierLabelPostfix(getType()))) {
				labelsToRemove.add(label);
			}
		}
	}

	public void categorizeEvent(EventResult event, Categories categories, Collection<String> labelsToAdd,
			Collection<String> labelsToRemove, Collection<String> existingLabels, String serviceId, String categoryId,
			ApiClient apiClient) {
		if ((event == null) || (!validateEvent(event))) {
			return;
		}

		removeExistingTierLabels(event, labelsToRemove);

		Collection<String> tierLabels = getTierLabels(event, categories);

		updateEventLabels(event, tierLabels, existingLabels, labelsToAdd, labelsToRemove, serviceId, categoryId,
				apiClient);
	}

	public static class AppCategorizer extends Categorizer {
		public static final Categorizer instance = new AppCategorizer();

		private AppCategorizer() {

		}

		@Override
		protected CategoryType getType() {
			return CategoryType.app;
		}

		@Override
		protected boolean validateEvent(EventResult event) {
			return (!CollectionUtil.safeIsEmpty(event.stack_frames));
		}

		@Override
		protected Collection<String> getTierLabels(EventResult event, Categories categories) {
			Collection<String> appLabels = new HashSet<>();

			for (Location location : event.stack_frames) {
				Collection<String> frameMatches = categories.getCategories(location.class_name, CategoryType.app);
				appLabels.addAll(frameMatches);
			}

			return appLabels;
		}
	}

	public static class CodeTierCategorizer extends Categorizer {
		public static final Categorizer instance = new CodeTierCategorizer();

		private CodeTierCategorizer() {

		}

		@Override
		protected CategoryType getType() {
			return CategoryType.infra;
		}

		@Override
		protected boolean validateEvent(EventResult event) {
			return (event.error_origin != null);
		}

		@Override
		protected Collection<String> getTierLabels(EventResult event, Categories categories) {
			return categories.getCategories(event.error_origin.class_name, CategoryType.infra);
		}
	}

	public static Categorizer get(CategoryType categoryType) {
		switch (categoryType) {
		case app:
			return AppCategorizer.instance;
		case infra:
			return CodeTierCategorizer.instance;
		default:
			return null;
		}
	}
}
