/**
 * Created: 22.07.2018
 */

package de.freese.metamodel.modelgen.naming;

import org.apache.commons.lang3.StringUtils;

/**
 * Default-Implementierung der Namenskonvertierung.
 *
 * @author Thomas Freese
 */
public class DefaultNamingStrategy extends AbstractNamingStrategy
{
    /**
     * Erstellt ein neues {@link DefaultNamingStrategy} Object.
     */
    public DefaultNamingStrategy()
    {
        super();
    }

    /**
     * @see de.freese.metamodel.modelgen.naming.NamingStrategy#getClassName(java.lang.String)
     */
    @Override
    public String getClassName(final String tableName)
    {
        String tName = normalize(tableName);

        tName = toCamelCase(tName);

        return tName;
    }

    /**
     * @see de.freese.metamodel.modelgen.naming.NamingStrategy#getFieldName(java.lang.String)
     */
    @Override
    public String getFieldName(final String columnName)
    {
        String cName = normalize(columnName);

        cName = toCamelCase(cName);

        cName = StringUtils.uncapitalize(cName);

        return cName;
    }

    /**
     * @see de.freese.metamodel.modelgen.naming.AbstractNamingStrategy#normalize(java.lang.String)
     */
    @Override
    protected String normalize(final String value)
    {
        String s = super.normalize(value);

        if (s.startsWith("t_") || s.startsWith("tbl_"))
        {
            s = s.replaceFirst("t_", "").replaceFirst("tbl_", "");
        }

        return s;
    }
}
