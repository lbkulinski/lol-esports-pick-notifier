package com.logankulinski.runner;

import com.logankulinski.jooq.Tables;
import com.logankulinski.model.JhinPick;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
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
import reactor.core.publisher.Mono;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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

    private String getMessage(JhinPick pick) {
        Objects.requireNonNull(pick);

        String outcome = pick.won() ? "won" : "lost";

        String player = pick.player();

        String tournament = pick.tournament();

        String message = "I %s a game played by %s at %s.".formatted(outcome, player, tournament);

        String vod = pick.vod();

        if (vod != null) {
            message += " Check it out [here](%s)!".formatted(vod);
        }

        return message;
    }

    private void notify(JhinPick pick) {
        Objects.requireNonNull(pick);

        Snowflake snowflake = Snowflake.of(this.channelId);

        String message = this.getMessage(pick);

        this.client.getChannelById(snowflake)
                   .ofType(TextChannel.class)
                   .flatMap(channel -> channel.createMessage(message))
                   .block();

        String gameId = pick.gameId();

        try {
            this.context.update(Tables.JHIN_PICKS)
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
