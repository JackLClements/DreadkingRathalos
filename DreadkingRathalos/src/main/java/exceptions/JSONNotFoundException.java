/*
 */
package exceptions;

/**
 *
 * @author Jack L. Clements
 */
public class JSONNotFoundException extends Exception{
    
    public JSONNotFoundException(String s){
        super(s);
    }
    
    /**
     *
     */
    @Override
    public void printStackTrace(){
        
    }
    
}
