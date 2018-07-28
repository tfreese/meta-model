/**
 * Created: 26.07.2018
 */

package de.freese.metamodel.codegen;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.metamodel.Config;
import de.freese.metamodel.metagen.model.Column;
import de.freese.metamodel.metagen.model.Table;

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
    private final Set<String> imports = new TreeSet<>();

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

    // /**
    // * Fügt der Klasse eine Annotation hinzu.
    // *
    // * @param annotationClass Class
    // * @param annotationValues {@link Map}
    // */
    // public void addAnnotation(final Class<? extends Annotation> annotationClass, final Map<String, Object> annotationValues)
    // {
    // Map<String, Object> values = this.annotations.computeIfAbsent(annotationClass, key -> new TreeMap<>());
    //
    // // // Default Werte der Annotation auslesen.
    // // for (Method method : annotationClass.getDeclaredMethods())
    // // {
    // // values.put(method.getName(), method.getDefaultValue());
    // // }
    //
    // // Definierte Werte setzen.
    // if (annotationValues != null)
    // {
    // values.putAll(annotationValues);
    // }
    //
    // this.imports.add(annotationClass.getName());
    //
    // // org.hibernate.annotations.common.annotationfactory.AnnotationFactory
    // // Annotation a = sun.reflect.annotation.AnnotationParser.annotationForMap(annotationType, values);
    // }

    /**
     * Fügt den Klassennamen zu den Imports hinzu.
     *
     * @param clazz {@link Class}
     */
    protected void addImport(final Class<?> clazz)
    {
        addImport(clazz.getName());
    }

    /**
     * Fügt den Klassennamen zu den Imports hinzu.
     *
     * @param className String
     */
    protected void addImport(final String className)
    {
        getImports().add(className);
    }

    /**
     * Liefert die Dateiendung.
     *
     * @return String
     */
    protected abstract String getFileExtension();

    /**
     * Liefert die Imports.
     *
     * @return {@link Set}
     */
    protected Set<String> getImports()
    {
        return this.imports;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @see de.freese.metamodel.codegen.CodeWriter#write(de.freese.metamodel.Config, de.freese.metamodel.metagen.model.Table)
     */
    @Override
    public void write(final Config config, final Table table) throws Exception
    {
        getImports().clear();

        StringWriter stringWriter = new StringWriter();
        PrintWriter stringPw = new PrintWriter(stringWriter);

        // Package und Import erst als letztes schreiben, da die Imports gesammelt werden müssen.
        writeClassJavaDoc(config, stringPw, table);
        writeClassAnnotations(config, stringPw, table);
        writeClassStart(config, stringPw, table);
        writeFields(config, stringPw, table);
        writeConstructor(config, stringPw, table);
        writeMethods(config, stringPw, table);
        writeHashcode(config, stringPw, table);
        writeEquals(config, stringPw, table);
        writeToString(config, stringPw, table);
        writeClassEnd(config, stringPw, table);

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
            writePackage(config, pw, table);
            writeImports(config, pw, table);

            pw.write(stringWriter.toString());
        }
    }

    // /**
    // * @param pw {@link PrintWriter}
    // * @param annotation Class
    // * @param values {@link Map}
    // */
    // protected void writeAnnotation(final PrintWriter pw, final Class<? extends Annotation> annotation, final Map<String, Object> values)
    // {
    // pw.print("@" + annotation.getSimpleName());
    //
    // if (!values.isEmpty())
    // {
    // pw.print("(");
    //
    // if ((values.size() == 1) && values.keySet().contains("value"))
    // {
    // // DefaultValue
    // Object value = values.get("value");
    //
    // if (value instanceof CharSequence)
    // {
    // pw.printf("\"%s\"", value);
    // }
    // else
    // {
    // pw.printf("%s", value);
    // }
    // }
    // else
    // {
    // StringJoiner joiner = new StringJoiner(", ");
    //
    // values.forEach((key, value) -> {
    // if (value instanceof CharSequence)
    // {
    // joiner.add(key + " = \"" + value + "\"");
    // }
    // else
    // {
    // joiner.add(key + " = " + value);
    // }
    // });
    //
    // pw.printf(joiner.toString());
    // }
    //
    // pw.print(")");
    // }
    //
    // pw.println();
    // }

    /**
     * @param config {@link Config}
     * @param pw {@link PrintWriter}
     * @param table {@link Table}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeClassAnnotations(Config config, final PrintWriter pw, final Table table) throws IOException;

    /**
     * @param config {@link Config}
     * @param pw {@link PrintWriter}
     * @param table {@link Table}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeClassEnd(Config config, final PrintWriter pw, final Table table) throws IOException;

    /**
     * @param config {@link Config}
     * @param pw {@link PrintWriter}
     * @param table {@link Table}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeClassJavaDoc(Config config, final PrintWriter pw, final Table table) throws IOException;

    /**
     * @param config {@link Config}
     * @param pw {@link PrintWriter}
     * @param table {@link Table}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeClassStart(Config config, final PrintWriter pw, final Table table) throws IOException;

    /**
     * @param config {@link Config}
     * @param pw {@link PrintWriter}
     * @param table {@link Table}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeConstructor(Config config, final PrintWriter pw, final Table table) throws IOException;

    /**
     * @param config {@link Config}
     * @param pw {@link PrintWriter}
     * @param table {@link Table}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeEquals(Config config, final PrintWriter pw, final Table table) throws IOException;

    /**
     * @param config {@link Config}
     * @param pw {@link PrintWriter}
     * @param column {@link Column}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeFieldAnnotations(Config config, final PrintWriter pw, final Column column) throws IOException;

    /**
     * @param config {@link Config}
     * @param pw {@link PrintWriter}
     * @param column {@link Column}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeFieldJavaDoc(Config config, final PrintWriter pw, final Column column) throws IOException;

    /**
     * @param config {@link Config}
     * @param pw {@link PrintWriter}
     * @param table {@link Table}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeFields(Config config, final PrintWriter pw, final Table table) throws IOException;

    /**
     * @param config {@link Config}
     * @param pw {@link PrintWriter}
     * @param table {@link Table}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeHashcode(Config config, final PrintWriter pw, final Table table) throws IOException;

    /**
     * @param config {@link Config}
     * @param pw {@link PrintWriter}
     * @param table {@link Table}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeImports(Config config, final PrintWriter pw, final Table table) throws IOException;

    /**
     * @param config {@link Config}
     * @param pw {@link PrintWriter}
     * @param table {@link Table}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeMethods(Config config, final PrintWriter pw, final Table table) throws IOException;

    /**
     * @param config {@link Config}
     * @param pw {@link PrintWriter}
     * @param table {@link Table}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writePackage(Config config, final PrintWriter pw, final Table table) throws IOException;

    /**
     * @param config {@link Config}
     * @param pw {@link PrintWriter}
     * @param table {@link Table}
     * @throws IOException Falls was schief geht.
     */
    protected abstract void writeToString(Config config, final PrintWriter pw, final Table table) throws IOException;
}
