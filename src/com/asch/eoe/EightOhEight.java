package com.asch.eoe;

import javax.sound.sampled.*;

import com.asch.eoe.envelopes.ADSREnvelope;
import com.asch.eoe.envelopes.ExponentialEnvelope;
import com.asch.eoe.filters.HighPassFilter;
import com.asch.eoe.filters.LowPassFilter;
import com.asch.eoe.filters.Resonator;
import com.asch.eoe.oscillators.Saw;
import com.asch.eoe.oscillators.Sine;
import com.asch.eoe.oscillators.Square;
import com.asch.eoe.oscillators.Triangle;

public class EightOhEight {

    private static AudioFormat format = new AudioFormat((float) Configuration.SAMPLE_RATE,
            Configuration.BITS_PER_SAMPLE, 2, true, false);
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

    private static void playScale(Sound sound) {
        double[] c_major_scale = { 261.63, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88, 523.25 };

        for (double pitch : c_major_scale) {
            sound.generate(pitch);
        }

        sound.setOscillator(new Triangle());
        for (double pitch : c_major_scale) {
            sound.generateWithFrequency(pitch);
        }

        sound.setOscillator(new Saw());
        for (double pitch : c_major_scale) {
            sound.generateWithFrequency(pitch);
        }

        sound.setOscillator(new Square());
        for (double pitch : c_major_scale) {
            sound.generateWithFrequency(pitch);
        }
    }

    private static void testBass(Sound sound) {
        sound.generateWithFrequency(50, 3);
        sound.generateWithFrequency(69, 0.5);
        sound.generateWithFrequency(43, 3);

        sound.generateWithFrequency(77.78, 0.5);
        sound.generateWithFrequency(82.41, 0.5);
        sound.generateWithFrequency(77.78, 0.5);
    }

    // Leaving this here for later but it is complete.
    private static void createCowbell() {
        Sound cowbell = new Sound(line);
        cowbell.addOscillator(new Square(545)).addOscillator(new Square(815));

        ExponentialEnvelope bellEnvelope = new ExponentialEnvelope(0.7, 0.001, 0.3);

        LowPassFilter lowPass = new LowPassFilter(700);
        HighPassFilter highPass = new HighPassFilter(700);

        cowbell.setEnvelope(bellEnvelope);
        cowbell.addFilter(lowPass);
        cowbell.addFilter(highPass);
        cowbell.generate(1);
    }

    public static void main(String[] args) {
        System.out.println("Hello world!");
        init();

        Sound bass = new Sound(line);
        bass.addOscillator(new Sine(50));
        // ADSREnvelope bassEnvelope = new ADSREnvelope(0.0001, 0.7, 1, 0.3);
        // bassEnvelope.setDuration(1.5);
        // bassEnvelope.setSustainDuration(0.15);
        ExponentialEnvelope bassEnvelope = new ExponentialEnvelope(1,0.0001, 0.8);
        bass.setEnvelope(bassEnvelope);
        //bass.setAmplifier(new VoltageControlledAmplifier(new ExponentialEnvelope(1.2, 0.0001, 1)));
        //bass.addFilter(new HighPassFilter(45));
        bass.addFilter(new LowPassFilter(50));
        bass.generate(2);

        // Envelope kick = new Envelope(0.05, 0.2,0.05, 0.05);
        // sound.setEnvelope(kick);



        line.drain();
    }
}