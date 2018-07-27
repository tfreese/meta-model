/**
 * Created: 08.07.2018
 */

package de.freese.metamodel.metagen.model;

import java.sql.JDBCType;
import java.sql.Types;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/**
 * Enthält die MetaDaten einer Spalte.
 *
 * @author Thomas Freese
 */
public class Column
{
    /**
     *
     */
    private static final int UNDEFINED = -1;

    /**
    *
    */
    private String comment = null;

    /**
    *
    */
    private int decimalDigits = UNDEFINED;

    /**
     *
     */
    private ForeignKey foreignKey = null;

    /**
     *
     */
    private String name = null;

    /**
    *
    */
    private boolean nullable = false;

    /**
    *
    */
    private int size = UNDEFINED;

    /**
     * @see Types
     */
    private int sqlType = Types.NULL;

    /**
    *
    */
    private Table table = null;

    /**
    *
    */
    private int tableIndex = 0;

    /**
    *
    */
    private String typeName = null;

    /**
     * Erstellt ein neues {@link Column} Object.
     */
    public Column()
    {
        super();
    }

    /**
     * Erstellt ein neues {@link Column} Object.
     *
     * @param table {@link Table}
     * @param name String
     */
    Column(final Table table, final String name)
    {
        super();

        this.table = Objects.requireNonNull(table, "table required");
        this.name = Objects.requireNonNull(StringUtils.defaultIfBlank(name, null), "name required");
    }

    /**
     * @return {@link JDBCType}
     */
    public JDBCType geJdbcType()
    {
        return JDBCType.valueOf(getSqlType());
    }

    /**
     * @return String
     */
    public String getComment()
    {
        return this.comment;
    }

    /**
     * @return int
     */
    public int getDecimalDigits()
    {
        return this.decimalDigits;
    }

    /**
     * @return {@link ForeignKey}
     */
    public ForeignKey getForeignKey()
    {
        return this.foreignKey;
    }

    /**
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return int
     */
    public int getSize()
    {
        return this.size;
    }

    /**
     * @return int
     * @see Types
     */
    public int getSqlType()
    {
        return this.sqlType;
    }

    /**
     * @return {@link Table}
     */
    public Table getTable()
    {
        return this.table;
    }

    /**
     * @return int
     */
    public int getTableIndex()
    {
        return this.tableIndex;
    }

    /**
     * @return String
     */
    public String getTypeName()
    {
        return this.typeName;
    }

    /**
     * @return boolean
     */
    public boolean hasDecimalDigits()
    {
        return getDecimalDigits() != UNDEFINED;
    }

    /**
     * @return boolean
     */
    public boolean hasSize()
    {
        return getSize() != UNDEFINED;
    }

    /**
     * @return boolean
     */
    public boolean isNullable()
    {
        return this.nullable;
    }

    /**
     * @param comment String
     */
    public void setComment(final String comment)
    {
        this.comment = comment;
    }

    /**
     * @param decimalDigits int
     */
    public void setDecimalDigits(final int decimalDigits)
    {
        this.decimalDigits = decimalDigits;
    }

    /**
     * @param foreignKey {@link ForeignKey}
     */
    public void setForeignKey(final ForeignKey foreignKey)
    {
        this.foreignKey = foreignKey;
    }

    /**
     * @param name String
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * @param nullable boolean
     */
    public void setNullable(final boolean nullable)
    {
        this.nullable = nullable;
    }

    /**
     * @param size int
     */
    public void setSize(final int size)
    {
        this.size = size;
    }

    /**
     * @param sqlType int
     * @see Types
     */
    public void setSqlType(final int sqlType)
    {
        this.sqlType = sqlType;
    }

    /**
     * @param table {@link Table}
     */
    public void setTable(final Table table)
    {
        this.table = table;
    }

    /**
     * @param tableIndex int
     */
    public void setTableIndex(final int tableIndex)
    {
        this.tableIndex = tableIndex;
    }

    /**
     * @param typeName String
     */
    public void setTypeName(final String typeName)
    {
        this.typeName = typeName;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Column [");
        builder.append("schema=").append(getTable().getSchema().getName());
        builder.append(", table=").append(getTable().getName());
        builder.append(", name=").append(getName());
        builder.append("]");

        return builder.toString();
    }
}
