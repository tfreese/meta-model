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

        register(JDBCType.BIGINT, new ClassType(Long.class, "null"), new ClassType(long.class, "0L"));
        register(JDBCType.BINARY, new ClassType(byte[].class, "null"));
        register(JDBCType.BIT, new ClassType(Boolean.class, "null"), new ClassType(boolean.class, "false"));
        register(JDBCType.BLOB, new ClassType(byte[].class, "null"));
        register(JDBCType.BOOLEAN, new ClassType(Boolean.class, "null"), new ClassType(boolean.class, "false"));
        register(JDBCType.CHAR, new ClassType(Character.class, "null"), new ClassType(char.class, "0"));
        register(JDBCType.CLOB, new ClassType(String.class, "null"));
        register(JDBCType.DATE, new ClassType(Date.class, "null")); // LocalDateTime
        register(JDBCType.DECIMAL, new ClassType(Long.class, "null"), new ClassType(long.class, "0L"));
        register(JDBCType.DOUBLE, new ClassType(Double.class, "null"), new ClassType(double.class, "0.0D"));
        register(JDBCType.FLOAT, new ClassType(Float.class, "null"), new ClassType(float.class, "0.0F"));
        register(JDBCType.INTEGER, new ClassType(Integer.class, "null"), new ClassType(int.class, "0"));
        register(JDBCType.NUMERIC, new ClassType(Double.class, "null"), new ClassType(double.class, "0.0D"));
        register(JDBCType.SMALLINT, new ClassType(Short.class, "null"), new ClassType(short.class, "0"));
        register(JDBCType.TIME, new ClassType(Date.class, "null"));
        register(JDBCType.TIMESTAMP, new ClassType(Date.class, "null"));
        register(JDBCType.VARCHAR, new ClassType(String.class, "null"));
    }
}
