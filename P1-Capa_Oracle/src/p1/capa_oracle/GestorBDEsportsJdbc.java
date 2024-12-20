package p1.capa_oracle;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import Excepcions.GestorBDProjecteException;
import Interficies.IGestorBDEsports;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import model.*;

/**
 *
 * @author Ferran Sanchís Armesto
 */
public class GestorBDEsportsJdbc implements IGestorBDEsports {
    
    /*
     * Aquest objecte és el que ha de mantenir la connexió amb el SGBD Es crea
     * en el constructor d'aquesta classe i manté la connexió fins que el
     * programador decideix tancar la connexió amb el mètode tancarCapa
     */
    private Connection conn;

    /**
     * Sentències preparades que es crearan només 1 vegada i s'utilitzaran
     * tantes vegades com siguin necessàries. En aquest programa es creen la
     * primera vegada que es necessiten i encara no han estat creades. També es
     * podrien crear al principi del programa, una vegada establerta la
     * connexió.
     */
    private PreparedStatement psDelListProduct;
    private PreparedStatement psUpdateProduct;
    private PreparedStatement psSelectJugador;

    /**
     * Estableix la connexió amb la BD.Les dades que necessita (url, usuari i
     * contrasenya) es llegiran d'un fitxer de configuració anomenat
     * empresaJDBC.xml" que cercarà a l'arrel de l'aplicació i que ha de
     * contenir les següents claus: url, user, password
     *
     * En cas que l'aplicació s'executés en Java anterior a 1.6, caldria també
     * passar el nom de la classe JDBC que permet la connexió amb el SGBDR.
     *
     * @throws Excepcions.GestorBDProjecteException
     */
    public GestorBDEsportsJdbc() throws GestorBDProjecteException {
        String nomFitxer = "empresaJDBC.xml";
        try {
            Properties props = new Properties();
            props.loadFromXML(new FileInputStream(nomFitxer));
            String[] claus = {"url", "user", "password"};
            String[] valors = new String[3];
            for (int i = 0; i < claus.length; i++) {
                valors[i] = props.getProperty(claus[i]);
                if (valors[i] == null || valors[i].isEmpty()) {
                    throw new GestorBDProjecteException("L'arxiu " + nomFitxer + " no troba la clau " + claus[i]);
                }
            }
            conn = DriverManager.getConnection(valors[0], valors[1], valors[2]);
            conn.setAutoCommit(false);
        } catch (IOException ex) {
            throw new GestorBDProjecteException("Problemes en recuperar l'arxiu de configuració " + nomFitxer, ex);
        } catch (SQLException ex) {
            throw new GestorBDProjecteException("No es pot establir la connexió.", ex);
        }
            
        
        /* Aquí es podrien crear tots els preparedStatement que es necessiten a la capa
           i que s'han declarat com a dades de la classe i així estalviaríem preguntar
           a cada mètode on es necessiten, si encara estan a null per procedir a crear-los.
        */
    }

