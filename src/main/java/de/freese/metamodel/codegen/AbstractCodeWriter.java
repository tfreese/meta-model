/**
 * Created: 29.07.2018
 */

package de.freese.metamodel.codegen;

import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.annotation.processing.Generated;
import org.apache.commons.lang3.StringUtils;
import de.freese.metamodel.Config;
import de.freese.metamodel.metagen.model.Column;
import de.freese.metamodel.metagen.model.Table;
import de.freese.metamodel.modelgen.ClassModel;
import de.freese.metamodel.modelgen.FieldModel;
import de.freese.metamodel.modelgen.mapping.Type;

/**
 * Java-Implementierung eines {@link CodeWriter}.
 *
 * @author Thomas Freese
 */
public abstract class AbstractCodeWriter implements CodeWriter
{
    /**
    *
    */
    protected static final String TAB = "    ";

    /**
     * Erstellt ein neues {@link AbstractCodeWriter} Object.
     */
    public AbstractCodeWriter()
    {
        super();
    }

    /**
     * @param config {@link Config}
     * @param classModel {@link ClassModel}
     */
    protected abstract void createClassAnnotations(Config config, final ClassModel classModel);

    /**
     * @param config {@link Config}
     * @param classModel {@link ClassModel}
     */
    protected abstract void createClassJavaDoc(Config config, final ClassModel classModel);

    /**
     * @param config {@link Config}
     * @param classModel {@link ClassModel}
     */
    protected abstract void createFields(Config config, final ClassModel classModel);

    /**
     * Liefert die Dateiendung.
     *
     * @return String
     */
    protected abstract String getFileExtension();

