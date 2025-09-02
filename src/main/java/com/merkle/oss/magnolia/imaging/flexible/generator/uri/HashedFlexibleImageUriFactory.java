package com.merkle.oss.magnolia.imaging.flexible.generator.uri;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.merkle.oss.magnolia.imaging.flexible.model.FlexibleParameter;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.servlet.ServletContext;

public class HashedFlexibleImageUriFactory extends FlexibleImageUriFactory {
	private final ImageDigest imageDigest;

	@Inject
	public HashedFlexibleImageUriFactory(
			final Provider<ServletContext> servletContext,
			final ImageDigest imageDigest
	) {
        super(servletContext);
        this.imageDigest = imageDigest;
	}

	/*
	 * /<context>/.imaging/flex/assetItemKey/hash/hashValue/param1Key/param1Value/.../fileName
	 *
	 * e.g. /author/.imaging/flex/jcr:b3ee7444-4830-4454-abbb-20fc35387032/crop/true/hash/466cea7f608f4b1b59fee12ff43f7e68/ratio/16:9/version/1733184000000/width/100/someImage.jpg
	 */
	public URI create(final FlexibleParameter parameter) {
		final URI uri = super.create(parameter);

		return super.create(new HashedFlexibleParameter(
				parameter,
				imageDigest.getMD5Hex(uri.toString())
		));
	}

	static class HashedFlexibleParameter extends FlexibleParameter {
		static final String HASH_PARAM = "hash";
		private final String hash;

		public HashedFlexibleParameter(
				final FlexibleParameter wrapped,
				final String hash
		) {
			super(wrapped.getDynamicImageParameter().orElse(null), wrapped.getRatio(), wrapped.getWidth(), wrapped.getVersion(), wrapped);
			this.hash = hash;
		}

		@Override
		public Map<String, String> toMap() {
			return Stream.concat(
					super.toMap().entrySet().stream(),
					Map.of(HASH_PARAM, hash).entrySet().stream()
			).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
		}
	}
}


