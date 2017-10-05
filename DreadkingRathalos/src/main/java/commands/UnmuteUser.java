/*
 */
package commands;

import java.util.List;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author Jack L. Clements
 */
public class UnmuteUser extends SubCommand {

    public UnmuteUser(String name, String description) {
        super(name, description);
    }

    @Override
    public void doAction(MessageReceivedEvent e) {
        Message msg = e.getMessage();
        List<User> users = msg.getMentionedUsers();
        if (e.getGuild().getSelfMember().hasPermission(Permission.BAN_MEMBERS) && e.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            for (User user : users) {
                MutedListener.unmuteUser(e.getGuild(), user);
            }
        } else {
            e.getChannel().sendMessage("Cannot umute members. Admin, please modify my permissions.").queue();
        }
    }
}
