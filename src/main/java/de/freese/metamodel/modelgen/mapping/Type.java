/**
 * Created: 25.07.2018
 */

package de.freese.metamodel.modelgen.mapping;

import java.util.Objects;

/**
 * @author Thomas Freese
 */
public class Type
{
    /**
     *
     */
    private final String defaultValueAsString;

    /**
     *
     */
    private final Class<?> typeClass;

    /**
     * Erstellt ein neues {@link Type} Object.
     *
     * @param typeClass {@link Class}
     * @param defaultValueAsString String
     */
    public Type(final Class<?> typeClass, final String defaultValueAsString)
    {
        super();

        this.typeClass = Objects.requireNonNull(typeClass, "typeClass required");
        this.defaultValueAsString = Objects.requireNonNull(defaultValueAsString, "defaultValueAsString required");
    }

    /**
     * @return String
     */
    public String getDefaultValueAsString()
    {
        return this.defaultValueAsString;
    }

    /**
     * @return Class<?>
     */
    public Class<?> getTypeClass()
    {
        return this.typeClass;
    }
}
