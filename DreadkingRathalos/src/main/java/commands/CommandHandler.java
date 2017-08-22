/*
 */
package commands;

import java.util.TreeMap;

/**
 * Contains treemap of commands - balanced red/black tree
 * @author Jack L. Clements
 */
public class CommandHandler extends Command { //wrap this as a command for the listener then parse individual instances
    
    private TreeMap<String, Command> commands;
    
    public CommandHandler(){
        super(".handler");
        commands = new TreeMap<>();
    }
    
    public void addCommand(String commandKey, Command command){
        commands.put(commandKey, command);
        commands.get("");//lookup
    }
    
}
