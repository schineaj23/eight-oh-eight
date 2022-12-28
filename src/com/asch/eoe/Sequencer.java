package com.asch.eoe;

import java.util.ArrayList;

import javax.sound.sampled.Clip;

public class Sequencer {
    private ArrayList<ArrayList<Clip>> sequence = new ArrayList<>();

    public Sequencer() {
        for(int i=0;i<16;i++) {
            sequence.add(new ArrayList<Clip>());
        }
    }

    public void addClipAtStep(Clip clip, int step) {
        System.out.printf("Adding clip at step %d\n", step+1);

        if(step < 1 || step > 16)
            return;

        if(!sequence.get(step).isEmpty() && sequence.get(step).contains(clip))
            return;

        sequence.get(step).add(clip);
    }

    public void removeClipAtStep(Clip clip, int step) {
        System.out.printf("Removing clip at step %d\n", step+1);

        if(step < 1 || step > 16)
            return;

        sequence.get(step).remove(clip);
    };

    public void clearSequence() {
        for(ArrayList<Clip> a : sequence) {
            a.clear();
        }
    }
}
