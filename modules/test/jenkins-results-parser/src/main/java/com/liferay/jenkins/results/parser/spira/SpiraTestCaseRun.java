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

package com.liferay.jenkins.results.parser.spira;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil.HttpRequestMethod;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class SpiraTestCaseRun extends BaseSpiraArtifact {

	public static SpiraTestCaseRun recordTestSpiraTestCaseRun(
		SpiraProject spiraProject, SpiraTestCaseObject spiraTestCaseObject) {

		List<SpiraTestCaseRun> spiraTestCaseRuns = recordTestSpiraTestCaseRuns(
			spiraProject, spiraTestCaseObject);

		if (spiraTestCaseRuns.isEmpty()) {
			return null;
		}

		if (spiraTestCaseRuns.size() > 1) {
			throw new RuntimeException("Duplicate records found");
		}

		return spiraTestCaseRuns.get(0);
	}

	public static List<SpiraTestCaseRun> recordTestSpiraTestCaseRuns(
		SpiraProject spiraProject,
		SpiraTestCaseObject... spiraTestCaseObjects) {

		Calendar calendar = Calendar.getInstance();

		List<JSONObject> requestJSONObjects = new ArrayList<>(
			spiraTestCaseObjects.length);

		for (SpiraTestCaseObject spiraTestCase : spiraTestCaseObjects) {
			JSONObject requestJSONObject = new JSONObject();

			requestJSONObject.put(
				SpiraTestCaseObject.ID_KEY, spiraTestCase.getID());
			requestJSONObject.put("ExecutionStatusId", STATUS_PASSED);
			requestJSONObject.put("RunnerMessage", spiraTestCase.getPath());
			requestJSONObject.put("RunnerName", "Liferay CI");
			requestJSONObject.put("RunnerStackTrace", "");
			requestJSONObject.put("RunnerTestName", spiraTestCase.getName());
			requestJSONObject.put(
				"StartDate", PathSpiraArtifact.toDateString(calendar));
			requestJSONObject.put("TestRunFormatId", RUNNER_FORMAT_PLAIN);

			requestJSONObjects.add(requestJSONObject);
		}

		return recordSpiraTestCaseRuns(
			spiraProject, requestJSONObjects.toArray(new JSONObject[0]));
	}

	protected static List<SpiraTestCaseRun> getSpiraTestCaseRuns(
		final SpiraProject spiraProject,
		final SpiraTestCaseObject spiraTestCase,
		final SearchQuery.SearchParameter... searchParameters) {

		return getSpiraArtifacts(
			SpiraTestCaseRun.class,
			new Supplier<List<JSONObject>>() {

				public List<JSONObject> get() {
					return _requestSpiraTestCaseRuns(
						spiraProject, spiraTestCase, searchParameters);
				}

			},
			new Function<JSONObject, SpiraTestCaseRun>() {

				public SpiraTestCaseRun apply(JSONObject jsonObject) {
					return new SpiraTestCaseRun(jsonObject);
				}

			},
			searchParameters);
	}

	protected static List<SpiraTestCaseRun> recordSpiraTestCaseRuns(
		SpiraProject spiraProject, JSONObject... requestJSONObjects) {

		String urlPath = "projects/{project_id}/test-runs/record-multiple";

		Map<String, String> urlPathReplacements = new HashMap<>();

		urlPathReplacements.put(
			"project_id", String.valueOf(spiraProject.getID()));

		JSONArray requestJSONArray = new JSONArray();

		for (JSONObject requestJSONObject : requestJSONObjects) {
			requestJSONArray.put(requestJSONObject);
		}

		try {
			JSONArray responseJSONArray = SpiraRestAPIUtil.requestJSONArray(
				urlPath, null, urlPathReplacements, HttpRequestMethod.POST,
				requestJSONArray.toString());

			List<SpiraTestCaseRun> spiraTestCaseRuns = new ArrayList<>();

			for (int i = 0; i < responseJSONArray.length(); i++) {
				JSONObject responseJSONObject = responseJSONArray.getJSONObject(
					i);

				responseJSONObject.put(
					SpiraProject.ID_KEY, spiraProject.getID());

				spiraTestCaseRuns.add(new SpiraTestCaseRun(responseJSONObject));
			}

			return spiraTestCaseRuns;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	protected static final String ID_KEY = "TestRunId";

	protected static final int RUNNER_FORMAT_HTML = 2;

	protected static final int RUNNER_FORMAT_PLAIN = 1;

	protected static final int STATUS_BLOCKED = 5;

	protected static final int STATUS_CAUTION = 6;

	protected static final int STATUS_FAILED = 1;

	protected static final int STATUS_NOT_APPLICABLE = 4;

	protected static final int STATUS_NOT_RUN = 3;

	protected static final int STATUS_PASSED = 2;

	private static List<JSONObject> _requestSpiraTestCaseRuns(
		SpiraProject spiraProject, SpiraTestCaseObject spiraTestCase,
		SearchQuery.SearchParameter... searchParameters) {

		Map<String, String> urlParameters = new HashMap<>();

		urlParameters.put("number_of_rows", String.valueOf(1000));
		urlParameters.put("sort_direction", "DESC");
		urlParameters.put("sort_field", ID_KEY);
		urlParameters.put("starting_row", String.valueOf(1));

		Map<String, String> urlPathReplacements = new HashMap<>();

		urlPathReplacements.put(
			"project_id", String.valueOf(spiraProject.getID()));
		urlPathReplacements.put(
			"test_case_id", String.valueOf(spiraTestCase.getID()));

		JSONArray requestJSONArray = new JSONArray();

		for (SearchQuery.SearchParameter searchParameter : searchParameters) {
			requestJSONArray.put(searchParameter.toFilterJSONObject());
		}

		try {
			JSONArray responseJSONArray = SpiraRestAPIUtil.requestJSONArray(
				"projects/{project_id}/test-cases/{test_case_id}/test-runs" +
					"/search",
				urlParameters, urlPathReplacements, HttpRequestMethod.POST,
				requestJSONArray.toString());

			List<JSONObject> spiraTestCaseRuns = new ArrayList<>();

			for (int i = 0; i < responseJSONArray.length(); i++) {
				JSONObject responseJSONObject = responseJSONArray.getJSONObject(
					i);

				responseJSONObject.put(
					SpiraProject.ID_KEY, spiraProject.getID());

				spiraTestCaseRuns.add(responseJSONObject);
			}

			return spiraTestCaseRuns;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private SpiraTestCaseRun(JSONObject jsonObject) {
		super(jsonObject);
	}

}