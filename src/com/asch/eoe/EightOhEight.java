package com.asch.eoe;

import javax.sound.sampled.*;

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

    public static void main(String[] args) {
        System.out.println("Hello world!");
        init();

        // Sound sound = new Sound(line);
        // sound.setOscillator(new Sine(50));
        // Envelope bass = new Envelope(0.05, 0.2, 1, 0.05);
        // sound.setEnvelope(bass);
        // testBass(sound);

        // Envelope kick = new Envelope(0.05, 0.2,0.05, 0.05);
        // sound.setEnvelope(kick);

        Sound cowbell = new Sound(line);
        cowbell.addOscillator(new Square(540)).addOscillator(new Square(800));
        //cowbell.addOscillator(new Saw(540.3));
        Envelope bellEnvelope = new Envelope(0.05, 0.15, 0.55, 0.6);
        bellEnvelope.setSustainDuration(0);
        bellEnvelope.setDuration(0.6);
        //Resonator resonator = new Resonator(2);
        LowPassFilter lowPass = new LowPassFilter(2640);
        HighPassFilter highPass = new HighPassFilter(2640);


        cowbell.setEnvelope(bellEnvelope);
        //cowbell.addFilter(resonator);
        cowbell.addFilter(lowPass);
        cowbell.addFilter(highPass);
        //cowbell.setAmplifier(new VoltageControlledAmplifier(bellEnvelope));
        cowbell.generate(0.5);

        line.drain();
    }
}