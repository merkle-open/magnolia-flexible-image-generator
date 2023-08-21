package com.merkle.oss.magnolia.imaging.flexible.configuration;

import com.merkle.oss.magnolia.imaging.flexible.generator.FlexibleImageGenerator;
import com.merkle.oss.magnolia.imaging.flexible.generator.FlexibleParameterProviderFactory;
import info.magnolia.jcr.nodebuilder.AbstractNodeOperation;
import info.magnolia.jcr.nodebuilder.ErrorHandler;
import info.magnolia.jcr.nodebuilder.NodeOperation;
import info.magnolia.jcr.nodebuilder.Ops;
import info.magnolia.jcr.nodebuilder.task.ErrorHandling;
import info.magnolia.jcr.nodebuilder.task.NodeBuilderTask;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AbstractRepositoryTask;
import info.magnolia.module.delta.TaskExecutionException;
import info.magnolia.repository.RepositoryConstants;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

public class InstallFlexibleImageGenerator extends AbstractRepositoryTask {
	private static final String TASK_NAME = "Flexible image generator";
	private static final String TASK_DESCRIPTION = "Install flexible image generator.";
	private static final String PATH = "/modules/imaging/config/generators";

	public InstallFlexibleImageGenerator() {
		super(TASK_NAME, TASK_DESCRIPTION);
	}

	@Override
	protected void doExecute(final InstallContext ctx) throws TaskExecutionException {
		new NodeBuilderTask(getName(), getDescription(), ErrorHandling.strict, RepositoryConstants.CONFIG, PATH,
				Ops.getOrAddNode("flex", NodeTypes.ContentNode.NAME).then(
						setOrAddProperty("class", FlexibleImageGenerator.class.getName()),
						Ops.getOrAddNode("outputFormat", NodeTypes.ContentNode.NAME).then(
								setOrAddProperty("quality", "80")
						),
						Ops.getOrAddNode("parameterProviderFactory", NodeTypes.ContentNode.NAME).then(
								setOrAddProperty("class", FlexibleParameterProviderFactory.class.getName())
						)
				)
		).execute(ctx);
	}

	public static NodeOperation setOrAddProperty(final String name, final Object newValue) {
		return new AbstractNodeOperation() {
			@Override
			protected Node doExec(final Node context, final ErrorHandler errorHandler) throws RepositoryException {
				final Value value = PropertyUtil.createValue(newValue, context.getSession().getValueFactory());
				context.setProperty(name, value);
				return context;
			}
		};
	}
}
