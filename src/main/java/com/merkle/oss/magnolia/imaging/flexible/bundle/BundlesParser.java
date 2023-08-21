package com.merkle.oss.magnolia.imaging.flexible.bundle;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.merkle.oss.magnolia.imaging.flexible.model.CustomRendition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BundlesParser {
	private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	public List<Bundle> parse(final String bundlesConfigFilePath) {
		return getBundleConfig(bundlesConfigFilePath)
				.map(this::create)
				.orElseThrow(() ->
						new IllegalArgumentException("Image bundles are not configured, see config file at '" + bundlesConfigFilePath + "'")
				);
	}

	private Optional<BundlesConfig> getBundleConfig(final String bundlesConfigFilePath) {
		return Optional
				.ofNullable(getClass().getClassLoader().getResourceAsStream(bundlesConfigFilePath))
				.map(InputStreamReader::new)
				.map(in ->
						new Gson().fromJson(in, BundlesConfig.class)
				);
	}

	private List<Bundle> create(final BundlesConfig bundlesConfig) {
		return bundlesConfig.getScanPaths()
				.stream()
				.flatMap(path ->
						create(bundlesConfig.getBundlesDirName(), path)
				)
				.collect(Collectors.toList());
	}

	private Stream<Bundle> create(final String bundlesDirName, final String path) {
		try {
			final String filePattern = path + "/**/" + bundlesDirName + "/*.json";
			LOG.info("Load image bundle definitions which match pattern '{}'", filePattern);
			final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + filePattern);
			final Stream.Builder<Stream<Bundle>> bundles = Stream.builder();

			final URI uri = getClass().getClassLoader().getResource(path).toURI();
			processResource(uri, p -> Files.walkFileTree(p, new SimpleFileVisitor<>() {
				@Override
				public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) {
					if (matcher.matches(file)) {
						bundles.accept(create(file));
					}
					return FileVisitResult.CONTINUE;
				}
			}));
			return bundles.build()
					.flatMap(Function.identity())
					.peek(this::updateBundle);
		} catch (IOException | URISyntaxException e) {
			LOG.error("Cannot resolve bundles in resource path "+ path, e);
			return Stream.empty();
		}
	}

	private void processResource(final URI uri, final IOConsumer<Path> consumer) throws IOException {
		try {
			consumer.accept(Paths.get(uri));
		} catch(FileSystemNotFoundException ex) {
			try(final FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
				consumer.accept(fs.provider().getPath(uri));
			}
		}
	}

	private Stream<Bundle> create(final Path bundleJson) {
		try (final BufferedReader reader = Files.newBufferedReader(bundleJson)) {
			LOG.info("Load bundle '{}'", bundleJson);
			return Arrays.stream(new Gson().fromJson(reader, Bundle[].class));
		} catch (IOException e) {
			LOG.error("Cannot open file {}", bundleJson);
			return Stream.empty();
		} catch (JsonSyntaxException | JsonIOException e) {
			LOG.error("Invalid JSON format {}", bundleJson);
			return Stream.empty();
		}
	}

	private void updateBundle(final Bundle bundle) {
		bundle.setImageSizes(getUpdatedImageSizes(bundle.getImageSizes(), bundle.getRatio(), bundle.isCrop()));
		bundle.setCustomRenditions(getUpdatedCustomRenditions(bundle.getCustomRenditions(), bundle.getRatio(), bundle.isCrop()));
	}

	private List<ImageSize> getUpdatedImageSizes(
			final List<ImageSize> imageSizes,
			@Nullable final Double ratio,
			@Nullable final Boolean crop
	) {
		return imageSizes.stream()
				.filter(imageSize -> imageSize.getHeight() != null || imageSize.getWidth() != null)
				.peek(imageSize -> updateImageSize(imageSize, ratio, crop))
				.collect(Collectors.toList());
	}

	private List<CustomRendition> getUpdatedCustomRenditions(
			final List<CustomRendition> customRenditions,
			@Nullable final Double ratio,
			@Nullable final Boolean crop
	) {
		return customRenditions.stream()
				.filter(imageSize -> imageSize.getHeight() != null || imageSize.getWidth() != null)
				.peek(imageSize -> updateImageSize(imageSize, ratio, crop))
				.collect(Collectors.toList());
	}

	private void updateImageSize(final ImageSize imageSize, @Nullable final Double ratio, @Nullable final Boolean crop) {
		if (imageSize.getRatio() == null) {
			imageSize.setRatio(ratio);
		}
		if (imageSize.isCrop() == null) {
			imageSize.setCrop(crop);
		}
		if (imageSize.getRatio() != null) {
			if (imageSize.getWidth() == null && imageSize.getHeight() != null) {
				int width = BigDecimal.valueOf(imageSize.getHeight() * imageSize.getRatio()).setScale(0, RoundingMode.UP).intValue();
				imageSize.setWidth(width);
			} else if (imageSize.getWidth() != null && imageSize.getHeight() == null) {
				int height = BigDecimal.valueOf(imageSize.getWidth() / imageSize.getRatio()).setScale(0, RoundingMode.UP).intValue();
				imageSize.setHeight(height);
			}
		}
	}

	interface IOConsumer<T> {
		void accept(T t) throws IOException;
	}
}