    /**
     * Tanca la connexió
     *
     * @throws org.milaifontanals.empresa.persistencia.GestorBDProjecteException
     */
    @Override
    public void tancarCapa() throws GestorBDProjecteException {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new GestorBDProjecteException("Error en fer rollback final.", ex);
            }
            try {
                conn.close();
            } catch (SQLException ex) {
                throw new GestorBDProjecteException("Error en tancar la connexió.\n", ex);
            }
        }
    }
    
    @Override
    public void confirmarCanvis() throws GestorBDProjecteException {
        try {
            conn.commit();
        } catch (SQLException ex) {
            throw new GestorBDProjecteException("Error en confirmar canvis", ex);
        }
    }

    @Override
    public void desferCanvis() throws GestorBDProjecteException {
        try {
            conn.rollback();
        } catch (SQLException ex) {
            throw new GestorBDProjecteException("Error en desfer canvis", ex);
        }
    }
    
    public List<String> getMemberNamesForTeam(int teamId) throws GestorBDProjecteException {
    List<String> members = new ArrayList<>();
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        // Corrected table and column names based on your schema
        String sql = "SELECT j.NOM, m.TITULAR FROM JUGADOR j " +
                     "JOIN MEMBRES m ON j.IDJUGADOR = m.IDJUGADOR " +
                     "JOIN EQUIP e ON m.IDEQUIP = e.IDEQUIP " +
                     "WHERE e.IDEQUIP = ?";
        ps = conn.prepareStatement(sql);
        ps.setInt(1, teamId);
        rs = ps.executeQuery();

        while (rs.next()) {
            String memberName = rs.getString("NOM");
            int titular = rs.getInt("TITULAR");
            String memberInfo = memberName + (titular == 1 ? " (Titular)" : " (Suplente)");
            members.add(memberInfo);
        }
    } catch (SQLException ex) {
        throw new GestorBDProjecteException("Error retrieving team members: " + ex.getMessage(), ex);
    } finally {
        // Close ResultSet and PreparedStatement in finally block
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                // Handle or log exception
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException ex) {
                // Handle or log exception
            }
        }
    }

    return members;
}

    
    /**
    * Fetches a {@link Jugador} object from the database based given a DNI
    *
    * This method prepares a SQL statement to search for a player in the "Jugadors" table
    * If no player is found, it returns null.
    *
    * @param dni The DNI of the player to search for.
    * @return A {@link Jugador} object populated with the player's data if found; otherwise, null.
    * @throws GestorBDProjecteException If there is an error during the database operation, such as
    *                                   issues with preparing the SQL statement or executing the query.
    */
    @Override
public Jugador cercarJugadorPerDNI(String dni) throws GestorBDProjecteException {
    Jugador jugador = null;
    ResultSet rs = null;

    if (psSelectJugador == null) {
        try {
            psSelectJugador = conn.prepareStatement("SELECT * FROM JUGADOR WHERE IDLEGAL = ?");
        } catch (SQLException ex) {
            throw new GestorBDProjecteException("Error preparing statement psSelectJugador", ex);
        }
    }

    try {
        psSelectJugador.setString(1, dni);
        rs = psSelectJugador.executeQuery();
        
        if (rs.next()) {
            // Populate the Jugador object with data from the ResultSet
            jugador = new Jugador();
            jugador.setId(rs.getInt("IDJUGADOR")); // Make sure these column names are correct
            jugador.setIdLegal(rs.getString("IDLEGAL"));
            jugador.setIban(rs.getString("IBAN"));
            jugador.setNom(rs.getString("NOM"));
            jugador.setCognom(rs.getString("COGNOM"));
            jugador.setAdresa(rs.getString("ADRECA"));
            jugador.setDataNaix(rs.getDate("DATANAIX"));
            jugador.setSexe(rs.getString("SEXE").charAt(0));
            jugador.setRevMedica(rs.getInt("REVMEDICA"));
            jugador.setRutaFoto(rs.getString("FOTO"));
            // jugador.setCate(fetchCategoriaById(rs.getInt("categoriaId")));
        }
    } catch (SQLException ex) {
        throw new GestorBDProjecteException("Error executing query for DNI: " + dni, ex);
    } finally {
        // Close the ResultSet if it was opened
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                throw new GestorBDProjecteException("Error closing ResultSet", ex);
            }
        }
}

return jugador; // Return the jugador or null if not found
}

    /**
     *
     * @param dataNaix
     * @return
     * @throws GestorBDProjecteException
     */
    @Override
