/*
 */
package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author Jack L. Clements
 */
public abstract class SubCommand {
    private String name;
    private String description;
    
    public SubCommand(){
        name = "";
        description = "";
    }
    
    public SubCommand(String name, String description){
        this.name = name;
        this.description = description;
    }
    
    public abstract void doAction(MessageReceivedEvent e);
    
    
    public String getName(){
        return this.name;
    }
    
    public String getDescription(){
        return this.description;
    }
}
