package com.tchepannou.kiosk.image;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImageDetectorTest {
    private final String BASE_URI = "http://www.img.com";

    private String html;

    @Before
    public void setUp() {
        html = "<p>" +
                "<img src='i1.png' />" +
                "<img src='i2.png' />" +
                "<img src='i3.png' />" +
                "</p>";
    }

    @Test
    public void shouldExtractImage() {
        // Given
        final Dimension d1 = createDimension(150, 150);
        final Dimension d2 = createDimension(150, 250);
        final Dimension d3 = createDimension(150, 350);
        final DimensionProvider provider = mock(DimensionProvider.class);
        when(provider.getDimension(any()))
                .thenReturn(d1)
                .thenReturn(d2)
                .thenReturn(d3);

        final ImageContext ctx = createContext(provider);

        // When
        final String result = new ImageExtractor().extract(html, ctx);

        // Then
        assertThat(result).isEqualTo(BASE_URI + "/i3.png");
    }

    @Test
    public void shouldReturnNullWhenDocumentHasSmallImages() {
        // Given
        final Dimension d1 = createDimension(10, 150);
        final Dimension d2 = createDimension(150, 20);
        final Dimension d3 = createDimension(50, 50);
        final DimensionProvider provider = mock(DimensionProvider.class);
        when(provider.getDimension(any()))
                .thenReturn(d1)
                .thenReturn(d2)
                .thenReturn(d3);

        final ImageContext ctx = createContext(provider);

        // When
        final String result = new ImageExtractor().extract(html, ctx);

        // Then
        assertThat(result).isNull();
    }

    @Test
    public void shouldReturnNullWhenDocumentHasNoImage() {
        // Given
        final String html = "<p>Hello world</p>";
        final DimensionProvider provider = mock(DimensionProvider.class);
        final ImageContext ctx = createContext(provider);

        // When
        final String result = new ImageExtractor().extract(html, ctx);

        // Then
        assertThat(result).isNull();
    }

    private Dimension createDimension(final int w, final int h) {
        final Dimension dim = mock(Dimension.class);
        when(dim.getHeight()).thenReturn(h);
        when(dim.getWidth()).thenReturn(w);
        return dim;
    }

    private ImageContext createContext(final DimensionProvider dimensionProvider) {
        final ImageContext ctx = mock(ImageContext.class);
        when(ctx.getDimensionProvider()).thenReturn(dimensionProvider);
        when(ctx.getBaseUri()).thenReturn(BASE_URI);
        when(ctx.getImageMinHeight()).thenReturn(100);
        when(ctx.getImageMinWidth()).thenReturn(100);
        return ctx;
    }

}
