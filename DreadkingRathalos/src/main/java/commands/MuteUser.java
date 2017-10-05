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
public class MuteUser extends SubCommand {

    public MuteUser(String name, String description){
        super(name, description);
    }
    
    
    @Override
    public void doAction(MessageReceivedEvent e) {
        Message msg = e.getMessage();
        List<User> users = msg.getMentionedUsers();
        if (e.getGuild().getSelfMember().hasPermission(Permission.BAN_MEMBERS) && e.getMember().hasPermission(Permission.BAN_MEMBERS)) { //if requesting user AND bot both have permission
            Guild guild = e.getGuild();
            for (User user : users) {
                if(!user.equals(guild.getOwner()) && !user.isBot()){ //let's just say no hierarchy needed for now
                     MutedListener.muteUser(e.getGuild(), user);
                }
                else{
                    e.getTextChannel().sendMessage("Cannot mute this user. Take it up with the admin.").queue();
                }
                
            }
        }
        else{
            e.getChannel().sendMessage("Cannot mute members. Admin, please modify my permissions.").queue();
        }
    }
    
  
    
    
    
}
