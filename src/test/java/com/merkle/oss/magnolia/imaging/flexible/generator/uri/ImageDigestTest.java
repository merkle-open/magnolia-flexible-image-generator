package com.merkle.oss.magnolia.imaging.flexible.generator.uri;

import static org.junit.jupiter.api.Assertions.*;

import info.magnolia.test.TestMagnoliaConfigurationProperties;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ImageDigestTest {
    private ImageDigest imageDigest;

    @BeforeEach
    void setUp() throws IOException {
        imageDigest = new ImageDigest(new TestMagnoliaConfigurationProperties(ImageDigest.SALT_PROPERTY_KEY, "someSalt"));
    }

    @Test
    void sameInputSameOutput() {
        assertEquals(
            imageDigest.getHash("someInput"),
            imageDigest.getHash("someInput")
        );
    }

    @Test
    void differentInputDifferentOutput() {
        assertNotEquals(
                imageDigest.getHash("someInput"),
                imageDigest.getHash("someOtherInput")
        );
    }
}
