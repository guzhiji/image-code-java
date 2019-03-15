package com.feiliks.imagecode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.krysalis.barcode4j.impl.codabar.CodabarBean;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code128.EAN128Bean;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.impl.pdf417.PDF417Bean;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.impl.upcean.EAN8Bean;
import org.krysalis.barcode4j.impl.upcean.UPCABean;
import org.krysalis.barcode4j.impl.upcean.UPCEBean;


public class ImageCodeServlet extends HttpServlet {

    private static final long serialVersionUID = 8963978600302823918L;
    private final static Map<String, AbstractGenerator> GENERATORS = new HashMap<>();
    private final static Logger LOG = Logger.getLogger(ImageCodeServlet.class.getCanonicalName());

    static {
        GENERATORS.put("qrcode", new ZXingQRCodeGenerator());
        GENERATORS.put("codabar", new Barcode4jGenerator(CodabarBean.class));
        GENERATORS.put("code39", new Barcode4jGenerator(Code39Bean.class));
        GENERATORS.put("code128", new Barcode4jGenerator(Code128Bean.class));
        GENERATORS.put("ean8", new Barcode4jGenerator(EAN8Bean.class));
        GENERATORS.put("ean13", new Barcode4jGenerator(EAN13Bean.class));
        GENERATORS.put("ean128", new Barcode4jGenerator(EAN128Bean.class));
        GENERATORS.put("upca", new Barcode4jGenerator(UPCABean.class));
        GENERATORS.put("upce", new Barcode4jGenerator(UPCEBean.class));
        GENERATORS.put("pdf417", new Barcode4jGenerator(PDF417Bean.class));
        GENERATORS.put("datamatrix", new DataMatrixGenerator());
    }

    private static Integer readInt(HttpServletRequest req, String key) {
        String str = req.getParameter(key);
        if (str == null)
            return null;
        try {
            return new Integer(str);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    protected void doGet(HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            PathInfo pathInfo = new PathInfo(req.getPathInfo());
            AbstractGenerator gen = GENERATORS.get(pathInfo.getCodingType());
            if (gen == null)
                throw new IllegalArgumentException("invalid coding type");
            String content = req.getParameter("content");
            if (content == null)
                throw new IllegalArgumentException("no content");
            String imageType = pathInfo.getImageType();
            if (imageType == null)
                imageType = "png";
            gen.generate(imageType, content, readInt(req, "size"), resp);
        } catch(IllegalArgumentException ex) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            LOG.log(Level.INFO, ex.getMessage(), ex);
        } catch(GenerationException ex) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

}
