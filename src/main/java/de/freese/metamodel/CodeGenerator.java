/**
 * Created: 26.07.2018
 */

package de.freese.metamodel;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Objects;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.metamodel.codegen.CodeWriter;
import de.freese.metamodel.metagen.MetaExporter;
import de.freese.metamodel.metagen.model.Schema;
import de.freese.metamodel.metagen.model.Table;
import de.freese.metamodel.modelgen.mapping.TypeMapping;
import de.freese.metamodel.modelgen.naming.NamingStrategy;

/**
 * @author Thomas Freese
 */
public class CodeGenerator
{
    /**
    *
    */
    private CodeWriter codeWriter = null;

    /**
     *
     */
    private Config config = new Config();

    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
    *
    */
    private MetaExporter metaExporter = null;

    /**
    *
    */
    private String schemaName = null;

    /**
    *
    */
    private String tableNamePattern = null;

    /**
     * Erstellt ein neues {@link CodeGenerator} Object.
     */
    public CodeGenerator()
    {
        super();
    }

    /**
     * Erzeugt den Quellcode aus dem MetaModel.
     *
     * @param dataSource {@link DataSource}
     * @throws Exception Falls was schief geht.
     */
    public void generate(final DataSource dataSource) throws Exception
    {
        // Export MetaModel.
        Objects.requireNonNull(dataSource, "dataSource required");
        Objects.requireNonNull(this.schemaName, "schemaName required");
        Objects.requireNonNull(this.metaExporter, "metaExporter required");

        Schema schema = this.metaExporter.export(dataSource, this.schemaName, this.tableNamePattern);

        // Code schreiben
        Objects.requireNonNull(this.config.getTargetFolder(), "targetFolder required");
        Objects.requireNonNull(this.codeWriter, "codeWriter required");

        for (Table table : schema.getTables())
        {
            this.codeWriter.write(this.config, table);
        }
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }

    /**
     * Konstruktor mit allen Parametern einbauen.
     *
     * @param addFullConstructor boolean
     */
    public void setAddFullConstructor(final boolean addFullConstructor)
    {
        this.config.setAddFullConstructor(addFullConstructor);
    }

    /**
     * @param codeWriter {@link CodeWriter}
     */
    public void setCodeWriter(final CodeWriter codeWriter)
    {
        this.codeWriter = codeWriter;
    }

    /**
     * @param metaExporter {@link MetaExporter}
     */
    public void setMetaExporter(final MetaExporter metaExporter)
    {
        this.metaExporter = metaExporter;
    }

    /**
     * @param namingStrategy {@link NamingStrategy}
     */
    public void setNamingStrategy(final NamingStrategy namingStrategy)
    {
        this.config.setNamingStrategy(namingStrategy);
    }

    /**
     * Packagename der generierten Klassen.<br>
     * Wird im TargetFolder entsprechend angelegt.<br>
     *
     * @see #setTargetFolder(Path)
     * @param packageName String
     */
    public void setPackageName(final String packageName)
    {
        this.config.setPackageName(packageName);
    }

    /**
     * @param schemaName String
     */
    public void setSchemaName(final String schemaName)
    {
        this.schemaName = schemaName;
    }

    /**
     * Lässt das Objekt {@link Serializable} implementieren.
     *
     * @param serializeable boolean
     */
    public void setSerializeable(final boolean serializeable)
    {
        this.config.setSerializeable(serializeable);
    }

    /**
     * @param tableNamePattern String
     */
    public void setTableNamePattern(final String tableNamePattern)
    {
        this.tableNamePattern = tableNamePattern;
    }

    /**
     * Verzeichnis in die Klassen generiert werden.<br>
     *
     * @see #setPackageName(String)
     * @param targetFolder {@link Path}
     */
    public void setTargetFolder(final Path targetFolder)
    {
        this.config.setTargetFolder(targetFolder);
    }

    /**
     * @param typeMapping {@link TypeMapping}
     */
    public void setTypeMapping(final TypeMapping typeMapping)
    {
        this.config.setTypeMapping(typeMapping);
    }

    /**
     * Erzeugt die javax.validation.* Annotations.
     *
     * @param validationAnnotations boolean
     */
    public void setValidationAnnotations(final boolean validationAnnotations)
    {
        this.config.setValidationAnnotations(validationAnnotations);
    }
}
