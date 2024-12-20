package model;

/**
 *
 * @author Classe
 */
public class User {
    private String name;
    private String password;

    public User() {
    }

    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }

    
    
    // No setters perque no s'ha de poder obtenir aquesta informacio. Es purament logica.

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
    
    
    @Override
    public String toString(){
        return "name: " + this.name + " password: " + this.password;
    }
    
}