public List<Jugador> cercarJugadorPerDataNaix(Date dataNaix) throws GestorBDProjecteException {
    List<Jugador> jugadores = new ArrayList<>();
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        ps = conn.prepareStatement("SELECT * FROM JUGADOR WHERE DATANAIX = ?");

        // Convert java.util.Date to java.sql.Date:
        java.sql.Date sqlDate = new java.sql.Date(dataNaix.getTime());
        ps.setDate(1, sqlDate); // Use the java.sql.Date object

        rs = ps.executeQuery();
        System.out.println("a");
        while (rs.next()) {
            System.out.println("b");
            Jugador jugador = new Jugador();
            jugador = new Jugador();
            jugador.setId(rs.getInt("IDJUGADOR")); // Make sure these column names are correct
            jugador.setIdLegal(rs.getString("IDLEGAL"));
            jugador.setIban(rs.getString("IBAN"));
            jugador.setNom(rs.getString("NOM"));
            jugador.setCognom(rs.getString("COGNOM"));
            jugador.setAdresa(rs.getString("ADRECA"));
            jugador.setDataNaix(rs.getDate("DATANAIX"));
            jugador.setSexe(rs.getString("SEXE").charAt(0));
            jugador.setRevMedica(rs.getInt("REVMEDICA"));
            jugador.setRutaFoto(rs.getString("FOTO"));

            jugadores.add(jugador);
        }
    } catch (SQLException ex) {
        throw new GestorBDProjecteException("Error executing query for date of birth: " + dataNaix, ex);
    } finally {
        // ... close ResultSet and PreparedStatement in finally block
    }

    return jugadores;
}

    @Override
    public Jugador cercarJugadorPerCategoria(Categoria cate) throws GestorBDProjecteException {
        Jugador jugador = null;
        ResultSet rs = null;
        PreparedStatement psSelectJugadorByCategoria = null;

        try {
            psSelectJugadorByCategoria = conn.prepareStatement("SELECT * FROM Jugadors WHERE categoriaId = ?");
            psSelectJugadorByCategoria.setInt(1, cate.getId());
            rs = psSelectJugadorByCategoria.executeQuery();

            if (rs.next()) {
                jugador = new Jugador();
                jugador.setId(rs.getInt("id"));
                jugador.setIdLegal(rs.getString("idLegal"));
                jugador.setIban(rs.getString("iban"));
                jugador.setNom(rs.getString("nom"));
                jugador.setCognom(rs.getString("cognom"));
                jugador.setAdresa(rs.getString("adresa"));
                jugador.setDataNaix(rs.getDate("dataNaix"));
                jugador.setSexe(rs.getString("sexe").charAt(0));
                jugador.setRevMedica(rs.getInt("revMedica"));
                jugador.setRutaFoto(rs.getString("rutaFoto"));
                jugador.setCate(cate);
            }
        } catch (SQLException ex) {
            throw new GestorBDProjecteException("Error executing query for Categoria: " + cate.getId(), ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    throw new GestorBDProjecteException("Error closing ResultSet", ex);
                }
            }
        }
        return jugador;
    }
    
    @Override
    public Equip cercarEquipPerID(int id) throws GestorBDProjecteException {
        Equip equip = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement("SELECT * FROM EQUIP WHERE IDEQUIP = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                equip = new Equip();
                equip.setIdEquip(rs.getInt("IDEQUIP"));
                equip.setNom(rs.getString("NOM"));
                equip.setSexe(rs.getString("SEXE").charAt(0));
                equip.setIdCategoria(rs.getInt("IDCATEGORIA"));
                equip.setTemporada(rs.getInt("TEMPORADA"));
            }
        } catch (SQLException ex) {
            throw new GestorBDProjecteException("Error searching for team by ID: " + ex.getMessage(), ex);
        } finally {
            // ... close ResultSet and PreparedStatement in finally block ...
        }

        return equip;
    }

    @Override
    public Equip cercarEquipPerNom(String nom) throws GestorBDProjecteException {
        Equip equip = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement("SELECT * FROM EQUIP WHERE NOM = ?");
            ps.setString(1, nom);
            rs = ps.executeQuery();

            if (rs.next()) {
                equip = new Equip();
                equip.setIdEquip(rs.getInt("IDEQUIP"));
                equip.setNom(rs.getString("NOM"));
                equip.setSexe(rs.getString("SEXE").charAt(0));
                equip.setIdCategoria(rs.getInt("IDCATEGORIA"));
                equip.setTemporada(rs.getInt("TEMPORADA"));
            }
        } catch (SQLException ex) {
            throw new GestorBDProjecteException("Error searching for team by name: " + ex.getMessage(), ex);
        } finally {
            // ... close ResultSet and PreparedStatement in finally block ...
        }

        return equip;
    }


    
    
    public List<Integer> obtenirLlistaTemporades() throws GestorBDProjecteException {
    List<Integer> temporades = new ArrayList<>();
    String sql = "SELECT TEMPORADA FROM Temporada";
    
    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            temporades.add(rs.getInt("TEMPORADA"));
        }
    } catch (SQLException ex) {
        throw new GestorBDProjecteException("Error retrieving seasons: " + ex.getMessage(), ex);
    }
    
    return temporades;
}
    
    public List<Categoria> obtenirLlistaCategories() throws GestorBDProjecteException {
    List<Categoria> categories = new ArrayList<>();
    String sql = "SELECT IDCATEGORIA, nom, edatMin, edatMax FROM Categoria"; // Correct column name here

    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            int id = rs.getInt("IDCATEGORIA"); // And here
            String nom = rs.getString("nom");
            int edatMin = rs.getInt("edatMin");
            int edatMax = rs.getInt("edatMax");
            categories.add(new Categoria(id, nom, edatMin, edatMax));
        }
    } catch (SQLException ex) {
        throw new GestorBDProjecteException("Error retrieving categories: " + ex.getMessage(), ex);
    }

    return categories;
}
    
    public List<Jugador> getAllPlayers() throws GestorBDProjecteException {
    List<Jugador> players = new ArrayList<>();
    String sql = "SELECT * FROM Jugador";
    
    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            Integer id = rs.getInt("IDJUGADOR");
            String idLegal = rs.getString("IDLEGAL");
            String iban = rs.getString("IBAN");
            String nom = rs.getString("NOM");
            String cognom = rs.getString("COGNOM");
            String adresa = rs.getString("ADRECA");
            Date dataNaix = rs.getDate("DATANAIX");
            char sexe = rs.getString("SEXE").charAt(0);
            int revMedica = rs.getInt("REVMEDICA");
            String rutaFoto = rs.getString("FOTO");
            
            Jugador jugador = new Jugador(id, idLegal, iban, nom, cognom, adresa, dataNaix, sexe, revMedica, rutaFoto);
            players.add(jugador);
        }
    } catch (SQLException ex) {
        throw new GestorBDProjecteException("Error retrieving all players: " + ex.getMessage(), ex);
    }
    
    return players;
}



    
    
