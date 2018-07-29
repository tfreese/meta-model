/**
 * Created: 29.07.2018
 */

package de.freese.metamodel.modelgen;

import java.util.Objects;
import de.freese.metamodel.metagen.model.Column;
import de.freese.metamodel.modelgen.mapping.Type;

/**
 * Definiert das Model eines Klassen-Attributs.
 *
 * @author Thomas Freese
 */
public class FieldModel extends AbstractModel
{
    /**
     *
     */
    private ClassModel classModel = null;

    /**
     *
     */
    private final Column column;

    /**
     *
     */
    private Type type = null;

    /**
     * Erstellt ein neues {@link FieldModel} Object.
     *
     * @param classModel {@link ClassModel}
     * @param column {@link Column}
     */
    public FieldModel(final ClassModel classModel, final Column column)
    {
        super();

        this.classModel = Objects.requireNonNull(classModel, "classModel required");
        this.column = Objects.requireNonNull(column, "column required");

        this.classModel.addField(this);
    }

    /**
     * @return {@link ClassModel}
     */
    public ClassModel getClassModel()
    {
        return this.classModel;
    }

    /**
     * @return {@link Column}
     */
    public Column getColumn()
    {
        return this.column;
    }

    /**
     * @return {@link Type}
     */
    public Type getType()
    {
        return this.type;
    }

    /**
     * @param classModel ClassModel
     */
    public void setClassModel(final ClassModel classModel)
    {
        this.classModel = classModel;
    }

    /**
     * @param type {@link Type}
     */
    public void setType(final Type type)
    {
        this.type = type;
    }
}