    /**
     * @see de.freese.metamodel.codegen.CodeWriter#write(de.freese.metamodel.Config, de.freese.metamodel.metagen.model.Table)
     */
    @Override
    public void write(final Config config, final Table table) throws Exception
    {
        ClassModel classModel = new ClassModel(table);
        classModel.setName(config.getNamingStrategy().getClassName(table.getName()));

        if (config.isSerializeable())
        {
            classModel.addInterface(Serializable.class);
        }

        if (config.isAddFullConstructor())
        {
            // Für Validierung.
            classModel.addImport(Objects.class);
        }

        createClassJavaDoc(config, classModel);
        createClassAnnotations(config, classModel);
        createFields(config, classModel);

        // Default Class-Annotation.
        classModel.addImport(Generated.class);
        classModel.addAnnotation("@Generated(\"" + getClass().getName() + "\")");

        // Datei schreiben.
        String packageDirectory = config.getPackageName().replace(".", "/");
        Path folder = config.getTargetFolder().resolve(packageDirectory);

        if (!Files.exists(folder))
        {
            Files.createDirectories(folder);
        }

        String fileName = config.getNamingStrategy().getClassName(table.getName()) + getFileExtension();
        Path path = folder.resolve(fileName);

        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(path, StandardCharsets.UTF_8)))
        {
            // Package
            pw.printf("// Created: %1$tY-%1$tm-%1$td %1$tH.%1$tM.%1$tS,%1$tL%n", new Date());
            pw.printf("package %s;%n", config.getPackageName());

            // Imports
            pw.println();
            classModel.getImports().forEach(i -> pw.printf("import %s;%n", i));

            // Class-JavaDoc
            pw.println();
            writeJavaDoc(pw, classModel.getComments(), "");

            // Class-Annotations
            classModel.getAnnotations().forEach(i -> pw.println(i));

            pw.print("public class " + classModel.getName());

            // Interfaces
            if (!classModel.getInterfaces().isEmpty())
            {
                pw.print(" implements ");

                StringJoiner joiner = new StringJoiner(", ");
                classModel.getInterfaces().forEach(i -> joiner.add(i.getSimpleName()));

                pw.print(joiner.toString());
            }

            pw.println();
            pw.println("{");

            // Serializeable
            // UUID uuid = UUID.randomUUID();
            // long oid = (uuid.getMostSignificantBits() >> 32) ^ uuid.getMostSignificantBits();
            // oid ^= (uuid.getLeastSignificantBits() >> 32) ^ uuid.getLeastSignificantBits();
            long oid = (config.getPackageName() + "." + classModel.getName()).hashCode();

            writeJavaDoc(pw, null, TAB);
            pw.printf(TAB + "private static final long serialVersionUID = %dL;%n", oid);

            // Fields
            for (FieldModel fieldModel : classModel.getFields())
            {
                Type type = fieldModel.getType();

                pw.println();
                writeJavaDoc(pw, fieldModel.getComments(), TAB);
                fieldModel.getAnnotations().forEach(i -> pw.println(TAB + i));
                pw.printf(TAB + "private %s %s = %s;%n", type.getSimpleName(), fieldModel.getName(), type.getDefaultValueAsString());
            }

            // Konstruktor
            writeConstructor(config, pw, classModel);

            // Methods
            writeMethods(config, pw, classModel);

            // hashCode
            writeHashcode(config, pw, classModel);

            // equals
            writeEquals(config, pw, classModel);

            // toString
            writeToString(config, pw, classModel);

            pw.println("}");
        }
    }

    /**
     * @param config {@link Config}
     * @param pw {@link PrintWriter}
     * @param classModel {@link ClassModel}
     */
    protected void writeConstructor(final Config config, final PrintWriter pw, final ClassModel classModel)
    {
        pw.println();
        writeJavaDoc(pw, Arrays.asList("Default Constructor"), TAB);

        pw.println(TAB + "public " + classModel.getName() + "()");
        pw.println(TAB + "{");
        pw.println(TAB + TAB + "super();");
        pw.println(TAB + "}");

        if (config.isAddFullConstructor())
        {
            pw.println();
            writeJavaDoc(pw, Arrays.asList("Full Constructor"), TAB, w -> {
                for (FieldModel fieldModel : classModel.getFields())
                {
                    pw.printf(TAB + " * @param %s %s%n", fieldModel.getName(), fieldModel.getType().getSimpleName());
                }
            });

            pw.print(TAB + "public " + classModel.getName() + "(");

            for (Iterator<FieldModel> iterator = classModel.getFields().iterator(); iterator.hasNext();)
            {
                FieldModel fieldModel = iterator.next();

                pw.print(fieldModel.getType().getSimpleName() + " " + fieldModel.getName());

                if (iterator.hasNext())
                {
                    pw.print(", ");
                }
            }

            pw.println(")");
            pw.println(TAB + "{");
            pw.println(TAB + TAB + "super();");
            pw.println();

            for (FieldModel fieldModel : classModel.getFields())
            {
                if (fieldModel.getColumn().isNullable())
                {
                    pw.printf(TAB + TAB + "this.%1$s = %1$s;%n", fieldModel.getName());
                }
                else
                {
                    pw.printf(TAB + TAB + "this.%1$s = Objects.requireNonNull(%1$s, \"not null value: %1$s required\");%n", fieldModel.getName());
                }
            }

            pw.println(TAB + "}");
        }
    }

    /**
     * @param config {@link Config}
     * @param pw {@link PrintWriter}
     * @param classModel {@link ClassModel}
     */
    protected void writeEquals(final Config config, final PrintWriter pw, final ClassModel classModel)
    {
        String className = classModel.getName();

        pw.println();
        writeJavaDoc(pw, Arrays.asList("@see java.lang.Object#equals(java.lang.Object)"), TAB);
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

        for (FieldModel fieldModel : classModel.getFields())
        {
            String fieldName = fieldModel.getName();
            Type type = fieldModel.getType();

            pw.println();

            if (type.isPrimitive())
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
     * @param config {@link Config}
     * @param pw {@link PrintWriter}
     * @param classModel {@link ClassModel}
     */
    protected void writeHashcode(final Config config, final PrintWriter pw, final ClassModel classModel)
    {
        pw.println();
        writeJavaDoc(pw, Arrays.asList("@see java.lang.Object#hashCode()"), TAB);
        pw.println(TAB + "@Override");
        pw.println(TAB + "public int hashCode()");
        pw.println(TAB + "{");

        pw.println(TAB + TAB + "final int prime = 31;");
        pw.println(TAB + TAB + "int result = 1;");

        // double vorhanden ?
        for (FieldModel fieldModel : classModel.getFields())
        {
            Type type = fieldModel.getType();

            if (type.equals(double.class))
            {
                pw.println(TAB + TAB + "long temp = 0L;");
                break;
            }
        }

        pw.println();

        for (FieldModel fieldModel : classModel.getFields())
        {
            String fieldName = fieldModel.getName();
            Type type = fieldModel.getType();

            if (type.equals(int.class))
            {
                pw.println(TAB + TAB + "result = prime * result + this." + fieldName + ";");
            }
            else if (type.equals(long.class))
            {
                pw.printf(TAB + TAB + "result = prime * result + (int) (this.%1$s ^ (this.%1$s >>> 32));%n", fieldName);
            }
            else if (type.equals(double.class))
            {
                pw.println(TAB + TAB + "temp = Double.doubleToLongBits(this." + fieldName + ");");
                pw.println(TAB + TAB + "result = prime * result + (int) (temp ^ (temp >>> 32));");
            }
            else if (type.equals(float.class))
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
     * @param pw {@link PrintWriter}
     * @param comments {@link List}
     * @param indent String
     */
    protected void writeJavaDoc(final PrintWriter pw, final List<String> comments, final String indent)
    {
        writeJavaDoc(pw, comments, indent, null);
    }

    /**
     * @param pw {@link PrintWriter}
     * @param comments {@link List}
     * @param indent String
     * @param paramsOrReturn {@link Consumer}
     */
    protected void writeJavaDoc(final PrintWriter pw, final List<String> comments, final String indent, final Consumer<PrintWriter> paramsOrReturn)
    {
        pw.println(indent + "/**");

        if ((comments == null) || comments.isEmpty())
        {
            // pw.println(indent + " *");
        }
        else
        {
            for (Iterator<String> iterator = comments.iterator(); iterator.hasNext();)
            {
                String comment = iterator.next();

                pw.print(indent + " * " + comment);

                if (iterator.hasNext())
                {
                    pw.print("<br>");
                }

                pw.println();
            }

            if (paramsOrReturn != null)
            {
                pw.println(indent + " *");
            }
        }

        if (paramsOrReturn != null)
        {
            paramsOrReturn.accept(pw);
        }

        pw.println(indent + " */");
    }

    /**
     * @param config {@link Config}
     * @param pw {@link PrintWriter}
     * @param classModel {@link ClassModel}
     */
    protected void writeMethods(final Config config, final PrintWriter pw, final ClassModel classModel)
    {
        for (FieldModel fieldModel : classModel.getFields())
        {
            String name = fieldModel.getName();
            String typeName = fieldModel.getType().getSimpleName();

            // Setter
            pw.println();
            writeJavaDoc(pw, fieldModel.getComments(), TAB, w -> w.println(TAB + " * @param " + name + " " + typeName));

            pw.println(TAB + "public void set" + StringUtils.capitalize(name) + "(" + typeName + " " + name + ")");
            pw.println(TAB + "{");

            // if (fieldModel.getColumn().isNullable())
            {
                pw.printf(TAB + TAB + "this.%1$s = %1$s;%n", name);
            }
            // else
            // {
            // pw.printf(TAB + TAB + "this.%1$s = Objects.requireNonNull(%1$s, \"not null value: %1$s required\");%n", fieldName);
            // }

            pw.println(TAB + "}");

            // Getter
            pw.println();
            writeJavaDoc(pw, fieldModel.getComments(), TAB, w -> w.println(TAB + " * @return " + typeName));

            pw.println(TAB + "public " + typeName + " get" + StringUtils.capitalize(name) + "()");
            pw.println(TAB + "{");
            pw.println(TAB + TAB + "return this." + name + ";");
            pw.println(TAB + "}");
        }
    }

    /**
     * @param config {@link Config}
     * @param pw {@link PrintWriter}
     * @param classModel {@link ClassModel}
     */
    protected void writeToString(final Config config, final PrintWriter pw, final ClassModel classModel)
    {
        Table table = classModel.getTable();
        List<Column> columns = null;

        // Finde alle Columns des PrimaryKeys.
        if (table.getPrimaryKey() != null)
        {
            columns = table.getPrimaryKey().getColumnsOrdered();
        }

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

        List<FieldModel> fields = new ArrayList<>();

        for (FieldModel fieldModel : classModel.getFields())
        {
            if (columns.contains(fieldModel.getColumn()))
            {
                fields.add(fieldModel);
            }
        }

        String className = classModel.getName();

        pw.println();
        writeJavaDoc(pw, Arrays.asList("@see java.lang.Object#toString()"), TAB);
        pw.println(TAB + "@Override");
        pw.println(TAB + "public String toString()");
        pw.println(TAB + "{");

        pw.println(TAB + TAB + "StringBuilder sb = new StringBuilder();");
        pw.println(TAB + TAB + "sb.append(\"" + className + " [\");");

        for (Iterator<FieldModel> iterator = fields.iterator(); iterator.hasNext();)
        {
            FieldModel fieldModel = iterator.next();

            String fieldName = fieldModel.getName();
            Type type = fieldModel.getType();

            if (type.isArray())
            {
                pw.printf(TAB + TAB + "sb.append(\"%1$s = \").append(Arrays.toString(this.%1$s))", fieldName);
            }
            else
            {
                pw.printf(TAB + TAB + "sb.append(\"%1$s = \").append(this.%1$s)", fieldName);
            }

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
