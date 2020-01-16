package com.feiliks.imagecode;

import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import org.krysalis.barcode4j.impl.codabar.CodabarBean;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code128.EAN128Bean;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.impl.pdf417.PDF417Bean;
import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.impl.upcean.EAN8Bean;
import org.krysalis.barcode4j.impl.upcean.UPCABean;
import org.krysalis.barcode4j.impl.upcean.UPCEBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


@Controller
@RequestMapping("/")
public class ImageCodeController {

    private final static Map<String, AbstractGenerator> GENERATORS = new HashMap<>();
    private final static Logger LOG = Logger.getLogger(ImageCodeController.class.getCanonicalName());

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

    @ExceptionHandler(IllegalArgumentException.class)
    private ResponseEntity<String> handleIllegalArguments(IllegalArgumentException ex) {
        LOG.log(Level.INFO, ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(GenerationException.class)
    private ResponseEntity<String> handleGenerationError(GenerationException ex) {
        LOG.log(Level.SEVERE, ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    @GetMapping("/{coding:[^\\.]+}")
    public void generateImageAsPng(
            @NonNull @PathVariable String coding,
            @NonNull @RequestParam("content") String content,
            @Nullable @RequestParam("size") Integer size,
            HttpServletResponse resp) {
        generateImage(coding, "png", content, size, resp);
    }

    @GetMapping("/{coding}.{type}")
    public void generateImage(
            @NonNull @PathVariable String coding,
            @NonNull @PathVariable String type,
            @NonNull @RequestParam("content") String content,
            @Nullable @RequestParam("size") Integer size,
            HttpServletResponse resp) {
        AbstractGenerator gen = GENERATORS.get(coding);
        if (gen == null)
            throw new IllegalArgumentException("invalid coding type");
        gen.generate(type, content, size, resp);
    }

    @Autowired
    private DecoderService decoderService;

    @PostMapping("/decode")
    @ResponseBody
    public DecoderService.DecodedMsg decodeImage(HttpServletRequest req) throws IOException, NotFoundException, FormatException {
        try (InputStream is = req.getInputStream()) {
            return decoderService.decode(is);
        }
    }

    @PostMapping("/decode/multipart")
    @ResponseBody
    public DecoderService.DecodedMsg decodeImage(@RequestParam("image") MultipartFile file) throws IOException, NotFoundException, FormatException {
        try (InputStream is = file.getInputStream()) {
            return decoderService.decode(is);
        }
    }

}
