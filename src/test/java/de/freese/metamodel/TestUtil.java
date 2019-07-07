/**
 * Created: 08.07.2018
 */

package de.freese.metamodel;

import oracle.jdbc.pool.OracleDataSource;
import org.apache.commons.lang3.StringUtils;
import org.hsqldb.jdbc.JDBCPool;
import org.mariadb.jdbc.MariaDbPoolDataSource;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;

import java.io.Closeable;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import javax.sql.DataSource;

/**
 * @author Thomas Freese
 */
public final class TestUtil
{
    /**
     * Fügt am Index 1 der Liste eine Trenn-Linie ein.<br>
     * Die Breite pro Spalte orientiert sich am ersten Wert (Header) der Spalte.<br>
     *
     * @param <T>       Konkreter Typ
     * @param rows      {@link List}
     * @param separator String
     */
    private static <T extends CharSequence> void addHeaderSeparator(final List<T[]> rows, final String separator)
    {
        if ((rows == null) || rows.isEmpty())
        {
            return;
        }

        String sep = (separator == null) || separator.trim().isEmpty() ? "-" : separator;

        int columnCount = rows.get(0).length;

        // Trenner zwischen Header und Daten.
        // T[] row = (T[]) Array.newInstance(String.class, columnCount);
        // T[] row = Arrays.copyOf(rows.get(0), columnCount);
        // T[] row = rows.get(0).clone();
        String[] row = new String[columnCount];

        for (int column = 0; column < columnCount; column++)
        {
            // row[column] = String.join("", Collections.nCopies(rows.get(0)[column].length(), sep));
            row[column] = StringUtils.repeat(sep, rows.get(0)[column].length());
        }

        rows.add(1, (T[]) row);
    }

    /**
     * @param dataSource {@link DataSource}
     *
     * @throws Exception Falls was schief geht.
     */
    public static void closeDataSource(final DataSource dataSource) throws Exception
    {
        if (dataSource instanceof AutoCloseable)
        {
            ((AutoCloseable) dataSource).close();
        }
        else if (dataSource instanceof Closeable)
        {
            ((Closeable) dataSource).close();
        }
        else if (dataSource instanceof JDBCPool)
        {
            ((JDBCPool) dataSource).close(1);
        }
        // else if (dataSource instanceof DisposableBean)
        // {
        // ((DisposableBean) dataSource).destroy();
        // }
    }

    /**
     * @param url String
     *
     * @return {@link DataSource}
     */
    public static DataSource createHsqlDBDataSource(final String url)
    {
        // jdbc:hsqldb:mem:mails
        // jdbc:hsqldb:file:/tmp/mails/mails;create=false;readonly=true;shutdown=true
        // jdbc:hsqldb:res:hsqldb/person;create=false;readonly=true
        JDBCPool dataSource = new JDBCPool();
        dataSource.setUrl(url);
        dataSource.setUser("sa");
        dataSource.setPassword("");

        return dataSource;
    }

    /**
     * @param url String
     *
     * @return {@link DataSource}
     *
     * @throws SQLException Falls was schief geht.
     */
    public static DataSource createMySQLDBDataSource(final String url) throws SQLException
    {
        // jdbc:mariadb://localhost:3306/kodi_video99
        // useInformationSchema: Für Anzeige der Kommentare
        MariaDbPoolDataSource dataSource = new MariaDbPoolDataSource(url + "?useInformationSchema=true");
        dataSource.setUser("tommy");
        dataSource.setPassword("tommy");

        return dataSource;
    }

    /**
     * @param url String
     *
     * @return {@link DataSource}
     *
     * @throws SQLException Falls was schief geht.
     */
    public static DataSource createOracleDataSource(final String url) throws SQLException
    {
        // jdbc:oracle:thin:@//HOST:1560/service
        // remarksReporting: Für Anzeige der Kommentare
        OracleDataSource dataSource = new OracleDataSource();
        dataSource.setDriverType("thin");
        dataSource.setURL(url + "?remarksReporting=true");
        dataSource.setUser("...");
        dataSource.setPassword("...");

        return dataSource;
    }

    /**
     * @param url String
     *
     * @return {@link DataSource}
     */
    public static DataSource createSQLiteDataSource(final String url)
    {
        // jdbc:sqlite:/tmp/MyVideos99.db
        SQLiteConfig config = new SQLiteConfig();
        //config.setReadOnly(true);
        //config.setReadUncommited(true);

        SQLiteDataSource dataSource = new SQLiteConnectionPoolDataSource(config);
        dataSource.setUrl(url);

        return dataSource;
    }

