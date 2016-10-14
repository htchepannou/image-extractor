package com.tchepannou.kiosk.image;

public interface ImageContext {
    String getBaseUri();

    String getImageCssSelector();

    DimensionProvider getDimensionProvider();

    int getImageMinWidth();

    int getImageMinHeight();
}
