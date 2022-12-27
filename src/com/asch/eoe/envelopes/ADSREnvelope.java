package com.asch.eoe.envelopes;

import com.asch.eoe.Configuration;
import com.asch.eoe.Envelope;

public class ADSREnvelope implements Envelope {
    // An ADSR Envelope Generator
    private double attackDuration;
    private double decayDuration;
    private double sustainGain;
    private double sustainDuration;
    private double releaseDuration;
    private double duration = Configuration.SAMPLE_RATE * 1f; // Default duration of the envelope is 1 second.

    public ADSREnvelope(double attackDuration, double decayDuration, double sustainGain, double releaseDuration) {
        this.attackDuration = attackDuration;
        this.decayDuration = decayDuration;
        this.sustainGain = sustainGain;
        this.releaseDuration = releaseDuration;
        this.sustainDuration = duration - (attackDuration + decayDuration + releaseDuration);
    }

    public ADSREnvelope setDuration(double duration) {
        this.duration = duration;
        // Since our max duration changed, adjust the sustain accordingly
        this.sustainDuration = duration - (attackDuration + decayDuration + releaseDuration);
        return this;
    }

    public ADSREnvelope setSustainDuration(double duration) {
        this.sustainDuration = duration;
        return this;
    }

    // https://www.desmos.com/calculator/xuxwmr8yte Visualization of ADSR envelope
    @Override
    public double sample(double input, double t) {
        // The sample time in terms of 't' units (used for intervals)
        double tAttack = attackDuration * Configuration.SAMPLE_RATE;
        double tDecay = decayDuration * Configuration.SAMPLE_RATE;
        double tSustain = sustainDuration * Configuration.SAMPLE_RATE;
        double tRelease = releaseDuration * Configuration.SAMPLE_RATE;

        // Conversion from 't' units to time in seconds
        double time = t / Configuration.SAMPLE_RATE;

        double coefficient = 0;

        // Attack Phase
        if (t >= 0 && t <= tAttack) {
            coefficient = (1 / attackDuration) * time;
        }

        // Decay Phase
        else if (t > tAttack && t <= tAttack + tDecay) {
            coefficient = ((sustainGain - 1) / sustainDuration) * (time - attackDuration) + 1;
        }

        // Sustain Phase
        else if (t > tAttack + tDecay && t <= tAttack + tDecay + tSustain) {
            coefficient = sustainGain;
        }

        // Release Phase
        else if (t > tAttack + tDecay + tSustain && t <= tAttack + tDecay + tSustain + tRelease) {
            coefficient = ((-sustainGain / releaseDuration) * (time - (attackDuration + decayDuration + sustainDuration))) + sustainGain;
        }

        return coefficient * input;
    }
}
