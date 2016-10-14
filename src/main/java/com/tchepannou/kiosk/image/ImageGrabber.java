package com.tchepannou.kiosk.image;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageGrabber {
    private static final String VERSION = "1.0";
    private static final String USER_AGENT = String.format("Mozilla/5.0 (compatible; kioskbot-i/%s)", VERSION);

    public ImageData grab(final String url) throws IOException {

        return url.startsWith("data:image/")
                ? decode(url)
                : download(url);

    }

    private ImageData decode(final String src) {
        final ImageData data = new ImageData();
        final int i = src.indexOf(';');
        final int j = src.indexOf(',');
        final String contentType = src.substring("data:".length(), i);
        final String base64 = src.substring(j + 1);
        final byte[] bytes = DatatypeConverter.parseBase64Binary(base64);

        data.setContentType(contentType);
        data.setContent(bytes);

        return data;

    }

    private ImageData download(final String url) throws IOException {
        try (final CloseableHttpClient client = HttpClients.createDefault()) {
            final HttpGet method = createHttpGet(url);
            try (CloseableHttpResponse response = client.execute(method)) {
                final Header header = response.getFirstHeader("Content-Type");
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                IOUtils.copy(response.getEntity().getContent(), out);

                final ImageData data = new ImageData();
                data.setContent(out.toByteArray());
                data.setContentType(header != null ? header.getValue() : null);
                return data;
            }
        }
    }

    private HttpGet createHttpGet(final String url) {
        final HttpGet method = new HttpGet(url);
        method.setHeader("Connection", "keep-alive");
        method.setHeader("User-Agent", USER_AGENT);
        return method;

    }
}
