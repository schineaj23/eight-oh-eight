package com.asch.eoe;

import com.asch.eoe.Sequencer.Steps;
import com.io7m.digal.core.DialBoundedIntegerConverter;
import com.io7m.digal.core.DialControl;
import com.io7m.digal.core.DialControlLabelled;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.effect.InnerShadow;

import javax.sound.sampled.Clip;

// Controls the UI logic.
// I hate how long this class is.
public class Controller {

    @FXML
    private DialControl accentLevel;

    @FXML
    private DialControl bassLevel;

    @FXML
    private DialControl bassTone;

    @FXML
    private DialControl bassDecay;

    @FXML
    private DialControl snareLevel;

    @FXML
    private DialControl snareTone;

    @FXML
    private DialControl snareSnappy;

    @FXML
    private DialControl claveRimLevel;

    @FXML
    private DialControl maracaClapLevel;

    @FXML
    private DialControl cowbellLevel;

    @FXML
    private DialControl cymbalTone;

    @FXML
    private DialControl cymbalDecay;

    @FXML
    private DialControl cymbalLevel;

    @FXML
    private Button startButton;

    @FXML
    private DialControl instrumentSelect;

    @FXML
    private DialControlLabelled tempo;

    @FXML
    private RadioButton step1;

    @FXML
    private RadioButton step2;

    @FXML
    private RadioButton step3;

    @FXML
    private RadioButton step4;

    @FXML
    private RadioButton step5;

    @FXML
    private RadioButton step6;

    @FXML
    private RadioButton step7;

    @FXML
    private RadioButton step8;

    @FXML
    private RadioButton step9;

    @FXML
    private RadioButton step10;

    @FXML
    private RadioButton step11;

    @FXML
    private RadioButton step12;

    @FXML
    private RadioButton step13;

    @FXML
    private RadioButton step14;

    @FXML
    private RadioButton step15;

    @FXML
    private RadioButton step16;

    @FXML
    private Button tapButton;

    @FXML
    private Button clearButton;

    private Clip selectedClip;

    private RadioButton[] stepRadioButtons;

    private final Sequencer sequencer = new Sequencer();

    public void initialize() {
        stepRadioButtons = new RadioButton[]{step1, step2, step3, step4, step5, step6, step7, step8, step9, step10, step11, step12, step13, step14, step15, step16};

        initializeSequencer();
        registerStepCallbacks();
        registerLevelCallbacks();

        // Tunable properties for instrument
        createSnareParameters();
        createBassParameters();
        createCymbalParameters();

        instrumentSelect.setTickCount(11);
        instrumentSelect.setValueConverter(new DialBoundedIntegerConverter(0, 11));
        instrumentSelect.convertedValue().addListener((observableValue, number, t1) -> {
            int val = instrumentSelect.convertedValue().intValue();
            System.out.println("Current Instrument: " + val);
            switch (val) {
                case 1 -> {
                    selectedClip = Instruments.bassClip;
                    System.out.println("Selected Bass Drum");
                }
                case 2 -> {
                    selectedClip = Instruments.snareClip;
                    System.out.println("Selected Snare");
                }
                case 6 -> {
                    selectedClip = Instruments.claveClip;
                    System.out.println("Selected Clave/Rimshot");
                }
                case 7 -> {
                    selectedClip = Instruments.handclapClip;
                    System.out.println("Selected Maracas/Handclap");
                }
                case 8 -> {
                    selectedClip = Instruments.cowbellClip;
                    System.out.println("Selected Cowbell");
                }
                case 9 -> {
                    selectedClip = Instruments.cymbalClip;
                    System.out.println("Selected Cymbal");
                }
                default -> {
                    selectedClip = null;
                    System.out.println("Selected not yet implemented");

                    // set all to not active if it doesnt exist
                    for (int i = 0; i < 16; i++) {
                        stepRadioButtons[i].setSelected(false);
                    }
                    return;
                }
            }
            displaySteps();
        });

        startButton.onActionProperty().set(e -> {
            sequencer.togglePlayState();

            // Remove all effects after we stop
            if (!sequencer.isPlaying()) {
                for (RadioButton b : stepRadioButtons) {
                    b.setEffect(null);
                }
            }
        });

        tempo.dial().setValueConverter(new DialBoundedIntegerConverter(30, 180));
        tempo.dial().setConvertedValue(90);
        sequencer.setTempo(90);
        tempo.dial().convertedValue().addListener((arg0, arg1, arg2) -> {
            int newTempo = tempo.dial().convertedValue().intValue();
            sequencer.setTempo(newTempo);
            System.out.printf("Changed tempo to %d\n", newTempo);
        });

        tapButton.onActionProperty().set(e -> {
            if(selectedClip == null)
                return;

            if(sequencer.isPlaying()) {
                updateSequence(sequencer.step().intValue(), true);
                displaySteps();
            }
            else
                Instruments.playClip(selectedClip);
        });

        clearButton.onActionProperty().set(e -> {
            sequencer.clearSequence();
            for (RadioButton b : stepRadioButtons) {
                b.setSelected(false);
                b.setEffect(null);
            }
        });
    }

    private void displaySteps() {
        int steps = sequencer.getStepsForClip(selectedClip);
        for (int i = 0; i < 16; i++) {
            stepRadioButtons[i].setSelected((steps & Steps.encodedSteps[i]) > 0);
        }
    }

    private void updateSequence(int id, boolean value) {
        if (selectedClip == null) {
            System.out.println("cannot updateSequence on null selectedClip!");
            return;
        }

        if (value) {
            sequencer.addClipAtStep(selectedClip, id);
        } else {
            sequencer.removeClipAtStep(selectedClip, id);
        }
    }

