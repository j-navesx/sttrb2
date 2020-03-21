/**
 * Sistemas de Telecomunicacoes 
 *          2018/2019
 *
 * ChallengeResponse.java
 *
 * Class that stores and manages the challenge-response information
 *
 * Created on February 20, 2019, 10:00
 *
 * @author Luis Bernardo
 */
package server;

import java.util.Date;

/**
 * Manage challenge-response information
 */
public class ChallengeResponse {
    /**
     * User name
     */
    private final String user;
    /**
     * Challenge: positions of the password requested
     */
    private final int[] pos;
    /**
     * Answer to the challenge
     */
    private final String key;
    /**
     * Creation date
     */
    private final Date creation_date;
    /**
     * Lifetime of the challenge where it may be used
     */
    private final long Lifetime;

    /**
     * Constructor - stores the object fields and the creation date
     * @param user
     * @param pos
     * @param key
     * @param validity 
     */
    public ChallengeResponse(String user, int[] pos, String key, long validity) {
        this.user= user;
        this.pos= pos;
        this.key= key;
        this.creation_date= new Date();
        this.Lifetime= validity;
    }

    /**
     * Return the user name
     * @return the user name
     */
    public String get_user() {
        return user;
    }
    /**
     * Returns the character positions in the password to be returned by the user
     * @return the array with the positions
     */
    public int[] get_challenge() {
        return pos;
    }

    /**
     * Checks if the entry is is_valid (within its lifetime)
     * @return true if is_valid, false otherwise
     */
    public boolean is_valid() {
        return ((creation_date.getTime()+Lifetime)>System.currentTimeMillis());
    }
    
    /**
     * Check if a valid challenge-response is stored
     * @return true if has challenge-response
     */
    public boolean has_challenge() {
        return is_valid() && (key!=null) && (key.length()==3);
    }
    
    /**
     * Validates the string sent by the browser
     * @param str   string with client string
     * @return true if is_valid, else otherwise
     */
    public boolean validate_client_string(String str) {
        // user=user&P1=1&K1=a&P2=2&K2=b&P3=3&K3=d&Login=Login
        String check_key= toString();
        
        if (str != null)
            return str.startsWith(check_key);   // Ignore the last part ("&Login=Login")
        else {
            System.err.println("Error is software: null pointer in ChallengeResponse.validate_client_string");
            return false;
        }
    }
    
    /**
     * Converts the contents to string
     * @return string with the challenge-response information
     */
    @Override
    public String toString() {
        if ((pos!=null) && (key!=null) && (key.length()==3))
            return "user="+user+"&P1="+pos[0]+"&K1="+key.charAt(0)+
                    "&P2="+pos[1]+"&K2="+key.charAt(1)+
                    "&P3="+pos[2]+"&K3="+key.charAt(2);
        else
            return "user="+user+"&P1=&K1=&P2=&K2=&P3=&K3=";
    }
  
}
