package com.logankulinski.config;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.ClientPresence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class DiscordConfiguration {
    @Bean
    public GatewayDiscordClient gatewayDiscordClient(@Value("${discord.token}") String token) {
        Objects.requireNonNull(token);

        DiscordClient client = DiscordClientBuilder.create(token).build();

        return client.gateway()
                     .setInitialPresence(shardInfo -> ClientPresence.online())
                     .login()
                     .block();
    }
}
