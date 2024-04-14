/*
 * This file is generated by jOOQ.
 */
package net.rupyber_studios.rupyber_database_api.jooq.tables.records;


import java.time.LocalDate;

import net.rupyber_studios.rupyber_database_api.jooq.tables.IncidentNumbersTable;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class IncidentNumbersRecord extends UpdatableRecordImpl<IncidentNumbersRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>IncidentNumbers.day</code>.
     */
    public void setDay(LocalDate value) {
        set(0, value);
    }

    /**
     * Getter for <code>IncidentNumbers.day</code>.
     */
    public LocalDate getDay() {
        return (LocalDate) get(0);
    }

    /**
     * Setter for <code>IncidentNumbers.number</code>.
     */
    public void setNumber(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>IncidentNumbers.number</code>.
     */
    public Integer getNumber() {
        return (Integer) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<LocalDate> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached IncidentNumbersRecord
     */
    public IncidentNumbersRecord() {
        super(IncidentNumbersTable.IncidentNumbers);
    }

    /**
     * Create a detached, initialised IncidentNumbersRecord
     */
    public IncidentNumbersRecord(LocalDate day, Integer number) {
        super(IncidentNumbersTable.IncidentNumbers);

        setDay(day);
        setNumber(number);
        resetChangedOnNotNull();
    }
}
