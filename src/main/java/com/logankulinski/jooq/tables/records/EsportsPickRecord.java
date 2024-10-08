/*
 * This file is generated by jOOQ.
 */
package com.logankulinski.jooq.tables.records;


import com.logankulinski.jooq.tables.EsportsPick;

import java.time.OffsetDateTime;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class EsportsPickRecord extends UpdatableRecordImpl<EsportsPickRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.esports_pick.game_id</code>.
     */
    public void setGameId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.esports_pick.game_id</code>.
     */
    public String getGameId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.esports_pick.message_id</code>.
     */
    public void setMessageId(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.esports_pick.message_id</code>.
     */
    public String getMessageId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.esports_pick.champion</code>.
     */
    public void setChampion(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.esports_pick.champion</code>.
     */
    public String getChampion() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.esports_pick.player</code>.
     */
    public void setPlayer(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.esports_pick.player</code>.
     */
    public String getPlayer() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.esports_pick.tournament</code>.
     */
    public void setTournament(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.esports_pick.tournament</code>.
     */
    public String getTournament() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.esports_pick.won</code>.
     */
    public void setWon(Boolean value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.esports_pick.won</code>.
     */
    public Boolean getWon() {
        return (Boolean) get(5);
    }

    /**
     * Setter for <code>public.esports_pick.timestamp</code>.
     */
    public void setTimestamp(OffsetDateTime value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.esports_pick.timestamp</code>.
     */
    public OffsetDateTime getTimestamp() {
        return (OffsetDateTime) get(6);
    }

    /**
     * Setter for <code>public.esports_pick.vod</code>.
     */
    public void setVod(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.esports_pick.vod</code>.
     */
    public String getVod() {
        return (String) get(7);
    }

    /**
     * Setter for <code>public.esports_pick.notified</code>.
     */
    public void setNotified(Boolean value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.esports_pick.notified</code>.
     */
    public Boolean getNotified() {
        return (Boolean) get(8);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached EsportsPickRecord
     */
    public EsportsPickRecord() {
        super(EsportsPick.ESPORTS_PICK);
    }

    /**
     * Create a detached, initialised EsportsPickRecord
     */
    public EsportsPickRecord(String gameId, String messageId, String champion, String player, String tournament, Boolean won, OffsetDateTime timestamp, String vod, Boolean notified) {
        super(EsportsPick.ESPORTS_PICK);

        setGameId(gameId);
        setMessageId(messageId);
        setChampion(champion);
        setPlayer(player);
        setTournament(tournament);
        setWon(won);
        setTimestamp(timestamp);
        setVod(vod);
        setNotified(notified);
        resetChangedOnNotNull();
    }
}
