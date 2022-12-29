package com.asch.eoe;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.sound.sampled.Clip;

public class Sequencer {
    // The nested ArrayList is for getting the clips to play at each step
    private ArrayList<ArrayList<Clip>> sequence = new ArrayList<>();

    // This is for getting each part when displaying and programming the sequence

    // NOTE: I separated these because I didn't want to search the nested arraylist
    // every time we change instruments to display
    private Hashtable<Clip, Integer> stepsForClip;

    public class Steps {
        // Each of the indexes correspond to the mask for that step
        public static final int[] encodedSteps = { 0x8000, 0x4000, 0x2000, 0x1000, 0x800, 0x400, 0x200, 0x100, 0x80,
                0x40, 0x20, 0x10, 0x8, 0x4, 0x2, 0x1 };
    }

    public Sequencer() {
        for (int i = 0; i < 16; i++) {
            sequence.add(new ArrayList<Clip>());
        }
        stepsForClip = new Hashtable<>();
    }

    public void addClipAtStep(Clip clip, int step) {
        System.out.printf("Adding clip at step %d\n", step + 1);

        if (step < 0 || step > 16)
            return;

        if (!sequence.get(step).isEmpty() && sequence.get(step).contains(clip))
            return;

        sequence.get(step).add(clip);

        int clips = stepsForClip.getOrDefault(clip, 0);
        clips |= Steps.encodedSteps[step];
        stepsForClip.put(clip, clips);
    }

    public void removeClipAtStep(Clip clip, int step) {
        System.out.printf("Removing clip at step %d\n", step + 1);

        if (step < 0 || step > 16)
            return;

        sequence.get(step).remove(clip);

        int clips = stepsForClip.getOrDefault(clip, 0);
        clips &= Steps.encodedSteps[step];
        stepsForClip.put(clip, clips);
    };

    public int getStepsForClip(Clip clip) {
        return stepsForClip.getOrDefault(clip, 0);
    }

    public void clearSequence() {
        for (ArrayList<Clip> a : sequence) {
            a.clear();
        }
        stepsForClip.clear();
    }
}
