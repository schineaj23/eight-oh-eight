import javax.sound.sampled.*;

public class EightOhEight {
    private static final int SAMPLE_RATE = 44100;
    private static final int BYTES_PER_SAMPLE = 2; // 16-bit audio
    private static final int BITS_PER_SAMPLE = 16; // 16-bit audio
    private static final double MAX_16_BIT = Short.MAX_VALUE; // 32,767
    private static final int SAMPLE_BUFFER_SIZE = 4096;

    private static AudioFormat format = new AudioFormat((float) SAMPLE_RATE, BITS_PER_SAMPLE, 2, true, false);
    private static SourceDataLine line;

    private static byte[] buffer;
    private static int bufferSize = 0;

    private static void init() {
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Line not supported!");
        }

        // Obtain and open the line
        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE);
        } catch (LineUnavailableException e) {
            System.out.println("Line unavailable!");
        }

        buffer = new byte[SAMPLE_BUFFER_SIZE * BYTES_PER_SAMPLE];

        line.start();
    }

    private static void play(double sample) {
        if (sample < -1.0)
            sample = -1.0;
        if (sample > +1.0)
            sample = +1.0;

        short s = (short) (MAX_16_BIT * sample);
        if (sample == 1.0)
            s = Short.MAX_VALUE;
        buffer[bufferSize++] = (byte) s;
        buffer[bufferSize++] = (byte) (s >> 8);

        if (bufferSize >= buffer.length) {
            line.write(buffer, 0, buffer.length);
            bufferSize = 0;
        }
    }

    private static void sineWave(double freq, double duration) {
        System.out.printf("Sine %.2fHz\n", freq);
        for (int i = 0; i <= SAMPLE_RATE * duration; i++) {
            double sample = 0.5 * Math.sin(2 * Math.PI * freq * i / SAMPLE_RATE);
            play(sample);
        }
    }

    private static void sineWave(double freq) {
        sineWave(freq, 1);
    }

    private static void sawWave(double freq, double duration) {
        System.out.printf("Saw %.2fHz\n", freq);
        for (double i = 0; i <= SAMPLE_RATE * duration; i++) {
            double p = SAMPLE_RATE / freq * 2;
            double sample = 2 * (i / p - Math.floor(0.5 + i / p));
            play(sample);
        }
    }

    private static void sawWave(double freq) {
        sawWave(freq, 1);
    }

    private static void squareWave(double freq, double duration) {
        System.out.printf("Square %.2fHz\n", freq);
        for (int i = 0; i <= SAMPLE_RATE * duration; i++) {
            double val = Math.sin(Math.PI * freq * i / SAMPLE_RATE);
            if (val < 0)
                val = -1;
            else if (val > 0)
                val = 1;
            double sample = 0.5 * val;
            play(sample);
        }
    }

    private static void squareWave(double freq) {
        squareWave(freq, 1);
    }

    private static void triangleWave(double freq, double duration) {
        System.out.printf("Triangle %.2fHz\n", freq);
        for (double i = 0; i <= SAMPLE_RATE * duration; i++) {
            double p = SAMPLE_RATE / freq * 2;
            double saw = 2 * (i / p - Math.floor(0.5 + i / p));
            double sample = 2 * Math.abs(saw) - 1;
            play(sample);
        }
    }

    private static void triangleWave(double freq) {
        triangleWave(freq, 1);
    }

    public static void main(String[] args) {
        System.out.println("Hello world!");
        init();

        double[] c_major_scale = { 261.63, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88, 523.25 };

        for (double pitch : c_major_scale) {
            sineWave(pitch);
            squareWave(pitch);
            sawWave(pitch);
            triangleWave(pitch);
        }

        line.drain();
    }
}