package com.asch.eoe;

import java.util.ArrayList;

public class Sound {
    private byte[] buffer;
    private int bufferSize = 0;

    private final ArrayList<Oscillator> oscillators = new ArrayList<>();
    private final ArrayList<Filter> filters = new ArrayList<>();
    private Envelope envelope;
    private VoltageControlledAmplifier amplifier;
    private double gain = 1;

    // "set" Oscillator implies that we only want one oscillator, so clear the list
    public Sound setOscillator(Oscillator oscillator) {
        removeOscillators();
        oscillators.add(oscillator);
        return this;
    }

    // Overridden by constants defined as Sine, Sawtooth, Triangle, Square
    public Sound addOscillator(Oscillator oscillator) {
        oscillators.add(oscillator);
        return this;
    }

    public void removeOscillators() {
        oscillators.clear();
    }

    public Sound setEnvelope(Envelope envelope) {
        this.envelope = envelope;
        return this;
    }

    public void removeEnvelope() {
        this.envelope = null;
    }

    public Sound addFilter(Filter filter) {
        this.filters.add(filter);
        return this;
    }

    public void removeFilters() {
        filters.clear();
    }

    public void setAmplifier(VoltageControlledAmplifier amplifier) {
        this.amplifier = amplifier;
    }

    public void setGain(double gain) {
        this.gain = gain;
    }

    public byte[] getData() {
        return buffer;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    // Combine our oscillator signals
    private double sampleOscillators(double t) {
        double sample = 0;
        // Taking the average of all the signals such that the sample is not >1
        double multiplier = 1f / oscillators.size();
        for (Oscillator o : oscillators) {
            sample += o.sample(t) * multiplier;
        }
        return sample;
    }

    private double applyFilters(double sample) {
        if (filters.isEmpty())
            return sample;

        // Pipe the output from filter to next filter
        double lastSample = sample;
        for (Filter f : filters) {
            lastSample = f.sample(lastSample);
        }
        return lastSample;
    }

    // This method generates the values of buffer for the signal
    public void generate(double duration) {
        // Initialize our buffer for generation, this is used by the clip
        buffer = new byte[(int) (Configuration.BYTES_PER_SAMPLE * duration * Configuration.SAMPLE_RATE)];

        for (double t = 0; t <= Configuration.SAMPLE_RATE * duration; t++) {
            // Step 1. Sample our base signal at a given point for oscillators
            double sample = sampleOscillators(t) * gain;

            // Step 2. Apply the envelope if necessary
            if (envelope != null) {
                sample = envelope.sample(sample, t);
            }

            // Step 3. Apply all filters to the signal
            sample = applyFilters(sample);

            // Step 4. Apply an amplifier if necessary
            if (amplifier != null) {
                sample = amplifier.sample(sample, t);
            }

            // Step 5. Encode this sample into our clip buffer
            encode(sample);
        }
    }

    private void encode(double sample) {
        if (sample < -1.0)
            sample = -1.0;
        if (sample > +1.0)
            sample = +1.0;

        short s = (short) (Configuration.MAX_16_BIT * sample);

        if (sample == 1.0)
            s = Short.MAX_VALUE;

        if(bufferSize >= buffer.length)
            return;

        // Convert the sample (a double from -1.0 to 1.0)
        // To a short value (-32767 to 32767) which is the raw data sent to the line
        // Since we specified that our audio format is little endian, put first byte of
        // sample in first index, then bit shift right by 8 and grab second byte
        buffer[bufferSize++] = (byte) s;
        buffer[bufferSize++] = (byte) (s >> 8);
    }
}
