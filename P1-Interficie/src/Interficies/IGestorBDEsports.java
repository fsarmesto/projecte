package Interficies;

import Excepcions.GestorBDProjecteException;
import java.util.Date;
import java.util.List;
import model.*;

/**
 *
 * @author Classe
 */
public interface IGestorBDEsports {
    public Jugador cercarJugadorPerDNI(String dni) throws GestorBDProjecteException;
    public List<Jugador> cercarJugadorPerDataNaix(Date dataNaix) throws GestorBDProjecteException;
    public Jugador cercarJugadorPerCategoria(Categoria cate) throws GestorBDProjecteException;
    public Equip cercarEquipPerID(int id) throws GestorBDProjecteException;
    public Equip cercarEquipPerNom(String nom) throws GestorBDProjecteException;
    public List<JugadorEquip> cercarMembres() throws GestorBDProjecteException;
    
    /**
     * Confirma els canvis efectuats a la BD
     *
     * @throws GestorBDProjecteException
     */
    void confirmarCanvis() throws GestorBDProjecteException;

    /**
     * Eliminar les transaccions no confirmades
     *
     * @throws GestorBDProjecteException
     */
    void desferCanvis() throws GestorBDProjecteException;
    
    /**
     * Tanca la connexió
     *
     * @throws GestorBDProjecteException
     */
    void tancarCapa() throws GestorBDProjecteException;
}