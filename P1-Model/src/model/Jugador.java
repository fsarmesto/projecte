package model;

import java.util.Date;

/**
 *
 * @author Classe
 */
public class Jugador implements Comparable {
    private Integer id;
    private String idLegal,iban;
    private String nom,cognom,adresa;
    private Date dataNaix;
    private char sexe;
    private int revMedica;
    private String rutaFoto;
    private Categoria cate;
    
     public Jugador(String idLegal, String iban, String nom, String cognom, String adreca, Date dataNaix, char sexe, int revMedica, String rutaFoto) {
        this.idLegal = idLegal;
        this.iban = iban;
        this.nom = nom;
        this.cognom = cognom;
        this.adresa = adreca;
        this.dataNaix = dataNaix;
        this.sexe = sexe;
        this.revMedica = revMedica;
        this.rutaFoto = rutaFoto;
    }

    public Jugador(Integer id, String idLegal, String iban, String nom, String cognom, String adreca, Date dataNaix, char sexe, int revMedica, String rutaFoto) {
        this.id = id;
        this.idLegal = idLegal;
        this.iban = iban;
        this.nom = nom;
        this.cognom = cognom;
        this.adresa = adreca;
        this.dataNaix = dataNaix;
        this.sexe = sexe;
        this.revMedica = revMedica;
        this.rutaFoto = rutaFoto;
    }
    
    

    public Jugador() {
    }

    public Categoria getCate() {
        return cate;
    }

    public void setCate(Categoria cate) {
        this.cate = cate;
    }

    
    public String getAdresa() {
        return adresa;
    }

    public String getCognom() {
        return cognom;
    }

    public Date getDataNaix() {
        return dataNaix;
    }

    public String getIban() {
        return iban;
    }

    public Integer getId() {
        return id;
    }

    public String getIdLegal() {
        return idLegal;
    }

    public String getNom() {
        return nom;
    }

    public int getRevMedica() {
        return revMedica;
    }

    public String getRutaFoto() {
        return rutaFoto;
    }

    public char getSexe() {
        return sexe;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public void setCognom(String cognom) {
        this.cognom = cognom;
    }

    public void setDataNaix(Date dataNaix) {
        this.dataNaix = dataNaix;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setIdLegal(String idLegal) {
        this.idLegal = idLegal;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setRevMedica(int revMedica) {
        this.revMedica = revMedica;
    }

    public void setRutaFoto(String rutaFoto) {
        this.rutaFoto = rutaFoto;
    }

    public void setSexe(char sexe) {
        this.sexe = sexe;
    }

    @Override
    public int compareTo(Object o) {
        Jugador j = (Jugador)o;
        return Integer.compare(this.id, j.getId());
    }
    @Override
    public String toString() {
        return nom;
    }
}
