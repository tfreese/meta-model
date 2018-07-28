/**
 * Created: 26.07.2018
 */

package de.freese.metamodel.codegen;

import de.freese.metamodel.Config;
import de.freese.metamodel.metagen.model.Table;

/**
 * Erzeugt aus den MetaDaten den Quellcode.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface CodeWriter
{
    /**
     * Schreibt den Code einer Klasse.
     *
     * @param config {@link Config}
     * @param table {@link Table}
     * @throws Exception Falls was schief geht.
     */
    public void write(Config config, Table table) throws Exception;
}
