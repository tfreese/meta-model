/**
 * Created: 28.07.2018
 */

package de.freese.metamodel.codegen;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.JDBCType;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.processing.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;
import de.freese.metamodel.Config;
import de.freese.metamodel.metagen.model.Column;
import de.freese.metamodel.metagen.model.Table;
import de.freese.metamodel.modelgen.mapping.Type;

/**
 * Java-Implementierung eines {@link CodeWriter}.
 *
 * @author Thomas Freese
 */
public class JavaCodeWriter extends AbstractCodeWriter
{
    /**
     * Erstellt ein neues {@link JavaCodeWriter} Object.
     */
    public JavaCodeWriter()
    {
        super();
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#getFileExtension()
     */
    @Override
    protected String getFileExtension()
    {
        return ".java";
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeClassAnnotations(de.freese.metamodel.Config, java.io.PrintWriter,
     *      de.freese.metamodel.metagen.model.Table)
     */
    @Override
    protected void writeClassAnnotations(final Config config, final PrintWriter pw, final Table table) throws IOException
    {
        addImport(Generated.class);
        pw.printf("@Generated(\"%s\")%n", getClass().getName());
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeClassEnd(de.freese.metamodel.Config, java.io.PrintWriter,
     *      de.freese.metamodel.metagen.model.Table)
     */
    @Override
    protected void writeClassEnd(final Config config, final PrintWriter pw, final Table table) throws IOException
    {
        pw.println("}");
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeClassJavaDoc(de.freese.metamodel.Config, java.io.PrintWriter,
     *      de.freese.metamodel.metagen.model.Table)
     */
    @Override
    protected void writeClassJavaDoc(final Config config, final PrintWriter pw, final Table table) throws IOException
    {
        pw.println();

        pw.println("/**");

        if (StringUtils.isNotBlank(table.getComment()))
        {
            pw.println(" * " + table.getComment() + "<br>");
        }

        pw.printf(" * Pojo für Tabelle %s.%s.%n", table.getSchema().getName(), table.getName());

        pw.println("*/");
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeClassStart(de.freese.metamodel.Config, java.io.PrintWriter,
     *      de.freese.metamodel.metagen.model.Table)
     */
    @Override
    protected void writeClassStart(final Config config, final PrintWriter pw, final Table table) throws IOException
    {
        String className = config.getNamingStrategy().getClassName(table.getName());

        pw.print("public class " + className);

        if (config.isSerializeable())
        {
            addImport(Serializable.class);
            pw.println(" implements " + Serializable.class.getSimpleName());
        }

        pw.println("{");

        if (config.isSerializeable())
        {
            // UUID uuid = UUID.randomUUID();
            // long oid = (uuid.getMostSignificantBits() >> 32) ^ uuid.getMostSignificantBits();
            // oid ^= (uuid.getLeastSignificantBits() >> 32) ^ uuid.getLeastSignificantBits();
            long oid = (config.getPackageName() + "." + className).hashCode();

            pw.println(TAB + "/**");
            pw.println(TAB + " *");
            pw.println(TAB + " */");
            pw.printf(TAB + "private static final long serialVersionUID = %dL;%n", oid);
        }
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeConstructor(de.freese.metamodel.Config, java.io.PrintWriter,
     *      de.freese.metamodel.metagen.model.Table)
     */
    @Override
    protected void writeConstructor(final Config config, final PrintWriter pw, final Table table) throws IOException
    {
        String className = config.getNamingStrategy().getClassName(table.getName());

        pw.println();
        pw.println(TAB + "/**");
        pw.println(TAB + " * Default Constructor");
        pw.println(TAB + " */");

        pw.println(TAB + "public " + className + "()");
        pw.println(TAB + "{");
        pw.println(TAB + TAB + "super();");
        pw.println(TAB + "}");

        if (config.isAddFullConstructor())
        {
            pw.println();
            pw.println(TAB + "/**");
            pw.println(TAB + " * Full Constructor");
            pw.println(TAB + " * ");

            for (Column column : table.getColumnsOrdered())
            {
                String fieldName = config.getNamingStrategy().getFieldName(column.getName());
                Type type = config.getTypeMapping().getType(column.geJdbcType(), column.isNullable());

                pw.printf(TAB + " * @param %s %s%n", fieldName, type.getTypeClass().getSimpleName());
            }

            pw.println(TAB + " */");

            pw.print(TAB + "public " + className + "(");

            for (Iterator<Column> iterator = table.getColumnsOrdered().iterator(); iterator.hasNext();)
            {
                Column column = iterator.next();

                String fieldName = config.getNamingStrategy().getFieldName(column.getName());
                Type type = config.getTypeMapping().getType(column.geJdbcType(), column.isNullable());

                pw.print(type.getTypeClass().getSimpleName() + " " + fieldName);

                if (iterator.hasNext())
                {
                    pw.print(", ");
                }
            }

            pw.println(")");
            pw.println(TAB + "{");
            pw.println(TAB + TAB + "super();");
            pw.println();

            for (Column column : table.getColumnsOrdered())
            {
                String fieldName = config.getNamingStrategy().getFieldName(column.getName());

                if (column.isNullable())
                {
                    pw.printf(TAB + TAB + "this.%1$s = %1$s;%n", fieldName);
                }
                else
                {
                    addImport(Objects.class);
                    pw.printf(TAB + TAB + "this.%1$s = Objects.requireNonNull(%1$s, \"not null value: %1$s required\");%n", fieldName);
                }
            }

            pw.println(TAB + "}");
        }
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeEquals(de.freese.metamodel.Config, java.io.PrintWriter, de.freese.metamodel.metagen.model.Table)
     */
    @Override
    protected void writeEquals(final Config config, final PrintWriter pw, final Table table) throws IOException
    {
        String className = config.getNamingStrategy().getClassName(table.getName());

        pw.println();
        pw.println(TAB + "/**");
        pw.println(TAB + " * @see java.lang.Object#equals(java.lang.Object)");
        pw.println(TAB + " */");
        pw.println(TAB + "@Override");
        pw.println(TAB + "public boolean equals(final Object obj)");
        pw.println(TAB + "{");

        pw.println(TAB + TAB + "if (this == obj)");
        pw.println(TAB + TAB + "{");
        pw.println(TAB + TAB + TAB + "return true;");
        pw.println(TAB + TAB + "}");

        pw.println();
        pw.println(TAB + TAB + "if (obj == null)");
        pw.println(TAB + TAB + "{");
        pw.println(TAB + TAB + TAB + "return false;");
        pw.println(TAB + TAB + "}");

        pw.println();
        pw.println(TAB + TAB + "if (getClass() != obj.getClass())");
        pw.println(TAB + TAB + "{");
        pw.println(TAB + TAB + TAB + "return false;");
        pw.println(TAB + TAB + "}");

        pw.println();
        pw.printf(TAB + TAB + "%1$s other = (%1$s) obj;%n", className);

        for (Column column : table.getColumnsOrdered())
        {
            String fieldName = config.getNamingStrategy().getFieldName(column.getName());
            Type type = config.getTypeMapping().getType(column.geJdbcType(), column.isNullable());

            pw.println();

            if (type.getTypeClass().isPrimitive())
            {
                pw.printf(TAB + TAB + "if (this.%1$s != other.%1$s)%n", fieldName);
                pw.println(TAB + TAB + "{");
                pw.println(TAB + TAB + TAB + "return false;");
                pw.println(TAB + TAB + "}");
            }
            else
            {
                pw.println(TAB + TAB + "if (this." + fieldName + " == null)");
                pw.println(TAB + TAB + "{");
                pw.println(TAB + TAB + TAB + "if (other." + fieldName + " == null)");
                pw.println(TAB + TAB + TAB + "{");
                pw.println(TAB + TAB + TAB + TAB + "return false;");
                pw.println(TAB + TAB + TAB + "}");
                pw.println(TAB + TAB + "}");
                pw.printf(TAB + TAB + "else if(!this.%1$s.equals(other.%1$s))%n", fieldName);
                pw.println(TAB + TAB + "{");
                pw.println(TAB + TAB + TAB + "return false;");
                pw.println(TAB + TAB + "}");
            }
        }

        pw.println();
        pw.println(TAB + TAB + "return true;");
        pw.println(TAB + "}");
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeFieldAnnotations(de.freese.metamodel.Config, java.io.PrintWriter,
     *      de.freese.metamodel.metagen.model.Column)
     */
    @Override
    protected void writeFieldAnnotations(final Config config, final PrintWriter pw, final Column column) throws IOException
    {
        // Validation Annotations
        if (config.isValidationAnnotations())
        {
            if (!column.isNullable())
            {
                // @NotNull
                addImport(NotNull.class);
                pw.println(TAB + "@" + NotNull.class.getSimpleName());
            }

            if (JDBCType.VARCHAR.equals(column.geJdbcType()))
            {
                // @Size(max=50)
                addImport(Size.class);
                pw.println(TAB + "@" + Size.class.getSimpleName() + "(max = " + column.getSize() + ")");
            }
        }
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeFieldJavaDoc(de.freese.metamodel.Config, java.io.PrintWriter,
     *      de.freese.metamodel.metagen.model.Column)
     */
    @Override
    protected void writeFieldJavaDoc(final Config config, final PrintWriter pw, final Column column) throws IOException
    {
        pw.println();
        pw.println(TAB + "/**");

        if (StringUtils.isNotBlank(column.getComment()))
        {
            pw.println(TAB + " * " + column.getComment() + "<br>");
        }
        else
        {
            pw.println(TAB + " *");
        }

        pw.println(TAB + " */");
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeFields(de.freese.metamodel.Config, java.io.PrintWriter, de.freese.metamodel.metagen.model.Table)
     */
    @Override
    protected void writeFields(final Config config, final PrintWriter pw, final Table table) throws IOException
    {
        for (Column column : table.getColumnsOrdered())
        {
            writeFieldJavaDoc(config, pw, column);
            writeFieldAnnotations(config, pw, column);

            String fieldName = config.getNamingStrategy().getFieldName(column.getName());
            Type type = config.getTypeMapping().getType(column.geJdbcType(), column.isNullable());

            pw.printf(TAB + "private %s %s = %s;%n", type.getTypeClass().getSimpleName(), fieldName, type.getDefaultValueAsString());
        }
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeHashcode(de.freese.metamodel.Config, java.io.PrintWriter,
     *      de.freese.metamodel.metagen.model.Table)
     */
    @Override
    protected void writeHashcode(final Config config, final PrintWriter pw, final Table table) throws IOException
    {
        pw.println();
        pw.println(TAB + "/**");
        pw.println(TAB + " * @see java.lang.Object#hashCode()");
        pw.println(TAB + " */");
        pw.println(TAB + "@Override");
        pw.println(TAB + "public int hashCode()");
        pw.println(TAB + "{");

        pw.println(TAB + TAB + "final int prime = 31;");
        pw.println(TAB + TAB + "int result = 1;");

        // double vorhanden ?
        for (Column column : table.getColumnsOrdered())
        {
            Type type = config.getTypeMapping().getType(column.geJdbcType(), column.isNullable());

            if (double.class.equals(type.getTypeClass()))
            {
                pw.println(TAB + TAB + "long temp = 0L;");
                break;
            }
        }

        pw.println();

        for (Column column : table.getColumnsOrdered())
        {
            String fieldName = config.getNamingStrategy().getFieldName(column.getName());
            Type type = config.getTypeMapping().getType(column.geJdbcType(), column.isNullable());

            if (int.class.equals(type.getTypeClass()))
            {
                pw.println(TAB + TAB + "result = prime * result + this." + fieldName + ";");
            }
            else if (long.class.equals(type.getTypeClass()))
            {
                pw.printf(TAB + TAB + "result = prime * result + (int) (this.%1$s ^ (this.%1$s >>> 32));%n", fieldName);
            }
            else if (double.class.equals(type.getTypeClass()))
            {
                pw.println(TAB + TAB + "temp = Double.doubleToLongBits(this." + fieldName + ");");
                pw.println(TAB + TAB + "result = prime * result + (int) (temp ^ (temp >>> 32));");
            }
            else if (float.class.equals(type.getTypeClass()))
            {
                pw.println(TAB + TAB + "result = prime * result + Float.floatToIntBits(this." + fieldName + ");");
            }
            else
            {
                pw.printf(TAB + TAB + "result = prime * result + ((this.%1$s == null) ? 0 : this.%1$s.hashCode());%n", fieldName);
            }
        }

        pw.println();
        pw.println(TAB + TAB + "return result;");
        pw.println(TAB + "}");
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeImports(de.freese.metamodel.Config, java.io.PrintWriter,
     *      de.freese.metamodel.metagen.model.Table)
     */
    @Override
    protected void writeImports(final Config config, final PrintWriter pw, final Table table) throws IOException
    {
        pw.println();

        // @formatter:off
        getImports().stream()
            .filter(i -> !i.toLowerCase().startsWith("long"))
            .filter(i -> !i.toLowerCase().startsWith("byte[]"))
            .filter(i -> !i.toLowerCase().startsWith("boolean"))
            .filter(i -> !i.toLowerCase().startsWith("char"))
            .filter(i -> !i.toLowerCase().startsWith("double"))
            .filter(i -> !i.toLowerCase().startsWith("float"))
            .filter(i -> !i.toLowerCase().startsWith("int"))
            .filter(i -> !i.toLowerCase().startsWith("short"))
            .filter(i -> !i.toLowerCase().startsWith("java.lang.string"))
            .filter(i -> !i.toLowerCase().startsWith("java.lang.class"))
            .forEach(i -> pw.printf("import %s;%n", i));
        // @formatter:on
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeMethods(de.freese.metamodel.Config, java.io.PrintWriter,
     *      de.freese.metamodel.metagen.model.Table)
     */
    @Override
    protected void writeMethods(final Config config, final PrintWriter pw, final Table table) throws IOException
    {
        for (Column column : table.getColumnsOrdered())
        {
            String fieldName = config.getNamingStrategy().getFieldName(column.getName());
            Type type = config.getTypeMapping().getType(column.geJdbcType(), column.isNullable());

            String typeSimpleName = type.getTypeClass().getSimpleName();

            // Setter
            pw.println();
            pw.println(TAB + "/**");
            pw.println(TAB + " * @param " + fieldName + " " + typeSimpleName);
            pw.println(TAB + " */");
            pw.println(TAB + "public void set" + StringUtils.capitalize(fieldName) + "(" + typeSimpleName + " " + fieldName + ")");
            pw.println(TAB + "{");

            // if (fieldModel.getColumn().isNullable())
            {
                pw.printf(TAB + TAB + "this.%1$s = %1$s;%n", fieldName);
            }
            // else
            // {
            // pw.printf(TAB + TAB + "this.%1$s = Objects.requireNonNull(%1$s, \"not null value: %1$s required\");%n", fieldName);
            // }

            pw.println(TAB + "}");

            // Getter
            pw.println();
            pw.println(TAB + "/**");
            pw.println(TAB + " * @return " + typeSimpleName);
            pw.println(TAB + " */");
            pw.println(TAB + "public " + typeSimpleName + " get" + StringUtils.capitalize(fieldName) + "()");
            pw.println(TAB + "{");
            pw.println(TAB + TAB + "return this." + fieldName + ";");
            pw.println(TAB + "}");
        }
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writePackage(de.freese.metamodel.Config, java.io.PrintWriter,
     *      de.freese.metamodel.metagen.model.Table)
     */
    @Override
    protected void writePackage(final Config config, final PrintWriter pw, final Table table) throws IOException
    {
        pw.printf("// Created: %1$tY-%1$tm-%1$td %1$tH.%1$tM.%1$tS,%1$tL%n", new Date());
        pw.printf("package %s;%n", config.getPackageName());
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeToString(de.freese.metamodel.Config, java.io.PrintWriter,
     *      de.freese.metamodel.metagen.model.Table)
     */
    @Override
    protected void writeToString(final Config config, final PrintWriter pw, final Table table) throws IOException
    {
        // Finde alle Columns des PrimaryKeys.
        List<Column> columns = table.getPrimaryKey().getColumnsOrdered();

        if (columns.isEmpty())
        {
            // Finde alle Columns mit UniqueConstraints.
            // @formatter:off
            columns = table.getUniqueConstraints().stream()
                    .flatMap(uc -> uc.getColumnsOrdered().stream())
                    .sorted(Comparator.comparing(Column::getTableIndex))
                    .collect(Collectors.toList());
            // @formatter:on
        }

        if (columns.isEmpty())
        {
            // Finde alle Columns mit ForeignKeys.
            // @formatter:off
            columns = table.getColumnsOrdered().stream()
                    .filter(c -> c.getForeignKey() != null)
                    .map(c -> c.getForeignKey().getColumn())
                    .sorted(Comparator.comparing(Column::getTableIndex))
                    .collect(Collectors.toList());
            // @formatter:on
        }

        if (columns.isEmpty())
        {
            // Alle Columns.
            columns = table.getColumnsOrdered();
        }

        String className = config.getNamingStrategy().getClassName(table.getName());

        pw.println();
        pw.println(TAB + "/**");
        pw.println(TAB + " * @see java.lang.Object#toString()");
        pw.println(TAB + " */");
        pw.println(TAB + "@Override");
        pw.println(TAB + "public String toString()");
        pw.println(TAB + "{");

        pw.println(TAB + TAB + "StringBuilder sb = new StringBuilder();");
        pw.println(TAB + TAB + "sb.append(\"" + className + " [\");");

        for (Iterator<Column> iterator = columns.iterator(); iterator.hasNext();)
        {
            Column column = iterator.next();

            String fieldName = config.getNamingStrategy().getFieldName(column.getName());

            pw.printf(TAB + TAB + "sb.append(\"%1$s = \").append(this.%1$s)", fieldName);

            if (iterator.hasNext())
            {
                pw.println(".append(\", \");");
            }
            else
            {
                pw.println(";");
            }
        }

        pw.println(TAB + TAB + "sb.append(\"]\");");

        pw.println();
        pw.println(TAB + TAB + "return sb.toString();");
        pw.println(TAB + "}");
    }
}
