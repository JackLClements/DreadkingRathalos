/*
 */
package SplatNet2;

import java.net.URL;

/**
 *
 * @author Jack L. Clements
 */
public class Stage {
    private String name;
    private String mode;
    private URL thumbnail;
    
            
    public Stage(){
        this.name = "undefined";
        this.mode = "turf war";
        this.thumbnail = null;
    }
    
    public Stage(String name, URL thumbnail){
        this.name = name;
        this.mode = "turf war";
        this.thumbnail = thumbnail;
    }
    
    public Stage(String name, String mode, URL thumbnail){
        this.name = name;
        this.thumbnail = thumbnail;
        this.mode = mode;
    }
    
    public String getName(){
        return this.name;
    }
    
    public String getMode(){
        return this.mode;
    }
    
    public URL getThumb(){
        return this.thumbnail;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setMode(String mode){
        this.mode = mode;
    }
    
    public void setThumb(URL thumbnail){
        this.thumbnail = thumbnail;
    }
    
}
