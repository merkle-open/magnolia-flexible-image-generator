package com.merkle.oss.magnolia.imaging.flexible.configuration;

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.Task;
import info.magnolia.module.model.Version;

import java.util.List;

import jakarta.inject.Inject;

public class FlexibleImageGeneratorModuleVersionHandler extends DefaultModuleVersionHandler {
	private final InstallFlexibleImageGenerator installFlexibleImageGenerator;

	@Inject
	public FlexibleImageGeneratorModuleVersionHandler(final InstallFlexibleImageGenerator installFlexibleImageGenerator) {
		this.installFlexibleImageGenerator = installFlexibleImageGenerator;
	}

	@Override
	protected final List<Task> getExtraInstallTasks(final InstallContext installContext) { // when module node does not exist
		return List.of(installFlexibleImageGenerator);
	}

	@Override
	protected final List<Task> getDefaultUpdateTasks(final Version forVersion) { //on every module update
		return List.of(installFlexibleImageGenerator);
	}
}
