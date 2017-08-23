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
    private URL thumbnail;
    
    public Stage(){
        name = "undefined";
        thumbnail = null;
    }
    
    public Stage(String name, URL thumbnail){
        this.name = name;
        this.thumbnail = thumbnail;
    }
    
    public String getName(){
        return this.name;
    }
    
    public URL getThumb(){
        return this.thumbnail;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    public void setThumb(URL thumbnail){
        this.thumbnail = thumbnail;
    }
    
}
