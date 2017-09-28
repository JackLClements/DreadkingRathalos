/*
 */
package commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author Jack L. Clements
 */
public class Nuke extends SubCommand {

    public Nuke(String name, String description) {
        super(name, description);
    }

    @Override
    public void doAction(MessageReceivedEvent e) {
        TextChannel channel = e.getTextChannel();
        MessageHistory history = channel.getHistory();
        history.retrievePast(100).complete(); //done intentionally, quit whining
        System.out.println(history.size());
        if (history.size() <= 2) {
            System.out.println("Using short delete");
            for (Message message : channel.getIterableHistory()) {
                message.delete().complete();
            }
        } else if (history.size() > 100) {
            System.out.println("Using heavy loarde");
            int iterationLimit = 1000; //sensible enough
            for (Message message : channel.getIterableHistory()) {
                message.delete().complete();
                if (iterationLimit-- <= 0) {
                    break;
                }
            }
        } else {
            System.out.println("Nuking 2 weeks");
            channel.deleteMessages(history.getRetrievedHistory()).queue(); //discord limits batch deletes to 2 weeks, will issue a fix later
        }
        channel.sendMessage("Nuked from orbit. It was the only way to be sure.").queue();
    }
}
