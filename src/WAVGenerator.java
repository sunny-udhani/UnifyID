import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class WAVGenerator {
    private List<Integer> randomInts = new ArrayList<>();
    private int subChunkSize2;
    private double[] rawData;

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

    public void generateFrequencyData()
    {
        try
        {
            int frequency = 440;
            int seconds = 5;
            int i=0;
            int dimension = seconds * 44100;
            this.rawData = new double[dimension];

            for(i=0;i<dimension;i++)
            {
                this.rawData[i] = randomInts.get(i%100) * Math.sin(2*Math.PI*i*frequency / 44100);
                System.out.println(this.rawData[i]);
            }

            this.subChunkSize2= rawData.length * 1 * 16 / 8;
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void generateWAV(){
        File f = new File("./sounds/out.wav");
        try
        {
            f.createNewFile();
            try
            {
                FileOutputStream fos = new FileOutputStream("./sounds/out.wav");
                DataOutputStream dos = new DataOutputStream(fos);
                this.generateFrequencyData();
                int chunkSize = 36 + this.subChunkSize2;
                dos.writeBytes("RIFF");
                dos.writeInt(chunkSize);
                dos.writeBytes("WAVE");
                dos.writeBytes("fmt");
                dos.writeInt(16);
                dos.writeShort(1);
                dos.writeShort(1);
                dos.writeInt(44100);
                dos.writeInt(44100*1*16/8);
                dos.writeShort(1*16/8);
                dos.writeShort(16);
                dos.writeBytes("data");

                int i=0;
                for(i=0;i<rawData.length;i++)
                {
                    dos.writeDouble(this.rawData[i]);
                }

            }
            catch(IOException exp)
            {
                System.err.println("File output stream error : " + exp.getMessage());
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {

        WAVGenerator foo = new WAVGenerator();
        foo.populateData(100);
        if(foo.randomInts.size() > 0)
        foo.generateWAV();
    }
}
