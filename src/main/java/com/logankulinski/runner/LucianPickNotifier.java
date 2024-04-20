package com.logankulinski.runner;

import com.logankulinski.jooq.Tables;
import com.logankulinski.model.Pick;
import com.logankulinski.service.DiscordService;
import net.dv8tion.jda.api.JDA;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public final class LucianPickNotifier implements ApplicationRunner {
    private final DSLContext context;

    private final JDA client;

    private final String channelId;

    private final DiscordService service;

    private static final Logger LOGGER;

    static {
        LOGGER = LoggerFactory.getLogger(LucianPickNotifier.class);
    }

    @Autowired
    public LucianPickNotifier(DSLContext context, @Qualifier("lucianDiscordClient") JDA client,
        @Value("${discord.channel-id}") String channelId, DiscordService service) {
        this.context = Objects.requireNonNull(context);

        this.client = Objects.requireNonNull(client);

        this.channelId = Objects.requireNonNull(channelId);

        this.service = Objects.requireNonNull(service);
    }

    private List<Pick> getPicks() {
        List<Pick> picks;

        try {
            picks = this.context.select()
                                .from(Tables.LUCIAN_PICKS)
                                .where(Tables.LUCIAN_PICKS.NOTIFIED.isFalse())
                                .orderBy(Tables.LUCIAN_PICKS.TIMESTAMP.desc())
                                .fetchInto(Pick.class);
        } catch (DataAccessException e) {
            String message = e.getMessage();

            LucianPickNotifier.LOGGER.error(message, e);

            return List.of();
        }

        return picks;
    }

    private void notify(Pick pick) {
        Objects.requireNonNull(pick);

        String messageId = this.service.notify(this.client, pick, this.channelId);

        String gameId = pick.gameId();

        try {
            this.context.update(Tables.LUCIAN_PICKS)
                        .set(Tables.LUCIAN_PICKS.MESSAGE_ID, messageId)
                        .set(Tables.LUCIAN_PICKS.NOTIFIED, true)
                        .where(Tables.LUCIAN_PICKS.GAME_ID.eq(gameId))
                        .execute();
        } catch (DataAccessException e) {
            String exceptionMessage = e.getMessage();

            LucianPickNotifier.LOGGER.error(exceptionMessage, e);
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        Objects.requireNonNull(args);

        List<Pick> picks = this.getPicks();

        for (Pick pick : picks) {
            this.notify(pick);
        }
    }
}
