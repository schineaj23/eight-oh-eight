package com.asch.eoe;

import javax.sound.sampled.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class EightOhEight extends Application {
    private static Mixer mixer;

    private static void initAudio() {
        DataLine.Info clipInfo = new DataLine.Info(Clip.class, Configuration.FORMAT);
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();

        if (!AudioSystem.isLineSupported(clipInfo)) {
            System.out.println("Line not supported!");
        }

        try {
            for (Mixer.Info m : mixerInfo) {
                if (m.getName().contains("Primary Sound Driver")) {
                    System.out.println("Found a suitable mixer!");
                    mixer = AudioSystem.getMixer(m);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Instruments.assignClipInformation(mixer, clipInfo);
    }

    public static void main(String[] args) {
        System.out.println("808 now playing.");
        initAudio();
        Instruments.createDefaultInstruments();

        launch(args);

        System.out.println("Cleaning up...");
        Instruments.shutdown();
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