public void afegirJugador(Jugador jugador) throws GestorBDProjecteException {
    String sql = "INSERT INTO JUGADOR (IDLEGAL, NOM, COGNOM, DATANAIX, SEXE, ADRECA, REVMEDICA, IBAN, FOTO) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"; // No CATEGORIAID
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, jugador.getIdLegal());
        ps.setString(2, jugador.getNom());
        ps.setString(3, jugador.getCognom());
        ps.setDate(4, new java.sql.Date(jugador.getDataNaix().getTime()));
        ps.setString(5, String.valueOf(jugador.getSexe()));
        ps.setString(6, jugador.getAdresa());
        ps.setInt(7, jugador.getRevMedica());
        ps.setString(8, jugador.getIban());
        ps.setString(9, jugador.getRutaFoto());
        // No need to set CATEGORIAID
        ps.executeUpdate();
    } catch (SQLException ex) {
        throw new GestorBDProjecteException("Error en afegir jugador: " + ex.getMessage(), ex);
    }
}


    public void eliminarJugador(String idLegal) throws GestorBDProjecteException {
        String sql = "DELETE FROM JUGADOR WHERE IDLEGAL = ?"; // Use IDLEGAL in WHERE clause
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idLegal);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new GestorBDProjecteException("No s'ha trobat cap jugador amb l'IDLEGAL: " + idLegal);
            }
        } catch (SQLException ex) {
            throw new GestorBDProjecteException("Error en eliminar el jugador amb IDLEGAL: " + idLegal, ex);
        }
    }


    public void actualitzarJugador(Jugador jugador) throws GestorBDProjecteException {
    String sql = "UPDATE JUGADOR SET IDLEGAL = ?, NOM = ?, COGNOM = ?, DATANAIX = ?, SEXE = ?, ADRECA = ?, REVMEDICA = ?, IBAN = ?, FOTO = ? WHERE IDLEGAL = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, jugador.getIdLegal());
        ps.setString(2, jugador.getNom());
        ps.setString(3, jugador.getCognom());
        ps.setDate(4, new java.sql.Date(jugador.getDataNaix().getTime()));
        ps.setString(5, String.valueOf(jugador.getSexe()));
        ps.setString(6, jugador.getAdresa());
        ps.setInt(7, jugador.getRevMedica());
        ps.setString(8, jugador.getIban());
        ps.setString(9, jugador.getRutaFoto());
        ps.setString(10, jugador.getIdLegal()); // Use IDLEGAL in WHERE clause to identify the player
        int rowsAffected = ps.executeUpdate();
        if (rowsAffected == 0) {
            throw new GestorBDProjecteException("No s'ha trobat cap jugador amb l'IDLEGAL: " + jugador.getIdLegal());
        }
    } catch (SQLException ex) {
        throw new GestorBDProjecteException("Error en actualitzar jugador: " + ex.getMessage(), ex);
    }
}

