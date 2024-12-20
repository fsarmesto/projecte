package Tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.Scanner;
import model.Equip;

/**
 *
 * @author Classe
 */
public class ProvaInsertEquip {
    public static void main(String[] args) {
        
        Connection con = null;
        
        try{
        con = DriverManager.getConnection("jdbc:oracle:thin:@//10.2.16.239:1521/XEPDB1", "Projecte", "proj");
        System.out.println("Connexio creada!");
        con.setAutoCommit(false);
        Scanner sc = new Scanner(System.in);
        System.out.println("Indica les seguents dades:");
        System.out.println("Nom Equip: ");
        String nom = sc.nextLine();
        System.out.println("ID Equip: ");
        int idEquip = sc.nextInt();
        char s = '.';
        System.out.println("Sexe de l'equip (h/d):");
        do{
            s = sc.next().charAt(0);
        }while(s != 'h' && s != 'd');
        System.out.println("ID Categoria: ");
        int idCategoria = sc.nextInt();
        System.out.println("Temporada: ");
        int temporada = sc.nextInt();
        
        Equip eq = new Equip(idEquip,nom,s,idCategoria,temporada);
        
        ResultSet rs = null;
        
        PreparedStatement ps = con.prepareStatement("select * from equip where idequip = ? and nom = ?"); 
        ps.setInt(1, eq.getIdEquip());
        ps.setString(2, eq.getNom());
        rs = ps.executeQuery();
            System.out.println("A");
        
        if (!rs.isBeforeFirst()){   
            
            ps = con.prepareStatement("insert into equip values (?,?,?,?,?)");
            ps.setInt(1, eq.getIdEquip());
            ps.setString(2, eq.getNom());
            ps.setInt(3, eq.getSexe());
            ps.setInt(4, eq.getIdCategoria());
            ps.setInt(5, eq.getTemporada());
            
            ps.executeUpdate();
            
            con.rollback();
            
            con.close();
            
        } else{
            System.out.println("L'equip ja existeix");
        }
        
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        
        
    } 
}

