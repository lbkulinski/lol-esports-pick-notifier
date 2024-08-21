package com.logankulinski.model;

import java.time.Instant;

public record Pick(
    String gameId,

    String messageId,

    Champion champion,

    String player,

    String tournament,

    boolean won,

    Instant timestamp,

    String vod,

    boolean notified
) {
}
