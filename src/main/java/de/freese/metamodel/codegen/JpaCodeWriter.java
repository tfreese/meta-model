/**
 * Created: 28.07.2018
 */

package de.freese.metamodel.codegen;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import org.apache.commons.lang3.StringUtils;
import de.freese.metamodel.Config;
import de.freese.metamodel.metagen.model.Column;
import de.freese.metamodel.metagen.model.Table;
import de.freese.metamodel.metagen.model.UniqueConstraint;

/**
 * Java-Implementierung für JPA-Entities eines {@link CodeWriter}.
 *
 * @author Thomas Freese
 */
public class JpaCodeWriter extends JavaCodeWriter
{
    /**
     * Erstellt ein neues {@link JpaCodeWriter} Object.
     */
    public JpaCodeWriter()
    {
        super();
    }

    /**
     * @see de.freese.metamodel.codegen.JavaCodeWriter#writeClassAnnotations(de.freese.metamodel.Config, java.io.PrintWriter,
     *      de.freese.metamodel.metagen.model.Table)
     */
    @Override
    protected void writeClassAnnotations(final Config config, final PrintWriter pw, final Table table) throws IOException
    {
        addImport(Entity.class);
        pw.println("@Entity");

        addImport(javax.persistence.Table.class);
        pw.print("@Table(");
        pw.printf("name = \"%s\"", table.getName());
        pw.printf(", schema = \"%s\"", table.getSchema().getName());

        if (!table.getUniqueConstraints().isEmpty())
        {
            pw.println();

            addImport(javax.persistence.UniqueConstraint.class);
            pw.println(TAB + ", uniqueConstraints = {");

            for (UniqueConstraint uc : table.getUniqueConstraints())
            {
                pw.print(TAB + TAB + "@UniqueConstraint(name = \"" + uc.getName() + "\", columnNames = {");

                for (Iterator<Column> iterator = uc.getColumnsOrdered().iterator(); iterator.hasNext();)
                {
                    pw.print("\"" + iterator.next().getName() + "\"");

                    if (iterator.hasNext())
                    {
                        pw.print(", ");
                    }
                }

                pw.println("})");
            }

            pw.println(TAB + "}");
        }

        pw.println(")");

        addImport(NamedNativeQuery.class);
        String className = config.getNamingStrategy().getClassName(table.getName());
        String alias = className.substring(0, 1).toLowerCase();

        pw.printf("@NamedNativeQuery(name = \"all%s.native\"", StringUtils.capitalize(className));
        pw.printf(", query = \"select %2$s.* from %1$s %2$s\")%n", table.getName(), alias);

        super.writeClassAnnotations(config, pw, table);
    }

    /**
     * @see de.freese.metamodel.codegen.JavaCodeWriter#writeFieldAnnotations(de.freese.metamodel.Config, java.io.PrintWriter,
     *      de.freese.metamodel.metagen.model.Column)
     */
    @Override
    protected void writeFieldAnnotations(final Config config, final PrintWriter pw, final Column column) throws IOException
    {
        // ID
        if (column.getTable().getPrimaryKey().getColumnsOrdered().contains(column))
        {
            addImport(Id.class);
            pw.println(TAB + "@Id");
        }

        // TODO Composite PrimaryKeys !

        // Column
        addImport(javax.persistence.Column.class);
        pw.print(TAB + "@Column(");
        pw.print("name = \"" + column.getName() + "\"");
        pw.print(", nullable = " + column.isNullable());

        if (column.getTable().getPrimaryKey().getColumnsOrdered().contains(column))
        {
            pw.print(", unique = true");
        }

        if (column.hasSize())
        {
            pw.print(", length = " + column.getSize());
        }

        pw.println(")");

        // Access
        addImport(Access.class);
        addImport(AccessType.class);
        pw.println(TAB + "@Access(AccessType.FIELD)");

        super.writeFieldAnnotations(config, pw, column);
    }
}
