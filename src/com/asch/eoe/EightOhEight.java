package com.asch.eoe;

import javax.sound.sampled.*;

import com.asch.eoe.envelopes.ADSREnvelope;
import com.asch.eoe.envelopes.ExponentialEnvelope;
import com.asch.eoe.filters.HighPassFilter;
import com.asch.eoe.filters.LowPassFilter;
import com.asch.eoe.filters.Resonator;
import com.asch.eoe.oscillators.Noise;
import com.asch.eoe.oscillators.Saw;
import com.asch.eoe.oscillators.Sine;
import com.asch.eoe.oscillators.Square;
import com.asch.eoe.oscillators.Triangle;

public class EightOhEight {

    private static SourceDataLine line;

    private static Clip cowbellClip;

    private static Mixer mixer;

    private static void init() {
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, Configuration.FORMAT);
        DataLine.Info clipInfo = new DataLine.Info(Clip.class, Configuration.FORMAT);
        Mixer.Info mixerInfo[] = AudioSystem.getMixerInfo();

        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Line not supported!");
        }

        for (Mixer.Info m : mixerInfo) {
            System.out.printf("Mixer: %s\n========\n", m.getName());
        }

        try {
            // TODO: detect default interface
            mixer = AudioSystem.getMixer(mixerInfo[5]);
            cowbellClip = (Clip) mixer.getLine(clipInfo);
            line = (SourceDataLine) mixer.getLine(info);
            line.open(Configuration.FORMAT, Configuration.SAMPLE_BUFFER_SIZE * Configuration.BYTES_PER_SAMPLE);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Obtain and open the line
        // try {
        // line = (SourceDataLine) AudioSystem.getLine(info);
        // line.open(format, Configuration.SAMPLE_BUFFER_SIZE *
        // Configuration.BYTES_PER_SAMPLE);
        // } catch (LineUnavailableException e) {
        // System.out.println("Line unavailable!");
        // }
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
        Sound cowbell = new Sound(cowbellClip);
        cowbell.addOscillator(new Square(545)).addOscillator(new Square(815));

        ExponentialEnvelope bellEnvelope = new ExponentialEnvelope(0.6, 0.001, 0.2);

        LowPassFilter lowPass = new LowPassFilter(700);
        HighPassFilter highPass = new HighPassFilter(700);

        cowbell.setEnvelope(bellEnvelope);
        cowbell.addFilter(lowPass);
        cowbell.addFilter(highPass);
        cowbell.generate(1);

        try {
            cowbellClip.open(Configuration.FORMAT, cowbell.getData(), 0, cowbell.getBufferSize());
        } catch (LineUnavailableException e) {
            System.out.println("Cowbell could not open line!");
        }

        System.out.printf("bufferSize: %d", cowbell.getBufferSize());

        cowbellClip.setFramePosition(0);
        cowbellClip.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static void createBassDrum() {
        Sound bass = new Sound(line);
        bass.addOscillator(new Sine(50));
        ExponentialEnvelope bassEnvelope = new ExponentialEnvelope(1, 0.0001, 0.8);
        bass.setEnvelope(bassEnvelope);
        bass.addFilter(new LowPassFilter(50));
        bass.generate(2);
    }

    private static void createClave() {
        Sound clave = new Sound(line);
        clave.addOscillator(new Sine(1200));
        ExponentialEnvelope bellEnvelope = new ExponentialEnvelope(1, 0.001, 0.05);
        LowPassFilter lowPass = new LowPassFilter(1200);
        HighPassFilter highPass = new HighPassFilter(1200);
        clave.setEnvelope(bellEnvelope);
        clave.addFilter(lowPass);
        clave.addFilter(highPass);
        clave.setAmplifier(new VoltageControlledAmplifier(bellEnvelope, 1.2));
        clave.generate(0.5);
    }

    private static void createSnare() {
        Sound snare = new Sound(line);
        snare.addOscillator(new Sine(89)).addOscillator(new Noise(0.12));

        // https://talkinmusic.com/snare-eq-phat-punchy-snare-eq/ tips for EQ'ing snares
        snare.addFilter(new LowPassFilter(1500));
        snare.addFilter(new HighPassFilter(60));

        ExponentialEnvelope snareEnvelope = new ExponentialEnvelope(2, 0.0001, 0.06);
        snare.setAmplifier(new VoltageControlledAmplifier(snareEnvelope));

        snare.generate(0.5);
    }

    // TODO: implement sound mixing such that I can synthesize the handclap
    // correctly
    // Use this tutorial: https://www.youtube.com/watch?v=lG1h28gv1HU
    private static void createHandClap() {
        Sound handclap = new Sound(line);
        handclap.setOscillator(new Noise(0.8));

        ADSREnvelope envelope = new ADSREnvelope(0.001, 0.01, 0.8, 0.42);
        envelope.setSustainDuration(0.2);
        handclap.setEnvelope(envelope);

        ExponentialEnvelope ampEnvelope = new ExponentialEnvelope(1, 0.01, 0.25);
        handclap.setAmplifier(new VoltageControlledAmplifier(ampEnvelope));

        handclap.addFilter(new HighPassFilter(1000));
        handclap.addFilter(new LowPassFilter(6000));

        handclap.generate(0.4);
    }

    public static void main(String[] args) {
        System.out.println("808 now playing.");
        init();

        createCowbell();

        line.drain();
    }
}