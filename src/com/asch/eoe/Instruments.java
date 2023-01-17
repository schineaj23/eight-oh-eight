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

public class Instruments {
    // This class implements the logic for each instrument
    // I first tried making every instrument its own datatype
    // However, I ran into issues preserving the same clip
    // While trying to access the instrument in a static manner
    // In the end I implemented this the way I did since I wanted a
    // "One instrument one object" paradigm with the methods modifying the instrument
    // The state of the instrument/what to play is preserved through the static Clip objects
    // and the user-controlled variables in the Controller class.

    public static Clip cowbellClip;
    public static Clip claveClip;
    public static Clip handclapClip;
    public static Clip snareClip;
    public static Clip bassClip;
    public static Clip cymbalClip;
    public static Clip openHatClip;
    public static Clip closedHatClip;
    public static Clip loCongaClip;
    public static Clip midCongaClip;
    public static Clip hiCongaClip;
    public static Clip loTomClip;
    public static Clip midTomClip;
    public static Clip hiTomClip;

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
        createCymbal(1, 0.7);
        createOpenHat(0.2);
        createClosedHat();
        createLoConga(1);
        createLoTom(1);
        createMidConga(1);
        createMidTom(1);
        createHiConga(1);
        createHiTom(1);
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
            loCongaClip = (Clip) mixer.getLine(clipInfo);
            midCongaClip = (Clip) mixer.getLine(clipInfo);
            hiCongaClip = (Clip) mixer.getLine(clipInfo);
            loTomClip = (Clip) mixer.getLine(clipInfo);
            midTomClip = (Clip) mixer.getLine(clipInfo);
            hiTomClip = (Clip) mixer.getLine(clipInfo);
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
        loCongaClip.drain();
        midCongaClip.drain();
        hiCongaClip.drain();
        loTomClip.drain();
        midTomClip.drain();
        hiTomClip.drain();

        cowbellClip.close();
        claveClip.close();
        bassClip.close();
        handclapClip.close();
        snareClip.close();
        cymbalClip.close();
        openHatClip.close();
        closedHatClip.close();
        loCongaClip.close();
        midCongaClip.close();
        hiCongaClip.close();
        loTomClip.close();
        midTomClip.close();
        hiTomClip.close();

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
        LowPassFilter lowPass = new LowPassFilter(1000);
        HighPassFilter highPass = new HighPassFilter(1500);
        clave.setEnvelope(bellEnvelope);
        clave.addFilter(lowPass);
        clave.addFilter(highPass);
        clave.setAmplifier(new VoltageControlledAmplifier(bellEnvelope, 2));
        clave.generate(0.5);

