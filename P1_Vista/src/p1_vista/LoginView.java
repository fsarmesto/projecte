package p1_vista;

import Excepcions.GestorBDProjecteException;
import p1.capa_oracle.GestorBDEsportsJdbc;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton menuButton;
    private GestorBDEsportsJdbc gBD;
    private TextArea txtInfo;

    public LoginView() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Login");
        frame.setSize(300, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(5, 2));

        frame.add(new JLabel("Username:"));
        usernameField = new JTextField();
        frame.add(usernameField);

        frame.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        frame.add(passwordField);

        loginButton = new JButton("Login");
        frame.add(loginButton);

        menuButton = new JButton("Manteniment Jugadors");
        menuButton.setEnabled(false);
        frame.add(menuButton);

        txtInfo = new TextArea(3, 25);
        txtInfo.setEditable(false);
        frame.add(txtInfo);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                authenticateUser(username, password);
            }
        });

        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MenuPrincipal menu = new MenuPrincipal(gBD);
                menu.setVisible(true);
            }
        });
    }

    private void authenticateUser(String username, String password) {
        try {
            gBD = new GestorBDEsportsJdbc();
            User user = gBD.checkUserCredentials(username, password);
            if (user != null) {
                menuButton.setEnabled(true);
                loginButton.setEnabled(false);
                usernameField.setEnabled(false);
                passwordField.setEnabled(false);
                txtInfo.setText("Login correcte: " + user.getName());
            } else {
                txtInfo.setText("Credencials invalides, Usuari: " + username + " contrasenya: " + password + "\n" + " Usuari: " + user);
                
            }
        } catch (GestorBDProjecteException ex) {
            txtInfo.setText("Error de connexio amb la BBDD: " + ex.getMessage());
        }
    }

    public void show() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        LoginView loginView = new LoginView();
        loginView.show();
    }
}
