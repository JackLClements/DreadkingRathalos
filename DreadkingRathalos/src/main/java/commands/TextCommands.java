/*
 */
package commands;

import java.util.TreeMap;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author Jack L. Clements
 */
public class TextCommands extends Command{
        
    private TreeMap<String, SubCommand> commands;
    
    
    public TextCommands(String commandKey) {
        super(commandKey);
        commands = new TreeMap<String, SubCommand>();
    }
    /**
     * NOTE - THE REASON JDA HAS SO MANY LISTENERS MAY BE A THREADING ISSUE
     * LOOK INTO SPINNING UP A NEW THREAD WITH EACH EXECUTION JUST IN CASE
     * SEE DOCUMENTATION
     * @param e 
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent e){
        if(!e.getAuthor().isBot()){
            String content = e.getMessage().getStrippedContent().trim().toLowerCase(); //normalise
            String command = content.split(" ")[0]; //takes first command that matches regex (maybe?) test
            if(commands.get(command) != null){
                System.out.println(command);
                commands.get(command).doAction(e);
            }
        }
    }
    
    public void add(String key, SubCommand command){
        commands.put(key, command);
    }
    
    /*
    public static void main(String[] args) {
        String content = ".TEST COMMAND testing 1 2 3 4 5";
        String command = content.split(" ")[0].trim().toLowerCase(); //takes first command that matches regex (maybe?) test
        System.out.println(command);
    }*/
    
}
