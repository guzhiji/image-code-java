package com.feiliks.imagecode;

import java.awt.Color;

import org.jfree.graphics2d.svg.SVGGraphics2D;

public class SvgUtil {

    public interface BitMatrixReader {
        boolean read(int x, int y);
    }

    public static String convert(int w, int h, BitMatrixReader reader) {
        SVGGraphics2D g = new SVGGraphics2D(w, h);
        g.setColor(Color.BLACK);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (reader.read(x, y))
                    g.fillRect(x, y, 1, 1);
            }
        }
        return g.getSVGDocument();
    }

}
