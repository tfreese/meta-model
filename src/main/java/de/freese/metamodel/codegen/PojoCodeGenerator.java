/**
 * Created: 22.04.2020
 */

package de.freese.metamodel.codegen;

import org.apache.commons.lang3.StringUtils;
import de.freese.metamodel.codegen.model.ClassModel;
import de.freese.metamodel.metagen.model.Table;

/**
 * @author Thomas Freese
 */
public class PojoCodeGenerator extends AbstractCodeGenerator
{
    /**
     * Erstellt ein neues {@link PojoCodeGenerator} Object.
     */
    public PojoCodeGenerator()
    {
        super();
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeGenerator#transformClassJavaDoc(de.freese.metamodel.metagen.model.Table,
     *      de.freese.metamodel.codegen.model.ClassModel)
     */
    @Override
    protected void transformClassJavaDoc(final Table table, final ClassModel classModel)
    {
        if (StringUtils.isNotBlank(table.getComment()))
        {
            classModel.addComment(table.getComment());
        }

        classModel.addComment("Pojo für Tabelle " + table.getSchema().getName() + "." + table.getName() + ".");
    }
}
