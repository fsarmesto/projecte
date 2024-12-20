package p1_vista;

import Excepcions.GestorBDProjecteException;
import p1.capa_oracle.GestorBDEsportsJdbc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MenuPrincipal extends JFrame {
    private GestorBDEsportsJdbc gBD;
    private JComboBox<Integer> seasonComboBox;
    private JButton selectSeasonButton;
    private JButton playersButton;
    private JTextArea txtInfo;

    public MenuPrincipal(GestorBDEsportsJdbc gBD) {
        this.gBD = gBD;
        initComponents();
        loadSeasons();
    }

    private void initComponents() {
        setTitle("Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));

        seasonComboBox = new JComboBox<>();
        selectSeasonButton = new JButton("Select Season");
        playersButton = new JButton("Players");

        selectSeasonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Integer selectedSeason = (Integer) seasonComboBox.getSelectedItem();
                if (selectedSeason != null) {
                    MantenimentEquips mantenimentEquips = new MantenimentEquips(gBD, selectedSeason);
                    mantenimentEquips.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(MenuPrincipal.this, "Please select a season first.");
                }
            }
        });

        playersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MantenimentJugadors mantenimentJugadors = new MantenimentJugadors(gBD);
                mantenimentJugadors.setVisible(true);
            }
        });
           

        buttonPanel.add(seasonComboBox);
        buttonPanel.add(selectSeasonButton);
        buttonPanel.add(playersButton);

        add(buttonPanel, BorderLayout.CENTER);

        txtInfo = new JTextArea(5, 30);
        txtInfo.setEditable(false);
        add(new JScrollPane(txtInfo), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void loadSeasons() {
        try {
            List<Integer> seasons = gBD.obtenirLlistaTemporades();
            for (Integer season : seasons) {
                seasonComboBox.addItem(season);
            }
            if (seasonComboBox.getItemCount() == 0) {
                txtInfo.setText("No seasons available.");
            }
        } catch (GestorBDProjecteException ex) {
            txtInfo.setText("Error loading seasons: " + ex.getMessage());
        }
    }

    public void setVisible(boolean b) {
        super.setVisible(b);
    }
}