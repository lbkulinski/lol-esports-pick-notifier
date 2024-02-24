package com.logankulinski.model;

import java.time.Instant;

public record JhinPick(
    String gameId,

    String messageId,

    String player,

    String tournament,

    boolean won,

    Instant timestamp,

    String vod,

    boolean notified
) {
}
