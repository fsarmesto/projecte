package Tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import model.Equip;

/**
 *
 * @author Classe
 */
public class ProvaSelectEquip {
    public static void main(String[] args) {
        
        Connection con = null;
        
        try{
        con = DriverManager.getConnection("jdbc:oracle:thin:@//10.2.16.239:1521/XEPDB1", "Projecte", "proj");
        System.out.println("Connexio creada!");
        String sql = "SELECT * from EQUIP";
        ResultSet rs = null;
        Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
        
        rs = stmt.executeQuery(sql);
        rs.first();
        
        while(!(rs.isLast())){
            Equip e = new Equip(rs.getInt("idequip"),rs.getString("nom"),
            rs.getString("sexe").charAt(0),rs.getInt("idcategoria"),rs.getInt("temporada"));
            
            System.out.println(e);
            System.out.println("\n------------------------\n");
            rs.next();
        }
        
        }catch(SQLException ex){
            System.out.println("No s'ha pogut iniciar la connexio: " + ex.getMessage());
        }
        
        
    } 
}
