package model;

/**
 *
 * @author Ferran Sanchis Armesto
 */
public class Categoria {
    private int id;
    private String nom;
    private int edatMin,edatMax;

    public Categoria() {
        
    }

    public Categoria(int id, String nom, int edatMin, int edatMax) {
        this.id = id;
        this.nom = nom;
        this.edatMin = edatMin;
        this.edatMax = edatMax;
    }

    public int getEdatMax() {
        return edatMax;
    }

    public int getEdatMin() {
        return edatMin;
    }

    public String getNom() {
        return nom;
    }

    public int getId() {
        return id;
    }
    
    @Override
    public String toString() {
        return nom; // Assuming 'nom' is the field that stores the player's name
    }
    
}
