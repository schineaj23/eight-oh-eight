package com.asch.eoe;

import java.util.ArrayList;

import javax.sound.sampled.Clip;
import javax.sound.sampled.SourceDataLine;

public class Sound {
    private byte[] buffer;
    private int bufferSize = 0;

    private SourceDataLine line;
    private Clip clip;

    private final ArrayList<Oscillator> oscillators = new ArrayList<>();
    private final ArrayList<Filter> filters = new ArrayList<>();
    private Envelope envelope;
    private VoltageControlledAmplifier amplifier;
    private double gain = 1;

    public Sound(SourceDataLine line) {
        this.line = line;
    }

    public Sound(Clip clip) {
        this.clip = clip;
    }

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
        // Initialize our buffer for generation
        // If we are using the Clip API, then make the buffer size correspond to the duration
        if (line == null) {
            buffer = new byte[(int) (Configuration.BYTES_PER_SAMPLE * duration * Configuration.SAMPLE_RATE)];
        } else {
            // Or else, use standard buffer size for Line
            buffer = new byte[Configuration.SAMPLE_BUFFER_SIZE * Configuration.BYTES_PER_SAMPLE];
        }

        for (double t = 0; t <= Configuration.SAMPLE_RATE * duration; t++) {
            /*
             * Design Choice: Have each filter and oscillator be an interface
             * This way we can iterate over the filters and oscillators in a generic
             * way without having specific logic in the 'Sound' class.
             * I did this to create a 'builder' API for creating each sound of the 808
             */
            double sample = sampleOscillators(t) * gain;

            if (envelope != null) {
                sample = envelope.sample(sample, t);
            }

            sample = applyFilters(sample);

            if (amplifier != null) {
                sample = amplifier.sample(sample, t);
            }

            // TODO: remove me once every sound is finished, and testing is over
            // this checks if we are using Clip API or Line API
            if (line == null) {
                encode(sample);
            } else {
                play(sample);
            }
        }
    }

    // Keeping generateWithFrequency since it has a nice way of operating with the
    // others
    public void generateWithFrequency(double freq, double duration) {
        for (Oscillator o : oscillators) {
            o.setFrequency(freq);
        }
        generate(duration);
    }

    public void generateWithFrequency(double freq) {
        generateWithFrequency(freq, 1);
    }

    // Write the buffered data to the line, only when we have the full signal.
    private void play(double sample) {
        if (sample < -1.0)
            sample = -1.0;
        if (sample > +1.0)
            sample = +1.0;

        short s = (short) (Configuration.MAX_16_BIT * sample);

        if (sample == 1.0)
            s = Short.MAX_VALUE;

        // Convert the sample (a double from -1.0 to 1.0)
        // To a short value (-32767 to 32767) which is the raw data sent to the line
        // Since we specified that our audio format is little endian, put first byte of
        // sample in first index, then bit shift right by 8 and grab second byte
        buffer[bufferSize++] = (byte) s;
        buffer[bufferSize++] = (byte) (s >> 8);

        if (bufferSize >= buffer.length) {
            line.write(buffer, 0, buffer.length);
            bufferSize = 0;
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
