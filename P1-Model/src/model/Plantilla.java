package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * @author Ferran Sanchis Armesto
 */

//Inicialitzar sempre en AL
public class Plantilla {
    ArrayList<JugadorEquip> je;
    Equip equipPlantilla;

    public Plantilla(Plantilla p) {
        this.je = p.getJe();
        this.equipPlantilla = p.getEquipPlantilla();
    }

    public ArrayList<JugadorEquip> getJe() {
        return je;
    }

    public Equip getEquipPlantilla() {
        return equipPlantilla;
    }
    
    public void afegirJugador(Jugador j,boolean titular){
        // Exemple: equipIgualada.afegirJugador((Object)Jugador jugador, true);
        JugadorEquip jeq = new JugadorEquip(j,titular);
        this.je.add(jeq);
    }
    
    /*
    * En aquesta funcio buscarem un jugador passat per parametre i el traurem
    de la BBDD. Per a fer el mateix nomes amb el seu DNI, hi ha una altra funcio
    */
    
    public boolean treureJugador(Jugador j){
        
        // Testing
        boolean existeix = false;
        JugadorEquip aux = null; 
        Iterator ite = je.iterator();
        while(!(je.isEmpty()) && ite.hasNext() && existeix == false){
            aux = (JugadorEquip)ite.next();
            if(aux.getJugador() == j){
                existeix = true;
            }
        }
        if(existeix){
            je.remove(aux);
        }else{
            System.out.println("Player not found");
        }
        return existeix;
    }
    
    public boolean treureJugador(String nom){
        // Es pot fer per nom o per Jugador inicialitzat directament
        boolean existeix = false;
        JugadorEquip aux = null; 
        Iterator ite = je.iterator();
        while(!(je.isEmpty()) && ite.hasNext() && existeix == false){
            aux = (JugadorEquip)ite.next();
            if(aux.getJugador().getNom().equals(nom)){
                existeix = true;
            }
        }
        if(existeix){
            je.remove(aux);
        }else{
            System.out.println("Player not found");
        }
        return existeix;
    }
    
    
    public Jugador cercaDNI(String dni){
        Jugador jugador = null;
       
        JugadorEquip aux = null; 
        Iterator ite = je.iterator();
        while(!(je.isEmpty()) && ite.hasNext() && jugador != null){
            aux = (JugadorEquip)ite.next();
            if(aux.getJugador().getIdLegal().equals(dni)){
                jugador = aux.getJugador();
            }
        }
        if(jugador != null){
            return jugador;
        }else{
            System.out.println("Player not found");
        }
        
        return jugador;
    }
    public Jugador cercaDataNaix(Date data){
        Jugador jugador = null;
       
        JugadorEquip aux = null; 
        Iterator ite = je.iterator();
        while(!(je.isEmpty()) && ite.hasNext() && jugador != null){
            aux = (JugadorEquip)ite.next();
            if(aux.getJugador().getDataNaix().equals(data)){
                jugador = aux.getJugador();
            }
        }
        if(jugador != null){
            return jugador;
        }else{
            System.out.println("Player not found");
        }
        
        return jugador;
    }
    
    
    public Jugador cercaCategoria(Categoria categoria){
        Jugador jugador = null;
       
        JugadorEquip aux = null; 
        Iterator ite = je.iterator();
        while(!(je.isEmpty()) && ite.hasNext() && jugador != null){
            aux = (JugadorEquip)ite.next();
            if(aux.getJugador().getCate().equals(categoria)){
                jugador = aux.getJugador();
            }
        }
        if(jugador != null){
            return jugador;
        }else{
            System.out.println("Player not found");
        }
        
        return jugador;
    }
}