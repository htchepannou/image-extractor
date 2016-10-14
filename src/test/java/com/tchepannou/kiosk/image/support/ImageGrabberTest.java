package com.tchepannou.kiosk.image.support;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Enumeration;

import static org.assertj.core.api.Assertions.assertThat;

public class ImageGrabberTest {
    private static final int PORT = 18080;

    private static Server server;
    private static byte[] bout;
    private ImageGrabber toolkit = new ImageGrabber();

    @BeforeClass
    public static void setUpClass() throws Exception {
        final InputStream in = ImageGrabberTest.class.getResourceAsStream("/img/ionic.png");
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(in, out);
        bout = out.toByteArray();

        server = new Server(PORT);
        server.setHandler(createHandler());
        server.start();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        server.stop();
    }


    @Test
    public void shouldExtractBase64() throws Exception {
        // Given
        final String url = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(bout);

        // When
        ImageData result = toolkit.grab(url);

        // Then
        assertThat(result.getContentType()).isEqualTo("image/jpeg");
        assertThat(result.getContent()).isEqualTo(bout);
    }

    @Test
    public void shouldDownloadImage() throws Exception {
        // Given

        // When
        ImageData result = toolkit.grab("http://localhost:" + PORT + "/img.png");

        // Then
        assertThat(result.getContentType()).isEqualTo("image/png");
        assertThat(result.getContent()).isEqualTo(bout);
    }

    private static Handler createHandler() {
        return new DefaultHandler() {
            @Override
            public void handle(final String s, final Request r, final HttpServletRequest request, final HttpServletResponse response)
                    throws IOException, ServletException {

                /* output headers */
                System.out.println(s);
                final Enumeration<String> names = request.getHeaderNames();
                while (names.hasMoreElements()) {
                    final String name = names.nextElement();
                    System.out.println(name + "=" + request.getHeader(name));
                }

                /* return content */
                response.getOutputStream().write(bout);
                response.setHeader("Content-Type", "image/png");
                r.setHandled(true);
            }
        };
    }

}
