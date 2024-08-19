package com.logankulinski.model;

import java.time.Instant;

public record Pick(
    String gameId,

    String messageId,

    String champion,

    String player,

    String tournament,

    boolean won,

    Instant timestamp,

    String vod,

    boolean notified
) {
}
