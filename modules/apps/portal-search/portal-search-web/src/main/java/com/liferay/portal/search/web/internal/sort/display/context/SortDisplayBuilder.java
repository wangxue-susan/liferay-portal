/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.search.web.internal.sort.display.context;

import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.web.internal.sort.configuration.SortPortletInstanceConfiguration;
import com.liferay.portal.search.web.internal.sort.portlet.SortPortletPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.portlet.RenderRequest;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
public class SortDisplayBuilder {

	public SortDisplayBuilder(
			Language language, Portal portal, RenderRequest renderRequest,
			SortPortletPreferences sortPortletPreferences)
		throws ConfigurationException {

		_language = language;
		_portal = portal;
		_renderRequest = renderRequest;
		_sortPortletPreferences = sortPortletPreferences;

		_themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = _themeDisplay.getPortletDisplay();

		_sortPortletInstanceConfiguration =
			portletDisplay.getPortletInstanceConfiguration(
				SortPortletInstanceConfiguration.class);
	}

	public SortDisplayContext build() {
		SortDisplayContext sortDisplayContext = new SortDisplayContext();

		sortDisplayContext.setDisplayStyleGroupId(getDisplayStyleGroupId());
		sortDisplayContext.setSortPortletInstanceConfiguration(
			_sortPortletInstanceConfiguration);
		sortDisplayContext.setParameterName(_parameterName);
		sortDisplayContext.setParameterValue(getParameterValue());
		sortDisplayContext.setRenderNothing(isRenderNothing());
		sortDisplayContext.setSortTermDisplayContexts(
			buildTermDisplayContexts());

		return sortDisplayContext;
	}

	public SortDisplayBuilder parameterName(String parameterName) {
		_parameterName = parameterName;

		return this;
	}

	public SortDisplayBuilder parameterValues(String... parameterValues) {
		if (parameterValues == null) {
			_selectedFields = Collections.emptyList();

			return this;
		}

		_selectedFields = Arrays.asList(parameterValues);

		return this;
	}

	protected SortTermDisplayContext buildTermDisplayContext(
		String label, String field) {

		SortTermDisplayContext sortTermDisplayContext =
			new SortTermDisplayContext();

		sortTermDisplayContext.setLabel(label);
		sortTermDisplayContext.setLanguageLabel(
			_language.get(
				_portal.getHttpServletRequest(_renderRequest), label));
		sortTermDisplayContext.setField(field);
		sortTermDisplayContext.setSelected(_selectedFields.contains(field));

		return sortTermDisplayContext;
	}

	protected List<SortTermDisplayContext> buildTermDisplayContexts() {
		List<SortTermDisplayContext> sortTermDisplayContexts =
			new ArrayList<>();

		JSONArray fieldsJSONArray =
			_sortPortletPreferences.getFieldsJSONArray();

		for (int i = 0; i < fieldsJSONArray.length(); i++) {
			JSONObject jsonObject = fieldsJSONArray.getJSONObject(i);

			sortTermDisplayContexts.add(
				buildTermDisplayContext(
					jsonObject.getString("label"),
					jsonObject.getString("field")));
		}

		return sortTermDisplayContexts;
	}

	protected long getDisplayStyleGroupId() {
		long displayStyleGroupId =
			_sortPortletInstanceConfiguration.displayStyleGroupId();

		if (displayStyleGroupId <= 0) {
			displayStyleGroupId = _themeDisplay.getScopeGroupId();
		}

		return displayStyleGroupId;
	}

	protected String getParameterValue() {
		if (_selectedFields.size() > 0) {
			return _selectedFields.get(_selectedFields.size() - 1);
		}

		return null;
	}

	protected boolean isRenderNothing() {
		JSONArray jsonArray = _sortPortletPreferences.getFieldsJSONArray();

		if (jsonArray == null) {
			return true;
		}

		if (jsonArray.length() == 0) {
			return true;
		}

		return false;
	}

	private final Language _language;
	private String _parameterName;
	private final Portal _portal;
	private final RenderRequest _renderRequest;
	private List<String> _selectedFields = Collections.emptyList();
	private final SortPortletInstanceConfiguration
		_sortPortletInstanceConfiguration;
	private final SortPortletPreferences _sortPortletPreferences;
	private final ThemeDisplay _themeDisplay;

}