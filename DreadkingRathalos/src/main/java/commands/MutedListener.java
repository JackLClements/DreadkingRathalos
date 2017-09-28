/*
 */
package commands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 *
 * @author Jack L. Clements
 */
public class MutedListener extends ListenerAdapter {

    private static HashMap<Guild, HashSet<User>> muted = new HashMap<>();

    public MutedListener() {
        
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) { //note clean this up later - it's messy
        try { //clean this up
            HashSet<User> users = muted.get(e.getGuild());
            if (users.contains(e.getMessage().getAuthor()) && e.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                e.getMessage().delete().queue();
            }
            if (!e.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                e.getChannel().sendMessage("I do not have the ability to mute users. Admin, please turn on \"Manage Messages\" in my permission settings.").queue();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void muteUser(Guild guild, User user) {
        System.out.println(guild.toString() + " " + user.toString());
        if (muted.containsKey(guild)) {
            HashSet<User> guildMuted = muted.get(guild);
            guildMuted.add(user);
        } else {
            HashSet<User> guildMuted = new HashSet<>();
            guildMuted.add(user);
            muted.put(guild, guildMuted);
        }
    }

    public static void unmuteUser(Guild guild, User user) {
        if (muted.containsKey(guild)) {
            HashSet<User> guildMuted = muted.get(guild);
            guildMuted.remove(user);
        }
    }

    public static void addGuilds(List<Guild> guilds) {
        for(Guild guild : guilds){
            muted.put(guild, new HashSet<>());
        }
    }
}
