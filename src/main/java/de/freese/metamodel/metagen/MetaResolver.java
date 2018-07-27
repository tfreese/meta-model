/**
 * Created: 08.07.2018
 */

package de.freese.metamodel.metagen;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class MetaResolver
{
    /**
    *
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaResolver.class);

    /**
     * Liefert auf Basis der DB-MetaDaten den entsprechenden Exporter für die Quelle.
     *
     * @param dataSource {@link DataSource}
     * @return {@link MetaExporter}
     * @throws SQLException Falls was schief geht.
     * @throws IllegalStateException Wenn keine Quelle ermittelt werden konnte.
     */
    public static MetaExporter determineMetaData(final DataSource dataSource) throws SQLException
    {
        MetaExporter metaModelGenerator = null;

        try (Connection connection = dataSource.getConnection())
        {
            DatabaseMetaData dbmd = connection.getMetaData();

            String product = dbmd.getDatabaseProductName().toLowerCase();
            product = product.split(" ")[0];
            // int majorVersion = dbmd.getDatabaseMajorVersion();
            // int minorVersion = dbmd.getDatabaseMinorVersion();

            switch (product)
            {
                case "oracle":
                    metaModelGenerator = new OracleMetaExporter();
                    break;
                case "hsql":
                    metaModelGenerator = new HsqldbMetaExporter();
                    break;
                case "mysql":
                    metaModelGenerator = new MariaDbMetaExporter();
                    break;
                case "sqlite":
                    metaModelGenerator = new SQLiteMetaExporter();
                    break;

                default:
                    String msg = String.format("No MetaModelGenerator found for: %s%n", dbmd.getDatabaseProductName());

                    LOGGER.error(msg);
                    throw new IllegalStateException(msg);
            }
        }

        return metaModelGenerator;
    }

    /**
     * Erstellt ein neues {@link MetaResolver} Object.
     */
    private MetaResolver()
    {
        super();
    }
}
