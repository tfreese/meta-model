/**
 * Created: 08.07.2018
 */

package de.freese.metamodel.metagen;

import java.sql.DatabaseMetaData;
import java.util.List;
import javax.sql.DataSource;
import de.freese.metamodel.metagen.model.Schema;

/**
 * Interface für einen {@link MetaExporter}.<br>
 * Bildet die Struktur einer Datenbank ab.
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
     * @param schemaNamePattern String
     * @param tableNamePattern String
     * @return {@link List}
     * @throws Exception Falls was schief geht.
     * @see DatabaseMetaData#getTables(String, String, String, String[])
     */
    public List<Schema> export(DataSource dataSource, String schemaNamePattern, String tableNamePattern) throws Exception;
}
