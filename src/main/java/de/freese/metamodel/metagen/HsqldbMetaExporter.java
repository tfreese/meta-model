/**
 * Created: 08.07.2018
 */

package de.freese.metamodel.metagen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import de.freese.metamodel.metagen.model.Schema;
import de.freese.metamodel.metagen.model.Sequence;

/**
 * {@link MetaExporter} für HSQLDB.
 *
 * @author Thomas Freese
 */
public class HsqldbMetaExporter extends AbstractMetaExporter
{
    /**
     * @see de.freese.metamodel.metagen.AbstractMetaExporter#generateSequences(javax.sql.DataSource, de.freese.metamodel.metagen.model.Schema)
     */
    @Override
    protected void generateSequences(final DataSource dataSource, final Schema schema) throws SQLException
    {
        StringBuilder sql = new StringBuilder();
        sql.append("select SEQUENCE_NAME, START_WITH, INCREMENT, NEXT_VALUE");
        sql.append(" from INFORMATION_SCHEMA.SYSTEM_SEQUENCES");
        sql.append(" where SEQUENCE_SCHEMA = ?");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql.toString()))
        {
            preparedStatement.setString(1, schema.getName());

            try (ResultSet resultSet = preparedStatement.executeQuery())
            {
                while (resultSet.next())
                {
                    String sequenceName = resultSet.getString("SEQUENCE_NAME");
                    long startWith = resultSet.getLong("START_WITH");
                    long increment = resultSet.getLong("INCREMENT");
                    long nextValue = resultSet.getLong("NEXT_VALUE");

                    Sequence sequence = schema.getSequence(sequenceName);
                    sequence.setStartWith(startWith);
                    sequence.setIncrement(increment);
                    sequence.setNextValue(nextValue);
                }
            }
        }
    }

    // /**
    // * @param dataSource {@link DataSource}
    // * @param table {@link Table}
    // * @throws SQLException Falls was schief geht.
    // */
    // void handleIndices(final DataSource dataSource, final Table table) throws SQLException
    // {
    // StringBuilder sql = new StringBuilder();
    // sql.append("select index_name, column_name FROM INFORMATION_SCHEMA.SYSTEM_INDEXINFO");
    // sql.append(" where non_unique = TRUE and table_schem = ? and table_name = ? and index_name not like 'SYS_%'");
    // sql.append(" order by ordinal_position asc");
    //
    // try (Connection con = dataSource.getConnection();
    // PreparedStatement stmt = con.prepareStatement(sql.toString()))
    // {
    // stmt.setString(1, table.getSchema().getName());
    // stmt.setString(2, table.getName());
    //
    // try (ResultSet rs = stmt.executeQuery())
    // {
    // while (rs.next())
    // {
    // // String name = rs.getString("INDEX_NAME");
    // // String columnName = rs.getString("COLUMN_NAME");
    //
    // // Index index = table.getIndex(name);
    // // Column column = table.getColumn(columnName);
    // // index.addColumn(keyIndex, column);
    // }
    // }
    // }
    // }

    // /**
    // * @param dataSource {@link DataSource}
    // * @param table {@link Table}
    // * @throws SQLException Falls was schief geht.
    // */
    // void handleUniqueConstraints(final DataSource dataSource, final Table table) throws SQLException
    // {
    // StringBuilder sql = new StringBuilder();
    // sql.append("select ts.constraint_name, ccu.column_name from INFORMATION_SCHEMA.TABLE_CONSTRAINTS ts");
    // sql.append(" inner join INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE ccu on ccu.table_name = ts.table_name");
    // sql.append(" and ccu.constraint_name = ts.constraint_name");
    // sql.append(" where ts.table_schema = ? and ts.table_name = ? and ts.constraint_type = 'UNIQUE'");
    //
    // try (Connection con = dataSource.getConnection();
    // PreparedStatement stmt = con.prepareStatement(sql.toString()))
    // {
    // stmt.setString(1, table.getSchema().getName());
    // stmt.setString(2, table.getName());
    //
    // try (ResultSet rs = stmt.executeQuery())
    // {
    // while (rs.next())
    // {
    // // String name = rs.getString("CONSTRAINT_NAME");
    // // String columnName = rs.getString("COLUMN_NAME");
    //
    // // UniqueConstraint index = table.getUniqueConstraint(name);
    // // Column column = table.getColumn(columnName);
    // // index.addColumn(keyIndex, column);
    // }
    // }
    // }
    // }

    // /**
    // * @param dataSource {@link DataSource}
    // * @param schema {@link Schema}
    // * @throws SQLException Falls was schief geht.
    // */
    // void handleViews(final DataSource dataSource, final Schema schema) throws SQLException
    // {
    // // List<View> viewList = new ArrayList<>();
    //
    // try (Connection con = dataSource.getConnection())
    // {
    // // DatabaseMetaData dbmd = con.getMetaData();
    //
    // StringBuilder sql = new StringBuilder();
    // sql.append("select table_name, view_definition");
    // sql.append(" from INFORMATION_SCHEMA.VIEWS");
    // sql.append(" where table_schema = ?");
    //
    // try (PreparedStatement stmt = con.prepareStatement(sql.toString()))
    // {
    // stmt.setString(1, schema.getName());
    //
    // try (ResultSet rs = stmt.executeQuery())
    // {
    // while (rs.next())
    // {
    // String name = rs.getString("TABLE_NAME");
    // // String def = rs.getString("VIEW_DEFINITION");
    //
    // // viewList.add(new View(schema, name, def));
    //
    // if (getLogger().isDebugEnabled())
    // {
    // getLogger().debug("Processing view: {}", name);
    // }
    // }
    // }
    // }
    //
    // // Spalten
    // // sql = new StringBuilder();
    // // sql.append("select column_name");
    // // sql.append(" from INFORMATION_SCHEMA.VIEW_COLUMN_USAGE");
    // // sql.append(" where view_schema = ? and view_name = ?");
    //
    // // for (View view : viewList)
    // // {
    // // // try (PreparedStatement stmt = con.prepareStatement(sql.toString()))
    // // // {
    // // // stmt.setString(1, view.getSchema());
    // // // stmt.setString(2, view.getName());
    // // //
    // // // try (ResultSet columns = stmt.executeQuery())
    // // // {
    // //
    // // try (ResultSet columns = dbmd.getColumns(schema.getName(), schema.getName(), view.getName(), null))
    // // {
    // // while (columns.next())
    // // {
    // // String columnName = columns.getString("COLUMN_NAME");
    // //
    // // view.addColumn(columnName);
    // // }
    // // }
    // // }
    // }
    // }
}
