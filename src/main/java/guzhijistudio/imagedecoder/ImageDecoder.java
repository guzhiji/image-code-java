package guzhijistudio.imagedecoder;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ImageDecoder {

    public static class DecodedMsg {
        private String text;
        private String type;
        private List<float[]> points;

        DecodedMsg(Result result) {
            text = result.getText();
            type = result.getBarcodeFormat().name();
            points = Arrays.stream(result.getResultPoints())
                    .map(p -> new float[]{p.getX(), p.getY()})
                    .collect(Collectors.toList());
        }

        public String getText() {
            return text;
        }

        public String getType() {
            return type;
        }

        public List<float[]> getPoints() {
            return points;
        }

    }

    private static DecodedMsg decode(InputStream is) throws IOException, NotFoundException {
        BufferedImage image = ImageIO.read(is);
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        // import com.google.zxing.oned.Code39Reader;
        // Code39Reader reader = new Code39Reader();
        // import com.google.zxing.oned.MultiFormatOneDReader;
        // MultiFormatOneDReader reader = new MultiFormatOneDReader(hints);
        MultiFormatReader reader = new MultiFormatReader();
        Map<DecodeHintType, List<BarcodeFormat>> hints = new HashMap<>();
        // hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        // hints.put(DecodeHintType.TRY_HARDER, true);
        // hints.put(DecodeHintType.PURE_BARCODE, true);
        hints.put(DecodeHintType.POSSIBLE_FORMATS,
                Arrays.asList(
                        BarcodeFormat.CODE_128,
                        BarcodeFormat.CODE_93,
                        BarcodeFormat.CODE_39,
                        BarcodeFormat.QR_CODE,
                        BarcodeFormat.DATA_MATRIX));
        Result result = reader.decode(bitmap, hints);
        return new DecodedMsg(result);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Please enter path to the image to decode.");
            return;
        }
        Path path = Paths.get(args[0]);
        try (InputStream is = Files.newInputStream(path)) {

            DecodedMsg msg = decode(is);

            System.out.println(msg.getType());
            System.out.println(msg.getText());
        }
    }

}
