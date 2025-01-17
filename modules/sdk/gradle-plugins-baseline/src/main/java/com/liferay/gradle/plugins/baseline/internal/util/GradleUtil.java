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

package com.liferay.gradle.plugins.baseline.internal.util;

import com.liferay.gradle.util.FileUtil;

import java.io.File;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Iterator;
import java.util.Set;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.ArtifactRepositoryContainer;
import org.gradle.api.artifacts.dsl.RepositoryHandler;
import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskContainer;

/**
 * @author Andrea Di Giorgi
 */
public class GradleUtil extends com.liferay.gradle.util.GradleUtil {

	public static <T extends Task> T addTask(
		Project project, String name, Class<T> clazz, boolean overwrite) {

		if (overwrite) {
			try {
				Task task = getTask(project, name);

				removeTask(project, task);
			}
			catch (Exception exception) {
				Logger logger = project.getLogger();

				if (logger.isDebugEnabled()) {
					logger.debug(
						"Unable to remove task {}:{}", project.getPath(), name);
				}
			}
		}

		return addTask(project, name, clazz);
	}

	public static File getSrcDir(SourceDirectorySet sourceDirectorySet) {
		Set<File> srcDirs = sourceDirectorySet.getSrcDirs();

		Iterator<File> iterator = srcDirs.iterator();

		return iterator.next();
	}

	public static boolean isFromMavenLocal(Project project, File file) {
		RepositoryHandler repositoryHandler = project.getRepositories();

		ArtifactRepository artifactRepository = repositoryHandler.findByName(
			ArtifactRepositoryContainer.DEFAULT_MAVEN_LOCAL_REPO_NAME);

		if (!(artifactRepository instanceof MavenArtifactRepository)) {
			return false;
		}

		MavenArtifactRepository mavenArtifactRepository =
			(MavenArtifactRepository)artifactRepository;

		Path repositoryPath = Paths.get(mavenArtifactRepository.getUrl());

		if (FileUtil.isChild(file, repositoryPath.toFile())) {
			return true;
		}

		return false;
	}

	public static void removeTask(Project project, Task task) {
		TaskContainer taskContainer = project.getTasks();

		taskContainer.remove(task);
	}

	public static boolean toBoolean(Object object) {
		object = toObject(object);

		if (object instanceof Boolean) {
			return (Boolean)object;
		}

		if (object instanceof String) {
			return Boolean.parseBoolean((String)object);
		}

		return false;
	}

}