/*
 * This file is generated by jOOQ.
 */
package net.rupyber_studios.rupyber_database_api.jooq.tables;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.rupyber_studios.rupyber_database_api.jooq.DefaultSchema;
import net.rupyber_studios.rupyber_database_api.jooq.Keys;
import net.rupyber_studios.rupyber_database_api.jooq.tables.PlayersTable.Players;
import net.rupyber_studios.rupyber_database_api.jooq.tables.StatusesTable.Statuses;
import net.rupyber_studios.rupyber_database_api.jooq.tables.records.StatusLogsRecord;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.InverseForeignKey;
import org.jooq.Name;
import org.jooq.Path;
import org.jooq.PlainSQL;
import org.jooq.QueryPart;
import org.jooq.Record;
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
public class StatusLogsTable extends TableImpl<StatusLogsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>StatusLogs</code>
     */
    public static final StatusLogsTable StatusLogs = new StatusLogsTable();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<StatusLogsRecord> getRecordType() {
        return StatusLogsRecord.class;
    }

    /**
     * The column <code>StatusLogs.id</code>.
     */
    public final TableField<StatusLogsRecord, Integer> id = createField(DSL.name("id"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>StatusLogs.playerId</code>.
     */
    public final TableField<StatusLogsRecord, Integer> playerId = createField(DSL.name("playerId"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>StatusLogs.statusId</code>.
     */
    public final TableField<StatusLogsRecord, Integer> statusId = createField(DSL.name("statusId"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>StatusLogs.changedAt</code>.
     */
    public final TableField<StatusLogsRecord, LocalDateTime> changedAt = createField(DSL.name("changedAt"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field(DSL.raw("CURRENT_TIMESTAMP"), SQLDataType.LOCALDATETIME)), this, "");

    private StatusLogsTable(Name alias, Table<StatusLogsRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private StatusLogsTable(Name alias, Table<StatusLogsRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>StatusLogs</code> table reference
     */
    public StatusLogsTable(String alias) {
        this(DSL.name(alias), StatusLogs);
    }

    /**
     * Create an aliased <code>StatusLogs</code> table reference
     */
    public StatusLogsTable(Name alias) {
        this(alias, StatusLogs);
    }

    /**
     * Create a <code>StatusLogs</code> table reference
     */
    public StatusLogsTable() {
        this(DSL.name("StatusLogs"), null);
    }

    public <O extends Record> StatusLogsTable(Table<O> path, ForeignKey<O, StatusLogsRecord> childPath, InverseForeignKey<O, StatusLogsRecord> parentPath) {
        super(path, childPath, parentPath, StatusLogs);
    }

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    public static class StatusLogs extends StatusLogsTable implements Path<StatusLogsRecord> {

        private static final long serialVersionUID = 1L;
        public <O extends Record> StatusLogs(Table<O> path, ForeignKey<O, StatusLogsRecord> childPath, InverseForeignKey<O, StatusLogsRecord> parentPath) {
            super(path, childPath, parentPath);
        }
        private StatusLogs(Name alias, Table<StatusLogsRecord> aliased) {
            super(alias, aliased);
        }

        @Override
        public StatusLogs as(String alias) {
            return new StatusLogs(DSL.name(alias), this);
        }

        @Override
        public StatusLogs as(Name alias) {
            return new StatusLogs(alias, this);
        }

        @Override
        public StatusLogs as(Table<?> alias) {
            return new StatusLogs(alias.getQualifiedName(), this);
        }
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<StatusLogsRecord> getPrimaryKey() {
        return Keys.pk_StatusLogs;
    }

    @Override
    public List<ForeignKey<StatusLogsRecord, ?>> getReferences() {
        return Arrays.asList(Keys.fk_StatusLogs_pk_Players, Keys.fk_StatusLogs_pk_Statuses);
    }

    private transient Players _players;

    /**
     * Get the implicit join path to the <code>Players</code> table.
     */
    public Players players() {
        if (_players == null)
            _players = new Players(this, Keys.fk_StatusLogs_pk_Players, null);

        return _players;
    }

    private transient Statuses _statuses;

    /**
     * Get the implicit join path to the <code>Statuses</code> table.
     */
    public Statuses statuses() {
        if (_statuses == null)
            _statuses = new Statuses(this, Keys.fk_StatusLogs_pk_Statuses, null);

        return _statuses;
    }

    @Override
    public StatusLogsTable as(String alias) {
        return new StatusLogsTable(DSL.name(alias), this);
    }

    @Override
    public StatusLogsTable as(Name alias) {
        return new StatusLogsTable(alias, this);
    }

    @Override
    public StatusLogsTable as(Table<?> alias) {
        return new StatusLogsTable(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public StatusLogsTable rename(String name) {
        return new StatusLogsTable(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public StatusLogsTable rename(Name name) {
        return new StatusLogsTable(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public StatusLogsTable rename(Table<?> name) {
        return new StatusLogsTable(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public StatusLogsTable where(Condition condition) {
        return new StatusLogsTable(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public StatusLogsTable where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public StatusLogsTable where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public StatusLogsTable where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public StatusLogsTable where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public StatusLogsTable where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public StatusLogsTable where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public StatusLogsTable where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public StatusLogsTable whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public StatusLogsTable whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}