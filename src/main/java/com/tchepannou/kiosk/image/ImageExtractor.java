package com.tchepannou.kiosk.image;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ImageExtractor {
    public String extract(final String html, final ImageContext context) {
        final Document doc = Jsoup.parse(html);
        doc.setBaseUri(context.getBaseUri());

        final String image = selectImage(doc, context);
        return image == null ? detectImage(doc, context) : image;
    }

    private String selectImage(final Document doc, final ImageContext context) {
        final String selector = context.getImageCssSelector();
        if (StringUtil.isBlank(selector)) {
            return null;
        }

        final Elements images = doc.select(selector);
        return images.isEmpty() ? null : images.get(0).attr("abs:src");
    }

    private String detectImage(final Document doc, final ImageContext context) {
        final Elements images = doc.select("img");
        if (images.isEmpty()) {
            return null;
        }

        final List<ImageInfo> imageInfos = images.stream()
                .map(elt -> toImageInfo(elt, context))
                .filter(img -> accept(img, context))
                .collect(Collectors.toList());
        if (imageInfos.isEmpty()){
            return null;
        }

        // Sort the images
        Collections.sort(imageInfos, (u, v) -> v.getSize() - u.getSize());

        // return the 1st
        return imageInfos.get(0).getUrl();
    }

    private ImageInfo toImageInfo(final Element elt, final ImageContext context) {
        final ImageInfo imageInfo = new ImageInfo();
        imageInfo.setElement(elt);

        final Dimension dim = context.getDimensionProvider().getDimension(imageInfo.getUrl());
        if (dim == null) {
            return null;
        }

        imageInfo.setDimension(dim);
        return imageInfo;
    }

    private boolean accept(final ImageInfo img, final ImageContext context) {
        return img != null &&
                img.getDimension().getHeight() >= context.getImageMinHeight() &&
                img.getDimension().getWidth() >= context.getImageMinWidth();
    }
}
