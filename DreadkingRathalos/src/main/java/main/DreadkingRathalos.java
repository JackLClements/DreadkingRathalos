package main;

/*
 */


import commands.GreetingEvent;
import commands.TextCommands;
import commands.TurfWar;


import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;




/**
 *
 * @author Jack L. Clements
 */
public class DreadkingRathalos extends ListenerAdapter{ //currently extends listener, extend to own thread
    private static final String TOKEN = "obscured"; //I really ought to obscure this in some fashion
    //note - https://github.com/DV8FromTheWorld/JDA/blob/master/src/examples/java/MessageListenerExample.java
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO 
        //1. Hello World
        //2. Basic running behaviour, response to commands without bricking channel
        //3. EventListener
        //4. Audio
        //5. Other features
        //Eventually - run on AWS cloud as far jar
        
        login();
        
    }
    
    public static void login(){ //note - set token
        //build blocking blocks until login, async waits until msg, can load resources this way
        try{
            JDA jda = new JDABuilder(AccountType.BOT).setToken(TOKEN).addEventListener(new DreadkingRathalos()).buildBlocking(); //add event listener
            jda.addEventListener(new GreetingEvent(".greet"));
            TextCommands handler = new TextCommands("");
            handler.add(".turfwar", new TurfWar(".turfwar", "Does a thing"));
            jda.addEventListener(handler);
            //note you can have a listener for each type of event, it may be easier to write response behaviour programatically this way
            //NOTE 2 - implement a red-black tree that binds commands to class listeners that then executes accordingly
            //NOTE 3 - all listeners trigger during all events, so it may be prudent to have a single listener object that passes commands along
            //use trim to sep. commands
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        JDA jda = event.getJDA(); //provided with every event in JDA
        long responseNumber = event.getResponseNumber(); //no of responses
        
        //Event specific info
        User author = event.getAuthor();
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();
        
        String msg = message.getContent(); //message str
        boolean bot = author.isBot(); //is author a bot
        System.out.println(msg);
        
        
        if(msg.contains("!hello")){
            System.out.println("true");
            channel.sendMessage("Hello World!").queue();
        }
        
    }
    
}
