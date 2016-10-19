package com.tchepannou.kiosk.image;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OgImageTest {

    @Test
    public void shouldExtractImages() throws Exception {
        // Given
        final ImageContext ctx = mock(ImageContext.class);
        when(ctx.getBaseUri()).thenReturn("http://www.foo.com");
        final String html = IOUtils.toString(getClass().getResourceAsStream("/og-image/og-image.html"));

        // When
        final String result = new ImageExtractor().extract(html, ctx);

        // Then
        assertThat(result).isEqualTo("http://www.x.com/img/main.png");
    }

}
