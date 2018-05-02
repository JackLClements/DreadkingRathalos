/*
 */
package commands;

import net.dv8tion.jda.core.entities.Message;
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
        //Note - sort all this out, use 2 weeks as a base case, otherwise iterate through 1000 messages
        if (history.size() <= 2) {
            for (Message message : channel.getIterableHistory()) {
                message.delete().complete();
            }
        } else if (history.size() >= 100) {
            int iterationLimit = 1000; //sensible enough
            for (Message message : channel.getIterableHistory()) {
                message.delete().complete();
                if (iterationLimit-- <= 0) {
                    break;
                }
            }
        } else { //this currently won't call, as discord limits batch deletes to 2 weeks, this may change later
            System.out.println("Nuking 2 weeks");
            channel.deleteMessages(history.getRetrievedHistory()).queue();
        }
        channel.sendMessage("Nuked from orbit. It was the only way to be sure.").queue();
    }
}
