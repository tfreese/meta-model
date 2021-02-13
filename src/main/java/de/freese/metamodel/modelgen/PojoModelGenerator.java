/**
 * Created: 22.04.2020
 */

package de.freese.metamodel.modelgen;

import org.apache.commons.lang3.StringUtils;
import de.freese.metamodel.metagen.model.Table;
import de.freese.metamodel.modelgen.model.ClassModel;

/**
 * @author Thomas Freese
 */
public class PojoModelGenerator extends AbstractModelGenerator
{
    /**
     * @see de.freese.metamodel.modelgen.AbstractModelGenerator#transformClassJavaDoc(de.freese.metamodel.metagen.model.Table,
     *      de.freese.metamodel.modelgen.model.ClassModel)
     */
    @Override
    protected void transformClassJavaDoc(final Table table, final ClassModel classModel)
    {
        if (StringUtils.isNotBlank(table.getComment()))
        {
            classModel.addComment(table.getComment());
        }

        classModel.addComment("Pojo für Tabelle " + table.getFullName() + ".");
    }
}
