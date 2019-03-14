package com.feiliks.imagecode;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.krysalis.barcode4j.impl.AbstractBarcodeBean;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.impl.datamatrix.DataMatrixBean;
import org.krysalis.barcode4j.impl.datamatrix.SymbolShapeHint;
import org.krysalis.barcode4j.impl.pdf417.PDF417Bean;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.output.svg.SVGCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;


@Controller
@RequestMapping("/")
public class ImageCodeController {

    private Integer readInt(HttpServletRequest req, String key) {
        String str = req.getParameter(key);
        if (str == null)
            return null;
        try {
            return new Integer(str);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private final static int RESOLUTION = 100;
    private final static Map<String, String> IMAGE_TYPES = new HashMap<>();
    static {
        IMAGE_TYPES.put("png", "image/png");
        IMAGE_TYPES.put("jpg", "image/jpeg");
        IMAGE_TYPES.put("gif", "image/gif");
        IMAGE_TYPES.put("svg", "image/svg+xml");
    }

    private static void generate(
            String content,
            String type,
            AbstractBarcodeBean gen,
            HttpServletResponse resp) throws Exception {
        resp.addHeader("Content-Type", IMAGE_TYPES.get(type));
        if (type.equals("svg")) {
            SVGCanvasProvider canvas = new SVGCanvasProvider(0);
            gen.generateBarcode(canvas, content);
            XmlUtil.convert(canvas.getDOMFragment(), resp.getWriter());
        } else {
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                resp.getOutputStream(), IMAGE_TYPES.get(type), RESOLUTION,
                BufferedImage.TYPE_BYTE_BINARY, false, 0);
            gen.generateBarcode(canvas, content);
            canvas.finish();
        }
    }

    @GetMapping("/qrcode")
    public void generateQrCodeAsPng(
            @NonNull @RequestParam("content") String content,
            @Nullable @RequestParam("size") Integer size,
            HttpServletResponse resp)
            throws IOException, WriterException {
        generateQrCode("png", content, size, resp);
    }

    @GetMapping("/qrcode.{type}")
    public void generateQrCode(
            @NonNull @PathVariable String type,
            @NonNull @RequestParam("content") String content,
            @Nullable @RequestParam("size") Integer size,
            HttpServletResponse resp)
            throws IOException, WriterException {
        if (type.equals("svg")) {
            if (size == null)
                size = 160;
            QRCodeWriter gen = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix matrix = gen.encode(content, BarcodeFormat.QR_CODE, size, size, hints);
            resp.addHeader("Content-Type", IMAGE_TYPES.get(type));
            resp.getWriter().print(SvgUtil.convert(size, size, (x, y) -> matrix.get(x, y)));
        } else {
            QRCode qrcode = QRCode.from(content).to(ImageType.valueOf(type.toUpperCase()))
                    .withHint(EncodeHintType.MARGIN, 1);
            if (size != null)
                qrcode.withSize(size, size);
            resp.addHeader("Content-Type", IMAGE_TYPES.get(type));
            qrcode.writeTo(resp.getOutputStream());
        }
    }

    @GetMapping("/code39")
    public void generateCode39AsPng(
            @NonNull @RequestParam("content") String content,
            @Nullable @RequestParam("size") Integer size,
            HttpServletResponse resp) throws Exception {
        generateCode39("png", content, size, resp);
    }

    @GetMapping("/code39.{type}")
    public void generateCode39(
            @NonNull @PathVariable String type,
            @NonNull @RequestParam("content") String content,
            @Nullable @RequestParam("size") Integer size,
            HttpServletResponse resp) throws Exception {
        Code39Bean gen = new Code39Bean();
        generate(content, type, gen, resp);
    }

    @GetMapping("/code128")
    public void generateCode128AsPng(
            @NonNull @RequestParam("content") String content,
            @Nullable @RequestParam("size") Integer size,
            HttpServletResponse resp) throws Exception {
        generateCode128("png", content, size, resp);
    }

    @GetMapping("/code128.{type}")
    public void generateCode128(
            @NonNull @PathVariable String type,
            @NonNull @RequestParam("content") String content,
            @Nullable @RequestParam("size") Integer size,
            HttpServletResponse resp) throws Exception {
        Code128Bean gen = new Code128Bean();
        if (size != null) {
            gen.setBarHeight(UnitConv.in2mm(size / 100.0));
        }
        generate(content, type, gen, resp);
    }

    @GetMapping("/ean13")
    public void generateEan13AsPng(
            @NonNull @RequestParam("content") String content,
            @Nullable @RequestParam("size") Integer size,
            HttpServletResponse resp) throws Exception {
        generateEan13("png", content, size, resp);
    }

    @GetMapping("/ean13.{type}")
    public void generateEan13(
            @NonNull @PathVariable String type,
            @NonNull @RequestParam("content") String content,
            @Nullable @RequestParam("size") Integer size,
            HttpServletResponse resp) throws Exception {
        EAN13Bean gen = new EAN13Bean();
        // codabar, ean-8, ean-128, upc-a, upc-e
        generate(content, type, gen, resp);
    }

    @GetMapping("/pdf417")
    public void generatePdf417AsPng(
            @NonNull @RequestParam("content") String content,
            @Nullable @RequestParam("size") Integer size,
            HttpServletResponse resp) throws Exception {
        generatePdf417("png", content, size, resp);
    }

    @GetMapping("/pdf417.{type}")
    public void generatePdf417(
            @NonNull @PathVariable String type,
            @NonNull @RequestParam("content") String content,
            @Nullable @RequestParam("size") Integer size,
            HttpServletResponse resp) throws Exception {
        PDF417Bean gen = new PDF417Bean();
        if (size != null) {
            gen.setBarHeight(UnitConv.in2mm(size / 100.0));
        }
        generate(content, type, gen, resp);
    }

    @GetMapping("/datamatrix")
    public void generateDataMatrixAsPng(
            @NonNull @RequestParam("content") String content,
            @Nullable @RequestParam("size") Integer size,
            HttpServletResponse resp) throws Exception {
        generateDataMatrix("png", content, size, resp);
    }

    @GetMapping("/datamatrix.{type}")
    public void generateDataMatrix(
            @NonNull @PathVariable String type,
            @NonNull @RequestParam("content") String content,
            @Nullable @RequestParam("size") Integer size,
            HttpServletResponse resp)
            throws Exception {
        DataMatrixBean gen = new DataMatrixBean();
        gen.setShape(SymbolShapeHint.FORCE_SQUARE);
        if (size != null) {
            double b = UnitConv.in2mm(1.0 / RESOLUTION); // base size 1px to mm
            double t = UnitConv.in2mm(1.0 * size / RESOLUTION); // target size to mm
            gen.setModuleWidth(b);
            double w = gen.calcDimensions(content).getWidth(); // original size in mm
            gen.setModuleWidth(b / w * t);
        }
        generate(content, type, gen, resp);
    }

}
