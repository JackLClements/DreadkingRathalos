/*
 */
package commands;

import java.awt.Color;
import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 *
 * @author Jack L. Clements
 */
public class TextCommands extends ListenerAdapter {

    private TreeMap<String, SubCommand> commands;

    public TextCommands() {
        commands = new TreeMap<String, SubCommand>();
    }

    /**
     * NOTE - THE REASON JDA HAS SO MANY LISTENERS MAY BE A THREADING ISSUE LOOK
     * INTO SPINNING UP A NEW THREAD WITH EACH EXECUTION JUST IN CASE SEE
     * DOCUMENTATION
     *
     * @param e
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (!e.getAuthor().isBot()) {
            String content = e.getMessage().getStrippedContent().trim().toLowerCase(); //normalise
            String command = content.split(" ")[0]; //takes first command that matches regex (maybe?) test further
            //NOTE - CLEAN THIS
            if (command.equals(".help")) {
                EmbedBuilder builder = new EmbedBuilder();
                builder.setTitle("Commands");
                builder.setAuthor("DreadkingRathalos", "https://github.com/JackLClements/DreadkingRathalos", "https://cdn.discordapp.com/app-icons/340616436668170240/3388c7f64ecb541a14f234e45cf59fe8.png");
                builder.setColor(Color.DARK_GRAY);
                builder.setTimestamp(Instant.now());
                for (Map.Entry<String, SubCommand> entry : commands.entrySet()) {
                    builder.addField(entry.getKey(), entry.getValue().getDescription(), true);
                }
                e.getChannel().sendMessage(builder.build()).queue();
            }

            if (commands.get(command) != null) {
                commands.get(command).doAction(e);
            }
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        MutedListener.addGuild(event.getGuild());
    }

    public void add(String key, SubCommand command) {
        commands.put(key, command);
    }
    /*
    public static void main(String[] args) {
        String content = ".TEST COMMAND testing 1 2 3 4 5";
        String command = content.split(" ")[0].trim().toLowerCase(); //takes first command that matches regex (maybe?) test
        System.out.println(command);
    }*/
}
