/*
 */
package commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 * 
 * @author Jack L. Clements
 */
public abstract class Command extends ListenerAdapter { //note - listeneradapter is identical to EventListener but includes predefined list of funcs.
   //note ought to have common stuff that all commands need
    protected String commandKey;
    
    public Command(String commandKey){
        this.commandKey = commandKey;
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        System.out.println("Message recieved! - " + commandKey);
        onCommand(e);
    }
    
    public void onCommand(MessageReceivedEvent e){
        if(e.getMessage().getContent().contains(commandKey)){
            System.out.println("Triggered!");
        }
    }
    
    public String getKey(){
        return this.commandKey;
    }
    
    
}
