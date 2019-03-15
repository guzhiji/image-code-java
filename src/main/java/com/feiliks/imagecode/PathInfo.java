package com.feiliks.imagecode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PathInfo {
    private static Pattern pathPattern = Pattern.compile("/(\\w+)(\\.(\\w+))?");

    private final String codingType;
    private final String imageType;

    public PathInfo(String path) {
        if (path == null)
            throw new IllegalArgumentException("invalid coding type");
        Matcher pMatcher = pathPattern.matcher(path);
        if (!pMatcher.find())
            throw new IllegalArgumentException("invalid coding type");
        codingType = pMatcher.group(1);
        imageType = pMatcher.group(3);
    }

    public String getCodingType() {
        return codingType;
    }

    public String getImageType() {
        return imageType;
    }

}
