/*
 */
package commands;

import java.util.List;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author Jack L. Clements
 */
public class KickBan extends SubCommand {

    public KickBan(String name, String description) {
        super(name, description);
    }

    @Override
    public void doAction(MessageReceivedEvent e) {
        Member selfMember = e.getGuild().getSelfMember();
        if (e.getGuild().getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
            if (selfMember.hasPermission(Permission.BAN_MEMBERS)) {
                Guild guild = e.getGuild();
                Message msg = e.getMessage();
                List<User> users = msg.getMentionedUsers();
                for (User user : users) {
                    Member member = guild.getMember(user);
                    if (selfMember.canInteract(member) && !user.isBot()) {
                        guild.getController().ban(user, 0, "With a mighty roar, Dreadking Rathalos carts you away from " + guild.getName() + ". Don't come back.").queue();
                    } else {
                        e.getTextChannel().sendMessage("Cannot ban this user. Take it up with the admin.").queue();
                    }

                }
            }
            else{
                e.getTextChannel().sendMessage("Cannot ban this user as you lack the relevant permissions. Take it up with the admin.").queue();
            }

        } else {
            e.getChannel().sendMessage("Cannot kick members. Admin, please modify my permissions.").queue();
        }

    }

}
