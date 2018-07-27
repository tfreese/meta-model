/**
 * Created: 26.07.2018
 */

package de.freese.metamodel.modelgen;

import de.freese.metamodel.modelgen.mapping.TypeMapping;
import de.freese.metamodel.modelgen.model.ClassModel;
import de.freese.metamodel.modelgen.naming.DefaultNamingStrategy;
import de.freese.metamodel.modelgen.naming.NamingStrategy;

/**
 * Konfiguration für die {@link ClassModel}-Erzeugung.
 *
 * @author Thomas Freese
 */
public class ModelConfig
{
    /**
    *
    */
    private boolean addFullConstructor = false;

    /**
    *
    */
    private NamingStrategy namingStrategy = new DefaultNamingStrategy();

    /**
    *
    */
    private String packageName = null;

    /**
    *
    */
    private boolean serializeable = false;

    /**
    *
    */
    private TypeMapping typeMapping = null;

    /**
    *
    */
    private boolean validationAnnotations = false;

    /**
     * Erstellt ein neues {@link ModelConfig} Object.
     */
    public ModelConfig()
    {
        super();
    }

    /**
     * @return {@link NamingStrategy}
     */
    public NamingStrategy getNamingStrategy()
    {
        return this.namingStrategy;
    }

    /**
     * @return String
     */
    public String getPackageName()
    {
        return this.packageName;
    }

    /**
     * @return {@link TypeMapping}
     */
    public TypeMapping getTypeMapping()
    {
        return this.typeMapping;
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
     * @return boolean
     */
    public boolean isSerializeable()
    {
        return this.serializeable;
    }

    /**
     * true = javax.validation.constraints.* Annotations mit einbauen.
     *
     * @return boolean
     */
    public boolean isValidationAnnotations()
    {
        return this.validationAnnotations;
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
     * @param namingStrategy {@link NamingStrategy}
     */
    public void setNamingStrategy(final NamingStrategy namingStrategy)
    {
        this.namingStrategy = namingStrategy;
    }

    /**
     * @param packageName String
     */
    public void setPackageName(final String packageName)
    {
        this.packageName = packageName;
    }

    /**
     * @param serializeable boolean
     */
    public void setSerializeable(final boolean serializeable)
    {
        this.serializeable = serializeable;
    }

    /**
     * @param typeMapping {@link TypeMapping}
     */
    public void setTypeMapping(final TypeMapping typeMapping)
    {
        this.typeMapping = typeMapping;
    }

    /**
     * true = javax.validation.constraints.* Annotations mit einbauen.
     *
     * @param validationAnnotations boolean
     */
    public void setValidationAnnotations(final boolean validationAnnotations)
    {
        this.validationAnnotations = validationAnnotations;
    }
}
