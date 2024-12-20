package p1_vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import model.*;
import model.Jugador;

import Excepcions.GestorBDProjecteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import p1.capa_oracle.GestorBDEsportsJdbc;

public class CercaJugadorDialog extends JDialog {

    private GestorBDEsportsJdbc gBD;
    private MantenimentJugadors parent;

    private JComboBox<String> searchCriteriaComboBox;
    private JTextField searchTextField;
    private JButton searchButton;
    private JButton cancelButton;

    public CercaJugadorDialog(MantenimentJugadors parent, GestorBDEsportsJdbc gBD) {
        super(parent, "Cerca Jugador", true); // modal dialog
        this.parent = parent;
        this.gBD = gBD;

        initComponents();

        setSize(400, 200);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new FlowLayout());

        searchCriteriaComboBox = new JComboBox<>(new String[]{"DNI", "Data de Naixement", "Other"});
        add(searchCriteriaComboBox);

        searchTextField = new JTextField(20);
        add(searchTextField);

        searchButton = new JButton("Cerca");
        add(searchButton);

        cancelButton = new JButton("Cancel·la");
        add(cancelButton);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String criteria = (String) searchCriteriaComboBox.getSelectedItem();
                String value = searchTextField.getText();

                if (criteria.equals("DNI")) {
                    cercarPerDNI(value);
                } else if (criteria.equals("Data de Naixement")) {
                    cercarPerDataNaixement(value);
                } else if (criteria.equals("Other")) {
                    // Implement more (I don't have time to finish this)
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the dialog
            }
        });
    }

    private void cercarPerDNI(String dni) {
        try {
            Jugador jugador = gBD.cercarJugadorPerDNI(dni);
            if (jugador != null) {
                parent.updateTable(jugador);
                dispose(); // Close the dialog
            } else {
                JOptionPane.showMessageDialog(this, "No player found with DNI: " + dni);
            }
        } catch (GestorBDProjecteException ex) {
            JOptionPane.showMessageDialog(this, "Error searching for player: " + ex.getMessage());
        }
    }
    
    private void cercarPerDataNaixement(String dataNaixement) {
    try {
        Date dataNaix = parseDate(dataNaixement);

        if (dataNaix != null) {
            List<Jugador> jugadores = gBD.cercarJugadorPerDataNaix(dataNaix);
            if (!jugadores.isEmpty()) {
                parent.updateTable(jugadores); // Update the table with the list of players
                dispose(); // Close the dialog
            } else {
                JOptionPane.showMessageDialog(this, "No players found with the specified date of birth.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid date format.");
        }
    } catch (GestorBDProjecteException ex) {
        JOptionPane.showMessageDialog(this, "Error searching for players: " + ex.getMessage());
    }
}

private Date parseDate(String dateString) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    dateFormat.setLenient(false);
    try {
        return dateFormat.parse(dateString);
    } catch (ParseException ex) {
        return null;
    }
}

}