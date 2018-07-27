/**
 * Created: 26.07.2018
 */

package de.freese.metamodel.modelgen;

import de.freese.metamodel.metagen.model.Table;
import de.freese.metamodel.modelgen.model.ClassModel;

/**
 * Erzeugt das {@link ClassModel} aus einer {@link Table}.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface ClassModelGenerator
{
    /**
     * Erzeugt das {@link ClassModel} aus einer {@link Table}.
     * 
     * @param config {@link ModelConfig}
     * @param table {@link Table}
     * @return {@link ClassModel}
     */
    public ClassModel generate(ModelConfig config, final Table table);
}
