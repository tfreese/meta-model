/**
 * Created: 29.07.2018
 */

package de.freese.metamodel.modelgen;

import java.util.ArrayList;
import java.util.List;

/**
 * Basis-Implementierung eines Model-Objekts.
 *
 * @author Thomas Freese
 */
public abstract class AbstractModel
{
    /**
    *
    */
    private final List<String> annotations = new ArrayList<>();

    /**
    *
    */
    private final List<String> comments = new ArrayList<>();

    /**
    *
    */
    public String name = null;

    /**
     * Erstellt ein neues {@link AbstractModel} Object.
     */
    public AbstractModel()
    {
        super();
    }

    /**
     * @param annotation String
     */
    public void addAnnotation(final String annotation)
    {
        this.annotations.add(annotation);
    }

    /**
     * @param comment String
     */
    public void addComment(final String comment)
    {
        this.comments.add(comment);
    }

    /**
     * @return {@link List}
     */
    public List<String> getAnnotations()
    {
        return new ArrayList<>(this.annotations);
    }

    /**
     * @return {@link List}
     */
    public List<String> getComments()
    {
        return new ArrayList<>(this.comments);
    }

    /**
     * @return String
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * @param name String
     */
    public void setName(final String name)
    {
        this.name = name;
    }
}
