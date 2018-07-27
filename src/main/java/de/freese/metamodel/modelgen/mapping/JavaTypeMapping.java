/**
 * Created: 22.07.2018
 */

package de.freese.metamodel.modelgen.mapping;

import java.sql.JDBCType;
import java.util.Date;

/**
 * {@link JDBCType}-Mapping für Java.
 *
 * @author Thomas Freese
 */
public class JavaTypeMapping extends AbstractTypeMapping
{
    /**
     * Erstellt ein neues {@link JavaTypeMapping} Object.
     */
    public JavaTypeMapping()
    {
        super();

        register(JDBCType.BIGINT, new Type(Long.class, "null"), new Type(long.class, "0L"));
        register(JDBCType.BINARY, new Type(byte[].class, "null"));
        register(JDBCType.BIT, new Type(Boolean.class, "null"), new Type(boolean.class, "false"));
        register(JDBCType.BLOB, new Type(byte[].class, "null"));
        register(JDBCType.BOOLEAN, new Type(Boolean.class, "null"), new Type(boolean.class, "false"));
        register(JDBCType.CHAR, new Type(Character.class, "null"), new Type(char.class, "0"));
        register(JDBCType.CLOB, new Type(String.class, "null"));
        register(JDBCType.DATE, new Type(Date.class, "null")); // LocalDateTime
        register(JDBCType.DECIMAL, new Type(Long.class, "null"), new Type(long.class, "0L"));
        register(JDBCType.DOUBLE, new Type(Double.class, "null"), new Type(double.class, "0.0D"));
        register(JDBCType.FLOAT, new Type(Float.class, "null"), new Type(float.class, "0.0F"));
        register(JDBCType.INTEGER, new Type(Integer.class, "null"), new Type(int.class, "0"));
        register(JDBCType.NUMERIC, new Type(Double.class, "null"), new Type(double.class, "0.0D"));
        register(JDBCType.SMALLINT, new Type(Short.class, "null"), new Type(short.class, "0"));
        register(JDBCType.TIME, new Type(Date.class, "null"));
        register(JDBCType.TIMESTAMP, new Type(Date.class, "null"));
        register(JDBCType.VARCHAR, new Type(String.class, "null"));
    }
}
