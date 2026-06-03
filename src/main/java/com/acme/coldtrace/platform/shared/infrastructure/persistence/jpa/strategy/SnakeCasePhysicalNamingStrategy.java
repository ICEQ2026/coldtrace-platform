package com.acme.coldtrace.platform.shared.infrastructure.persistence.jpa.strategy;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

import static io.github.encryptorcode.pluralize.Pluralize.pluralize;

/**
 * Hibernate physical naming strategy that converts identifiers to snake case.
 * Table names are also pluralized before snake-case conversion.
 *
 * @since 1.0
 * @see org.hibernate.boot.model.naming.PhysicalNamingStrategy
 */
public class SnakeCasePhysicalNamingStrategy implements PhysicalNamingStrategy {
    /**
     * Converts the catalog name to snake case.
     *
     * @param identifier catalog name identifier
     * @param jdbcEnvironment JDBC environment
     * @return snake-case catalog identifier
     */
    @Override
    public Identifier toPhysicalCatalogName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(identifier);
    }

    /**
     * Converts the schema name to snake case.
     * @param identifier schema name
     * @param jdbcEnvironment jdbc environment
     * @return snake-case schema name
     */
    @Override
    public Identifier toPhysicalSchemaName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(identifier);
    }

    /**
     * Converts the table name to snake case and pluralizes it.
     * @param identifier table name
     * @param jdbcEnvironment jdbc environment
     * @return snake-case and pluralized table name
     */
    @Override
    public Identifier toPhysicalTableName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(this.toPlural(identifier));
    }

    /**
     * Converts the sequence name to snake case.
     * @param identifier sequence name
     * @param jdbcEnvironment jdbc environment
     * @return snake-case sequence name
     */
    @Override
    public Identifier toPhysicalSequenceName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(identifier);
    }

    /**
     * Converts the column name to snake case.
     * @param identifier column name
     * @param jdbcEnvironment jdbc environment
     * @return snake-case column name
     */
    @Override
    public Identifier toPhysicalColumnName(Identifier identifier, JdbcEnvironment jdbcEnvironment) {
        return this.toSnakeCase(identifier);
    }

    /**
     * Converts the identifier to snake case.
     * @param identifier object identifier
     * @return snake-case identifier
     */
    private Identifier toSnakeCase(final Identifier identifier) {
        if (identifier == null) return null;

        final String regex = "([a-z])([A-Z])";
        final String replacement = "$1_$2";
        final String newName = identifier.getText()
                .replaceAll(regex, replacement)
                .toLowerCase();
        return Identifier.toIdentifier(newName);
    }

    /**
     * Pluralizes the identifier.
     * @param identifier object identifier
     * @return pluralized identifier
     */
    private Identifier toPlural(final Identifier identifier) {
        final String newName = pluralize(identifier.getText());
        return Identifier.toIdentifier(newName);
    }
}
