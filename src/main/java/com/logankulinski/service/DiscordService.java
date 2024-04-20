package com.logankulinski.service;

import com.logankulinski.model.Pick;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public final class DiscordService {
    private String getMessageText(Pick pick) {
        Objects.requireNonNull(pick);

        String outcome = pick.won() ? "won" : "lost";

        String player = pick.player();

        String tournament = pick.tournament();

        String messageText = "I %s a game played by %s at %s.".formatted(outcome, player, tournament);

        String vod = pick.vod();

        if (vod != null) {
            messageText += " Check it out [here](%s)!".formatted(vod);
        }

        return messageText;
    }

    private MessageChannel getChannel(JDA client, String channelId) {
        Objects.requireNonNull(client);

        Objects.requireNonNull(channelId);

        MessageChannel channel = client.getChannelById(MessageChannel.class, channelId);

        if (channel == null) {
            String message = "A channel with ID %s was not found.".formatted(channelId);

            throw new RuntimeException(message);
        }

        return channel;
    }

    private String createMessage(JDA client, Pick pick, String channelId) {
        Objects.requireNonNull(client);

        Objects.requireNonNull(pick);

        Objects.requireNonNull(channelId);

        MessageChannel channel = this.getChannel(client, channelId);

        String messageText = this.getMessageText(pick);

        Message message = channel.sendMessage(messageText)
                                 .complete();

        return message.getId();
    }

    private void updateMessage(JDA client, Pick pick, String channelId) {
        Objects.requireNonNull(client);

        Objects.requireNonNull(pick);

        Objects.requireNonNull(channelId);

        MessageChannel channel = this.getChannel(client, channelId);

        String messageId = pick.messageId();

        String messageText = this.getMessageText(pick);

        channel.editMessageById(messageId, messageText)
               .queue();
    }

    public String notify(JDA client, Pick pick, String channelId) {
        Objects.requireNonNull(client);

        Objects.requireNonNull(pick);

        Objects.requireNonNull(channelId);

        String messageId = pick.messageId();

        if (messageId == null) {
            messageId = this.createMessage(client, pick, channelId);
        } else {
            this.updateMessage(client, pick, channelId);
        }

        return messageId;
    }
}
