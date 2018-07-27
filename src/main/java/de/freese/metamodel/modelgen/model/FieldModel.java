/**
 * Created: 27.07.2018
 */

package de.freese.metamodel.modelgen.model;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import org.apache.commons.lang3.StringUtils;
import de.freese.metamodel.metagen.model.Column;
import de.freese.metamodel.modelgen.mapping.Type;

/**
 * Model eines Klassen-Attributs.
 *
 * @author Thomas Freese
 */
public class FieldModel
{
    /**
     *
     */
    private final Map<Class<? extends Annotation>, Map<String, Object>> annotations = new TreeMap<>(Comparator.comparing(Class::getSimpleName));

    /**
     *
     */
    private final ClassModel classModel;

    /**
    *
    */
    private final Column column;

    /**
    *
    */
    private final List<String> comments = new ArrayList<>();

    /**
    *
    */
    private String name = null;

    /**
     *
     */
    private Type type = null;

    /**
     * Erstellt ein neues {@link FieldModel} Object.
     *
     * @param classModel {@link ClassModel}
     * @param column {@link Column}
     */
    public FieldModel(final ClassModel classModel, final Column column)
    {
        super();

        this.classModel = Objects.requireNonNull(classModel, "classModel required");
        this.column = Objects.requireNonNull(column, "column required");
    }

    /**
     * Fügt einem Attribut eine Annotation hinzu.
     *
     * @param annotationClass {@link Class}
     * @param annotationValues {@link Map}
     */
    public void addAnnotation(final Class<? extends Annotation> annotationClass, final Map<String, Object> annotationValues)
    {
        Map<String, Object> values = this.annotations.computeIfAbsent(annotationClass, key -> new TreeMap<>());

        // // Default Werte der Annotation auslesen.
        // for (Method method : annotationType.getDeclaredMethods())
        // {
        // values.put(method.getName(), method.getDefaultValue());
        // }

        // Definierte Werte setzen.
        if (annotationValues != null)
        {
            values.putAll(annotationValues);
        }

        getClassModel().addImport(annotationClass.getTypeName());

        // org.hibernate.annotations.common.annotationfactory.AnnotationFactory
        // Annotation a = sun.reflect.annotation.AnnotationParser.annotationForMap(annotationType, values);
    }

    /**
     * Fügt dem Attribut Kommentare hinzu.
     *
     * @param line String
     * @param lines String[]
     */
    public void addComment(final String line, final String...lines)
    {
        if (StringUtils.isNotBlank(line))
        {
            this.comments.add(line);
        }

        for (String l : lines)
        {
            if (StringUtils.isNotBlank(l))
            {
                this.comments.add(l);
            }
        }
    }

    /**
     * @return Map<Class<? extends Annotation>,Map<String,Object>>
     */
    public Map<Class<? extends Annotation>, Map<String, Object>> getAnnotations()
    {
        return this.annotations;
    }

    /**
     * @return {@link ClassModel}
     */
    public ClassModel getClassModel()
    {
        return this.classModel;
    }

    /**
     * @return {@link Column}
     */
    public Column getColumn()
    {
        return this.column;
    }

    /**
     * Liefert alle Kommentare.
     *
     * @return {@link List}
     */
    public List<String> getComments()
    {
        return this.comments;
    }

    /**
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @return {@link Type}
     */
    public Type getType()
    {
        return this.type;
    }

    /**
     * @param name String
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * @param type {@link Type}
     */
    public void setType(final Type type)
    {
        this.type = type;

        getClassModel().addImport(type.getTypeClass().getName());
    }
}
