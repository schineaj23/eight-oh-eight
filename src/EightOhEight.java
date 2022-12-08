import javax.sound.sampled.*;

public class EightOhEight {
    private final int SAMPLE_RATE = 44100;
    private static final int BYTES_PER_SAMPLE = 2;                // 16-bit audio
    private static final int BITS_PER_SAMPLE = 16;                // 16-bit audio
    private static final double MAX_16_BIT = Short.MAX_VALUE;     // 32,767
    private static final int SAMPLE_BUFFER_SIZE = 4096;

    AudioFormat format = new AudioFormat((float) SAMPLE_RATE, SAMPLE_BUFFER_SIZE, 2, false, false);
    Line line = AudioSystem.getSourceDataLine(format);

    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}