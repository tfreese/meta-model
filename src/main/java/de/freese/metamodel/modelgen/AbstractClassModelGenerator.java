/**
 * Created: 22.07.2018
 */

package de.freese.metamodel.modelgen;

import java.io.Serializable;
import java.sql.JDBCType;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.freese.metamodel.metagen.model.Column;
import de.freese.metamodel.metagen.model.Table;
import de.freese.metamodel.modelgen.mapping.Type;
import de.freese.metamodel.modelgen.model.ClassModel;
import de.freese.metamodel.modelgen.model.FieldModel;

/**
 * Basis-Implementierung des CodeGenerators.
 *
 * @author Thomas Freese
 */
public abstract class AbstractClassModelGenerator implements ClassModelGenerator
{
    /**
    *
    */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Erstellt ein neues {@link AbstractClassModelGenerator} Object.
     */
    public AbstractClassModelGenerator()
    {
        super();
    }

    /**
     * @see de.freese.metamodel.modelgen.ClassModelGenerator#generate(de.freese.metamodel.modelgen.ModelConfig, de.freese.metamodel.metagen.model.Table)
     */
    @Override
    public ClassModel generate(final ModelConfig config, final Table table)
    {
        ClassModel classModel = generateClassModel(config, table);
        classModel.setAddFullConstructor(config.isAddFullConstructor());

        // Serializable
        if (config.isSerializeable())
        {
            classModel.addInterface(Serializable.class);
        }

        // Fields
        for (Column column : table.getColumnsOrdered())
        {
            FieldModel fieldModel = generateFieldModel(config, classModel, column);
            classModel.addField(fieldModel);

            generateFieldComments(config, fieldModel);
            generateFieldAnnotations(config, fieldModel);
        }

        return classModel;
    }

    /**
     * @param config {@link ModelConfig}
     * @param table {@link Table}
     * @return {@link ClassModel}
     */
    protected ClassModel generateClassModel(final ModelConfig config, final Table table)
    {
        ClassModel classModel = new ClassModel(table);
        classModel.setPackageName(config.getPackageName());

        String name = config.getNamingStrategy().getClassName(table.getName());
        classModel.setName(name);

        classModel.addClassComment(table.getComment());

        return classModel;
    }

    /**
     * @param config {@link ModelConfig}
     * @param fieldModel {@link FieldModel}
     */
    protected void generateFieldAnnotations(final ModelConfig config, final FieldModel fieldModel)
    {
        // Validation Annotations
        if (config.isValidationAnnotations())
        {
            if (!fieldModel.getColumn().isNullable())
            {
                // @NotNull
                fieldModel.addAnnotation(NotNull.class, null);
            }

            if (JDBCType.VARCHAR.equals(fieldModel.getColumn().geJdbcType()))
            {
                // @Size(max=50)
                Map<String, Object> values = new TreeMap<>();
                values.put("max", fieldModel.getColumn().getSize());

                fieldModel.addAnnotation(Size.class, values);
            }
        }
    }

    /**
     * @param config {@link ModelConfig}
     * @param fieldModel {@link FieldModel}
     */
    protected void generateFieldComments(final ModelConfig config, final FieldModel fieldModel)
    {
        StringJoiner joiner = new StringJoiner("; ");

        Column column = fieldModel.getColumn();

        joiner.add("Column: " + column.getName());
        joiner.add(column.geJdbcType().toString());
        joiner.add(column.isNullable() ? "nullable" : "not null");

        if (column.hasSize())
        {
            joiner.add("size = " + column.getSize());
        }

        if (column.hasDecimalDigits())
        {
            joiner.add("digits = " + column.getDecimalDigits());
        }

        List<Column> primaryKeyColumns = column.getTable().getPrimaryKey().getColumnsOrdered();

        if (primaryKeyColumns.contains(column))
        {
            joiner.add("Primary Key");
        }

        fieldModel.addComment(joiner.toString());
    }

    /**
     * @param config {@link ModelConfig}
     * @param classModel {@link ClassModel}
     * @param column {@link Column}
     * @return {@link ClassModel}
     */
    protected FieldModel generateFieldModel(final ModelConfig config, final ClassModel classModel, final Column column)
    {
        FieldModel fieldModel = new FieldModel(classModel, column);

        String name = config.getNamingStrategy().getFieldName(column.getName());
        fieldModel.setName(name);

        Type type = config.getTypeMapping().getType(column.geJdbcType(), column.isNullable());
        fieldModel.setType(type);

        return fieldModel;
    }

    /**
     * @return {@link Logger}
     */
    protected Logger getLogger()
    {
        return this.logger;
    }
}
