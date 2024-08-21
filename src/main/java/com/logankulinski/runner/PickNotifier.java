package com.logankulinski.runner;

import com.logankulinski.jooq.Tables;
import com.logankulinski.model.Champion;
import com.logankulinski.model.Pick;
import com.logankulinski.service.DiscordService;
import net.dv8tion.jda.api.JDA;
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
import java.util.Map;
import java.util.Objects;

@Component
public final class PickNotifier implements ApplicationRunner {
    private final DSLContext context;

    private final String channelId;

    private final DiscordService service;

    private final Map<Champion, JDA> discordClients;

    private static final Logger LOGGER;

    static {
        LOGGER = LoggerFactory.getLogger(PickNotifier.class);
    }

    @Autowired
    public PickNotifier(DSLContext context, @Value("${discord.channel-id}") String channelId, DiscordService service,
        Map<Champion, JDA> discordClients) {
        this.context = Objects.requireNonNull(context);

        this.channelId = Objects.requireNonNull(channelId);

        this.service = Objects.requireNonNull(service);

        this.discordClients = Objects.requireNonNull(discordClients);
    }

    private List<Pick> getPicks() {
        List<Pick> picks;

        try {
            picks = this.context.select()
                                .from(Tables.ESPORTS_PICK)
                                .where(Tables.ESPORTS_PICK.NOTIFIED.isFalse())
                                .orderBy(Tables.ESPORTS_PICK.TIMESTAMP.desc())
                                .fetchInto(Pick.class);
        } catch (DataAccessException e) {
            String message = e.getMessage();

            PickNotifier.LOGGER.error(message, e);

            return List.of();
        }

        return picks;
    }

    private void notify(Pick pick) {
        Objects.requireNonNull(pick);

        Champion champion = pick.champion();

        JDA client = this.discordClients.get(champion);

        if (client == null) {
            return;
        }

        String messageId = this.service.notify(client, pick, this.channelId);

        String gameId = pick.gameId();

        try {
            this.context.update(Tables.ESPORTS_PICK)
                        .set(Tables.ESPORTS_PICK.MESSAGE_ID, messageId)
                        .set(Tables.ESPORTS_PICK.NOTIFIED, true)
                        .where(Tables.ESPORTS_PICK.GAME_ID.eq(gameId))
                        .execute();
        } catch (DataAccessException e) {
            String exceptionMessage = e.getMessage();

            PickNotifier.LOGGER.error(exceptionMessage, e);
        }
    }

    @Override
    public void run(ApplicationArguments args) {
        Objects.requireNonNull(args);

        List<Pick> picks = this.getPicks();

        for (Pick pick : picks) {
            this.notify(pick);
        }

        this.discordClients.values()
                           .forEach(JDA::shutdown);
    }
}
