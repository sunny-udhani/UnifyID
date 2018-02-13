import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sunny Udhani
 */
public class ImageGenerator {

    private List<Integer> randomInts = new ArrayList<>();
    private static final int HEIGHT = 128;
    private static final int WIDTH = 128;
    private static final int LIMIT = 10000;


    public void populateData(int dataLen) {
        BufferedReader rd = null;
        String line = "";

        try {

            URL url = new URL("https://www.random.org/integers/?num=" + dataLen
                    + "&min=0&max=10000&col=1&base=10&format=plain&rnd=new");

            HttpURLConnection http = (HttpURLConnection) url.openConnection();

            http.setRequestMethod("GET");

            rd = new BufferedReader(new InputStreamReader(http.getInputStream()));

            while ((line = rd.readLine()) != null) {
                int pr = Integer.parseInt(line);
                randomInts.add(pr);
            }

            rd.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rd = null;
        }
    }

    public void generateImage() {
        int max = HEIGHT * WIDTH;
        BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        File outputFile = null;
        int index = 0;

        // Extracting height X width number of random numbers to generate a random Image
        // in chunks of 10000 as its the limit
        while (max > 0) {
            if (max > LIMIT) {
                populateData(LIMIT);
                max = max - LIMIT;
            } else {
                populateData(max);
                max = max - max;
            }
        }

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {

                // each number is within 0-10000 in value and we generate 3 different values
                // from one random value by using different divisors
                int r = (int) (Math.floor(randomInts.get(index) / 2)) % 255;
                int g = (int) (Math.floor(randomInts.get(index) / 3)) % 255;
                int b = (int) (Math.floor(randomInts.get(index) / 4)) % 255;
                index++;

                // Shifting RGB values to create 24bit value for Buffer Image
                int rgbVal = (r << 16 | g << 8 | b);

                img.setRGB(j, i, rgbVal);
            }
        }

        // Saving the random Image to file
        try {
            outputFile = new File("./image/generatedFromRandomIntegers.png");
            ImageIO.write(img, "png", outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException {

        ImageGenerator foo = new ImageGenerator();
        foo.generateImage();
    }
}