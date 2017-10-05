package main;

/*
 */
import Audio.AudioChannelManager;
import commands.GreetingEvent;
import commands.KickBan;
import commands.LeagueBattle;
import commands.MuteUser;
import commands.MutedListener;
import commands.Nuke;
import commands.RankedBattle;
import commands.TextCommands;
import commands.TurfWar;
import commands.Unban;
import commands.UnmuteUser;
import java.util.ArrayList;
import java.util.Collection;
import net.dv8tion.jda.bot.JDABot;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
/**
 *
 * @author Jack L. Clements
 */
public class DreadkingRathalos extends ListenerAdapter { //currently extends listener, extend to own thread

    private static final String TOKEN = "TOKEN"; //I really ought to obscure this in some fashion
    //note - https://github.com/DV8FromTheWorld/JDA/blob/master/src/examples/java/MessageListenerExample.java
    
    private static JDA api;
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO 
        //2. Basic running behaviour, response to commands without bricking channel
        //5. Other features
        //Eventually - run on AWS cloud as far jar
        login();

    }
    
    public static void login() { //note - set token
        //build blocking blocks until login, async waits until msg, can load resources this way
        try {
            JDABuilder jda = new JDABuilder(AccountType.BOT).setToken(TOKEN); //add event listener
            //jda.addEventListener(new GreetingEvent(".greet"));
            TextCommands handler = new TextCommands();
            handler.add(".turfwar", new TurfWar(".turfwar", "Displays current Turf War stages"));
            handler.add(".ranked", new RankedBattle(".ranked", "Displayes current Ranked stages and gamemode"));
            handler.add(".league", new LeagueBattle(".league", "Displayes current Ranked stages and gamemode"));
            handler.add(".mute", new MuteUser(".mute", "Mutes the mentioned users (assuming you have permission)"));
            handler.add(".unmute", new UnmuteUser(".unmute", "Unmutes the mentioned users (assuming you have permission)"));
            handler.add(".kickban", new KickBan(".kickban", "Kicks and bans a mentioned user"));
            handler.add(".unban", new Unban(".unban", "Unbans the mentioned users"));
            handler.add(".nuke", new Nuke(".nuke", "Deletes queued messages"));
            //jda.addEventListener(new GreetingEvent());
            jda.addEventListener(new MutedListener());
            jda.addEventListener(new AudioChannelManager());
            jda.addEventListener(handler);
            
            
            api = jda.buildBlocking();
            
            MutedListener.addGuilds(api.getGuilds());
            
            
            //note you can have a listener for each type of event, it may be easier to write response behaviour programatically this way
            //NOTE 2 - implement a red-black tree that binds commands to class listeners that then executes accordingly
            //NOTE 3 - all listeners trigger during all events, so it may be prudent to have a single listener object that passes commands along
            //use trim to sep. commands
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        JDA jda = event.getJDA(); //provided with every event in JDA
        long responseNumber = event.getResponseNumber(); //no of responses

        //Event specific info
        User author = event.getAuthor();
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();

        String msg = message.getContent(); //message str
        boolean bot = author.isBot(); //is author a bot
        System.out.println("msg - " + msg);
    }
    
    //need update for invites to new servers to join immediately

}
