/**
 * Created: 25.07.2018
 */

package de.freese.metamodel.modelgen.mapping;

/**
 * @author Thomas Freese
 */
public interface Type
{
    /**
     * @param clazz {@link Class}
     * @return boolean
     */
    public boolean equals(Class<?> clazz);

    /**
     * @return String
     */
    public String getSimpleName();

    /**
     * @return boolean
     */
    public boolean isArray();

    /**
     * @return boolean
     */
    public boolean isAssoziation();

    /**
     * @return boolean
     */
    public boolean isCollection();

    /**
     * @return boolean
     */
    public boolean isPrimitive();
}
