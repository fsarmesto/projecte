package p1_vista;

import Excepcions.GestorBDProjecteException;
import model.Categoria;
import p1.capa_oracle.GestorBDEsportsJdbc;
import model.Equip;
import model.Jugador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AddMemberDialog extends JDialog {
    private GestorBDEsportsJdbc gBD;
    private JTextField nomField;
    private JComboBox<Character> sexeComboBox;
    private JComboBox<Categoria> categoriaComboBox;
    private JComboBox<Jugador> playerComboBox;
    private JCheckBox titularCheckBox;
    private JButton addButton;
    private JButton cancelButton;
    private int temporada;
    private Equip selectedTeam;

    public AddMemberDialog(MantenimentEquips parent, GestorBDEsportsJdbc gBD, Equip selectedTeam, int selectedSeason) {
        super(parent, "Afegir Membre a l'Equip", true);
        this.gBD = gBD;
        this.temporada = selectedSeason;
        this.selectedTeam = selectedTeam;
        initComponents(selectedSeason);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents(int selectedSeason) {
        setLayout(new GridLayout(4, 2));

        playerComboBox = new JComboBox<>();
        titularCheckBox = new JCheckBox("Titular");
        add(new JLabel("Titular:"));
        add(titularCheckBox);
        
        try {
            List<Jugador> players = gBD.getAllPlayers(); // Assuming you have this method
            for (Jugador player : players) {
                playerComboBox.addItem(player);
            }
        } catch (GestorBDProjecteException ex) {
            JOptionPane.showMessageDialog(this, "Error al carregar jugadors: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        addButton = new JButton("Afegir");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMemberToTeam();
            }
        });

        cancelButton = new JButton("Cancel·lar");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        add(new JLabel("Jugador:"));
        add(playerComboBox);
        add(titularCheckBox);
        add(addButton);
        add(cancelButton);
    }

    private void addMemberToTeam() {
        Jugador selectedPlayer = (Jugador) playerComboBox.getSelectedItem();
        if (selectedPlayer == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un jugador.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check gender restrictions
        if (!isPlayerAllowed(selectedPlayer, selectedTeam)) {
            JOptionPane.showMessageDialog(this, "Aquest jugador no pot unir-se a aquest equip a causa de restriccions de sexe.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean isTitular = titularCheckBox.isSelected();

        try {
            gBD.addMemberToTeam(selectedPlayer.getId(), selectedTeam.getIdEquip(), isTitular ? 1 : 0); // 1 for titular, 0 for not
            JOptionPane.showMessageDialog(this, "Jugador afegit a l'equip correctament.");
            dispose();
        } catch (GestorBDProjecteException ex) {
            JOptionPane.showMessageDialog(this, "Error en afegir el jugador a l'equip: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isPlayerAllowed(Jugador player, Equip team) {
        if (player.getSexe() != 'M' && player.getSexe() != team.getSexe()) {
            return false;
        }
        return true;
    }
}
