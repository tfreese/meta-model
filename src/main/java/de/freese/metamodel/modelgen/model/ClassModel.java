/**
 * Created: 22.07.2018
 */

package de.freese.metamodel.modelgen.model;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;
import de.freese.metamodel.metagen.model.Table;
import de.freese.metamodel.modelgen.mapping.Type;

/**
 * Model einer Klasse.
 *
 * @author Thomas Freese
 */
public class ClassModel
{
    /**
    *
    */
    private boolean addFullConstructor = false;

    /**
    *
    */
    private final Map<Class<? extends Annotation>, Map<String, Object>> classAnnotations = new TreeMap<>(Comparator.comparing(Class::getSimpleName));

    /**
    *
    */
    private final List<String> classComments = new ArrayList<>();

    /**
    *
    */
    private final List<FieldModel> fields = new ArrayList<>();

    /**
     *
     */
    private final Set<String> imports = new TreeSet<>();

    /**
    *
    */
    private final Set<Class<?>> interfaces = new TreeSet<>(Comparator.comparing(Class::getSimpleName));

    /**
     *
     */
    private String name = null;

    /**
     *
     */
    private String packageName = null;

    /**
     *
     */
    private final Table table;

    /**
     * Erstellt ein neues {@link ClassModel} Object.
     *
     * @param table {@link Table}
     */
    public ClassModel(final Table table)
    {
        super();

        this.table = Objects.requireNonNull(table, "table required");
    }

    /**
     * Fügt der Klasse eine Annotation hinzu.
     *
     * @param annotationClass Class
     * @param annotationValues {@link Map}
     */
    public void addClassAnnotation(final Class<? extends Annotation> annotationClass, final Map<String, Object> annotationValues)
    {
        Map<String, Object> values = getClassAnnotations().computeIfAbsent(annotationClass, key -> new TreeMap<>());

        // // Default Werte der Annotation auslesen.
        // for (Method method : annotationClass.getDeclaredMethods())
        // {
        // values.put(method.getName(), method.getDefaultValue());
        // }

        // Definierte Werte setzen.
        values.putAll(annotationValues);

        this.imports.add(annotationClass.getName());

        // org.hibernate.annotations.common.annotationfactory.AnnotationFactory
        // Annotation a = sun.reflect.annotation.AnnotationParser.annotationForMap(annotationType, values);
    }

    /**
     * Fügt der Klasse Kommentare hinzu.
     *
     * @param line String
     * @param lines String[]
     */
    public void addClassComment(final String line, final String...lines)
    {
        if (StringUtils.isNotBlank(line))
        {
            this.classComments.add(line);
        }

        for (String l : lines)
        {
            if (StringUtils.isNotBlank(l))
            {
                this.classComments.add(l);
            }
        }
    }

    /**
     * Fügt ein Attribut der Klasse hinzu.
     *
     * @param fieldModel {@link FieldModel}
     */
    public void addField(final FieldModel fieldModel)
    {
        this.fields.add(fieldModel);
    }

    /**
     * Fügt einen Klassen-Namen den Imports hinzu.
     *
     * @param className String
     */
    public void addImport(final String className)
    {
        this.imports.add(className);
    }

    /**
     * Fügt ein Interface der Klasse hinzu.
     *
     * @param ifClass {@link Class}
     */
    public void addInterface(final Class<?> ifClass)
    {
        this.interfaces.add(ifClass);
        this.imports.add(ifClass.getName());
    }

    /**
     * Liefert die Annotations der Klasse.
     *
     * @return {@link Map}
     */
    public Map<Class<? extends Annotation>, Map<String, Object>> getClassAnnotations()
    {
        Map<Class<? extends Annotation>, Map<String, Object>> annotationMap = this.classAnnotations;

        return annotationMap;
    }

    /**
     * Liefert die Klassen-Kommentare.
     *
     * @return List<String>
     */
    public List<String> getClassComments()
    {
        return this.classComments;
    }

    /**
     * Liefert des Typ des Attributs.
     *
     * @param fieldName String
     * @return {@link Type}
     */
    public FieldModel getField(final String fieldName)
    {
        return this.fields.stream().filter(f -> f.getName().equals(fieldName)).findFirst().get();
    }

    /**
     * Liefert alle Attribut-Namen.
     *
     * @return {@link List}<String>
     */
    public List<FieldModel> getFields()
    {
        return new ArrayList<>(this.fields);
    }

    /**
     * Liefert alle Klassen-Namen für den Import.
     *
     * @return {@link List}<String>
     */
    public List<String> getImports()
    {
        return new ArrayList<>(this.imports);
    }

    /**
     * Liefert die definierten Interfaces der Klasse.
     *
     * @return {@link List}
     */
    public List<Class<?>> getInterfaces()
    {
        return new ArrayList<>(this.interfaces);
    }

    /**
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return String
     */
    public String getPackageName()
    {
        return this.packageName;
    }

    /**
     * @return {@link Table}
     */
    public Table getTable()
    {
        return this.table;
    }

    /**
     * Konstruktor mit allen Parametern einbauen.
     *
     * @return boolean
     */
    public boolean isAddFullConstructor()
    {
        return this.addFullConstructor;
    }

    /**
     * Konstruktor mit allen Parametern einbauen.
     *
     * @param addFullConstructor boolean
     */
    public void setAddFullConstructor(final boolean addFullConstructor)
    {
        this.addFullConstructor = addFullConstructor;
    }

    /**
     * @param name String
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * @param packageName String
     */
    public void setPackageName(final String packageName)
    {
        this.packageName = packageName;
    }
}
