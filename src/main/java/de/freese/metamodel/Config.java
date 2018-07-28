/**
 * Created: 26.07.2018
 */

package de.freese.metamodel;

import java.nio.file.Path;
import de.freese.metamodel.modelgen.mapping.TypeMapping;
import de.freese.metamodel.modelgen.naming.DefaultNamingStrategy;
import de.freese.metamodel.modelgen.naming.NamingStrategy;

/**
 * Konfiguration für die Code-Erzeugung.
 *
 * @author Thomas Freese
 */
public class Config
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
    private Path targetFolder = null;

    /**
    *
    */
    private TypeMapping typeMapping = null;

    /**
    *
    */
    private boolean validationAnnotations = false;

    /**
     * Erstellt ein neues {@link Config} Object.
     */
    public Config()
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
     * Verzeichnis in die Klassen generiert werden.<br>
     *
     * @return {@link Path}
     */
    public Path getTargetFolder()
    {
        return this.targetFolder;
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
     * Verzeichnis in die Klassen generiert werden.<br>
     *
     * @see #setPackageName(String)
     * @param targetFolder {@link Path}
     */
    public void setTargetFolder(final Path targetFolder)
    {
        this.targetFolder = targetFolder;
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
