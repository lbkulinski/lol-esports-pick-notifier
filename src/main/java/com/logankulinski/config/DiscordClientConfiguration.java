package com.logankulinski.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class DiscordClientConfiguration {
    @Bean
    public JDA jhinClient(@Value("${discord.jhin-token}") String token) throws InterruptedException {
        Objects.requireNonNull(token);

        JDA jda = JDABuilder.createDefault(token)
                            .build();

        jda.awaitReady();

        return jda;
    }

    @Bean
    public JDA lucianClient(@Value("${discord.lucian-token}") String token) throws InterruptedException {
        Objects.requireNonNull(token);

        JDA jda = JDABuilder.createDefault(token)
                            .build();

        jda.awaitReady();

        return jda;
    }
}
