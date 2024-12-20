package p1_vista;

import Excepcions.GestorBDProjecteException;
import p1.capa_oracle.GestorBDEsportsJdbc;
import model.Jugador;
import model.Categoria;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MantenimentJugadors extends JFrame {

    private GestorBDEsportsJdbc gBD = null;
    private JFrame f;
    private JTable jTable;
    private DefaultTableModel tJugadors;
    private JTextArea txtInfo;
    private JDialog form;
    private JTextField idLegal, nom, cognom, iban, adresa, dataNaix, sexe, revMedica, rutaFoto;
    private JComboBox<Categoria> cateComboBox;
    private Boolean esAlta;
    private int filaModificada;

    public MantenimentJugadors(GestorBDEsportsJdbc gBD) {
        this.gBD = gBD;
        initComponents();
        try {
            loadAllPlayers();
        } catch (GestorBDProjecteException ex) {
            Logger.getLogger(MantenimentJugadors.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void updateTable(Jugador jugador) {
        tJugadors.setRowCount(0); // Clear existing data
        tJugadors.addRow(new Object[]{jugador.getIdLegal(), jugador.getNom(), jugador.getCognom()});
    }
    
    public void updateTable(java.util.List<Jugador> jugadores) {
        tJugadors.setRowCount(0); // Clear existing data
        for (Jugador j : jugadores) {
            tJugadors.addRow(new Object[]{j.getIdLegal(), j.getNom(), j.getCognom()});
        }
    }

    private void initComponents() {
        f = new JFrame("Manteniment de Jugadors");
        setupMainComponents();
        setupActionListeners();
        setupWindowClosing();
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private void loadAllPlayers() throws GestorBDProjecteException {
        List<Jugador> players = gBD.getAllPlayers();
        for (Jugador player : players) {
            tJugadors.addRow(new Object[]{player.getIdLegal(), player.getNom(), player.getCognom()});
        }
    }

    private void setupMainComponents() {
        tJugadors = new DefaultTableModel();
        jTable = new JTable(tJugadors);
        tJugadors.addColumn("ID Legal");
        tJugadors.addColumn("Nom");
        tJugadors.addColumn("Cognom");

        JScrollPane scrollPane = new JScrollPane(jTable);
        f.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        
        JButton btnCerca = new JButton("Cerca");
        btnCerca.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openCercaDialog();
            }
        });
        buttonPanel.add(btnCerca);
        buttonPanel.add(new JButton("Alta"));
        buttonPanel.add(new JButton("Modifica"));
        buttonPanel.add(new JButton("Elimina"));
        buttonPanel.add(new JButton("Neteja"));

        f.add(buttonPanel, BorderLayout.SOUTH);

        txtInfo = new JTextArea(5, 30);
        txtInfo.setEditable(false);
        f.add(new JScrollPane(txtInfo), BorderLayout.NORTH);
    }
    
    private void openCercaDialog() {
        CercaJugadorDialog dialog = new CercaJugadorDialog(this, gBD);
        dialog.setVisible(true);
    }

    private void setupActionListeners() {
        JPanel buttonPanel = (JPanel) f.getContentPane().getComponent(1);
        for (Component c : buttonPanel.getComponents()) {
            if (c instanceof JButton) {
                ((JButton) c).addActionListener(new GestioBotons());
            }
        }
    }

    private void setupWindowClosing() {
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (gBD != null) {
                    try {
                        gBD.tancarCapa();
                    } catch (GestorBDProjecteException ex) {
                        txtInfo.setText("Error en tancar la connexió.\n\nMotiu:\n\n" + ex.getMessage());
                    }
                }
                System.exit(0);
            }
        });
    }

    private void formulari() {
        form = new JDialog(f, true);

        // Create JTextFields for all attributes
        idLegal = new JTextField(20);
        nom = new JTextField(20);
        cognom = new JTextField(20);
        iban = new JTextField(20);
        adresa = new JTextField(20);
        dataNaix = new JTextField(10);
        sexe = new JTextField(1);
        revMedica = new JTextField(10);
        rutaFoto = new JTextField(20);

        cateComboBox = new JComboBox<>(getCategories()); // Popular categories

        JPanel formPanel = new JPanel(new GridLayout(12, 2));

        formPanel.add(new JLabel("ID Legal:"));
        formPanel.add(idLegal);

        formPanel.add(new JLabel("Nom:"));
        formPanel.add(nom);

        formPanel.add(new JLabel("Cognom:"));
        formPanel.add(cognom);

        formPanel.add(new JLabel("IBAN:"));
        formPanel.add(iban);

        formPanel.add(new JLabel("Adreça:"));
        formPanel.add(adresa);

        formPanel.add(new JLabel("Data Naix:"));
        formPanel.add(dataNaix);

        formPanel.add(new JLabel("Sexe:"));
        formPanel.add(sexe);

        formPanel.add(new JLabel("Rev. Medica:"));
        formPanel.add(revMedica);

        formPanel.add(new JLabel("Ruta Foto:"));
        formPanel.add(rutaFoto);

        formPanel.add(new JLabel("Categoria:"));
        formPanel.add(cateComboBox);

        JButton saveButton = new JButton("Desa");
        JButton cancelButton = new JButton("Cancel·la");
        saveButton.addActionListener(new GestioBotons());
        cancelButton.addActionListener(new GestioBotons());

        formPanel.add(saveButton);
        formPanel.add(cancelButton);

        form.add(formPanel);
        form.pack();
        form.setLocationRelativeTo(f);
    }

    private Categoria[] getCategories() {
        // Agafar les categories
        try {
            List<Categoria> categoriesList = gBD.obtenirLlistaCategories();
            return categoriesList.toArray(new Categoria[0]);
        } catch (GestorBDProjecteException e) {
            txtInfo.setText("Error loading categories: " + e.getMessage());
            return new Categoria[0]; // Retorna array buit si hi ha error
        }
    }

    private void netejaForm() {
        idLegal.setText("");
        nom.setText("");
        cognom.setText("");
        iban.setText("");
        adresa.setText("");
        dataNaix.setText("");
        sexe.setText("");
        revMedica.setText("");
        rutaFoto.setText("");
        cateComboBox.setSelectedIndex(0); // Reset category selection
    }

    private class GestioBotons implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case "Alta":
                    altaJugador();
                    break;
                case "Modifica":
                    modificaJugador();
                    break;
                case "Elimina":
                    eliminaJugador();
                    break;
                case "Neteja":
                    netejaTable();
                    break;
                case "Desa":
                    desaJugador();
                    break;
                case "Cancel·la":
                    form.setVisible(false);
                    break;
            }
        }
    }

    private void altaJugador() {
        if (form == null) {
            formulari();
        } else {
            netejaForm();
        }
        esAlta = true;
        form.setTitle("Alta de Jugador");
        form.setVisible(true);
    }

    private void modificaJugador() {
        filaModificada = jTable.getSelectedRow();
        if (filaModificada == -1) {
            txtInfo.setText("No hi ha cap jugador seleccionat");
            return;
        }
        if (form == null) {
            formulari();
        } else {
            netejaForm();
        }
        esAlta = false;
        form.setTitle("Modificació de Jugador");
        
        try {
            String dni = (String) tJugadors.getValueAt(filaModificada, 0);

            Jugador jugador = gBD.cercarJugadorPerDNI(dni);

            if (jugador == null) {
                txtInfo.setText("No se encontró ningún jugador con el DNI: " + dni);
                return;
            }

            idLegal.setText(jugador.getIdLegal());
            nom.setText(jugador.getNom());
            cognom.setText(jugador.getCognom());
            iban.setText(jugador.getIban());
            adresa.setText(jugador.getAdresa());
            
            // Populate dataNaix field with null check:
            Date existingDataNaix = jugador.getDataNaix();
            if (existingDataNaix != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                dataNaix.setText(dateFormat.format(existingDataNaix));
            } else {
                dataNaix.setText(""); // Or set a default value
            }

            // Populate sexe field:
            if (jugador.getSexe() == 'H') { // Assuming 'M' for male and 'F' for female
                sexe.setText("H");
            } else if (jugador.getSexe() == 'D') {
                sexe.setText("D");
            } else {
                sexe.setText(""); // Handle cases where sex is not 'M' or 'F'
            }

            revMedica.setText(String.valueOf(jugador.getRevMedica()));
            rutaFoto.setText(jugador.getRutaFoto());

            // Populate Categoria (assuming you have a way to get the Categoria list)
            // You might need to adjust this based on how you store categories
            List<Categoria> categories = gBD.obtenirLlistaCategories(); // You may need a try-catch here
            cateComboBox.removeAllItems(); // Clear existing items
            for (Categoria cat : categories) {
                cateComboBox.addItem(cat);
            }

            // Find and select the player's current category in the combo box
            if (jugador.getCate() != null) {
                for (int i = 0; i < cateComboBox.getItemCount(); i++) {
                    Categoria item = cateComboBox.getItemAt(i);
                    if (item.getId() == jugador.getCate().getId()) {
                        cateComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }

        } catch (GestorBDProjecteException ex) {
            txtInfo.setText("Error retrieving player's data: " + ex.getMessage());
        }

        form.setVisible(true);
    }

    private void eliminaJugador() {
        int filaSeleccionada = jTable.getSelectedRow();
        if (filaSeleccionada == -1) {
            txtInfo.setText("No hi ha cap jugador seleccionat");
            return;
        }

        String idLegalEliminar = (String) tJugadors.getValueAt(filaSeleccionada, 0); // Get IDLEGAL as String

        try {
            gBD.eliminarJugador(idLegalEliminar); // Pass IDLEGAL (String) to the method
            tJugadors.removeRow(filaSeleccionada);
            txtInfo.setText("Jugador eliminat correctament.");
            gBD.confirmarCanvis();
        } catch (GestorBDProjecteException ex) {
            txtInfo.setText("Error en eliminar jugador: " + ex.getMessage());
            try {
                gBD.desferCanvis();
            } catch (GestorBDProjecteException ex1) {
                txtInfo.setText(txtInfo.getText() + "\nError en desfer canvis: " + ex1.getMessage());
            }
        }
    }

    private void netejaTable() {
        tJugadors.setRowCount(0);
        txtInfo.setText("");
    }

    private void desaJugador() {
        String idLegalValue = idLegal.getText();
        String nomValue = nom.getText();
        String cognomValue = cognom.getText();
        String ibanValue = iban.getText();
        String adresaValue = adresa.getText();
        String sexeValue = sexe.getText();
        String revMedicaValue = revMedica.getText();
        String rutaFotoValue = rutaFoto.getText();

        try {
            Date dataNaixValue = null; // Initialize to null
            String dataNaixString = dataNaix.getText().trim();

            if (!dataNaixString.isEmpty()) { // Only parse if not empty
                dataNaixValue = new SimpleDateFormat("dd-MM-yyyy").parse(dataNaixString);
            }

            Jugador jugador;

            if (esAlta) { // Adding a player
                jugador = new Jugador(idLegalValue, ibanValue, nomValue, cognomValue,
                                      adresaValue, dataNaixValue, sexeValue.charAt(0),
                                      Integer.parseInt(revMedicaValue), rutaFotoValue);
                // Set the category for the new player
                Categoria selectedCategoria = (Categoria) cateComboBox.getSelectedItem();
                jugador.setCate(selectedCategoria);
                gBD.afegirJugador(jugador);
                txtInfo.setText("Alta efectuada.");
                tJugadors.addRow(new Object[]{idLegalValue, nomValue, cognomValue});
            } else { // Modifying a player
                jugador = new Jugador(idLegalValue, ibanValue, nomValue, cognomValue,
                                      adresaValue, dataNaixValue, sexeValue.charAt(0),
                                      Integer.parseInt(revMedicaValue), rutaFotoValue);
                // Set the category for the player
                Categoria selectedCategoria = (Categoria) cateComboBox.getSelectedItem();
                jugador.setCate(selectedCategoria);
                gBD.actualitzarJugador(jugador);
                txtInfo.setText("Modificació efectuada.");
                tJugadors.setValueAt(nomValue, filaModificada, 1);
                tJugadors.setValueAt(cognomValue, filaModificada, 2);
            }

            gBD.confirmarCanvis();
            tJugadors.fireTableDataChanged(); // Refresh the table data

        } catch (Exception ex) {
	        txtInfo.setText("Error en desar jugador: " + ex.getMessage());
	        try {
	            gBD.desferCanvis(); // Assuming desferCanvis method exists in gBD
	        } catch (GestorBDProjecteException ex1) {
	            txtInfo.setText(txtInfo.getText() + "\nError en desfer canvis: " + ex1.getMessage());
	        }
        }

        form.setVisible(false);
    }
}