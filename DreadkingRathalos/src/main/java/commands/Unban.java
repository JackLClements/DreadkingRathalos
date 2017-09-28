/*
 */
package commands;

import java.util.List;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author Jack L. Clements
 */
public class Unban extends SubCommand{
    
    public Unban(String name, String description){
        super(name, description);
    }
    
    @Override
    public void doAction(MessageReceivedEvent e) {
        if (e.getGuild().getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
            Guild guild = e.getGuild();
            Message msg = e.getMessage();
            List<User> users = msg.getMentionedUsers();
            for (User user : users) {
                //guild.getController().kick(member).queue(); //not needed?
                guild.getController().unban(user).queue();
            }
        }
        else{
            e.getChannel().sendMessage("Cannot kick members. Admin, please modify my permissions.").queue();
        }
    }
    
}