    /**
     * Die Spaltenbreite der Elemente wird auf den breitesten Wert durch das Padding aufgefüllt.<br>
     * Ist das Padding null oder leer wird nichts gemacht.<br>
     * Beim Padding werden die CharSequences durch Strings ersetzt.
     *
     * @param <T>     Konkreter Typ
     * @param rows    {@link List}
     * @param padding String
     *
     * @see #write(List, PrintStream, String)
     */
    private static <T extends CharSequence> void padding(final List<T[]> rows, final String padding)
    {
        if ((rows == null) || rows.isEmpty())
        {
            return;
        }

        int columnCount = rows.get(0).length;

        // Breite pro Spalte rausfinden.
        int[] columnWidth = new int[columnCount];

        // @formatter:off
        IntStream.range(0, columnCount).forEach(column
                ->
                {
                    columnWidth[column] = rows.stream()
                            .parallel()
                            .map(r -> r[column])
                            .mapToInt(CharSequence::length)
                            .max()
                            .orElse(0);
        });
        // @formatter:on

        // Strings pro Spalte formatieren und schreiben.
        String pad = (padding == null) || padding.trim().isEmpty() ? " " : padding;

        rows.stream().parallel().forEach(r ->
        {
            for (int column = 0; column < columnCount; column++)
            {
                String value = rightPad(r[column].toString(), columnWidth[column], pad);

                r[column] = (T) value;
            }
        });
    }

    /**
     * @param value   String
     * @param size    int
     * @param padding String
     *
     * @return String
     */
    private static String rightPad(final String value, final int size, final String padding)
    {
        String newValue = "";

        newValue = StringUtils.rightPad(value, size, padding);
        // if (value == null || value.trim().isEmpty())
        // {
        // return newValue;
        // }
        //
        // String val = value;
        //
        // // String.format("%-10s", "bar").replace(' ', '*');
        // newValue = String.format("%-" + size + "s", val);
        //
        return newValue;
    }

    /**
     * Erzeugt aus dem {@link ResultSet} eine Liste mit den Column-Namen in der ersten Zeile und den Daten.<br>
     * Wenn das ResultSet einen Typ != ResultSet.TYPE_FORWARD_ONLY besitzt, wird {@link ResultSet#first()} aufgerufen und kann weiter verwendet werden.
     *
     * @param resultSet {@link ResultSet}
     *
     * @return {@link List}
     *
     * @throws SQLException Falls was schief geht.
     */
    private static List<String[]> toList(final ResultSet resultSet) throws SQLException
    {
        Objects.requireNonNull(resultSet, "resultSet required");

        List<String[]> rows = new ArrayList<>();

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Spaltennamen / Header
        String[] header = new String[columnCount];
        rows.add(header);

        for (int column = 1; column <= columnCount; column++)
        {
            header[column - 1] = metaData.getColumnLabel(column).toUpperCase();
        }

        // Daten
        while (resultSet.next())
        {
            String[] row = new String[columnCount];
            rows.add(row);

            for (int column = 1; column <= columnCount; column++)
            {
                Object obj = resultSet.getObject(column);
                String value = null;

                if (obj == null)
                {
                    value = "";
                }
                else if (obj instanceof byte[])
                {
                    value = new String((byte[]) obj);
                }
                else
                {
                    value = obj.toString();
                }

                row[column - 1] = value;
            }
        }

        if (resultSet.getType() != ResultSet.TYPE_FORWARD_ONLY)
        {
            resultSet.first();
        }

        return rows;
    }

    /**
     * Schreibt die Liste in den PrintStream.<br>
     * Der Stream wird nicht geschlossen.
     *
     * @param <T>       Konkreter Typ von CharSequence
     * @param rows      {@link List}
     * @param ps        {@link PrintStream}
     * @param separator String
     */
    private static <T extends CharSequence> void write(final List<T[]> rows, final PrintStream ps, final String separator)
    {
        Objects.requireNonNull(rows, "rows required");
        Objects.requireNonNull(ps, "printStream required");

        if (rows.isEmpty())
        {
            return;
        }

        // int columnCount = rows.get(0).length;

        // Strings pro Spalte schreiben, parallel() verfälscht die Reihenfolge.
        // rows.forEach(r -> ps.println(Stream.of(r).collect(Collectors.joining(separator))));
        rows.forEach(r -> ps.println(StringUtils.join(r, separator)));

        ps.flush();
    }

    /**
     * Schreibt das ResultSet in den PrintStream.<br>
     * Dabei wird die Spaltenbreite auf den breitesten Wert angepasst.<br>
     * Der Stream wird nicht geschlossen.<br>
     * Wenn das ResultSet einen Typ != ResultSet.TYPE_FORWARD_ONLY besitzt, wird ResultSet.first() aufgerufen und kann weiter verwendet werden.
     *
     * @param resultSet {@link ResultSet}
     * @param ps        {@link PrintStream}
     *
     * @throws SQLException Falls was schief geht.
     */
    public static void write(final ResultSet resultSet, final PrintStream ps) throws SQLException
    {
        List<String[]> rows = toList(resultSet);
        padding(rows, " ");
        addHeaderSeparator(rows, "-");

        write(rows, ps, " | ");

        if (resultSet.getType() != ResultSet.TYPE_FORWARD_ONLY)
        {
            resultSet.first();
        }
    }

    /**
     * Erstellt ein neues {@link TestUtil} Object.
     */
    private TestUtil()
    {
        super();
    }
}
