/**
 * Created: 08.07.2018
 */

package de.freese.metamodel.metagen;

import java.sql.DatabaseMetaData;
import javax.sql.DataSource;
import de.freese.metamodel.metagen.model.Schema;

/**
 * Interface für einen {@link MetaExporter}.
 *
 * @author Thomas Freese
 */
@FunctionalInterface
public interface MetaExporter
{
    /**
     * Liefert das Schema mit dem Meta-Modell.
     *
     * @param dataSource {@link DataSource}
     * @param schemaName String
     * @param tableNamePattern String
     * @return {@link Schema}
     * @throws Exception Falls was schief geht.
     * @see DatabaseMetaData#getTables(String, String, String, String[])
     */
    public Schema export(DataSource dataSource, String schemaName, String tableNamePattern) throws Exception;
}
