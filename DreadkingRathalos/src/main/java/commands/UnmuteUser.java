/*
 */
package commands;

import java.util.List;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author Jack L. Clements
 */
public class UnmuteUser extends SubCommand{
    
    public UnmuteUser(String name, String description){
        super(name, description);
    }
    
    @Override
    public void doAction(MessageReceivedEvent e) {
        Message msg = e.getMessage();
        List<User> users = msg.getMentionedUsers();
        for(User user : users){
            MutedListener.unmuteUser(e.getGuild(), user);
        }
    }
}