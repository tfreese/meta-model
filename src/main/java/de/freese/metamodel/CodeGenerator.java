/**
 * Created: 26.07.2018
 */

package de.freese.metamodel;

import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.metamodel.codegen.CodeWriter;
import de.freese.metamodel.codegen.JavaCodeWriter;
import de.freese.metamodel.metagen.MetaExporter;
import de.freese.metamodel.metagen.model.Schema;
import de.freese.metamodel.metagen.model.Table;
import de.freese.metamodel.modelgen.ClassModelGenerator;
import de.freese.metamodel.modelgen.DefaultClassModelGenerator;
import de.freese.metamodel.modelgen.ModelConfig;
import de.freese.metamodel.modelgen.mapping.TypeMapping;
import de.freese.metamodel.modelgen.model.ClassModel;
import de.freese.metamodel.modelgen.naming.NamingStrategy;

/**
 * @author Thomas Freese
 */
public class CodeGenerator
{
    /**
     *
     */
    private ClassModelGenerator classModelGenerator = new DefaultClassModelGenerator();

    /**
       *
       */
    private CodeWriter codeWriter = null;

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
    private ModelConfig modelConfig = new ModelConfig();

    /**
    *
    */
    private String schemaName = null;

    /**
    *
    */
    private String tableNamePattern = null;

    /**
    *
    */
    private Path targetFolder = null;

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

        // Erzeugen des ClassModels
        Objects.requireNonNull(this.modelConfig.getNamingStrategy(), "namingStrategy required");
        Objects.requireNonNull(this.modelConfig.getPackageName(), "packageName required");
        Objects.requireNonNull(this.modelConfig.getTypeMapping(), "typeMapping required");
        Objects.requireNonNull(this.classModelGenerator, "classModelGenerator required");

        List<ClassModel> models = new ArrayList<>();

        for (Table table : schema.getTables())
        {
            ClassModel model = this.classModelGenerator.generate(this.modelConfig, table);
            models.add(model);
        }

        // Code schreiben
        Objects.requireNonNull(this.targetFolder, "targetFolder required");
        Objects.requireNonNull(this.codeWriter, "codeWriter required");

        String packageDirectory = this.modelConfig.getPackageName().replace(".", "/");
        Path folder = this.targetFolder.resolve(packageDirectory);

        if (!Files.exists(folder))
        {
            Files.createDirectories(folder);
        }

        for (ClassModel model : models)
        {
            Path pathModel = folder.resolve(generateFileName(model));

            try (PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(pathModel, StandardCharsets.UTF_8)))
            {
                this.codeWriter.write(printWriter, model);
            }
        }
    }

    /**
     * Liefert den Dateinamen des {@link ClassModel}.
     *
     * @param model {@link ClassModel}
     * @return String
     */
    protected String generateFileName(final ClassModel model)
    {
        String fileName = null;

        if (this.codeWriter instanceof JavaCodeWriter)
        {
            fileName = model.getName() + ".java";
        }
        else
        {
            throw new IllegalStateException("no fileName for unkown CodeWriter: " + this.codeWriter.getClass().getSimpleName());
        }

        return fileName;
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
        this.modelConfig.setAddFullConstructor(addFullConstructor);
    }

    /**
     * @param classModelGenerator {@link ClassModelGenerator}
     */
    public void setClassModelGenerator(final ClassModelGenerator classModelGenerator)
    {
        this.classModelGenerator = classModelGenerator;
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
        this.modelConfig.setNamingStrategy(namingStrategy);
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
        this.modelConfig.setPackageName(packageName);
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
        this.modelConfig.setSerializeable(serializeable);
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
        this.targetFolder = targetFolder;
    }

    /**
     * @param typeMapping {@link TypeMapping}
     */
    public void setTypeMapping(final TypeMapping typeMapping)
    {
        this.modelConfig.setTypeMapping(typeMapping);
    }

    /**
     * Erzeugt die javax.validation.* Annotations.
     *
     * @param validationAnnotations boolean
     */
    public void setValidationAnnotations(final boolean validationAnnotations)
    {
        this.modelConfig.setValidationAnnotations(validationAnnotations);
    }
}