@Override
public List<JugadorEquip> cercarMembres() throws GestorBDProjecteException {
    List<JugadorEquip> membres = new ArrayList<>();
    ResultSet rs = null;
    PreparedStatement psSelectMembres = null;

    try {
        psSelectMembres = conn.prepareStatement(
            "SELECT je.*, j.* FROM JugadorsEquips je " +
            "JOIN Jugadors j ON je.jugadorId = j.id"
        );
        rs = psSelectMembres.executeQuery();

        while (rs.next()) {
            Jugador jugador = new Jugador();
            jugador.setId(rs.getInt("id"));
            jugador.setIdLegal(rs.getString("idLegal"));
            jugador.setIban(rs.getString("iban"));
            jugador.setNom(rs.getString("nom"));
            jugador.setCognom(rs.getString("cognom"));
            jugador.setAdresa(rs.getString("adresa"));
            jugador.setDataNaix(rs.getDate("dataNaix"));
            jugador.setSexe(rs.getString("sexe").charAt(0));
            jugador.setRevMedica(rs.getInt("revMedica"));
            jugador.setRutaFoto(rs.getString("rutaFoto"));

            JugadorEquip membre = new JugadorEquip();
            membre.setJugador(jugador);
            membre.setEsTitular(rs.getBoolean("esTitular"));
            membres.add(membre);
        }
    } catch (SQLException ex) {
        throw new GestorBDProjecteException("Error executing query for cercarMembres", ex);
    } finally {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                throw new GestorBDProjecteException("Error closing ResultSet", ex);
            }
        }
    }
    return membres;
}

public User checkUserCredentials(String username, String password) throws GestorBDProjecteException {
    User user = null;
    PreparedStatement psSelectUser = null;
    ResultSet rs = null;
    
    System.out.println("USER: " + username + "|||| PASS : " + password);

    try {
        psSelectUser = conn.prepareStatement("SELECT * FROM USUARI WHERE LOGIN = ? AND PASSWORD = UTL_RAW.CAST_TO_RAW(?)");
            psSelectUser.setString(1, username);
            psSelectUser.setString(2, password);

            rs = psSelectUser.executeQuery();

            if (rs.next()) {
                user = new User(rs.getString("LOGIN"), rs.getString("NOM"));
            }
    } catch (SQLException ex) {
        throw new GestorBDProjecteException("Error checking user credentials: " + ex.getMessage(), ex);
    } finally {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                throw new GestorBDProjecteException("Error closing ResultSet", ex);
            }
        }
        if (psSelectUser != null) {
            try {
                psSelectUser.close();
            } catch (SQLException ex) {
                throw new GestorBDProjecteException("Error closing PreparedStatement", ex);
            }
        }
    }
    return user;
}

public List<String> getTeamsBySeason(int season) throws GestorBDProjecteException {
    List<String> teamNames = new ArrayList<>();
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        ps = conn.prepareStatement("SELECT NOM FROM EQUIP WHERE TEMPORADA = ?");
        ps.setInt(1, season);
        rs = ps.executeQuery();

        while (rs.next()) {
            String teamName = rs.getString("NOM");
            teamNames.add(teamName);
        }
    } catch (SQLException ex) {
        throw new GestorBDProjecteException("Error getting teams by season: " + ex.getMessage(), ex);
    } finally {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                // Handle or log exception
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException ex) {
                // Handle or log exception
            }
        }
    }

    return teamNames;
}


