package com.asch.eoe;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import com.asch.eoe.Sequencer.Steps;
import com.io7m.digal.core.DialBoundedIntegerConverter;
import com.io7m.digal.core.DialControl;
import com.io7m.digal.core.DialControlLabelled;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.effect.InnerShadow;

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
        stepRadioButtons = new RadioButton[] { step1, step2, step3, step4, step5, step6, step7, step8, step9, step10,
                step11, step12, step13, step14, step15, step16 };

        initializeSequencer();
        registerStepCallbacks();
        registerLevelCallbacks();

        // Tunable properties for instrument
        createSnareParameters();
        createBassParameters();

        instrumentSelect.setTickCount(11);
        instrumentSelect.setValueConverter(new DialBoundedIntegerConverter(0, 11));
        instrumentSelect.convertedValue().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                int val = instrumentSelect.convertedValue().intValue();
                System.out.println("Current Instrument: " + val);
                switch (val) {
                    case 1: {
                        selectedClip = EightOhEight.bassClip;
                        System.out.println("Selected Bass Drum");
                        break;
                    }
                    case 2: {
                        selectedClip = EightOhEight.snareClip;
                        System.out.println("Selected Snare");
                        break;
                    }
                    case 6: {
                        selectedClip = EightOhEight.claveClip;
                        System.out.println("Selected Clave/Rimshot");
                        break;
                    }
                    case 7: {
                        selectedClip = EightOhEight.handclapClip;
                        System.out.println("Selected Maracas/Handclap");
                        break;
                    }
                    case 8: {
                        selectedClip = EightOhEight.cowbellClip;
                        System.out.println("Selected Cowbell");
                        break;
                    }
                    default: {
                        selectedClip = null;
                        System.out.println("Selected not yet implemented");
                    }
                }

                if (selectedClip == null) {
                    // set all to not active if it doesnt exist
                    for (int i = 0; i < 16; i++) {
                        stepRadioButtons[i].setSelected(false);
                    }
                    return;
                }

                int steps = sequencer.getStepsForClip(selectedClip);
                for (int i = 0; i < 16; i++) {
                    int state = (steps & Steps.encodedSteps[i]);
                    boolean doAction = state > 0;
                    System.out.printf("Instrument: %d state: %d, doAction %b\n", val, state, doAction);
                    stepRadioButtons[i].setSelected(doAction);
                }
            }
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
        tempo.dial().convertedValue().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                int newTempo = tempo.dial().convertedValue().intValue();
                sequencer.setTempo(newTempo);
                System.out.printf("Changed tempo to %d\n", newTempo);
            }
        });

        tapButton.onActionProperty().set(e -> {
            if (selectedClip != null) {
                EightOhEight.playClip(selectedClip);
            }
        });

        clearButton.onActionProperty().set(e -> {
            sequencer.clearSequence();
            for (RadioButton b : stepRadioButtons) {
                b.setSelected(false);
                b.setEffect(null);
            }
        });
    }

    private void updateSequence(int id, boolean value) {
        if (selectedClip == null) {
            System.out.println("cannot updateSequence on null selectedClip!");
            return;
        }

        if (value) {
            sequencer.removeClipAtStep(selectedClip, id - 1);
        } else {
            sequencer.addClipAtStep(selectedClip, id - 1);
        }
    }

    private void initializeSequencer() {
        // Start the sequencer thread once the UI is up and running.
        sequencer.start();

        sequencer.step().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number newNumber) {
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

                if (val > 15)
                    return;

                stepRadioButtons[val].setEffect(new InnerShadow());
                // Set the previously highlighted step marker to default
                stepRadioButtons[(val == 0) ? 15 : val - 1].setEffect(null);
            }
        });
    }

    private void registerStepCallbacks() {
        System.out.println("Registering Step Callbacks");
        step1.selectedProperty().addListener((ChangeListener<Boolean>) (observableValue, value, arg2) -> updateSequence(1, value));
        step2.selectedProperty().addListener((ChangeListener<Boolean>) (observableValue, value, arg2) -> updateSequence(2, value));
        step3.selectedProperty().addListener((ChangeListener<Boolean>) (observableValue, value, arg2) -> updateSequence(3, value));
        step4.selectedProperty().addListener((ChangeListener<Boolean>) (observableValue, value, arg2) -> updateSequence(4, value));
        step5.selectedProperty().addListener((ChangeListener<Boolean>) (observableValue, value, arg2) -> updateSequence(5, value));
        step6.selectedProperty().addListener((ChangeListener<Boolean>) (observableValue, value, arg2) -> updateSequence(6, value));
        step7.selectedProperty().addListener((ChangeListener<Boolean>) (observableValue, value, arg2) -> updateSequence(7, value));
        step8.selectedProperty().addListener((ChangeListener<Boolean>) (observableValue, value, arg2) -> updateSequence(8, value));
        step9.selectedProperty().addListener((ChangeListener<Boolean>) (observableValue, value, arg2) -> updateSequence(9, value));
        step10.selectedProperty().addListener((ChangeListener<Boolean>) (observableValue, value, arg2) -> updateSequence(10, value));
        step11.selectedProperty().addListener((ChangeListener<Boolean>) (observableValue, value, arg2) -> updateSequence(11, value));
        step12.selectedProperty().addListener((ChangeListener<Boolean>) (observableValue, value, arg2) -> updateSequence(12, value));
        step13.selectedProperty().addListener((ChangeListener<Boolean>) (observableValue, value, arg2) -> updateSequence(13, value));
        step14.selectedProperty().addListener((ChangeListener<Boolean>) (observableValue, value, arg2) -> updateSequence(14, value));
        step15.selectedProperty().addListener((ChangeListener<Boolean>) (observableValue, value, arg2) -> updateSequence(15, value));
        step16.selectedProperty().addListener((ChangeListener<Boolean>) (observableValue, value, arg2) -> updateSequence(16, value));
    }

    private void setClipVolume(Clip clip, float value) {
        // https://stackoverflow.com/questions/40514910/set-volume-of-java-clip
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(20f * (float) Math.log10(value));
    }

    private void registerLevelCallbacks() {
        System.out.println("Registering Clip Level Callbacks");
        bassLevel.setRawValue(2);
        snareLevel.setRawValue(0.5);
        claveRimLevel.setRawValue(0.5);
        maracaClapLevel.setRawValue(0.5);
        cowbellLevel.setRawValue(0.5);

        bassLevel.rawValue().addListener((obsValue, oldVal, newVal) -> {
            setClipVolume(EightOhEight.bassClip, newVal.floatValue());
        });
        snareLevel.rawValue().addListener((obsValue, oldVal, newVal) -> {
            setClipVolume(EightOhEight.snareClip, newVal.floatValue());
        });
        claveRimLevel.rawValue().addListener((obsValue, oldVal, newVal) -> {
            setClipVolume(EightOhEight.claveClip, newVal.floatValue());
        });
        maracaClapLevel.rawValue().addListener((obsValue, oldVal, newVal) -> {
            setClipVolume(EightOhEight.handclapClip, newVal.floatValue());
        });
        cowbellLevel.rawValue().addListener((obsValue, oldVal, newVal) -> {
            setClipVolume(EightOhEight.cowbellClip, newVal.floatValue());
        });
    }

    private void createSnareParameters() {
        int defaultTone = 89;
        double defaultSnappy = 0.06;
        int defaultSnappyInt = (int) (defaultSnappy * 10000);

        snareTone.setValueConverter(new DialBoundedIntegerConverter(defaultTone / 2, defaultTone * 2));
        snareTone.setConvertedValue(defaultTone);
        snareTone.setOnMouseReleased(e -> {
            if (sequencer.isPlaying()) {
                sequencer.togglePlayState();
                for (RadioButton b : stepRadioButtons)
                    b.setEffect(null);
            }
            EightOhEight.createSnare(snareTone.convertedValue().doubleValue(),
                    (defaultSnappy * 2) - snareSnappy.convertedValue().doubleValue() / 10000f);
        });

        snareSnappy.setValueConverter(new DialBoundedIntegerConverter(defaultSnappyInt / 2, defaultSnappyInt * 2));
        snareSnappy.setConvertedValue(defaultSnappyInt);
        snareSnappy.setOnMouseReleased(e -> {
            if (sequencer.isPlaying()) {
                sequencer.togglePlayState();
                for (RadioButton b : stepRadioButtons)
                    b.setEffect(null);
            }
            EightOhEight.createSnare(snareTone.convertedValue().doubleValue(),
                    (defaultSnappy * 2) - snareSnappy.convertedValue().doubleValue() / 10000f);
        });
    }

    private void createBassParameters() {
        int defaultTone = 50;
        double defaultDecay = 0.8;
        int defaultDecayInt = (int) (defaultDecay * 10000);

        bassTone.setValueConverter(new DialBoundedIntegerConverter(defaultTone / 2, defaultTone * 4));
        bassTone.setConvertedValue(defaultTone);
        bassTone.setOnMouseReleased(e -> {
            if (sequencer.isPlaying()) {
                sequencer.togglePlayState();
                for (RadioButton b : stepRadioButtons)
                    b.setEffect(null);
            }
            EightOhEight.createBassDrum(bassTone.convertedValue().doubleValue(),
                    bassDecay.convertedValue().doubleValue() / 10000f);
        });

        bassDecay.setValueConverter(new DialBoundedIntegerConverter(defaultDecayInt / 2, defaultDecayInt * 2));
        bassDecay.setConvertedValue(defaultDecayInt);
        bassDecay.setOnMouseReleased(e -> {
            if (sequencer.isPlaying()) {
                sequencer.togglePlayState();
                for (RadioButton b : stepRadioButtons)
                    b.setEffect(null);
            }
            EightOhEight.createBassDrum(bassTone.convertedValue().doubleValue(),
                    bassDecay.convertedValue().doubleValue() / 10000f);
        });
    }

    public void shutdown() {
        sequencer.interrupt();
    }
}