package com.asch.eoe.envelopes;

import com.asch.eoe.Configuration;
import com.asch.eoe.Envelope;

public class ExponentialEnvelope implements Envelope {
    private double attackDuration;
    private double decayDuration;
    private double gain;
    private double timeConstant = 0;

    public ExponentialEnvelope(double gain, double attackDuration, double decayDuration) {
        this.attackDuration = attackDuration;
        this.decayDuration = decayDuration;
        this.gain = gain;
        this.timeConstant = 1 / (Configuration.SAMPLE_RATE * decayDuration);
    }

    @Override
    public double sample(double input, double t) {
        // The sample time in terms of 't' units (used for intervals)
        double tAttack = attackDuration * Configuration.SAMPLE_RATE;
        double tDecay = decayDuration * Configuration.SAMPLE_RATE;

        // Conversion from 't' units to time in seconds
        double time = t / Configuration.SAMPLE_RATE;

        double coefficient = 0;

        // Attack Phase
        if (t >= 0 && t <= tAttack) {
            coefficient = (1 / attackDuration) * time;
        }

        // Decay Phase
        if (t > tAttack) {
            coefficient = Math.exp(-timeConstant*t);
        }

        return coefficient * gain * input;
    }
}
