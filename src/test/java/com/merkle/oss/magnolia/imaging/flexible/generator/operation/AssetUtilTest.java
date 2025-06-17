package com.merkle.oss.magnolia.imaging.flexible.generator.operation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import info.magnolia.dam.api.Asset;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class AssetUtilTest {
    private AssetUtil assetUtil;

    @BeforeEach
    void setUp() {
        assetUtil = new AssetUtil();
    }

    @ParameterizedTest
    @CsvSource({
            "someImage.jpg, true",
            "someImage2.jpg, false",
            "someAnimation.gif, false",
            "someAnimation2.gif, true"
    })
    void isAnimated(final String inputFilePath, final boolean expected) throws IOException {
        final Asset assetMock = mock(Asset.class);
        doAnswer(invocationOnMock -> getUrl(inputFilePath).openStream()).when(assetMock).getContentStream();
        doAnswer(invocationOnMock -> Files.probeContentType(Path.of(getUrl(inputFilePath).toURI()))).when(assetMock).getMimeType();
        assertEquals(
                expected,
                assetUtil.isAnimated(assetMock)
        );
    }

    @ParameterizedTest
    @CsvSource({
            "someImage.jpg, true",
            "someImage2.jpg, true",
            "someAnimation.gif, true",
            "someVectorGraphic.svg, false",
            "someVideo.mp4, false"
    })
    void isImage(final String inputFilePath, final boolean expected) {
        final Asset assetMock = mock(Asset.class);
        doAnswer(invocationOnMock -> Files.probeContentType(Path.of(getUrl(inputFilePath).toURI()))).when(assetMock).getMimeType();
        assertEquals(
                expected,
                assetUtil.isImage(assetMock)
        );
    }

    private URL getUrl(final String inputFilePath) {
        return getClass().getClassLoader().getResource("com/merkle/oss/magnolia/imaging/flexible/generator/operation/" + inputFilePath);
    }
}