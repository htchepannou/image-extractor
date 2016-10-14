package com.tchepannou.kiosk.image;

import org.jsoup.nodes.Element;

public class ImageInfo {
    private Element element;
    private Dimension dimension;

    public Element getElement() {
        return element;
    }

    public void setElement(final Element element) {
        this.element = element;
    }

    public String getUrl() {
        return element.attr("abs:src");
    }

    public int getSize(){
        return dimension != null ? dimension.getWidth() * dimension.getHeight() : null;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(final Dimension dimension) {
        this.dimension = dimension;
    }
}
