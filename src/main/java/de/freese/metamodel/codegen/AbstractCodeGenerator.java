/**
 * Created: 22.04.2020
 */

package de.freese.metamodel.codegen;

import java.io.BufferedOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.JDBCType;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;
import de.freese.metamodel.codegen.model.ClassModel;
import de.freese.metamodel.codegen.model.FieldModel;
import de.freese.metamodel.codegen.writer.CodeWriter;
import de.freese.metamodel.metagen.MetaExporter;
import de.freese.metamodel.metagen.model.Column;
import de.freese.metamodel.metagen.model.Schema;
import de.freese.metamodel.metagen.model.Table;
import de.freese.metamodel.modelgen.mapping.ClassType;
import de.freese.metamodel.modelgen.mapping.TypeMapping;
import de.freese.metamodel.modelgen.naming.DefaultNamingStrategy;
import de.freese.metamodel.modelgen.naming.NamingStrategy;

/**
 * @author Thomas Freese
 */
public abstract class AbstractCodeGenerator
{
    /**
    *
    */
    private boolean addFullConstructor = false;

    /**
    *
    */
    private CodeWriter codeWriter = null;

    /**
    *
    */
    private MetaExporter metaExporter = null;

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
    private String schemaName = null;

    /**
    *
    */
    private boolean serializeable = false;

    /**
    *
    */
    private String tableNamePattern = null;

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
     * Erstellt ein neues {@link AbstractCodeGenerator} Object.
     */
    public AbstractCodeGenerator()
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
        Objects.requireNonNull(this.targetFolder, "targetFolder required");
        Objects.requireNonNull(this.codeWriter, "codeWriter required");

