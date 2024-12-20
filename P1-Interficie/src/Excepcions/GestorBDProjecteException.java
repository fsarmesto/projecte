package Excepcions;

/**
 *
 * @author Ferran Sanchis Armesto
 */
public class GestorBDProjecteException extends Exception {

    public GestorBDProjecteException (String message)    
    {      
        super(message);    
    } 
    public GestorBDProjecteException(String message, Throwable cause) {
        super(message, cause);
    }
    
    
}
