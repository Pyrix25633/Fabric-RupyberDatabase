/*
 * This file is generated by jOOQ.
 */
package net.rupyber_studios.rupyber_database_api.jooq.tables;


import java.time.LocalDate;
import java.util.Collection;

import net.rupyber_studios.rupyber_database_api.jooq.DefaultSchema;
import net.rupyber_studios.rupyber_database_api.jooq.Keys;
import net.rupyber_studios.rupyber_database_api.jooq.tables.records.EmergencyCallNumbersRecord;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.PlainSQL;
import org.jooq.QueryPart;
import org.jooq.SQL;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.Stringly;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EmergencyCallNumbersTable extends TableImpl<EmergencyCallNumbersRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>EmergencyCallNumbers</code>
     */
    public static final EmergencyCallNumbersTable EmergencyCallNumbers = new EmergencyCallNumbersTable();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<EmergencyCallNumbersRecord> getRecordType() {
        return EmergencyCallNumbersRecord.class;
    }

    /**
     * The column <code>EmergencyCallNumbers.day</code>.
     */
    public final TableField<EmergencyCallNumbersRecord, LocalDate> day = createField(DSL.name("day"), SQLDataType.LOCALDATE, this, "");

    /**
     * The column <code>EmergencyCallNumbers.number</code>.
     */
    public final TableField<EmergencyCallNumbersRecord, Integer> number = createField(DSL.name("number"), SQLDataType.INTEGER.nullable(false), this, "");

    private EmergencyCallNumbersTable(Name alias, Table<EmergencyCallNumbersRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private EmergencyCallNumbersTable(Name alias, Table<EmergencyCallNumbersRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>EmergencyCallNumbers</code> table reference
     */
    public EmergencyCallNumbersTable(String alias) {
        this(DSL.name(alias), EmergencyCallNumbers);
    }

    /**
     * Create an aliased <code>EmergencyCallNumbers</code> table reference
     */
    public EmergencyCallNumbersTable(Name alias) {
        this(alias, EmergencyCallNumbers);
    }

    /**
     * Create a <code>EmergencyCallNumbers</code> table reference
     */
    public EmergencyCallNumbersTable() {
        this(DSL.name("EmergencyCallNumbers"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<EmergencyCallNumbersRecord> getPrimaryKey() {
        return Keys.pk_EmergencyCallNumbers;
    }

    @Override
    public EmergencyCallNumbersTable as(String alias) {
        return new EmergencyCallNumbersTable(DSL.name(alias), this);
    }

    @Override
    public EmergencyCallNumbersTable as(Name alias) {
        return new EmergencyCallNumbersTable(alias, this);
    }

    @Override
    public EmergencyCallNumbersTable as(Table<?> alias) {
        return new EmergencyCallNumbersTable(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public EmergencyCallNumbersTable rename(String name) {
        return new EmergencyCallNumbersTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public EmergencyCallNumbersTable rename(Name name) {
        return new EmergencyCallNumbersTable(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public EmergencyCallNumbersTable rename(Table<?> name) {
        return new EmergencyCallNumbersTable(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public EmergencyCallNumbersTable where(Condition condition) {
        return new EmergencyCallNumbersTable(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public EmergencyCallNumbersTable where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public EmergencyCallNumbersTable where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public EmergencyCallNumbersTable where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public EmergencyCallNumbersTable where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public EmergencyCallNumbersTable where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public EmergencyCallNumbersTable where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public EmergencyCallNumbersTable where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public EmergencyCallNumbersTable whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public EmergencyCallNumbersTable whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