    private void initializeSequencer() {
        // Start the sequencer thread once the UI is up and running.
        sequencer.start();

        sequencer.step().addListener((observableValue, number, newNumber) -> {
            // Since the sequencer runs on a separate thread
            // It is possible for the value to change after we already paused
            // So clear effects from all buttons to be safe
            if (!sequencer.isPlaying()) {
                for (RadioButton b : stepRadioButtons) {
                    b.setEffect(null);
                }
                return;
            }

            int val = newNumber.intValue();

            if (val > 15) return;

            stepRadioButtons[val].setEffect(new InnerShadow());
            // Set the previously highlighted step marker to default
            stepRadioButtons[(val == 0) ? 15 : val - 1].setEffect(null);
        });
    }

    private void registerStepCallbacks() {
        System.out.println("Registering Step Callbacks");
        for(RadioButton b : stepRadioButtons) {
            b.selectedProperty().addListener((o, ov, value) -> {
                updateSequence(Integer.parseInt(b.getId().substring(4)) - 1, value);
            });
        }
    }

    private void registerLevelCallbacks() {
        System.out.println("Registering Clip Level Callbacks");
        bassLevel.setRawValue(2);
        snareLevel.setRawValue(0.5);
        claveRimLevel.setRawValue(0.5);
        maracaClapLevel.setRawValue(0.5);
        cowbellLevel.setRawValue(0.5);
        cymbalLevel.setRawValue(0.5);

        bassLevel.rawValue().addListener((obsValue, oldVal, newVal) -> Instruments.setClipVolume(Instruments.bassClip, newVal.floatValue()));
        snareLevel.rawValue().addListener((obsValue, oldVal, newVal) -> Instruments.setClipVolume(Instruments.snareClip, newVal.floatValue()));
        claveRimLevel.rawValue().addListener((obsValue, oldVal, newVal) -> Instruments.setClipVolume(Instruments.claveClip, newVal.floatValue()));
        maracaClapLevel.rawValue().addListener((obsValue, oldVal, newVal) -> Instruments.setClipVolume(Instruments.handclapClip, newVal.floatValue()));
        cowbellLevel.rawValue().addListener((obsValue, oldVal, newVal) -> Instruments.setClipVolume(Instruments.cowbellClip, newVal.floatValue()));
        cymbalLevel.rawValue().addListener((obsValue, oldVal, newVal) -> Instruments.setClipVolume(Instruments.cymbalClip, newVal.floatValue()));
    }

    private void clearTimeline() {
        if (sequencer.isPlaying()) {
            sequencer.togglePlayState();
            for (RadioButton b : stepRadioButtons)
                b.setEffect(null);
        }
    }

    private void createSnareParameters() {
        int defaultTone = 89;
        double defaultSnappy = 0.06;
        int defaultSnappyInt = (int) (defaultSnappy * 10000);

        snareTone.setValueConverter(new DialBoundedIntegerConverter(defaultTone / 2, defaultTone * 2));
        snareTone.setConvertedValue(defaultTone);
        snareTone.setOnMouseReleased(e -> {
            clearTimeline();
            Instruments.createSnare(snareTone.convertedValue().doubleValue(), (defaultSnappy * 2) - snareSnappy.convertedValue().doubleValue() / 10000f);
        });

        snareSnappy.setValueConverter(new DialBoundedIntegerConverter(defaultSnappyInt / 2, defaultSnappyInt * 2));
        snareSnappy.setConvertedValue(defaultSnappyInt);
        snareSnappy.setOnMouseReleased(e -> {
            clearTimeline();
            Instruments.createSnare(snareTone.convertedValue().doubleValue(), (defaultSnappy * 2) - snareSnappy.convertedValue().doubleValue() / 10000f);
        });
    }

    private void createBassParameters() {
        int defaultTone = 50;
        double defaultDecay = 0.8;
        int defaultDecayInt = (int) (defaultDecay * 10000);

        bassTone.setValueConverter(new DialBoundedIntegerConverter(defaultTone / 2, defaultTone * 4));
        bassTone.setConvertedValue(defaultTone);
        bassTone.setOnMouseReleased(e -> {
            clearTimeline();
            Instruments.createBassDrum(bassTone.convertedValue().doubleValue(), bassDecay.convertedValue().doubleValue() / 10000f);
        });

        bassDecay.setValueConverter(new DialBoundedIntegerConverter(defaultDecayInt / 2, defaultDecayInt * 2));
        bassDecay.setConvertedValue(defaultDecayInt);
        bassDecay.setOnMouseReleased(e -> {
            clearTimeline();
            Instruments.createBassDrum(bassTone.convertedValue().doubleValue(), bassDecay.convertedValue().doubleValue() / 10000f);
        });
    }

    private void createCymbalParameters() {
        int defaultTone = 103;
        double defaultDecay = 0.7;
        int defaultDecayInt = (int) (defaultDecay * 10000);

        cymbalTone.setValueConverter(new DialBoundedIntegerConverter(defaultTone / 2, defaultTone * 4));
        cymbalTone.setConvertedValue(defaultTone);
        cymbalTone.setOnMouseReleased(e -> {
            clearTimeline();
            Instruments.createCymbal(cymbalTone.convertedValue().doubleValue(), cymbalDecay.convertedValue().doubleValue() / 10000f);
        });

        cymbalDecay.setValueConverter(new DialBoundedIntegerConverter(defaultDecayInt / 2, defaultDecayInt * 2));
        cymbalDecay.setConvertedValue(defaultDecayInt);
        cymbalDecay.setOnMouseReleased(e -> {
            clearTimeline();
            Instruments.createCymbal(cymbalTone.convertedValue().doubleValue(), cymbalDecay.convertedValue().doubleValue() / 10000f);
        });
    }

    public void shutdown() {
        sequencer.interrupt();
    }
}