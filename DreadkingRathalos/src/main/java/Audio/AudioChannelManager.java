/*
 */
package Audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.HashMap;
import java.util.Map;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.AudioManager;

/**
 *
 * @author Jack L. Clements
 */
public class AudioChannelManager extends ListenerAdapter {

    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    //default constructor
    public AudioChannelManager() {
        this.musicManagers = new HashMap<>(); //why do we need this?
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);

    }

    private synchronized GuildMusicManager getMusicManager(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    //is there sense in abstracting this further?
    //unlikely as it would require static contexts with guild related pointers.
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String[] command = event.getMessage().getContent().split(" ", 2);
        Guild guild = event.getGuild();

        if (guild != null) {
            if (".play".equals(command[0]) && command.length == 2) {
                loadAndPlay(event.getTextChannel(), command[1]);
            } else if (".skip".equals(command[0])) {
                skipTrack(event.getTextChannel());
            }
        }

        super.onMessageReceived(event);
    }
    
    //Master control function - needs cleaning BADLY
    
      private void loadAndPlay(final TextChannel channel, final String trackUrl) {
        GuildMusicManager musicManager = getMusicManager(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                

                play(channel.getGuild(), musicManager, track);
                channel.sendMessage("Adding to queue " + track.getInfo().title).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                channel.sendMessage("Adding to queue " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

                play(channel.getGuild(), musicManager, firstTrack);
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nothing found by " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }
    
    
    
    //control functions - can write more e.g. vote
    
    private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track) {
        connect(guild.getAudioManager());

        musicManager.scheduler.queue(track);
    }

    private void skipTrack(TextChannel channel) {
        GuildMusicManager musicManager = getMusicManager(channel.getGuild());
        musicManager.scheduler.nextTrack();
        channel.sendMessage("Skipped to next track.").queue();
    }

    private void connect(AudioManager audioManager) {
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
            if (!audioManager.getGuild().getVoiceChannelsByName("music", true).isEmpty()) {
                audioManager.openAudioConnection(audioManager.getGuild().getVoiceChannelsByName("music", true).get(0));
            } 
            else {
                if (audioManager.getGuild().getSelfMember().hasPermission(Permission.MANAGE_CHANNEL)) {
                    audioManager.getGuild().getController().createVoiceChannel("music").complete();
                    audioManager.openAudioConnection(audioManager.getGuild().getVoiceChannelsByName("music", true).get(0));
                } else {
                    audioManager.getGuild().getTextChannels().get(0).sendMessage("Unable to create dedicated audio channel. Admins, please give me permission to manage channel.").queue();
                }
            }
        }
    }
}
