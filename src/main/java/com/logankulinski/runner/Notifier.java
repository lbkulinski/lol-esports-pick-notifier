package com.logankulinski.runner;

import com.logankulinski.jooq.Tables;
import com.logankulinski.model.JhinPick;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public final class Notifier implements ApplicationRunner {
    private final DSLContext context;

    private static final Logger LOGGER;

    static {
        LOGGER = LoggerFactory.getLogger(Notifier.class);
    }

    @Autowired
    public Notifier(DSLContext context) {
        this.context = Objects.requireNonNull(context);
    }


    @Override
    public void run(ApplicationArguments args) {
        Objects.requireNonNull(args);

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

            return;
        }

        picks.forEach(System.out::println);
    }
}
