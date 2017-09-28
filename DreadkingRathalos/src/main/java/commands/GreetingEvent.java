/*
 */
package commands;

import java.util.List;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.user.UserOnlineStatusUpdateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 *
 * @author Jack L. Clements
 */
public class GreetingEvent extends ListenerAdapter{
    public static final String BOSS_ID = "Supernintendo";
    public static final String BOSS_DISC = "7406";
    
    public GreetingEvent(){
        
    }
    
    @Override
    public void onUserOnlineStatusUpdate(UserOnlineStatusUpdateEvent event){
        User user = event.getUser();
        if(isBoss(user) && event.getPreviousOnlineStatus().equals(OnlineStatus.OFFLINE)){
            List<Guild> guilds = user.getMutualGuilds(); //get guilds shared by me and w/e
            for(Guild guild : guilds){
                //MessageChannel channel = guild.getSelfMember().getDefaultChannel(); 
                MessageChannel channel = guild.getDefaultChannel(); //default channel used by guild, msgchannel is souped up txtchannel, may cause errors, check later
                channel.sendMessage("Welcome, Master.").queue(); //note as all funcs will be mapped to the JDA instance, check triggers w/ print statements
            }
        }
    }
    
    public static boolean isBoss(User user){
        return user.getName().equals(BOSS_ID) && user.getDiscriminator().equals(BOSS_DISC);
    }
}
