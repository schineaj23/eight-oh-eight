package com.asch.eoe.oscillators;

import com.asch.eoe.Configuration;
import com.asch.eoe.Oscillator;

public class FrequencyModulatedOscillator extends Oscillator {

    private Oscillator modulator;

    private double carrierGain = 1f;
    private double modulatorGain = 1f;

    private final double carrierFreq;
    private final double modulatorFreq;

    public FrequencyModulatedOscillator(Oscillator carrier, Oscillator modulator) {
        this.modulator = modulator;

        this.carrierFreq = 2 * Math.PI * carrier.getFrequency() / (double) Configuration.SAMPLE_RATE;
        this.modulatorFreq = 2 * Math.PI * modulator.getFrequency() / (double)Configuration.SAMPLE_RATE;
    }

    public void setCarrierGain(double gain) {
        this.carrierGain = gain;
    }

    public void setModulatorGain(double gain) {
        this.modulatorGain = gain;
    }

    @Override
    public double getFrequency() {
        return modulatorFreq;
    }

    @Override
    public double sample(double t) {
        // Approximate FM(t) ~= Asin(wc t + b m(t)), where m(t) = modulator
        double beta = modulatorGain / modulatorFreq;
        return carrierGain * Math.sin(carrierFreq * t + beta * modulator.sample(t));
    }
}