        for (Table table : schema.getTables())
        {
            ClassModel classModel = transformClass(table);
            classModel.setPackageName(getPackageName());

            List<Column> toStringColumns = getColumnsForToString(table);

            // @formatter:off
            classModel.getFields().stream()
                    .filter(field -> {
                        field.setUseForToString(false);

                        Column column = field.getPayload();
                        return toStringColumns.contains(column);
                    })
                    .forEach(field -> field.setUseForToString(true));
                    ;
            // @formatter:on

            // Datei schreiben.
            String packageDirectory = getPackageName().replace(".", "/");
            Path folder = getTargetFolder().resolve(packageDirectory);

            if (!Files.exists(folder))
            {
                Files.createDirectories(folder);
            }

            String fileName = getNamingStrategy().getClassName(table.getName()) + getCodeWriter().getFileExtension();
            Path path = folder.resolve(fileName);

            try (PrintStream ps = new PrintStream(new BufferedOutputStream(Files.newOutputStream(path)), true, StandardCharsets.UTF_8))
            {
                getCodeWriter().write(classModel, ps);

                ps.flush();
            }
        }
    }

    /**
     * @return {@link CodeWriter}
     */
    protected CodeWriter getCodeWriter()
    {
        return this.codeWriter;
    }

    /**
     * @param table {@link Table}
     * @return {@link List}
     */
    protected List<Column> getColumnsForToString(final Table table)
    {
        List<Column> columns = null;

        // Finde alle Columns des PrimaryKeys.
        if (table.getPrimaryKey() != null)
        {
            columns = table.getPrimaryKey().getColumnsOrdered();
        }

        if (columns.isEmpty())
        {
            // Finde alle Columns mit UniqueConstraints.
            // @formatter:off
            columns = table.getUniqueConstraints().stream()
                    .flatMap(uc -> uc.getColumnsOrdered().stream())
                    .sorted(Comparator.comparing(Column::getTableIndex))
                    .collect(Collectors.toList());
            // @formatter:on
        }

        if (columns.isEmpty())
        {
            // Finde alle Columns mit ForeignKeys.
            // @formatter:off
            columns = table.getColumnsOrdered().stream()
                    .filter(c -> c.getForeignKey() != null)
                    .map(c -> c.getForeignKey().getColumn())
                    .sorted(Comparator.comparing(Column::getTableIndex))
                    .collect(Collectors.toList());
            // @formatter:on
        }

        if (columns.isEmpty())
        {
            // Alle Columns.
            columns = table.getColumnsOrdered();
        }

        return columns;
    }

    /**
     * @return {@link NamingStrategy}
     */
    protected NamingStrategy getNamingStrategy()
    {
        return this.namingStrategy;
    }

    /**
     * @return String
     */
    protected String getPackageName()
    {
        return this.packageName;
    }

    /**
     * @return {@link Path}
     */
    protected Path getTargetFolder()
    {
        return this.targetFolder;
    }

    /**
     * @return {@link TypeMapping}
     */
    protected TypeMapping getTypeMapping()
    {
        return this.typeMapping;
    }

    /**
     * @return boolean
     */
    protected boolean isAddFullConstructor()
    {
        return this.addFullConstructor;
    }

    /**
     * @return boolean
     */
    protected boolean isSerializeable()
    {
        return this.serializeable;
    }

    /**
     * @return boolean
     */
    protected boolean isValidationAnnotations()
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
     * @param schemaName String
     */
    public void setSchemaName(final String schemaName)
    {
        this.schemaName = schemaName;
    }

    /**
     * @param serializeable boolean
     */
    public void setSerializeable(final boolean serializeable)
    {
        this.serializeable = serializeable;
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

    /**
     * @param table {@link Table}
     * @return {@link ClassModel}
     */
    protected ClassModel transformClass(final Table table)
    {
        String name = getNamingStrategy().getClassName(table.getName());
        ClassModel classModel = new ClassModel(name);
        classModel.setSerializeable(isSerializeable());
        classModel.setAddFullConstructor(isAddFullConstructor());

        transformClassJavaDoc(table, classModel);
        transformClassAnnotations(table, classModel);

        for (Column column : table.getColumnsOrdered())
        {
            transformField(column, classModel);
        }

        return classModel;
    }

    /**
     * @param table {@link Table}
     * @param classModel {@link ClassModel}
     */
    protected void transformClassAnnotations(final Table table, final ClassModel classModel)
    {
        // NO OP
    }

    /**
     * @param table {@link Table}
     * @param classModel {@link ClassModel}
     */
    protected abstract void transformClassJavaDoc(final Table table, final ClassModel classModel);

    /**
     * @param column {@link Column}
     * @param classModel {@link ClassModel}
     */
    protected void transformField(final Column column, final ClassModel classModel)
    {
        String fieldName = getNamingStrategy().getFieldName(column.getName());
        ClassType type = (ClassType) getTypeMapping().getType(column.getJdbcType(), column.isNullable());

        FieldModel fieldModel = classModel.addField(fieldName, type.getJavaClass());
        fieldModel.setDefaultValueAsString(type.getDefaultValueAsString());
        fieldModel.setPayload(column);

        transformFieldComments(column, fieldModel);
        transformFieldAnnotations(column, fieldModel);
    }

    /**
     * @param column {@link Column}
     * @param fieldModel {@link FieldModel}
     */
    protected void transformFieldAnnotations(final Column column, final FieldModel fieldModel)
    {
        // Validation Annotations
        if (isValidationAnnotations())
        {
            if (!column.isNullable())
            {
                // @NotNull
                fieldModel.addImport(NotNull.class);
                fieldModel.addAnnotation("@" + NotNull.class.getSimpleName());
            }

            if (JDBCType.VARCHAR.equals(column.getJdbcType()))
            {
                // @Size(max=50)
                fieldModel.addImport(Size.class);
                fieldModel.addAnnotation("@" + Size.class.getSimpleName() + "(max = " + column.getSize() + ")");
            }
        }
    }

    /**
     * @param column {@link Column}
     * @param fieldModel {@link FieldModel}
     */
    protected void transformFieldComments(final Column column, final FieldModel fieldModel)
    {
        if (StringUtils.isNotBlank(column.getComment()))
        {
            fieldModel.addComment(column.getComment());
        }
    }
}