public List<Equip> obtenirEquipsPerTemporada(int temporada) throws GestorBDProjecteException {
        List<Equip> equips = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = conn.prepareStatement("SELECT * FROM EQUIP WHERE TEMPORADA = ?");
            ps.setInt(1, temporada);
            rs = ps.executeQuery();

            while (rs.next()) {
                Equip equip = new Equip(rs.getInt("IDEQUIP"), rs.getString("NOM"), rs.getString("SEXE").charAt(0), rs.getInt("IDCATEGORIA"), rs.getInt("TEMPORADA"));
                equips.add(equip);
            }
        } catch (SQLException ex) {
            throw new GestorBDProjecteException("Error getting teams by season: " + ex.getMessage(), ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }

        return equips;
    }

public void addTeam(Equip newTeam) throws GestorBDProjecteException {
    PreparedStatement ps = null;

    try {
        ps = conn.prepareStatement("INSERT INTO EQUIP (NOM, SEXE, IDCATEGORIA, TEMPORADA) VALUES (?, ?, ?, ?)");
        ps.setString(1, newTeam.getNom());
        ps.setString(2, String.valueOf(newTeam.getSexe()));
        ps.setInt(3, newTeam.getIdCategoria());
        ps.setInt(4, newTeam.getTemporada());
        ps.executeUpdate();

    } catch (SQLException ex) {
        throw new GestorBDProjecteException("Error adding team: " + ex.getMessage(), ex);
    } finally {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}

public void deleteTeam(Equip teamToDelete) throws GestorBDProjecteException {
    PreparedStatement ps = null;

    try {
        ps = conn.prepareStatement("DELETE FROM EQUIP WHERE IDEQUIP = ?");
        ps.setInt(1, teamToDelete.getIdEquip());
        ps.executeUpdate();

    } catch (SQLException ex) {
        throw new GestorBDProjecteException("Error deleting team: " + ex.getMessage(), ex);
    } finally {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}

 public void modifyTeam(Equip updatedTeam) throws GestorBDProjecteException {
    PreparedStatement ps = null;

    try {
        ps = conn.prepareStatement("UPDATE EQUIP SET NOM = ?, SEXE = ?, IDCATEGORIA = ?, TEMPORADA = ? WHERE IDEQUIP = ?");
        ps.setString(1, updatedTeam.getNom());
        ps.setString(2, String.valueOf(updatedTeam.getSexe()));
        ps.setInt(3, updatedTeam.getIdCategoria());
        ps.setInt(4, updatedTeam.getTemporada());
        ps.setInt(5, updatedTeam.getIdEquip());
        ps.executeUpdate();

    } catch (SQLException ex) {
        throw new GestorBDProjecteException("Error updating team: " + ex.getMessage(), ex);
    } finally {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
 
 public void addMemberToTeam(int playerId, int teamId, int titular) throws GestorBDProjecteException {
    PreparedStatement ps = null;

    try {
        ps = conn.prepareStatement("INSERT INTO MEMBRES (IDJUGADOR, IDEQUIP, TITULAR) VALUES (?, ?, ?)");
        ps.setInt(1, playerId);
        ps.setInt(2, teamId);
        ps.setInt(3, titular); // Set titular status (1 or 0)
        ps.executeUpdate();

    } catch (SQLException ex) {
        throw new GestorBDProjecteException("Error adding member to team: " + ex.getMessage(), ex);
    } finally {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
 }

public void removeMemberFromTeam(int playerId, int teamId) throws GestorBDProjecteException {
    PreparedStatement ps = null;

    try {
        ps = conn.prepareStatement("DELETE FROM MEMBRES WHERE IDJUGADOR = ? AND IDEQUIP = ?");
        ps.setInt(1, playerId);
        ps.setInt(2, teamId);
        ps.executeUpdate();

    } catch (SQLException ex) {
        throw new GestorBDProjecteException("Error removing member from team: " + ex.getMessage(), ex);
    } finally {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException ex) {
                // Handle or log exception
            }
        }
    }
}



 
}
