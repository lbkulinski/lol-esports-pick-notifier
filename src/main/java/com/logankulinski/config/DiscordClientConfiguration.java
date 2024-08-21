package com.logankulinski.config;

import com.logankulinski.model.Champion;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Objects;

@Configuration
public class DiscordClientConfiguration {
    @Bean
    public Map<Champion, JDA> discordClients(@Value("${discord.jhin-token}") String jhinToken,
        @Value("${discord.lucian-token}") String lucianToken,
        @Value("${discord.draven-token}") String dravenToken) throws InterruptedException {
        Objects.requireNonNull(jhinToken);

        Objects.requireNonNull(lucianToken);

        Objects.requireNonNull(dravenToken);

        JDA jhinClient = JDABuilder.createDefault(jhinToken)
                                   .build();

        jhinClient.awaitReady();

        JDA lucianClient = JDABuilder.createDefault(lucianToken)
                                     .build();

        lucianClient.awaitReady();

        JDA dravenClient = JDABuilder.createDefault(dravenToken)
                                     .build();

        dravenClient.awaitReady();

        return Map.of(
            Champion.JHIN, jhinClient,
            Champion.LUCIAN, lucianClient,
            Champion.DRAVEN, dravenClient
        );
    }
}
