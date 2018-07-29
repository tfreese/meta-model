/**
 * Created: 29.07.2018
 */

package de.freese.metamodel.codegen;

import java.sql.JDBCType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;
import de.freese.metamodel.Config;
import de.freese.metamodel.metagen.model.Column;
import de.freese.metamodel.metagen.model.Table;
import de.freese.metamodel.modelgen.ClassModel;
import de.freese.metamodel.modelgen.FieldModel;
import de.freese.metamodel.modelgen.mapping.Type;

/**
 * Java-Implementierung eines {@link CodeWriter}.
 *
 * @author Thomas Freese
 */
public class PojoCodeWriter extends AbstractCodeWriter
{
    /**
     * Erstellt ein neues {@link PojoCodeWriter} Object.
     */
    public PojoCodeWriter()
    {
        super();
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#createClassAnnotations(de.freese.metamodel.Config, de.freese.metamodel.modelgen.ClassModel)
     */
    @Override
    protected void createClassAnnotations(final Config config, final ClassModel classModel)
    {
        // NOOP
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#createClassJavaDoc(de.freese.metamodel.Config, de.freese.metamodel.modelgen.ClassModel)
     */
    @Override
    protected void createClassJavaDoc(final Config config, final ClassModel classModel)
    {
        Table table = classModel.getTable();

        if (StringUtils.isNotBlank(table.getComment()))
        {
            classModel.addComment(table.getComment());
        }

        classModel.addComment("Pojo für Tabelle " + table.getSchema().getName() + "." + table.getName());
    }

    /**
     * @param config {@link Config}
     * @param fieldModel {@link FieldModel}
     */
    protected void createFieldAnnotations(final Config config, final FieldModel fieldModel)
    {
        ClassModel classModel = fieldModel.getClassModel();
        Column column = fieldModel.getColumn();

        // Validation Annotations
        if (config.isValidationAnnotations())
        {
            if (!column.isNullable())
            {
                // @NotNull
                classModel.addImport(NotNull.class);
                fieldModel.addAnnotation("@" + NotNull.class.getSimpleName());
            }

            if (JDBCType.VARCHAR.equals(column.getJdbcType()))
            {
                // @Size(max=50)
                classModel.addImport(Size.class);
                fieldModel.addAnnotation("@" + Size.class.getSimpleName() + "(max = " + column.getSize() + ")");
            }
        }
    }

    /**
     * @param config {@link Config}
     * @param fieldModel {@link FieldModel}
     */
    protected void createFieldComments(final Config config, final FieldModel fieldModel)
    {
        Column column = fieldModel.getColumn();

        if (StringUtils.isNotBlank(column.getComment()))
        {
            fieldModel.addComment(column.getComment());
        }
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#createFields(de.freese.metamodel.Config, de.freese.metamodel.modelgen.ClassModel)
     */
    @Override
    protected void createFields(final Config config, final ClassModel classModel)
    {
        for (Column column : classModel.getTable().getColumnsOrdered())
        {
            FieldModel fieldModel = new FieldModel(classModel, column);

            String fieldName = config.getNamingStrategy().getFieldName(column.getName());
            Type type = config.getTypeMapping().getType(column.getJdbcType(), column.isNullable());

            fieldModel.setName(fieldName);
            fieldModel.setType(type);

            createFieldComments(config, fieldModel);
            createFieldAnnotations(config, fieldModel);
        }
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeWriter#getFileExtension()
     */
    @Override
    protected String getFileExtension()
    {
        return ".java";
    }
}