        assignClip(clave, claveClip);
    }

    public static void createSnare(double tone, double snappy) {
        System.out.printf("Generating Snare with Tone: %.0f and Snappy %.3f\n", tone, snappy);

        Sound snare = new Sound();
        Sine sine = new Sine(tone);
        sine.setGain(2);
        snare.addOscillator(sine).addOscillator(new Noise(0.12)); // 89

        // https://talkinmusic.com/snare-eq-phat-punchy-snare-eq/ tips for EQ'ing snares

        ExponentialEnvelope snareEnvelope = new ExponentialEnvelope(2, 0.0001, snappy);// 0.06
        snare.setAmplifier(new VoltageControlledAmplifier(snareEnvelope));

        snare.generate(0.5);

        assignClip(snare, snareClip);
    }

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

    public static void createCymbal(double toneMultiplier, double decay) {
        Sound cymbal = new Sound();

        FrequencyModulatedOscillator bankA = new FrequencyModulatedOscillator(new Square(1047 * toneMultiplier), new Square(1481 * toneMultiplier));
        FrequencyModulatedOscillator bankB = new FrequencyModulatedOscillator(new Square(1109 * toneMultiplier), new Square(1049 * toneMultiplier));
        FrequencyModulatedOscillator bankC = new FrequencyModulatedOscillator(new Saw(1175 * toneMultiplier), new Saw(1480 * toneMultiplier));

        cymbal.addOscillator(bankA).addOscillator(bankB).addOscillator(bankC).addOscillator(new Noise(0.3));

        cymbal.addFilter(new BandPassFilter(1050));
        cymbal.addFilter(new HighPassFilter(2490));

        ExponentialEnvelope cymbalEnvelope = new ExponentialEnvelope(1, 0.15, decay);

        cymbal.setEnvelope(cymbalEnvelope);

        cymbal.generate(3);

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

        hat.addFilter(new HighPassFilter(24000));
        ExponentialEnvelope envelope = new ExponentialEnvelope(1, 0.0001, decay);
        hat.setEnvelope(envelope);
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

        hat.addFilter(new HighPassFilter(24000));
        ExponentialEnvelope envelope = new ExponentialEnvelope(1, 0.0001, 0.07);
        hat.setEnvelope(envelope);
        hat.generate(0.8);

        assignClip(hat, closedHatClip);
    }

    public static void createLoConga(double tuningMultiplier) {
        Sound conga = new Sound();
        Sine sine = new Sine(89 * tuningMultiplier); // 89
        sine.setGain(2);
        conga.addOscillator(sine);

        ExponentialEnvelope snareEnvelope = new ExponentialEnvelope(1, 0.0001, 0.18);// 0.18
        conga.setAmplifier(new VoltageControlledAmplifier(snareEnvelope, 1.1));

        conga.generate(0.5);

        assignClip(conga, loCongaClip);
    }

    public static void createLoTom(double tuningMultiplier) {
        Sound tom = new Sound();
        Sine sine = new Sine(40 * tuningMultiplier); // 89
        sine.setGain(2);
        tom.addOscillator(sine);

        ExponentialEnvelope snareEnvelope = new ExponentialEnvelope(1, 0.0001, 0.18);// 0.18
        tom.setAmplifier(new VoltageControlledAmplifier(snareEnvelope, 1.1));

        tom.generate(0.5);

        assignClip(tom, loTomClip);
    }

    public static void createMidConga(double tuningMultiplier) {
        Sound conga = new Sound();
        Sine sine = new Sine(120 * tuningMultiplier);
        sine.setGain(2);
        conga.addOscillator(sine);

        ExponentialEnvelope snareEnvelope = new ExponentialEnvelope(1, 0.0001, 0.07); // 0.07
        conga.setAmplifier(new VoltageControlledAmplifier(snareEnvelope, 1.1));

        conga.generate(0.5);

        assignClip(conga, midCongaClip);
    }

    public static void createMidTom(double tuningMultiplier) {
        Sound tom = new Sound();
        Sine sine = new Sine(60 * tuningMultiplier);
        sine.setGain(2);
        tom.addOscillator(sine);

        ExponentialEnvelope snareEnvelope = new ExponentialEnvelope(1, 0.0001, 0.18);// 0.18
        tom.setAmplifier(new VoltageControlledAmplifier(snareEnvelope, 1.1));

        tom.addFilter(new LowPassFilter(60));

        tom.generate(0.5);

        assignClip(tom, midTomClip);
    }

    public static void createHiConga(double tuningMultiplier) {
        Sound conga = new Sound();
        Sine sine = new Sine(189); // 189
        sine.setGain(2);
        conga.addOscillator(sine);

        ExponentialEnvelope snareEnvelope = new ExponentialEnvelope(1, 0.0001, 0.07); // 0.07
        conga.setAmplifier(new VoltageControlledAmplifier(snareEnvelope, 1.1));

        conga.generate(0.5);

        assignClip(conga, hiCongaClip);
    }

    public static void createHiTom(double tuningMultiplier) {
        Sound tom = new Sound();
        Sine sine = new Sine(80 * tuningMultiplier);
        sine.setGain(2);
        tom.addOscillator(sine);

        ExponentialEnvelope snareEnvelope = new ExponentialEnvelope(1, 0.0001, 0.09);// 0.18
        tom.setAmplifier(new VoltageControlledAmplifier(snareEnvelope, 1.1));

        tom.generate(0.5);

        assignClip(tom, hiTomClip);
    }
}