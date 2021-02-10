/**
 * Created: 29.07.2018
 */

package de.freese.metamodel.codegen.writer;

/**
 * Java-Implementierung eines {@link CodeWriter}.
 *
 * @author Thomas Freese
 */
public class JavaCodeWriter extends AbstractCodeWriter
{
    /**
     * @see de.freese.metamodel.codegen.writer.AbstractCodeWriter#getFileExtension()
     */
    @Override
    public String getFileExtension()
    {
        return ".java";
    }
}
