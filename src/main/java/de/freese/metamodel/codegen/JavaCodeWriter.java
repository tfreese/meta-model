/**
 * Created: 26.07.2018
 */

package de.freese.metamodel.codegen;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import de.freese.metamodel.modelgen.mapping.Type;
import de.freese.metamodel.modelgen.model.ClassModel;
import de.freese.metamodel.modelgen.model.FieldModel;

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
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeClassAnnotations(java.io.PrintWriter, de.freese.metamodel.modelgen.model.ClassModel)
     */
    @Override
    protected void writeClassAnnotations(final PrintWriter pw, final ClassModel model) throws IOException
    {
        // pw.printf("@Generated(\"%s\")%n", getClass().getName());

        Map<Class<? extends Annotation>, Map<String, Object>> annotations = model.getClassAnnotations();

        annotations.forEach((annotation, values) -> {
            writeAnnotation(pw, annotation, values);
        });
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeClassEnd(java.io.PrintWriter, de.freese.metamodel.modelgen.model.ClassModel)
     */
    @Override
    protected void writeClassEnd(final PrintWriter pw, final ClassModel model) throws IOException
    {
        pw.println("}");
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeClassJavaDoc(java.io.PrintWriter, de.freese.metamodel.modelgen.model.ClassModel)
     */
    @Override
    protected void writeClassJavaDoc(final PrintWriter pw, final ClassModel model) throws IOException
    {
        pw.println();

        pw.println("/**");

        if (!model.getClassComments().isEmpty())
        {
            model.getClassComments().forEach(line -> pw.println(" * " + line + "<br>"));
        }

        pw.printf(" * Pojo für Tabelle %s.%s.%n", model.getTable().getSchema().getName(), model.getTable().getName());

        pw.println("*/");
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeClassStart(java.io.PrintWriter, de.freese.metamodel.modelgen.model.ClassModel)
     */
    @Override
    protected void writeClassStart(final PrintWriter pw, final ClassModel model) throws IOException
    {
        pw.print("public class " + model.getName());

        List<Class<?>> interfaces = model.getInterfaces();

        if (!interfaces.isEmpty())
        {
            pw.print(" implements");

            interfaces.forEach(i -> pw.print(" " + i.getSimpleName()));

            pw.println();
        }

        pw.println("{");

        if (interfaces.contains(Serializable.class))
        {
            // UUID uuid = UUID.randomUUID();
            // long oid = (uuid.getMostSignificantBits() >> 32) ^ uuid.getMostSignificantBits();
            // oid ^= (uuid.getLeastSignificantBits() >> 32) ^ uuid.getLeastSignificantBits();
            long oid = (model.getPackageName() + "." + model.getName()).hashCode();

            pw.println(TAB + "/**");
            pw.println(TAB + " *");
            pw.println(TAB + " */");
            pw.printf(TAB + "private static final long serialVersionUID = %dL;%n", oid);
        }
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeConstructor(java.io.PrintWriter, de.freese.metamodel.modelgen.model.ClassModel)
     */
    @Override
    protected void writeConstructor(final PrintWriter pw, final ClassModel model) throws IOException
    {
        pw.println();
        pw.println(TAB + "/**");
        pw.println(TAB + " * Default Constructor");
        pw.println(TAB + " */");

        pw.println(TAB + "public " + model.getName() + "()");
        pw.println(TAB + "{");
        pw.println(TAB + TAB + "super();");
        pw.println(TAB + "}");

        if (model.isAddFullConstructor())
        {
            pw.println();
            pw.println(TAB + "/**");
            pw.println(TAB + " * Full Constructor");
            pw.println(TAB + " * ");

            for (FieldModel fieldModel : model.getFields())
            {
                pw.printf(TAB + " * @param %s %s%n", fieldModel.getName(), fieldModel.getType().getTypeClass().getSimpleName());
            }

            pw.println(TAB + " */");

            pw.print(TAB + "public " + model.getName() + "(");

            for (Iterator<FieldModel> iterator = model.getFields().iterator(); iterator.hasNext();)
            {
                FieldModel fieldModel = iterator.next();

                pw.print(fieldModel.getType().getTypeClass().getSimpleName() + " " + fieldModel.getName());

                if (iterator.hasNext())
                {
                    pw.print(", ");
                }
            }

            pw.println(")");
            pw.println(TAB + "{");
            pw.println(TAB + TAB + "super();");
            pw.println();

            for (FieldModel fieldModel : model.getFields())
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
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeEquals(java.io.PrintWriter, de.freese.metamodel.modelgen.model.ClassModel)
     */
    @Override
    protected void writeEquals(final PrintWriter pw, final ClassModel model) throws IOException
    {
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
        pw.printf(TAB + TAB + "%1$s other = (%1$s) obj;%n", model.getName());

        for (FieldModel fieldModel : model.getFields())
        {
            Type type = fieldModel.getType();
            pw.println();

            if (type.getTypeClass().isPrimitive())
            {
                pw.printf(TAB + TAB + "if (this.%1$s != other.%1$s)%n", fieldModel.getName());
                pw.println(TAB + TAB + "{");
                pw.println(TAB + TAB + TAB + "return false;");
                pw.println(TAB + TAB + "}");
            }
            else
            {
                pw.println(TAB + TAB + "if (this." + fieldModel.getName() + " == null)");
                pw.println(TAB + TAB + "{");
                pw.println(TAB + TAB + TAB + "if (other." + fieldModel.getName() + " == null)");
                pw.println(TAB + TAB + TAB + "{");
                pw.println(TAB + TAB + TAB + TAB + "return false;");
                pw.println(TAB + TAB + TAB + "}");
                pw.println(TAB + TAB + "}");
                pw.printf(TAB + TAB + "else if(!this.%1$s.equals(other.%1$s))%n", fieldModel.getName());
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
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeFieldAnnotations(java.io.PrintWriter, de.freese.metamodel.modelgen.model.FieldModel)
     */
    @Override
    protected void writeFieldAnnotations(final PrintWriter pw, final FieldModel fieldModel) throws IOException
    {
        Map<Class<? extends Annotation>, Map<String, Object>> annotations = fieldModel.getAnnotations();

        annotations.forEach((annotation, values) -> {
            pw.print(TAB);
            writeAnnotation(pw, annotation, values);
        });
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeFieldJavaDoc(java.io.PrintWriter, de.freese.metamodel.modelgen.model.FieldModel)
     */
    @Override
    protected void writeFieldJavaDoc(final PrintWriter pw, final FieldModel fieldModel) throws IOException
    {
        pw.println();
        pw.println(TAB + "/**");

        List<String> comments = fieldModel.getComments();

        if (!comments.isEmpty())
        {
            comments.forEach(line -> pw.println(TAB + " * " + line + "<br>"));
        }
        else
        {
            pw.println(TAB + " *");
        }

        pw.println(TAB + " */");
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeFields(java.io.PrintWriter, de.freese.metamodel.modelgen.model.ClassModel)
     */
    @Override
    protected void writeFields(final PrintWriter pw, final ClassModel model) throws IOException
    {
        for (FieldModel fieldModel : model.getFields())
        {
            writeFieldJavaDoc(pw, fieldModel);
            writeFieldAnnotations(pw, fieldModel);

            Type type = fieldModel.getType();

            pw.printf(TAB + "private %s %s = %s;%n", type.getTypeClass().getSimpleName(), fieldModel.getName(), type.getDefaultValueAsString());
        }
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeHashcode(java.io.PrintWriter, de.freese.metamodel.modelgen.model.ClassModel)
     */
    @Override
    protected void writeHashcode(final PrintWriter pw, final ClassModel model) throws IOException
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
        for (FieldModel fieldModel : model.getFields())
        {
            if (double.class.equals(fieldModel.getType().getTypeClass()))
            {
                pw.println(TAB + TAB + "long temp = 0L;");
                break;
            }
        }

        pw.println();

        for (FieldModel fieldModel : model.getFields())
        {
            Type type = fieldModel.getType();

            if (int.class.equals(type.getTypeClass()))
            {
                pw.println(TAB + TAB + "result = prime * result + this." + fieldModel.getName() + ";");
            }
            else if (long.class.equals(type.getTypeClass()))
            {
                pw.printf(TAB + TAB + "result = prime * result + (int) (this.%1$s ^ (this.%1$s >>> 32));%n", fieldModel.getName());
            }
            else if (double.class.equals(type.getTypeClass()))
            {
                pw.println(TAB + TAB + "temp = Double.doubleToLongBits(this." + fieldModel.getName() + ");");
                pw.println(TAB + TAB + "result = prime * result + (int) (temp ^ (temp >>> 32));");
            }
            else if (float.class.equals(type.getTypeClass()))
            {
                pw.println(TAB + TAB + "result = prime * result + Float.floatToIntBits(this." + fieldModel.getName() + ");");
            }
            else
            {
                pw.printf(TAB + TAB + "result = prime * result + ((this.%1$s == null) ? 0 : this.%1$s.hashCode());%n", fieldModel.getName());
            }
        }

        pw.println();
        pw.println(TAB + TAB + "return result;");
        pw.println(TAB + "}");
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeImports(java.io.PrintWriter, de.freese.metamodel.modelgen.model.ClassModel)
     */
    @Override
    protected void writeImports(final PrintWriter pw, final ClassModel model) throws IOException
    {
        pw.println();

        // @formatter:off
        model.getImports().stream()
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
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeMethods(java.io.PrintWriter, de.freese.metamodel.modelgen.model.ClassModel)
     */
    @Override
    protected void writeMethods(final PrintWriter pw, final ClassModel model) throws IOException
    {
        for (FieldModel fieldModel : model.getFields())
        {
            String fieldName = fieldModel.getName();
            String typeSimpleName = fieldModel.getType().getTypeClass().getSimpleName();

            // Setter
            pw.println();
            pw.println(TAB + "/**");
            pw.println(TAB + " * @param " + fieldName + " " + typeSimpleName);
            pw.println(TAB + " */");
            pw.println(TAB + "public void set" + StringUtils.capitalize(fieldName) + "(" + typeSimpleName + " " + fieldName + ")");
            pw.println(TAB + "{");

            if (fieldModel.getColumn().isNullable())
            {
                pw.printf(TAB + TAB + "this.%1$s = %1$s;%n", fieldName);
            }
            else
            {
                pw.printf(TAB + TAB + "this.%1$s = Objects.requireNonNull(%1$s, \"not null value: %1$s required\");%n", fieldName);
            }

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
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writePackage(java.io.PrintWriter, de.freese.metamodel.modelgen.model.ClassModel)
     */
    @Override
    protected void writePackage(final PrintWriter pw, final ClassModel model) throws IOException
    {
        pw.printf("// Created: %1$tY-%1$tm-%1$td %1$tH.%1$tM.%1$tS,%1$tL%n", new Date());
        pw.printf("package %s;%n", model.getPackageName());
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeToString(java.io.PrintWriter, de.freese.metamodel.modelgen.model.ClassModel)
     */
    @Override
    protected void writeToString(final PrintWriter pw, final ClassModel model) throws IOException
    {
        // Finde alle Attribute des PrimaryKeys.
        List<FieldModel> fields = model.getPrimaryKeyFields();

        if (fields.isEmpty())
        {
            // Finde alle Attribute mit UniqueConstraints.
            fields = model.getUniqueConstraintFields();
        }

        if (fields.isEmpty())
        {
            // Finde alle Attribute mit ForeignKeys.
            fields = model.getForeignKeyFields();
        }

        if (fields.isEmpty())
        {
            // Alle Attribute.
            fields = model.getFields();
        }

        pw.println();
        pw.println(TAB + "/**");
        pw.println(TAB + " * @see java.lang.Object#toString()");
        pw.println(TAB + " */");
        pw.println(TAB + "@Override");
        pw.println(TAB + "public String toString()");
        pw.println(TAB + "{");

        pw.println(TAB + TAB + "StringBuilder sb = new StringBuilder();");
        pw.println(TAB + TAB + "sb.append(\"" + model.getName() + " [\");");

        for (Iterator<FieldModel> iterator = fields.iterator(); iterator.hasNext();)
        {
            FieldModel fieldModel = iterator.next();
            pw.printf(TAB + TAB + "sb.append(\"%1$s = \").append(this.%1$s)", fieldModel.getName());

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
