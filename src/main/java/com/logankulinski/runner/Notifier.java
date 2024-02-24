package com.logankulinski.runner;

import com.logankulinski.jooq.Tables;
import com.logankulinski.model.JhinPick;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public final class Notifier implements ApplicationRunner {
    private final DSLContext context;

    private final GatewayDiscordClient client;

    private final String channelId;

    private static final Logger LOGGER;

    static {
        LOGGER = LoggerFactory.getLogger(Notifier.class);
    }

    @Autowired
    public Notifier(DSLContext context, GatewayDiscordClient client,
        @Value("${discord.channel-id}") String channelId) {
        this.context = Objects.requireNonNull(context);

        this.client = Objects.requireNonNull(client);

        this.channelId = Objects.requireNonNull(channelId);
    }

    private List<JhinPick> getPicks() {
        List<JhinPick> picks;

        try {
            picks = this.context.select()
                                .from(Tables.JHIN_PICKS)
                                .where(Tables.JHIN_PICKS.NOTIFIED.isFalse())
                                .orderBy(Tables.JHIN_PICKS.TIMESTAMP.desc())
                                .fetchInto(JhinPick.class);
        } catch (DataAccessException e) {
            String message = e.getMessage();

            Notifier.LOGGER.error(message, e);

            return List.of();
        }

        return picks;
    }

    private String getMessageText(JhinPick pick) {
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

    private String createMessage(JhinPick pick) {
        Objects.requireNonNull(pick);

        Snowflake channelSnowflake = Snowflake.of(this.channelId);

        String messageText = this.getMessageText(pick);

        Message message = this.client.getChannelById(channelSnowflake)
                                     .ofType(TextChannel.class)
                                     .flatMap(channel -> channel.createMessage(messageText))
                                     .block();

        String messageId = null;

        if (message != null) {
            messageId = message.getId()
                               .asString();
        }

        return messageId;
    }

    private void updateMessage(JhinPick pick) {
        Objects.requireNonNull(pick);

        Snowflake channelSnowflake = Snowflake.of(this.channelId);

        String messageId = pick.messageId();

        Snowflake messageSnowflake = Snowflake.of(messageId);

        this.client.getMessageById(channelSnowflake, messageSnowflake)
                   .flatMap(message -> {
                       String messageText = this.getMessageText(pick);

                       return message.edit()
                                     .withContentOrNull(messageText);
                   })
                   .block();
    }

    private void notify(JhinPick pick) {
        Objects.requireNonNull(pick);

        String messageId = pick.messageId();

        if (messageId == null) {
            messageId = this.createMessage(pick);
        } else {
            this.updateMessage(pick);
        }

        String gameId = pick.gameId();

        try {
            this.context.update(Tables.JHIN_PICKS)
                        .set(Tables.JHIN_PICKS.MESSAGE_ID, messageId)
                        .set(Tables.JHIN_PICKS.NOTIFIED, true)
                        .where(Tables.JHIN_PICKS.GAME_ID.eq(gameId))
                        .execute();
        } catch (DataAccessException e) {
            String exceptionMessage = e.getMessage();

            Notifier.LOGGER.error(exceptionMessage, e);
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        Objects.requireNonNull(args);

        List<JhinPick> picks = this.getPicks();

        for (JhinPick pick : picks) {
            this.notify(pick);
        }
    }
}
