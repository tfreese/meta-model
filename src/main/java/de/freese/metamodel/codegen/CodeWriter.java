/**
 * Created: 26.07.2018
 */

package de.freese.metamodel.codegen;

import java.io.PrintWriter;
import de.freese.metamodel.modelgen.model.ClassModel;

/**
 * Erzeugt aus dem {@link ClassModel} den Quellcode.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface CodeWriter
{
    /**
     * Schreibt den Code einer Klasse.
     *
     * @param pw {@link PrintWriter}
     * @param model {@link ClassModel}
     * @throws Exception Falls was schief geht.
     */
    public void write(PrintWriter pw, ClassModel model) throws Exception;
}
