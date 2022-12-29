package com.asch.eoe;

import javax.sound.sampled.Clip;

import com.asch.eoe.Sequencer.Steps;
import com.io7m.digal.core.DialBoundedIntegerConverter;
import com.io7m.digal.core.DialControl;
import com.io7m.digal.core.DialControlLabelled;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.HBox;

public class Controller {

    @FXML
    private Label label;

    @FXML
    private Button startButton;

    @FXML
    private DialControl instrumentSelect;

    @FXML
    private DialControlLabelled tempo;

    @FXML
    private HBox stepContainer;

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

    private Clip selectedClip;

    public void initialize() {
        RadioButton[] stepRadioButtons = { step1, step2, step3, step4, step5, step6, step7, step8, step9, step10,
                step11, step12, step13, step14, step15, step16 };

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
                    boolean currentState = stepRadioButtons[i].selectedProperty().get();
                    boolean doAction = (steps & Steps.encodedSteps[i]) != 0;
                    if (currentState != doAction)
                        stepRadioButtons[i].setSelected(!currentState);
                }
            }
        });
        registerStepCallbacks();

        startButton.onActionProperty().set(e -> {
            System.out.println("start button onActionProperty");
            EightOhEight.cowbellClip.loop(1);
            // EightOhEight.accentClip.loop(1);
        });

        tempo.dial().setValueConverter(new DialBoundedIntegerConverter(30, 180));
        tempo.dial().setConvertedValue(90);

        tapButton.onActionProperty().set(e -> {
            if(selectedClip != null) {
                selectedClip.loop(1);
            }
        });

        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        // label.setText("Hello, JavaFX " + javafxVersion + "\nRunning on Java " +
        // javaVersion + ".");
    }

    private Sequencer sequencer = new Sequencer();

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

    private void registerStepCallbacks() {
        System.out.println("step buttons");
        step1.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean value, Boolean arg2) {
                updateSequence(1, value);
            }
        });
        step2.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean value, Boolean arg2) {
                updateSequence(2, value);
            }
        });
        step3.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean value, Boolean arg2) {
                updateSequence(3, value);
            }
        });
        step4.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean value, Boolean arg2) {
                updateSequence(4, value);
            }
        });
        step5.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean value, Boolean arg2) {
                updateSequence(5, value);
            }
        });
        step6.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean value, Boolean arg2) {
                updateSequence(6, value);
            }
        });
        step7.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean value, Boolean arg2) {
                updateSequence(7, value);
            }
        });
        step8.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean value, Boolean arg2) {
                updateSequence(8, value);
            }
        });
        step9.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean value, Boolean arg2) {
                updateSequence(9, value);
            }
        });
        step10.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean value, Boolean arg2) {
                updateSequence(10, value);
            }
        });
        step11.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean value, Boolean arg2) {
                updateSequence(11, value);
            }
        });
        step12.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean value, Boolean arg2) {
                updateSequence(12, value);
            }
        });
        step13.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean value, Boolean arg2) {
                updateSequence(13, value);
            }
        });
        step14.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean value, Boolean arg2) {
                updateSequence(14, value);
            }
        });
        step15.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean value, Boolean arg2) {
                updateSequence(15, value);
            }
        });
        step16.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean value, Boolean arg2) {
                updateSequence(16, value);
            }
        });
    }
}