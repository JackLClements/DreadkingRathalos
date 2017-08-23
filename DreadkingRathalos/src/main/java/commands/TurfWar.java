/*
 */
package commands;

import Misc.SquidKids;
import SplatNet2.Stage;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

/**
 *
 * @author Jack L. Clements
 */
public class TurfWar extends SubCommand {

    public TurfWar(String name, String description) {
        super(name, description);
    }

    @Override
    public void doAction(MessageReceivedEvent e) {
        /*embed builder useful, but not here
            EmbedBuilder builder = new EmbedBuilder();
            builder.setDescription("Test");
            builder.setImage("http://i.imgur.com/2hzuHFJ.png");
            builder.
            MessageEmbed msg = builder.build();
            e.getChannel().sendMessage(msg).queue(); */
        
        e.getChannel().sendMessage("The current regular battle stages are...").queue();
        
        Stage [] stages = SquidKids.turfWar();
        MessageBuilder msg = new MessageBuilder();
        msg.append(stages[0].getName(), MessageBuilder.Formatting.BOLD);
        try {
            InputStream stream = stages[0].getThumb().openStream();
            e.getChannel().sendFile(stream, "stage.png", msg.build()).queue();
        } catch (Exception ex) {
            Logger.getLogger(TurfWar.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        e.getChannel().sendMessage("and...").queue();
        
        msg.clear();
        
        msg.append(stages[1].getName(), MessageBuilder.Formatting.BOLD);
        try {
            InputStream stream = stages[1].getThumb().openStream();
            e.getChannel().sendFile(stream, "stage.png", msg.build()).queue();
        } catch (Exception ex) {
            Logger.getLogger(TurfWar.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //NOTE FIX THIS FOR SPLATFEST LMAO
        
        e.getChannel().sendMessage("Until next time - don't get cooked... stay off the hook!").queue();
    }
}
