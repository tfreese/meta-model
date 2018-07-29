/**
 * Created: 08.07.2018
 */

package de.freese.metamodel.metagen;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.metamodel.metagen.model.Column;
import de.freese.metamodel.metagen.model.ForeignKey;
import de.freese.metamodel.metagen.model.Index;
import de.freese.metamodel.metagen.model.Schema;
import de.freese.metamodel.metagen.model.Table;
import de.freese.metamodel.metagen.model.UniqueConstraint;

/**
 * Basis-Implementierung eines {@link MetaExporter}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractMetaExporter implements MetaExporter
{
    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erstellt ein neues {@link AbstractMetaExporter} Object.
     */
    public AbstractMetaExporter()
    {
        super();
    }

    /**
     * Erzeugt das Meta-Modell der Spalten einer Tabelle.
     *
     * @param table {@link Table}
     * @param resultSet ResultSet {@link ResultSet}
     * @throws SQLException Falls was schief geht.
     */
    protected void createColumn(final Table table, final ResultSet resultSet) throws SQLException
    {
        String columnName = resultSet.getString("COLUMN_NAME");
        int dataType = resultSet.getInt("DATA_TYPE");
        String typeName = resultSet.getString("TYPE_NAME");
        int columnSize = resultSet.getInt("COLUMN_SIZE");
        int decimalDigits = resultSet.getInt("DECIMAL_DIGITS");
        String comment = resultSet.getString("REMARKS");
        boolean nullable = resultSet.getBoolean("NULLABLE");
        int tableIndex = resultSet.getInt("ORDINAL_POSITION");

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("Processing Column: {}.{}", table.getName(), columnName);
        }

        Column column = table.getColumn(columnName);
        column.setSqlType(dataType);
        column.setTypeName(typeName);
        column.setComment(comment);
        column.setNullable(nullable);
        column.setSize(columnSize);
        column.setDecimalDigits(decimalDigits);
        column.setTableIndex(tableIndex);

        // try
        // {
        // Charset charset = StandardCharsets.UTF_8;
        // column.setComment(new String(comment.getBytes(charset), charset));
        // }
        // catch (Exception ex)
        // {
        // getLogger().warn(ex.getMessage());
        // }
    }

    /**
     * Erzeugt das Meta-Modell eines PrimaryKeys einer Tabelle.
     *
     * @param table {@link Table}
     * @param resultSet ResultSet {@link ResultSet}
     * @throws SQLException Falls was schief geht.
     */
    protected void createForeignKey(final Table table, final ResultSet resultSet) throws SQLException
    {
        String fkName = resultSet.getString("FK_NAME");
        String columnName = resultSet.getString("FKCOLUMN_NAME");
        String refTableName = resultSet.getString("PKTABLE_NAME");
        String refColumnName = resultSet.getString("PKCOLUMN_NAME");

        if (StringUtils.isBlank(fkName))
        {
            fkName = table.getName().toUpperCase() + "_FK";
        }

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("Processing ForeignKey: {} on {}.{} -> {}.{}", fkName, table.getName(), columnName, refTableName, refColumnName);
        }

        Column column = table.getColumn(columnName);

        Table refTable = table.getSchema().getTable(refTableName);
        Column refColumn = refTable.getColumn(refColumnName);

        ForeignKey foreignKey = new ForeignKey();
        foreignKey.setName(fkName);
        foreignKey.setColumn(column);
        foreignKey.setRefColumn(refColumn);

        column.setForeignKey(foreignKey);
    }

    /**
     * Erzeugt das Meta-Modell der Indices einer Tabelle.
     *
     * @param table {@link Table}
     * @param resultSet ResultSet {@link ResultSet}
     * @throws SQLException Falls was schief geht.
     */
    protected void createIndices(final Table table, final ResultSet resultSet) throws SQLException
    {
        String indexName = resultSet.getString("INDEX_NAME");
        String columnName = resultSet.getString("COLUMN_NAME");
        int keyColumnIndex = resultSet.getInt("ORDINAL_POSITION");

        // NON_UNIQUE = true
        boolean unique = !resultSet.getBoolean("NON_UNIQUE");

        if (StringUtils.isBlank(indexName))
        {
            if (!unique)
            {
                indexName = table.getName().toUpperCase() + "_IDX";
            }
            else
            {
                indexName = table.getName().toUpperCase() + "_UNQ";
            }
        }

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("Processing Index: {} on {}.{}", indexName, table.getName(), columnName);
        }

        Column column = table.getColumn(columnName);

        if (!unique)
        {
            Index index = table.getIndex(indexName);
            index.addColumn(keyColumnIndex, column);
        }
        else
        {
            UniqueConstraint uniqueConstraint = table.getUniqueConstraint(indexName);
            uniqueConstraint.addColumn(keyColumnIndex, column);
        }
    }

    /**
     * Erzeugt das Meta-Modell eines PrimaryKeys einer Tabelle.
     *
     * @param table {@link Table}
     * @param resultSet ResultSet {@link ResultSet}
     * @throws SQLException Falls was schief geht.
     */
    protected void createPrimaryKey(final Table table, final ResultSet resultSet) throws SQLException
    {
        String pkName = resultSet.getString("PK_NAME");
        String columnName = resultSet.getString("COLUMN_NAME");
        int keyColumnIndex = resultSet.getInt("KEY_SEQ");

        if (StringUtils.isBlank(pkName))
        {
            pkName = table.getName().toUpperCase() + "_PK";
        }

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("Processing PrimaryKey: {} on {}.{}", pkName, table.getName(), columnName);
        }

        table.addPrimaryKeycolumn(pkName, keyColumnIndex, columnName);
    }

    /**
     * Erzeugt das Meta-Modell einer Tabelle.
     *
     * @param schema {@link Schema}
     * @param resultSet ResultSet {@link ResultSet}
     * @throws SQLException Falls was schief geht.
     */
    protected void createTable(final Schema schema, final ResultSet resultSet) throws SQLException
    {
        // String catalog = resultSet.getString("TABLE_CAT");
        // String schema = resultSet.getString("TABLE_SCHEM");
        String tableName = resultSet.getString("TABLE_NAME");
        String comment = resultSet.getString("REMARKS");

        // if (StringUtils.isBlank(schema) && StringUtils.isNotBlank(catalog))
        // {
        // schema = catalog;
        // }

        if (getLogger().isDebugEnabled())
        {
            getLogger().debug("Processing Table: {}", tableName);
        }

        Table table = schema.getTable(tableName);
        table.setComment(comment);
    }

    /**
     * @see de.freese.metamodel.metagen.MetaExporter#export(javax.sql.DataSource, java.lang.String, java.lang.String)
     */
    @Override
    public Schema export(final DataSource dataSource, final String schemaName, final String tableNamePattern) throws Exception
    {
        Objects.requireNonNull(dataSource, "dataSource required");
        Objects.requireNonNull(schemaName, "schemaName required");

        Schema schema = new Schema();
        schema.setName(schemaName);

        // Tabellen des Schemas
        generateTables(dataSource, schema, tableNamePattern);

        // Sequences des Schemas
        generateSequences(dataSource, schema);

        // Spalten der Tabellen
        for (Table table : schema.getTables())
        {
            generateColumns(dataSource, table);
        }

        // PrimaryKeys der Tabellen
        for (Table table : schema.getTables())
        {
            generatePrimaryKeys(dataSource, table);
        }

        // ForeignKeys der Spalten
        for (Table table : schema.getTables())
        {
            generateForeignKeys(dataSource, table);
        }

        // Indices der Tabelle
        for (Table table : schema.getTables())
        {
            generateIndices(dataSource, table);
        }

        schema.validate();

        return schema;
    }

    /**
     * @param dataSource {@link DataSource}
     * @param table {@link Table}
     * @throws SQLException Falls was schief geht.
     */
    protected void generateColumns(final DataSource dataSource, final Table table) throws SQLException
    {
        try (Connection connection = dataSource.getConnection())
        {
            DatabaseMetaData dbmd = connection.getMetaData();

            try (ResultSet resultSet = dbmd.getColumns(table.getSchema().getName(), table.getSchema().getName(), table.getName(), null))
            {
                while (resultSet.next())
                {
                    createColumn(table, resultSet);
                }
            }
        }
    }

    /**
     * @param dataSource {@link DataSource}
     * @param table {@link Table}
     * @throws SQLException Falls was schief geht.
     */
    protected void generateForeignKeys(final DataSource dataSource, final Table table) throws SQLException
    {
        try (Connection connection = dataSource.getConnection())
        {
            DatabaseMetaData dbmd = connection.getMetaData();

            // ForeignKeys von dieser Tabelle.
            try (ResultSet resultSet = dbmd.getImportedKeys(table.getSchema().getName(), table.getSchema().getName(), table.getName()))
            {
                while (resultSet.next())
                {
                    createForeignKey(table, resultSet);
                }
            }

            // ForeignKeys auf diese Tabelle.
            // try (ResultSet resultSet = dbmd.getExportedKeys(table.getSchema().getName(), table.getSchema().getName(), table.getName()))
            // {
            // while (resultSet.next())
            // {
            // createForeignKey(table, resultSet);
            // }
            // }
        }
    }

    /**
     * @param dataSource {@link DataSource}
     * @param table {@link Table}
     * @throws SQLException Falls was schief geht.
     */
    protected void generateIndices(final DataSource dataSource, final Table table) throws SQLException
    {
        try (Connection connection = dataSource.getConnection())
        {
            DatabaseMetaData dbmd = connection.getMetaData();

            try (ResultSet resultSet = dbmd.getIndexInfo(table.getSchema().getName(), table.getSchema().getName(), table.getName(), false, true))
            {
                while (resultSet.next())
                {
                    createIndices(table, resultSet);
                }
            }
        }
    }

    /**
     * @param dataSource {@link DataSource}
     * @param table {@link Table}
     * @throws SQLException Falls was schief geht.
     */
    protected void generatePrimaryKeys(final DataSource dataSource, final Table table) throws SQLException
    {
        try (Connection connection = dataSource.getConnection())
        {
            DatabaseMetaData dbmd = connection.getMetaData();

            try (ResultSet resultSet = dbmd.getPrimaryKeys(table.getSchema().getName(), table.getSchema().getName(), table.getName()))
            {
                while (resultSet.next())
                {
                    createPrimaryKey(table, resultSet);
                }
            }
        }
    }

    /**
     * @param dataSource {@link DataSource}
     * @param schema {@link Schema}
     * @throws SQLException Falls was schief geht.
     */
    protected abstract void generateSequences(final DataSource dataSource, final Schema schema) throws SQLException;

    /**
     * @param dataSource {@link DataSource}
     * @param schema {@link Schema}
     * @param tableNamePattern String
     * @throws SQLException Falls was schief geht.
     */
    protected void generateTables(final DataSource dataSource, final Schema schema, final String tableNamePattern) throws SQLException
    {
        try (Connection connection = dataSource.getConnection())
        {
            DatabaseMetaData dbmd = connection.getMetaData();

            try (ResultSet resultSet = dbmd.getTables(schema.getName(), schema.getName(), tableNamePattern, new String[]
            {
                    "TABLE"
            }))
            {
                while (resultSet.next())
                {
                    createTable(schema, resultSet);
                }
            }
        }
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }
}
