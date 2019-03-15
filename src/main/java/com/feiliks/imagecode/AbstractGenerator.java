package com.feiliks.imagecode;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.krysalis.barcode4j.tools.UnitConv;


public abstract class AbstractGenerator {

    protected final static int RESOLUTION = 100;
    protected final static Map<String, String> IMAGE_TYPES = new HashMap<>();
    static {
        IMAGE_TYPES.put("png", "image/png");
        IMAGE_TYPES.put("jpg", "image/jpeg");
        IMAGE_TYPES.put("gif", "image/gif");
        IMAGE_TYPES.put("svg", "image/svg+xml");
    }

    protected String getMIMEType(String type) {
        String out = IMAGE_TYPES.get(type);
        if (out == null)
            throw new IllegalArgumentException("invalid type");
        return out;
    }

    protected double px2mm(double px) {
        return UnitConv.in2mm(px / RESOLUTION);
    }

    public abstract void generate(String type, String content, Integer size, HttpServletResponse resp);

}
