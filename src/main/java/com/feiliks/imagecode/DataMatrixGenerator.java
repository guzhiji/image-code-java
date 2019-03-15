package com.feiliks.imagecode;

import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.impl.datamatrix.SymbolShapeHint;

public class DataMatrixGenerator extends Barcode4jGenerator {

    public DataMatrixGenerator() {
        super(DataMatrixBean.class);
    }

    @Override
    protected AbstractBarcodeBean getConfiguredBean(String content, Integer size)
            throws InstantiationException, IllegalAccessException {
        DataMatrixBean gen = (DataMatrixBean) beanClass.newInstance();
        gen.setShape(SymbolShapeHint.FORCE_SQUARE);
        if (size != null) {
            if (size <= 0)
                throw new IllegalArgumentException("invalid size");
            double b = px2mm(1.0); // base size 1px to mm
            double t = px2mm(size); // target size to mm
            gen.setModuleWidth(b);
            double w = gen.calcDimensions(content).getWidth(); // original size in mm
            gen.setModuleWidth(b / w * t);
        }
        return gen;
    }

}
