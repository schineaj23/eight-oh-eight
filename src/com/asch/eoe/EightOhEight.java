package com.asch.eoe;

import javax.sound.sampled.*;

import com.asch.eoe.oscillators.Saw;
import com.asch.eoe.oscillators.Sine;
import com.asch.eoe.oscillators.Square;
import com.asch.eoe.oscillators.Triangle;

public class EightOhEight {

    private static AudioFormat format = new AudioFormat((float) Configuration.SAMPLE_RATE, Configuration.BITS_PER_SAMPLE, 2, true, false);
    private static SourceDataLine line;

    private static void init() {
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Line not supported!");
        }

        // Obtain and open the line
        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format, Configuration.SAMPLE_BUFFER_SIZE * Configuration.BYTES_PER_SAMPLE);
        } catch (LineUnavailableException e) {
            System.out.println("Line unavailable!");
        }

        line.start();
    }

    public static void main(String[] args) {
        System.out.println("Hello world!");
        init();

        double[] c_major_scale = { 261.63, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88, 523.25 };

        Sound sound = new Sound(line);
        sound.setOscillator(new Sine());

        for(double pitch : c_major_scale) {
            sound.generate(pitch);
        }

        sound.setOscillator(new Triangle());
        for(double pitch : c_major_scale) {
            sound.generate(pitch);
        }

        sound.setOscillator(new Saw());
        for(double pitch : c_major_scale) {
            sound.generate(pitch);
        }

        sound.setOscillator(new Square());
        for(double pitch : c_major_scale) {
            sound.generate(pitch);
        }

        line.drain();
    }
}