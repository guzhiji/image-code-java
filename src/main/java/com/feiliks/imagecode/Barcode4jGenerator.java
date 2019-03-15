package com.feiliks.imagecode;

import java.awt.image.BufferedImage;

import javax.servlet.http.HttpServletResponse;

import org.krysalis.barcode4j.BarcodeDimension;
import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;


public class Barcode4jGenerator extends AbstractGenerator {

    protected final Class<? extends AbstractBarcodeBean> beanClass;

    public Barcode4jGenerator(Class<? extends AbstractBarcodeBean> bc) {
        beanClass = bc;
    }

    protected AbstractBarcodeBean getConfiguredBean(String content, Integer size)
            throws InstantiationException, IllegalAccessException {
        AbstractBarcodeBean gen = beanClass.newInstance();
        if (size != null) {
            if (size <= 0)
                throw new IllegalArgumentException("invalid size");
            double b = px2mm(1.0);
            double t = px2mm(size);
            gen.setModuleWidth(b);
            BarcodeDimension dim = gen.calcDimensions(content);
            if (dim.getHeight() > t) gen.setBarHeight(t);
            double w = dim.getWidth();
            if (w > t) gen.setModuleWidth(b / w * t);
        }
        return gen;
    }

    @Override
    public void generate(String type, String content, Integer size, HttpServletResponse resp) {
        String mime = getMIMEType(type);
        resp.addHeader("Content-Type", mime);
        try {
            if (type.equals("svg")) {
                SVGCanvasProvider canvas = new SVGCanvasProvider(0);
                getConfiguredBean(content, size).generateBarcode(canvas, content);
                XmlUtil.convert(canvas.getDOMFragment(), resp.getWriter());
            } else {
                BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                    resp.getOutputStream(), mime, RESOLUTION,
                    BufferedImage.TYPE_BYTE_BINARY, false, 0);
                getConfiguredBean(content, size).generateBarcode(canvas, content);
                canvas.finish();
            }
        } catch (Exception ex) {
            throw new GenerationException("generation error", ex);
        }
    }

}
