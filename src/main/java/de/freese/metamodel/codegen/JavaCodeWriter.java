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
        pw.print(String.format("public class %s", model.getName()));

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
            pw.printf("%sprivate static final long serialVersionUID = %dL;%n", TAB, oid);
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
                pw.printf(TAB + TAB + "this.%s = %s;%n", fieldModel.getName(), fieldModel.getName());
            }

            pw.println(TAB + "}");
        }
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

            pw.printf("%sprivate %s %s = %s;%n", TAB, type.getTypeClass().getSimpleName(), fieldModel.getName(), type.getDefaultValueAsString());
        }
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#writeHashcodeEquals(java.io.PrintWriter, de.freese.metamodel.modelgen.model.ClassModel)
     */
    @Override
    protected void writeHashcodeEquals(final PrintWriter pw, final ClassModel model) throws IOException
    {
        // TODO Auto-generated method stub
        // @formatter:off
//        /**
//         * @see java.lang.Object#hashCode()
//         */
//        @Override
//        public int hashCode()
//        {
//        final int prime = 31;
//        int result = 1;
//        result = prime * result + ((this.id_Double == null) ? 0 : this.id_Double.hashCode());
//        result = prime * result + ((this.id_Float == null) ? 0 : this.id_Float.hashCode());
//        result = prime * result + ((this.id_Integer == null) ? 0 : this.id_Integer.hashCode());
//        result = prime * result + ((this.id_Long == null) ? 0 : this.id_Long.hashCode());
//
//        long temp;
//        temp = Double.doubleToLongBits(this.id_double);
//        result = prime * result + (int) (temp ^ (temp >>> 32));
//        result = prime * result + Float.floatToIntBits(this.id_float);
//        result = prime * result + this.id_int;
//        result = prime * result + (int) (this.id_long ^ (this.id_long >>> 32));
//        result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
//        result = prime * result + ((this.vorname == null) ? 0 : this.vorname.hashCode());
//        return result;
//        }
//
//        /**
//         * @see java.lang.Object#equals(java.lang.Object)
//         */
//        @Override
//        public boolean equals(final Object obj)
//        {
//            if (this == obj)
//            {
//                return true;
//            }
//
//            if (obj == null)
//            {
//                return false;
//            }
//
//            if (getClass() != obj.getClass())
//            {
//                return false;
//            }
//
//            Person other = (Person) obj;
//
//            if (this.id != other.id)
//            {
//                return false;
//            }
//
//            if (this.name == null)
//            {
//                if (other.name != null)
//                {
//                    return false;
//                }
//            }
//            else if (!this.name.equals(other.name))
//            {
//                return false;
//            }
//
//            if (this.vorname == null)
//            {
//                if (other.vorname != null)
//                {
//                    return false;
//                }
//            }
//            else if (!this.vorname.equals(other.vorname))
//            {
//                return false;
//            }
//
//            return true;
//        }

        // @formatter:on
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
            pw.printf(TAB + " * @param %s %s%n", fieldName, typeSimpleName);
            pw.println(TAB + " */");
            pw.printf(TAB + "public void set%s(%s %s)%n", StringUtils.capitalize(fieldName), typeSimpleName, fieldName);
            pw.println(TAB + "{");
            pw.printf(TAB + TAB + "this.%s = %s;%n", fieldName, fieldName);
            pw.println(TAB + "}");

            // Getter
            pw.println();
            pw.println(TAB + "/**");
            pw.printf(TAB + " * @return %s%n", typeSimpleName);
            pw.println(TAB + " */");
            pw.printf(TAB + "public %s get%s()%n", typeSimpleName, StringUtils.capitalize(fieldName));
            pw.println(TAB + "{");
            pw.printf(TAB + TAB + "return this.%s;%n", fieldName);
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
        // TODO Auto-generated method stub
    }
}
