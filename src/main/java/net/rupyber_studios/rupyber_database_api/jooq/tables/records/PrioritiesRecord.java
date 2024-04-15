/*
 * This file is generated by jOOQ.
 */
package net.rupyber_studios.rupyber_database_api.jooq.tables.records;


import net.rupyber_studios.rupyber_database_api.jooq.tables.PrioritiesTable;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PrioritiesRecord extends UpdatableRecordImpl<PrioritiesRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>Priorities.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>Priorities.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>Priorities.priority</code>.
     */
    public void setPriority(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>Priorities.priority</code>.
     */
    public String getPriority() {
        return (String) get(1);
    }

    /**
     * Setter for <code>Priorities.color</code>.
     */
    public void setColor(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>Priorities.color</code>.
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
     * Create a detached PrioritiesRecord
     */
    public PrioritiesRecord() {
        super(PrioritiesTable.Priorities);
    }

    /**
     * Create a detached, initialised PrioritiesRecord
     */
    public PrioritiesRecord(Integer id, String priority, Integer color) {
        super(PrioritiesTable.Priorities);

        setId(id);
        setPriority(priority);
        setColor(color);
        resetChangedOnNotNull();
    }
}