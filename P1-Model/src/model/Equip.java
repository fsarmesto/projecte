package model;

/**
 *
 * @author Classe
 */
public class Equip implements Comparable {
    private int idEquip;
    private String nom;
    private char sexe;
    private int idCategoria,temporada;

    public Equip() {
    }

    public Equip(int idEquip, String nom, char sexe, int idCategoria, int temporada) {
        this.setIdEquip(idEquip);
        this.setNom(nom);
        this.setSexe(sexe);
        this.setIdCategoria(idCategoria);
        this.setTemporada(temporada);
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public int getIdEquip() {
        return idEquip;
    }

    public String getNom() {
        return nom;
    }

    public char getSexe() {
        return sexe;
    }

    public int getTemporada() {
        return temporada;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public void setIdEquip(int idEquip) {
        this.idEquip = idEquip;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setSexe(char sexe) {
        this.sexe = sexe;
    }

    public void setTemporada(int temporada) {
        this.temporada = temporada;
    }
    
    public void equipAString(){
        
    }

    @Override
    public int compareTo(Object o) {
        Equip eq = (Equip)o;
        return Integer.compare(this.idEquip, eq.getIdEquip());
    }
    
    @Override
    public String toString() {
        return "Id equip: " + this.getIdEquip() + " \nNom: " + this.getNom() + " \nSexe: " + this.getSexe() + 
                " \nID Categoria: " + this.getIdCategoria() + " \nTemporada: " + this.getTemporada();
    }
    
}
