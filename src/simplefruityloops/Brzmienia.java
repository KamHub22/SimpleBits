/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simplefruityloops;

import javax.sound.midi.*;

/**
 *
 * @author Kamil
 */
public class Brzmienia {

    Sequencer sekwenser;
    Sequence sqn;
    Track sciezka;

    public void zacznijGrac() {
        try {
            sekwenser = MidiSystem.getSequencer();
            sekwenser.open();

            sqn = new Sequence(Sequence.PPQ, 4);

            sciezka = sqn.createTrack();
        
            // sekwenser.setLoopCount(4);
            sekwenser.setTempoInBPM(80);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MidiEvent komunikaty(int pkc, int kanal, int instrument, int glebia, int takt) {

        MidiEvent zdarzenie = null;
        try {
            ShortMessage wiadomosc = new ShortMessage();
            wiadomosc.setMessage(pkc, kanal, instrument, glebia);
            zdarzenie = new MidiEvent(wiadomosc, takt);
        } catch (InvalidMidiDataException e) {
        }
        return zdarzenie;
    }
    int i = 2;

    public void tempoBMP() {

        if (sekwenser.getTempoInBPM() < 140) {
            sekwenser.setTempoInBPM(120 + i);
        }
        i += 2;
        System.out.println(sekwenser.getTempoInBPM());
    }
}
