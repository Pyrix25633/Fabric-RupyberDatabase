/*
 * This file is generated by jOOQ.
 */
package net.rupyber_studios.rupyber_database_api.jooq.tables.records;


import net.rupyber_studios.rupyber_database_api.jooq.tables.PlayersTable;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PlayersRecord extends UpdatableRecordImpl<PlayersRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>Players.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>Players.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>Players.uuid</code>.
     */
    public void setUuid(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>Players.uuid</code>.
     */
    public String getUuid() {
        return (String) get(1);
    }

    /**
     * Setter for <code>Players.username</code>.
     */
    public void setUsername(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>Players.username</code>.
     */
    public String getUsername() {
        return (String) get(2);
    }

    /**
     * Setter for <code>Players.online</code>.
     */
    public void setOnline(Boolean value) {
        set(3, value);
    }

    /**
     * Getter for <code>Players.online</code>.
     */
    public Boolean getOnline() {
        return (Boolean) get(3);
    }

    /**
     * Setter for <code>Players.status</code>.
     */
    public void setStatus(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>Players.status</code>.
     */
    public Integer getStatus() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>Players.rankId</code>.
     */
    public void setRankid(Integer value) {
        set(5, value);
    }

    /**
     * Getter for <code>Players.rankId</code>.
     */
    public Integer getRankid() {
        return (Integer) get(5);
    }

    /**
     * Setter for <code>Players.callsign</code>.
     */
    public void setCallsign(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>Players.callsign</code>.
     */
    public String getCallsign() {
        return (String) get(6);
    }

    /**
     * Setter for <code>Players.callsignReserved</code>.
     */
    public void setCallsignreserved(Boolean value) {
        set(7, value);
    }

    /**
     * Getter for <code>Players.callsignReserved</code>.
     */
    public Boolean getCallsignreserved() {
        return (Boolean) get(7);
    }

    /**
     * Setter for <code>Players.password</code>.
     */
    public void setPassword(String value) {
        set(8, value);
    }

    /**
     * Getter for <code>Players.password</code>.
     */
    public String getPassword() {
        return (String) get(8);
    }

    /**
     * Setter for <code>Players.token</code>.
     */
    public void setToken(String value) {
        set(9, value);
    }

    /**
     * Getter for <code>Players.token</code>.
     */
    public String getToken() {
        return (String) get(9);
    }

    /**
     * Setter for <code>Players.settings</code>.
     */
    public void setSettings(String value) {
        set(10, value);
    }

    /**
     * Getter for <code>Players.settings</code>.
     */
    public String getSettings() {
        return (String) get(10);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached PlayersRecord
     */
    public PlayersRecord() {
        super(PlayersTable.Players);
    }

    /**
     * Create a detached, initialised PlayersRecord
     */
    public PlayersRecord(Integer id, String uuid, String username, Boolean online, Integer status, Integer rankid, String callsign, Boolean callsignreserved, String password, String token, String settings) {
        super(PlayersTable.Players);

        setId(id);
        setUuid(uuid);
        setUsername(username);
        setOnline(online);
        setStatus(status);
        setRankid(rankid);
        setCallsign(callsign);
        setCallsignreserved(callsignreserved);
        setPassword(password);
        setToken(token);
        setSettings(settings);
        resetChangedOnNotNull();
    }
}