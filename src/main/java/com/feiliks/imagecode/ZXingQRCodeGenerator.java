package com.feiliks.imagecode;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;


public class ZXingQRCodeGenerator extends AbstractGenerator {

    @Override
    public void generate(String type, String content, Integer size, HttpServletResponse resp) {
        resp.addHeader("Content-Type", getMIMEType(type));
        if (type.equals("svg")) {
            if (size == null)
                size = 160;
            if (size <= 0)
                throw new IllegalArgumentException("invalid size");
            try {
                QRCodeWriter gen = new QRCodeWriter();
                Map<EncodeHintType, Object> hints = new HashMap<>();
                hints.put(EncodeHintType.MARGIN, 1);
                BitMatrix matrix = gen.encode(content, BarcodeFormat.QR_CODE, size, size, hints);
                resp.getWriter().print(SvgUtil.convert(size, size, (x, y) -> matrix.get(x, y)));
            } catch(Exception ex) {
                throw new GenerationException("generation error", ex);
            }
        } else {
            QRCode qrcode = QRCode.from(content).to(ImageType.valueOf(type.toUpperCase()))
                    .withHint(EncodeHintType.MARGIN, 1);
            if (size != null) {
                if (size <= 0)
                    throw new IllegalArgumentException("invalid size");
                qrcode.withSize(size, size);
            }
            try {
                qrcode.writeTo(resp.getOutputStream());
            } catch(Exception ex) {
                throw new GenerationException("generation error", ex);
            }
        }
    }

}
