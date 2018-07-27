/**
 * Created: 26.07.2018
 */

package de.freese.metamodel.codegen;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;
import javax.annotation.processing.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.metamodel.modelgen.model.ClassModel;
import de.freese.metamodel.modelgen.model.FieldModel;

/**
 * Basis-Implementierung eines {@link CodeWriter}.
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
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erstellt ein neues {@link AbstractCodeWriter} Object.
     */
    public AbstractCodeWriter()
    {
        super();
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @see de.freese.metamodel.codegen.CodeWriter#write(java.io.PrintWriter, de.freese.metamodel.modelgen.model.ClassModel)
     */
    @Override
    public void write(final PrintWriter pw, final ClassModel model) throws Exception
    {
        Map<String, Object> values = new TreeMap<>();
        values.put("value", getClass().getName());
        model.addClassAnnotation(Generated.class, values);

        writePackage(pw, model);
        writeImports(pw, model);
        writeClassJavaDoc(pw, model);
        writeClassAnnotations(pw, model);
        writeClassStart(pw, model);
        writeFields(pw, model);
        writeConstructor(pw, model);
        writeMethods(pw, model);
        writeHashcodeEquals(pw, model);
        writeToString(pw, model);
        writeClassEnd(pw, model);
    }

    /**
     * @param pw {@link PrintWriter}
     * @param annotation Class
     * @param values {@link Map}
     */
    protected void writeAnnotation(final PrintWriter pw, final Class<? extends Annotation> annotation, final Map<String, Object> values)
    {
        pw.print("@" + annotation.getSimpleName());

        if (!values.isEmpty())
        {
            pw.print("(");

            if ((values.size() == 1) && values.keySet().contains("value"))
            {
                // DefaultValue
                Object value = values.get("value");

                if (value instanceof CharSequence)
                {
                    pw.printf("\"%s\"", value);
                }
                else
                {
                    pw.printf("%s", value);
                }
            }
            else
            {
                StringJoiner joiner = new StringJoiner(", ");

                values.forEach((key, value) -> {
                    if (value instanceof CharSequence)
                    {
                        joiner.add(key + " =  \"" + value + "\"");
                    }
                    else
                    {
                        joiner.add(key + " = " + value);
                    }
                });

                pw.printf(joiner.toString());
            }

            pw.print(")");
        }

        pw.println();
    }

    /**
     * @param pw {@link PrintWriter}
     * @param model {@link ClassModel}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeClassAnnotations(final PrintWriter pw, final ClassModel model) throws IOException;

    /**
     * @param pw {@link PrintWriter}
     * @param model {@link ClassModel}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeClassEnd(final PrintWriter pw, final ClassModel model) throws IOException;

    /**
     * @param pw {@link PrintWriter}
     * @param model {@link ClassModel}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeClassJavaDoc(final PrintWriter pw, final ClassModel model) throws IOException;

    /**
     * @param pw {@link PrintWriter}
     * @param model {@link ClassModel}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeClassStart(final PrintWriter pw, final ClassModel model) throws IOException;

    /**
     * @param pw {@link PrintWriter}
     * @param model {@link ClassModel}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeConstructor(final PrintWriter pw, final ClassModel model) throws IOException;

    /**
     * @param pw {@link PrintWriter}
     * @param fieldModel {@link FieldModel}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeFieldAnnotations(final PrintWriter pw, final FieldModel fieldModel) throws IOException;

    /**
     * @param pw {@link PrintWriter}
     * @param fieldModel {@link FieldModel}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeFieldJavaDoc(final PrintWriter pw, final FieldModel fieldModel) throws IOException;

    /**
     * @param pw {@link PrintWriter}
     * @param model {@link ClassModel}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeFields(final PrintWriter pw, final ClassModel model) throws IOException;

    /**
     * @param pw {@link PrintWriter}
     * @param model {@link ClassModel}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeHashcodeEquals(final PrintWriter pw, final ClassModel model) throws IOException;

    /**
     * @param pw {@link PrintWriter}
     * @param model {@link ClassModel}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeImports(final PrintWriter pw, final ClassModel model) throws IOException;

    /**
     * @param pw {@link PrintWriter}
     * @param model {@link ClassModel}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeMethods(final PrintWriter pw, final ClassModel model) throws IOException;

    /**
     * @param pw {@link PrintWriter}
     * @param model {@link ClassModel}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writePackage(final PrintWriter pw, final ClassModel model) throws IOException;

    /**
     * @param pw {@link PrintWriter}
     * @param model {@link ClassModel}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeToString(final PrintWriter pw, final ClassModel model) throws IOException;
}
