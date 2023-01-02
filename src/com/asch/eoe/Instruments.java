package com.asch.eoe;

import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;

import com.asch.eoe.envelopes.ADSREnvelope;
import com.asch.eoe.envelopes.ExponentialEnvelope;
import com.asch.eoe.filters.BandPassFilter;
import com.asch.eoe.filters.HighPassFilter;
import com.asch.eoe.filters.LowPassFilter;
import com.asch.eoe.oscillators.*;


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
    public static Clip cymbalClip;
    public static Clip openHatClip;
    public static Clip closedHatClip;

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
        createCymbal(102.8, 0.7);
        createOpenHat(0.2);
        createClosedHat();
    }

    public static void assignClipInformation(Mixer mixer, DataLine.Info clipInfo) {
        try {
            cowbellClip = (Clip) mixer.getLine(clipInfo);
            claveClip = (Clip) mixer.getLine(clipInfo);
            handclapClip = (Clip) mixer.getLine(clipInfo);
            snareClip = (Clip) mixer.getLine(clipInfo);
            bassClip = (Clip) mixer.getLine(clipInfo);
            cymbalClip = (Clip) mixer.getLine(clipInfo);
            openHatClip = (Clip) mixer.getLine(clipInfo);
            closedHatClip = (Clip) mixer.getLine(clipInfo);
        } catch (LineUnavailableException e) {
            System.out.println("assignClipInformation() failed!");
        }
    }

    public static void shutdown() {
        cowbellClip.drain();
        claveClip.drain();
        bassClip.drain();
        handclapClip.drain();
        snareClip.drain();
        cymbalClip.drain();
        openHatClip.drain();
        closedHatClip.drain();

        cowbellClip.close();
        claveClip.close();
        bassClip.close();
        handclapClip.close();
        snareClip.close();
        cymbalClip.close();
        openHatClip.close();
        closedHatClip.close();

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

    // I tried. I really tried.
    public static void createCymbal(double masterFreq, double decay) {
        Sound cymbal = new Sound();

        Square masterOsc = new Square(masterFreq);
        masterOsc.setGain(0.1);
        Square slaveOsc1 = new Square(masterFreq * 1.1414);
        slaveOsc1.setGain(0.1);
        Square slaveOsc2 = new Square(masterFreq * 1.1962);
        slaveOsc2.setGain(0.1);
        Square slaveOsc3 = new Square(masterFreq * 2.1430);
        slaveOsc3.setGain(0.1);
        Square slaveOsc4 = new Square(masterFreq * 2.4961);
        slaveOsc4.setGain(0.1);
        Square slaveOsc5 = new Square(masterFreq * 2.0558);
        slaveOsc5.setGain(0.1);

        cymbal.addOscillator(masterOsc)
                .addOscillator(slaveOsc1)
                .addOscillator(slaveOsc2)
                .addOscillator(slaveOsc3)
                .addOscillator(slaveOsc4)
                .addOscillator(slaveOsc5)
                .addOscillator(new Noise(0.2));

        cymbal.addFilter(new BandPassFilter(4430))
                .addFilter(new HighPassFilter(3730))
                .addFilter(new BandPassFilter(6270))
                .addFilter(new HighPassFilter(2640))
                .addFilter(new HighPassFilter(1980));

        ADSREnvelope cymbalEnvelope = new ADSREnvelope(0.005, 0.051, 0.8, 0.5);
        cymbalEnvelope.setSustainDuration(0.445);
        ExponentialEnvelope ampEnvelope = new ExponentialEnvelope(1, 0.001, decay);

        cymbal.setEnvelope(ampEnvelope);
        //cymbal.setAmplifier(new VoltageControlledAmplifier(ampEnvelope));

        cymbal.generate(1);

        assignClip(cymbal, cymbalClip);
    }

    public static void createOpenHat(double decay) {
        Sound hat = new Sound();

        // ABCD algorithm. A <- B modulates <- C modulates <- D modulates
        Square waveD = new Square(459);
        Square waveC = new Square(160);
        Square waveB = new Square(444);
        Square waveA = new Square(261);

        waveD.setGain(0.7);
        waveC.setGain(0.9);
        waveB.setGain(0.5);
        waveA.setGain(0.5);

        FrequencyModulatedOscillator bankCD = new FrequencyModulatedOscillator(waveC, waveD);
        FrequencyModulatedOscillator bankBC = new FrequencyModulatedOscillator(waveB, bankCD);
        FrequencyModulatedOscillator bankAB = new FrequencyModulatedOscillator(waveA, bankBC);

        hat.addOscillator(bankAB);

        // hat.addOscillator(new Noise(0.2)).addOscillator(new Noise(0.2)).addOscillator(new Noise(0.2));

        hat.addFilter(new HighPassFilter(24000));
        ExponentialEnvelope envelope = new ExponentialEnvelope(1, 0.0001, decay);
        hat.setEnvelope(envelope);
        //hat.setAmplifier(new VoltageControlledAmplifier(envelope, 1.2));
        hat.generate(1);

        assignClip(hat, openHatClip);
    }

    public static void createClosedHat() {
        Sound hat = new Sound();

        // ABCD algorithm. A <- B modulates <- C modulates <- D modulates
        Square waveD = new Square(459);
        Square waveC = new Square(160);
        Square waveB = new Square(444);
        Square waveA = new Square(261);

        waveD.setGain(0.7);
        waveC.setGain(0.9);
        waveB.setGain(0.5);
        waveA.setGain(0.5);

        FrequencyModulatedOscillator bankCD = new FrequencyModulatedOscillator(waveC, waveD);
        FrequencyModulatedOscillator bankBC = new FrequencyModulatedOscillator(waveB, bankCD);
        FrequencyModulatedOscillator bankAB = new FrequencyModulatedOscillator(waveA, bankBC);

        hat.addOscillator(bankAB);

        // hat.addOscillator(new Noise(0.2)).addOscillator(new Noise(0.2)).addOscillator(new Noise(0.2));

        hat.addFilter(new HighPassFilter(24000));
        ExponentialEnvelope envelope = new ExponentialEnvelope(1, 0.0001, 0.07);
        hat.setEnvelope(envelope);
        //hat.setAmplifier(new VoltageControlledAmplifier(envelope, 1.2));
        hat.generate(0.8);

        assignClip(hat, closedHatClip);
    }
}