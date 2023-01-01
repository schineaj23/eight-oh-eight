package com.asch.eoe;

import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;

import com.asch.eoe.envelopes.ADSREnvelope;
import com.asch.eoe.envelopes.ExponentialEnvelope;
import com.asch.eoe.filters.HighPassFilter;
import com.asch.eoe.filters.LowPassFilter;
import com.asch.eoe.oscillators.Noise;
import com.asch.eoe.oscillators.Sine;
import com.asch.eoe.oscillators.Square;


// I'm not happy about how this turned out.
// I would rather have the instruments be their own classes with a common interface
// However the implementation would be different every time since the tunable parameters
// Are different, also the fact that I only wanted one instance while not having to
// address them by number in an array made things difficult.
// I'm not sure the best course of action but this class will do for the moment.
public class Instruments {
    public static Clip cowbellClip;
    public static Clip claveClip;
    public static Clip handclapClip;
    public static Clip snareClip;
    public static Clip bassClip;

    private static void assignClip(Sound sound, Clip clip) {
        // When updating make sure that we are not modifying a clip that is currently open
        if (clip.isOpen())
            clip.close();

        try {
            clip.open(Configuration.FORMAT, sound.getData(), 0, sound.getBufferSize());
        } catch (LineUnavailableException e) {
            System.out.printf("%s could not open line!\n", sound);
        }
    }

    public static void playClip(Clip clip) {
        clip.stop();
        clip.flush();
        clip.setFramePosition(0);
        clip.start();
    }

    public static void createDefaultInstruments() {
        createCowbell();
        createBassDrum(50, 0.8);
        createClave();
        createHandClap();
        createSnare(89, 0.06);
    }

    public static void assignClipInformation(Mixer mixer, DataLine.Info clipInfo) {
        try {
            cowbellClip = (Clip) mixer.getLine(clipInfo);
            claveClip = (Clip) mixer.getLine(clipInfo);
            handclapClip = (Clip) mixer.getLine(clipInfo);
            snareClip = (Clip) mixer.getLine(clipInfo);
            bassClip = (Clip) mixer.getLine(clipInfo);
        } catch(LineUnavailableException e) {
            System.out.println("assignClipInformation() failed!");
        }
    }

    public static void shutdown() {
        cowbellClip.drain();
        claveClip.drain();
        bassClip.drain();
        handclapClip.drain();
        snareClip.drain();

        cowbellClip.close();
        claveClip.close();
        bassClip.close();
        handclapClip.close();
        snareClip.close();

        System.out.println("Instruments.shutdown() successful");
    }

    public static void setClipVolume(Clip clip, float value) {
        // https://stackoverflow.com/questions/40514910/set-volume-of-java-clip
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(value));
    }

    public static void createCowbell() {
        Sound cowbell = new Sound();
        cowbell.addOscillator(new Square(545)).addOscillator(new Square(815));

        ExponentialEnvelope bellEnvelope = new ExponentialEnvelope(0.6, 0.001, 0.2);

        LowPassFilter lowPass = new LowPassFilter(700);
        HighPassFilter highPass = new HighPassFilter(700);

        cowbell.setEnvelope(bellEnvelope);
        cowbell.addFilter(lowPass);
        cowbell.addFilter(highPass);
        cowbell.generate(1);

        assignClip(cowbell, cowbellClip);
    }

    public static void createBassDrum(double tone, double decay) {
        Sound bass = new Sound();
        bass.addOscillator(new Sine(tone)); // 50
        ExponentialEnvelope bassEnvelope = new ExponentialEnvelope(1, 0.0001, decay); // 0.8
        bass.setEnvelope(bassEnvelope);
        bass.addFilter(new LowPassFilter(tone)); // 50
        bass.generate(2);

        assignClip(bass, bassClip);
    }

    public static void createClave() {
        Sound clave = new Sound();
        clave.addOscillator(new Sine(1200));
        ExponentialEnvelope bellEnvelope = new ExponentialEnvelope(1, 0.001, 0.05);
        LowPassFilter lowPass = new LowPassFilter(1200);
        HighPassFilter highPass = new HighPassFilter(1200);
        clave.setEnvelope(bellEnvelope);
        clave.addFilter(lowPass);
        clave.addFilter(highPass);
        clave.setAmplifier(new VoltageControlledAmplifier(bellEnvelope, 1.2));
        clave.generate(0.5);

        assignClip(clave, claveClip);
    }

    public static void createSnare(double tone, double snappy) {
        System.out.printf("Generating Snare with Tone: %.0f and Snappy %.3f\n", tone, snappy);

        Sound snare = new Sound();
        snare.addOscillator(new Sine(tone)).addOscillator(new Noise(0.12)); // 89

        // https://talkinmusic.com/snare-eq-phat-punchy-snare-eq/ tips for EQ'ing snares
        snare.addFilter(new LowPassFilter(1500));
        snare.addFilter(new HighPassFilter(60));

        ExponentialEnvelope snareEnvelope = new ExponentialEnvelope(2, 0.0001, snappy);// 0.06
        snare.setAmplifier(new VoltageControlledAmplifier(snareEnvelope));

        snare.generate(0.5);

        assignClip(snare, snareClip);
    }

    // TODO: implement sound mixing so I can synthesize the handclap correctly
    // Use this tutorial: https://www.youtube.com/watch?v=lG1h28gv1HU
    public static void createHandClap() {
        Sound handclap = new Sound();
        handclap.setOscillator(new Noise(0.8));

        ADSREnvelope envelope = new ADSREnvelope(0.001, 0.01, 0.8, 0.42);
        envelope.setSustainDuration(0.2);
        handclap.setEnvelope(envelope);

        ExponentialEnvelope ampEnvelope = new ExponentialEnvelope(1, 0.01, 0.25);
        handclap.setAmplifier(new VoltageControlledAmplifier(ampEnvelope));

        handclap.addFilter(new HighPassFilter(1000));
        handclap.addFilter(new LowPassFilter(6000));

        handclap.generate(0.4);

        assignClip(handclap, handclapClip);
    }
}