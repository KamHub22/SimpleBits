package simplefruityloops;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.*;
import static java.awt.image.ImageObserver.WIDTH;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;
import javax.sound.midi.*;
import javax.swing.*;

public class SimpleFruityLoops implements MetaEventListener {

    JFrame ramka;
    BorderLayout uklad = new BorderLayout();
    JPanel paneltla;
    JPanel panel1;
    JPanel panel2;
    JPanel panel3;
    JButton przycisk[] = new JButton[4];
    ArrayList<JCheckBox> klikacz;
    JLabel jakiInstrument[];
    Brzmienia sounds = new Brzmienia();
    int[] instrumenty = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};
    boolean co;

    public static void main(String[] args) {

        SimpleFruityLoops sfl = new SimpleFruityLoops();
        sfl.tworzMiniStudio();
    }

    public void tworzMiniStudio() {
        ramka = new JFrame("SimpleFruityLoops");
        ramka.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        paneltla = new JPanel(uklad);
        paneltla.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //PRZYCISKI WYBORU
        panel3 = new JPanel();
        panel3.setLayout(new BoxLayout(panel3, BoxLayout.Y_AXIS));

        for (int i = 0; i < 4; i++) {
            String nazwa[] = {"Start", "Stop", "Szybciej", "Wolniej",};
            przycisk[i] = new JButton(nazwa[i]);
            panel3.add(przycisk[i]);
        }
        przycisk[0].addActionListener(new Start());
        przycisk[1].addActionListener(new Stop());
        przycisk[2].addActionListener(new Szybciej());
        przycisk[3].addActionListener(new Wolniej());

        JMenuBar menu = new JMenuBar();
        JMenu plik = new JMenu("Plik");
        JMenuItem odtworz = new JMenuItem("Odtworz");
        JMenuItem zapisz = new JMenuItem("Zapisz");
        JMenuItem wyjdz = new JMenuItem("Wyjdz");
        JMenuItem autor = new JMenuItem("Autor");
        plik.add(odtworz);
        plik.add(zapisz);
        plik.add(wyjdz);
        menu.add(plik);
        menu.add(autor);

        odtworz.addActionListener(new Odtworz());
        zapisz.addActionListener(new Zapisz());
        wyjdz.addActionListener(new Wyjdz());
        autor.addActionListener(new Autor());

        //NAZWY INSTRUMENTÓW
        panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
        jakiInstrument = new JLabel[16];
        for (int i = 0; i < 16; i++) {
            jakiInstrument[i] = new JLabel();
            panel1.add(jakiInstrument[i]);
        }

        try {
            Scanner scan1 = new Scanner(new File("nazwyinstrumentow.txt"));
            int i = 0;
            while (scan1.hasNext()) {

                Scanner scan2 = new Scanner(scan1.next()).useDelimiter(";");
                String str2 = scan2.next();
                jakiInstrument[i].setText(str2);
                Font czcionka = new Font("serif", Font.BOLD, 16);
                jakiInstrument[i++].setFont(czcionka);
            }
        } catch (Exception e) {
            e.getMessage();
        }

        //ŚRODEK
        GridLayout siatkaPolWyboru = new GridLayout(16, 16);
        siatkaPolWyboru.setVgap(1);
        siatkaPolWyboru.setHgap(2);
        panel2 = new JPanel(siatkaPolWyboru);
        klikacz = new ArrayList<JCheckBox>();

        for (int i = 0; i < 256; i++) {
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            klikacz.add(c);
            panel2.add(c);
        }

        ramka.getContentPane().add(paneltla);

        paneltla.add(BorderLayout.WEST, panel1);
        paneltla.add(BorderLayout.CENTER, panel2);
        paneltla.add(BorderLayout.EAST, panel3);

        sounds.zacznijGrac();
        ramka.setJMenuBar(menu);
        ramka.setSize(1200, 800);
        // ramka.setBounds(50, 50, 300, 300);
        ramka.pack();
        ramka.setVisible(true);

    }

    @Override
    public void meta(MetaMessage meta) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void utworzSciezkeOdtworz() {
        int[] listaSciezki = null;

        sounds.sqn.deleteTrack(sounds.sciezka);
        sounds.sciezka = sounds.sqn.createTrack();

        for (int i = 0; i < 16; i++) {
            listaSciezki = new int[16];

            int klucz = instrumenty[i];

            for (int j = 0; j < 16; j++) {
                JCheckBox jc = (JCheckBox) klikacz.get(j + (16 * i));
                if (jc.isSelected()) {
                    listaSciezki[j] = klucz;
                } else {
                    listaSciezki[j] = 0;
                }
            }
            utworzSciezke(listaSciezki);
            sounds.sciezka.add(sounds.komunikaty(176, 1, 127, 0, 16));
        }
        sounds.sciezka.add(sounds.komunikaty(192, 9, 1, 0, 15));
        try {
            sounds.sekwenser.setSequence(sounds.sqn);
            sounds.sekwenser.setLoopCount(sounds.sekwenser.LOOP_CONTINUOUSLY);
            sounds.sekwenser.start();
            sounds.sekwenser.setTempoInBPM(80);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // koniec metody

    public void utworzSciezke(int[] lista) {
        for (int i = 0; i < 16; i++) {
            int klucz = lista[i];
            if (klucz != 0) {
                sounds.sciezka.add(sounds.komunikaty(144, 9, klucz, 100, i));
                sounds.sciezka.add(sounds.komunikaty(128, 9, klucz, 100, i + 1));
            }
        }
    }

    public class Start implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            utworzSciezkeOdtworz();
        }
    }

    public class Stop implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            sounds.sekwenser.stop();
        }
    }

    public class Szybciej implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            float wspTempa = sounds.sekwenser.getTempoFactor();
            sounds.sekwenser.setTempoFactor((float) ((float) wspTempa * 1.03));
        }
    }

    public class Wolniej implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            float wspTempa = sounds.sekwenser.getTempoFactor();
            sounds.sekwenser.setTempoFactor((float) ((float) wspTempa * .97));
        }
    }

    public class Odtworz implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            co = true;
            JFileChooser plik = new JFileChooser();
            plik.showOpenDialog(ramka);
            relacjeZPlikiem(plik.getSelectedFile());

        }

    }

    public class Zapisz implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            co = false;
            JFileChooser plik = new JFileChooser();
            plik.showSaveDialog(ramka);
            relacjeZPlikiem(plik.getSelectedFile());
        }

    }

    public class Wyjdz implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ramka.dispose();

        }

    }
    
    public class Autor implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(paneltla,  "Autor: Kamil Sowa \nWiek: 23 lat \n Pochodzenie: Polska");
        }
        
    }

    private void relacjeZPlikiem(File file) {
        if (co) {
            boolean[] stanyPol = null;
            try {
                FileInputStream plikOdczytu = new FileInputStream(file);
                ObjectInputStream obiektOdczytu = new ObjectInputStream(plikOdczytu);
                stanyPol = (boolean[]) obiektOdczytu.readObject();
                
            } catch (Exception ee) {
                System.out.println("Nie da sie otworzyć tego pliku");
                ee.printStackTrace();
            }
            
            for (int i = 0; i<256; i++) {
                JCheckBox pole = (JCheckBox) klikacz.get(i);
                if (stanyPol[i]) {
                    pole.setSelected(true);
                } else {
                    pole.setSelected(false);
                }
            }
            sounds.sekwenser.stop();
            utworzSciezkeOdtworz();
        }
        else {
            boolean [] stanyPol = new boolean[256];
            
            for (int i = 0; i < 256; i++) {
                JCheckBox pole = (JCheckBox) klikacz.get(i);
                if(pole.isSelected()) {
                    stanyPol[i] = true;
                }
            }
            try {
                FileOutputStream plikZapisu = new FileOutputStream(file);
                ObjectOutputStream obiektZapisu = new ObjectOutputStream (plikZapisu);
                obiektZapisu.writeObject(stanyPol);
            }catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

}
