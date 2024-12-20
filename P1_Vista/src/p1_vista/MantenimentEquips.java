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

public class MantenimentEquips extends JFrame {

    private GestorBDEsportsJdbc gBD;
    private int selectedSeason;
    private JList<Equip> teamList;
    private DefaultListModel<Equip> teamListModel;
    private JTextArea txtInfo;
    private JButton addTeamButton, modifyTeamButton, deleteTeamButton, searchTeamButton, checkMembersButton;
    private CheckMembersDialog checkMembersDialog;

    public MantenimentEquips(GestorBDEsportsJdbc gBD, int selectedSeason) {
        this.gBD = gBD;
        this.selectedSeason = selectedSeason;
        initComponents();
        loadTeams();
    }

    private void initComponents() {
        setTitle("Manteniment Equips - Season: " + selectedSeason);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        teamListModel = new DefaultListModel<>();
        teamList = new JList<>(teamListModel);
        JScrollPane teamListScrollPane = new JScrollPane(teamList);
        teamListScrollPane.setPreferredSize(new Dimension(300, 200));
        add(teamListScrollPane, BorderLayout.CENTER);

        txtInfo = new JTextArea(5, 30);
        txtInfo.setEditable(false);
        add(new JScrollPane(txtInfo), BorderLayout.SOUTH);

        // Create buttons for actions
        addTeamButton = new JButton("Afegir Equip");
        modifyTeamButton = new JButton("Modificar Equip");
        deleteTeamButton = new JButton("Eliminar Equip");
        searchTeamButton = new JButton("Cercar Equip");
        checkMembersButton = new JButton("Veure Membres");

        // Add action listeners to buttons
        addTeamButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addTeam();
            }
        });

        modifyTeamButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modifyTeam();
            }
        });

        deleteTeamButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteTeam();
            }
        });

        searchTeamButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchTeam();
            }
        });

        checkMembersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkMembers();
            }
        });

        // Add buttons to a panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addTeamButton);
        buttonPanel.add(modifyTeamButton);
        buttonPanel.add(deleteTeamButton);
        buttonPanel.add(searchTeamButton);
        buttonPanel.add(checkMembersButton);

        // Add button panel to the frame
        add(buttonPanel, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void loadTeams() {
        try {
            List<Equip> teams = gBD.obtenirEquipsPerTemporada(selectedSeason);
            teamListModel.clear();
            if (teams.isEmpty()) {
                txtInfo.setText("No teams found for season " + selectedSeason);
            } else {
                for (Equip team : teams) {
                    teamListModel.addElement(team);
                }
            }
        } catch (GestorBDProjecteException ex) {
            txtInfo.setText("Error loading teams: " + ex.getMessage());
        }
    }

    private void addTeam() {
        AddTeamDialog dialog = new AddTeamDialog(this, gBD, selectedSeason);
        dialog.setVisible(true);
    }

    private void modifyTeam() {
        Equip selectedTeam = teamList.getSelectedValue();
        if (selectedTeam == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un equip per modificar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ModifyTeamDialog dialog = new ModifyTeamDialog(this, gBD, selectedTeam);
        dialog.setVisible(true);
    }

    private void deleteTeam() {
        Equip selectedTeam = teamList.getSelectedValue();
        if (selectedTeam == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un equip per eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Estas segur que vols eliminar l'equip " + selectedTeam.getNom() + "?",
                "Confirmar Eliminació",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                gBD.deleteTeam(selectedTeam);
                teamListModel.removeElement(selectedTeam);
                txtInfo.setText("Equip eliminat correctament.");
            } catch (GestorBDProjecteException ex) {
                txtInfo.setText("Error en eliminar l'equip: " + ex.getMessage());
            }
        }
    }

    private void searchTeam() {
        String searchTerm = JOptionPane.showInputDialog(this, "Introdueix el terme de cerca (nom de l'equip):");
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            try {
                Equip foundTeam = gBD.cercarEquipPerNom(searchTerm); // Returns a single Equip
                teamListModel.clear();
                if (foundTeam != null) {
                    teamListModel.addElement(foundTeam); // Add the single team directly
                } else {
                    txtInfo.setText("No s'han trobat equips amb el terme de cerca: " + searchTerm);
                }
            } catch (GestorBDProjecteException ex) {
                txtInfo.setText("Error en cercar equips: " + ex.getMessage());
            }
        }
    }

    private void checkMembers() {
        Equip selectedTeam = teamList.getSelectedValue();
        if (selectedTeam == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un equip.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            List<String> members = gBD.getMemberNamesForTeam(selectedTeam.getIdEquip());
            checkMembersDialog = new CheckMembersDialog(this, selectedTeam.getNom(), members);
            checkMembersDialog.setVisible(true);
        } catch (GestorBDProjecteException ex) {
            JOptionPane.showMessageDialog(this, "Error al obtener los miembros del equipo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void refreshTeamList() {
        loadTeams();
    }

    // Inner class for AddTeamDialog
    class AddTeamDialog extends JDialog {
        private GestorBDEsportsJdbc gBD;
        private JTextField nomField;
        private JComboBox<Character> sexeComboBox;
        private JComboBox<Categoria> categoriaComboBox;
        private int temporada;

        public AddTeamDialog(JFrame parent, GestorBDEsportsJdbc gBD, int selectedSeason) {
            super(parent, "Afegir Equip", true);
            this.gBD = gBD;
            this.temporada = selectedSeason;
            initComponents();
        }

        private void initComponents() {
            setLayout(new GridLayout(5, 2));
            nomField = new JTextField();
            sexeComboBox = new JComboBox<>(new Character[]{'M', 'F'});
            categoriaComboBox = new JComboBox<>();

            try {
                List<Categoria> categories = gBD.obtenirLlistaCategories();
                for (Categoria cat : categories) {
                    categoriaComboBox.addItem(cat);
                }
            } catch (GestorBDProjecteException ex) {
                JOptionPane.showMessageDialog(this, "Error al carregar categories: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            JButton addButton = new JButton("Afegir");
            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addTeamToDatabase();
                }
            });

            JButton cancelButton = new JButton("Cancel·lar");
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            add(new JLabel("Nom:"));
            add(nomField);
            add(new JLabel("Sexe:"));
            add(sexeComboBox);
            add(new JLabel("Categoria:"));
            add(categoriaComboBox);
            add(addButton);
            add(cancelButton);

            pack();
            setLocationRelativeTo(getParent());
        }

        private void addTeamToDatabase() {
            String nom = nomField.getText();
            char sexe = (char) sexeComboBox.getSelectedItem();
            Categoria categoria = (Categoria) categoriaComboBox.getSelectedItem();

            if (nom.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nom no pot ser buit.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Equip newTeam = new Equip();
                newTeam.setNom(nom);
                newTeam.setSexe(sexe);
                newTeam.setIdCategoria(categoria.getId());
                newTeam.setTemporada(this.temporada);
                gBD.addTeam(newTeam);
                ((MantenimentEquips) getParent()).refreshTeamList();
                dispose();
            } catch (GestorBDProjecteException ex) {
                JOptionPane.showMessageDialog(this, "Error en afegir l'equip: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Inner class for ModifyTeamDialog
    class ModifyTeamDialog extends JDialog {
        private GestorBDEsportsJdbc gBD;
        private JTextField nomField;
        private JComboBox<Character> sexeComboBox;
        private JComboBox<Categoria> categoriaComboBox;
        private Equip teamToModify;

        public ModifyTeamDialog(JFrame parent, GestorBDEsportsJdbc gBD, Equip teamToModify) {
            super(parent, "Modificar Equip", true);
            this.gBD = gBD;
            this.teamToModify = teamToModify;
            initComponents();
        }

        private void initComponents() {
            setLayout(new GridLayout(5, 2));
            nomField = new JTextField(teamToModify.getNom());
            sexeComboBox = new JComboBox<>(new Character[]{'M', 'F'});
            sexeComboBox.setSelectedItem(teamToModify.getSexe());
            categoriaComboBox = new JComboBox<>();

            try {
                List<Categoria> categories = gBD.obtenirLlistaCategories();
                for (Categoria cat : categories) {
                    categoriaComboBox.addItem(cat);
                }
                Categoria currentCategory = findCategoryById(categories, teamToModify.getIdCategoria());
                if (currentCategory != null) {
                    categoriaComboBox.setSelectedItem(currentCategory);
                }
            } catch (GestorBDProjecteException ex) {
                JOptionPane.showMessageDialog(this, "Error al carregar categories: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

            JButton updateButton = new JButton("Actualitzar");
            updateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateTeamInDatabase();
                }
            });

            JButton cancelButton = new JButton("Cancel·lar");
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

            add(new JLabel("Nom:"));
            add(nomField);
            add(new JLabel("Sexe:"));
            add(sexeComboBox);
            add(new JLabel("Categoria:"));
            add(categoriaComboBox);
            add(updateButton);
            add(cancelButton);

            pack();
            setLocationRelativeTo(getParent());
        }

        private Categoria findCategoryById(List<Categoria> categories, int id) {
            for (Categoria cat : categories) {
                if (cat.getId() == id) {
                    return cat;
                }
            }
            return null; // I could maybe handle this more appropiately if I had more time for this project
        }

        private void updateTeamInDatabase() {
            String nom = nomField.getText();
            char sexe = (char) sexeComboBox.getSelectedItem();
            Categoria categoria = (Categoria) categoriaComboBox.getSelectedItem();

            if (nom.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nom no pot ser buit.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                teamToModify.setNom(nom);
                teamToModify.setSexe(sexe);
                teamToModify.setIdCategoria(categoria.getId());
                // teamToModify.setTemporada(selectedSeason);

                gBD.modifyTeam(teamToModify);
                ((MantenimentEquips) getParent()).refreshTeamList();
                dispose();
            } catch (GestorBDProjecteException ex) {
                JOptionPane.showMessageDialog(this, "Error en modificar l'equip: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Inner class for CheckMembersDialog
    class CheckMembersDialog extends JDialog {
        private JList<String> membersList;
        private DefaultListModel<String> membersListModel;
        private GestorBDEsportsJdbc gBD;

        public CheckMembersDialog(JFrame parent, String teamName, List<String> members) {
            super(parent, "Membres de l'Equip: " + teamName, true);
            this.gBD = ((MantenimentEquips) parent).gBD;
            initComponents(members);
            setSize(400, 300);
            setLocationRelativeTo(parent);
        }
        
        private void initComponents(List<String> members) {
            setLayout(new BorderLayout());

            membersListModel = new DefaultListModel<>();
            for (String member : members) {
                membersListModel.addElement(member);
            }
            membersList = new JList<>(membersListModel);

            JScrollPane membersScrollPane = new JScrollPane(membersList);
            add(membersScrollPane, BorderLayout.CENTER);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            JButton addMemberButton = new JButton("Afegir Membre");
            addMemberButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    addMember();
                }
            });
            buttonPanel.add(addMemberButton);

            JButton removeMemberButton = new JButton("Eliminar Membre");
            removeMemberButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    removeMember();
                }
            });
            buttonPanel.add(removeMemberButton);

            JButton closeButton = new JButton("Tancar");
            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            buttonPanel.add(closeButton);

            add(buttonPanel, BorderLayout.SOUTH);
        }

        public void loadMembers(int teamId) {
            try {
                List<String> members = gBD.getMemberNamesForTeam(teamId);
                membersListModel.clear();
                if (members.isEmpty()) {
                    // Optionally display a message indicating no members found
                } else {
                    for (String member : members) {
                        membersListModel.addElement(member);
                    }
                }
            } catch (GestorBDProjecteException ex) {
                JOptionPane.showMessageDialog(this, "Error al carregar els membres de l'equip: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        private void addMember() {
            Equip selectedTeam = teamList.getSelectedValue();
            if (selectedTeam == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un equip.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Pass MantenimentEquips.this to refer to the outer class instance
            AddMemberDialog dialog = new AddMemberDialog(MantenimentEquips.this, gBD, selectedTeam, selectedSeason);
            dialog.setVisible(true);

            // Refresh the CheckMembersDialog if it's open
            if (checkMembersDialog != null && checkMembersDialog.isShowing()) {
                checkMembersDialog.loadMembers(selectedTeam.getIdEquip());
            }
        }
        
        private void removeMember() {
            Equip selectedTeam = teamList.getSelectedValue();
            if (selectedTeam == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un equip.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedMember = membersList.getSelectedValue();
            if (selectedMember == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un miembro para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Extract player ID from the selected member string
            int playerId = extractPlayerId(selectedMember);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Estàs segur que vols eliminar el jugador " + selectedMember + " de l'equip?",
                    "Confirmar Eliminació",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    gBD.removeMemberFromTeam(playerId, selectedTeam.getIdEquip());
                    // Refresh the members list in CheckMembersDialog if it's open
                    if (checkMembersDialog != null && checkMembersDialog.isShowing()) {
                        checkMembersDialog.loadMembers(selectedTeam.getIdEquip());
                    }
                    txtInfo.setText("Jugador eliminat de l'equip correctament.");
                } catch (GestorBDProjecteException ex) {
                    txtInfo.setText("Error en eliminar el jugador de l'equip: " + ex.getMessage());
                }
            }
        }
        
        private int extractPlayerId(String memberInfo) {
            String playerName = memberInfo.split(" \\(")[0].trim(); // Extract player name
            try {
                List<Jugador> players = gBD.getAllPlayers();
                for (Jugador player : players) {
                    if (player.getNom().equals(playerName)) {
                        return player.getId();
                    }
                }
            } catch (GestorBDProjecteException ex) {
                JOptionPane.showMessageDialog(this, "Error al buscar el ID del jugador: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            return -1; // Return -1 or handle the error appropriately if the player is not found
        }
    }
}