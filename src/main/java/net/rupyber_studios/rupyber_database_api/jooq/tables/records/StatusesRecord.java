/*
 * This file is generated by jOOQ.
 */
package net.rupyber_studios.rupyber_database_api.jooq.tables.records;


import net.rupyber_studios.rupyber_database_api.jooq.tables.StatusesTable;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class StatusesRecord extends UpdatableRecordImpl<StatusesRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>Statuses.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>Statuses.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>Statuses.status</code>.
     */
    public void setStatus(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>Statuses.status</code>.
     */
    public String getStatus() {
        return (String) get(1);
    }

    /**
     * Setter for <code>Statuses.color</code>.
     */
    public void setColor(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>Statuses.color</code>.
     */
    public Integer getColor() {
        return (Integer) get(2);
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
     * Create a detached StatusesRecord
     */
    public StatusesRecord() {
        super(StatusesTable.Statuses);
    }

    /**
     * Create a detached, initialised StatusesRecord
     */
    public StatusesRecord(Integer id, String status, Integer color) {
        super(StatusesTable.Statuses);

        setId(id);
        setStatus(status);
        setColor(color);
        resetChangedOnNotNull();
    }
}