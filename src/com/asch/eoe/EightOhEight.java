package com.asch.eoe;

import javax.sound.sampled.*;

import com.asch.eoe.envelopes.ADSREnvelope;
import com.asch.eoe.envelopes.ExponentialEnvelope;
import com.asch.eoe.filters.HighPassFilter;
import com.asch.eoe.filters.LowPassFilter;
import com.asch.eoe.oscillators.Noise;
import com.asch.eoe.oscillators.Saw;
import com.asch.eoe.oscillators.Sine;
import com.asch.eoe.oscillators.Square;
import com.asch.eoe.oscillators.Triangle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class EightOhEight extends Application {

    private static SourceDataLine line;

    public static Clip cowbellClip;
    public static Clip claveClip;
    public static Clip handclapClip;
    public static Clip snareClip;
    public static Clip bassClip;

    private static Mixer mixer;

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

    private static void initAudio() {
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, Configuration.FORMAT);
        DataLine.Info clipInfo = new DataLine.Info(Clip.class, Configuration.FORMAT);
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();

        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Line not supported!");
        }

        try {
            for (Mixer.Info m : mixerInfo) {
                // System.out.printf("Mixer: %s %s\n======== ", m.getName(), m.getDescription());
                if (m.getName().contains("Primary Sound Driver")) {
                    System.out.println("Found a suitable mixer!");
                    mixer = AudioSystem.getMixer(m);
                    break;
                }
                System.out.println();
            }

            cowbellClip = (Clip) mixer.getLine(clipInfo);
            claveClip = (Clip) mixer.getLine(clipInfo);
            handclapClip = (Clip) mixer.getLine(clipInfo);
            snareClip = (Clip) mixer.getLine(clipInfo);
            bassClip = (Clip) mixer.getLine(clipInfo);


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
        double[] c_major_scale = {261.63, 293.66, 329.63, 349.23, 392.00, 440.00, 493.88, 523.25};

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
    public static void createCowbell() {
        Sound cowbell = new Sound(cowbellClip);
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
        Sound bass = new Sound(bassClip);
        bass.addOscillator(new Sine(tone)); // 50
        ExponentialEnvelope bassEnvelope = new ExponentialEnvelope(1, 0.0001, decay); // 0.8
        bass.setEnvelope(bassEnvelope);
        bass.addFilter(new LowPassFilter(tone)); // 50
        bass.generate(2);

        assignClip(bass, bassClip);
    }

    public static void createClave() {
        Sound clave = new Sound(claveClip);
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

        Sound snare = new Sound(snareClip);
        snare.addOscillator(new Sine(tone)).addOscillator(new Noise(0.12)); // 89

        // https://talkinmusic.com/snare-eq-phat-punchy-snare-eq/ tips for EQ'ing snares
        snare.addFilter(new LowPassFilter(1500));
        snare.addFilter(new HighPassFilter(60));

        ExponentialEnvelope snareEnvelope = new ExponentialEnvelope(2, 0.0001, snappy);// 0.06
        snare.setAmplifier(new VoltageControlledAmplifier(snareEnvelope));

        snare.generate(0.5);

        assignClip(snare, snareClip);
    }

    // TODO: implement sound mixing such that I can synthesize the handclap
    // correctly
    // Use this tutorial: https://www.youtube.com/watch?v=lG1h28gv1HU
    public static void createHandClap() {
        Sound handclap = new Sound(handclapClip);
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

    public static void main(String[] args) {
        System.out.println("808 now playing.");
        initAudio();

        // TODO: put create for all, or have some clas that instantiates all objects and their controllers
        createCowbell();
        createBassDrum(50, 0.8);
        createClave();
        createHandClap();
        createSnare(89, 0.06);

        launch(args);

        System.out.println("Cleaning up...");
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
        line.close();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("ui/EightOhEight.fxml")));
        Parent root = loader.load();
        primaryStage.setTitle("EightOhEight");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        Controller controller = loader.getController();
        primaryStage.setOnHidden(e -> {
            controller.shutdown();
            Platform.exit();
        });
        primaryStage.show();
    }
}