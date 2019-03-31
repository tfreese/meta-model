/**
 * Created: 08.07.2018
 */

package de.freese.metamodel.metagen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.metamodel.TestUtil;
import de.freese.metamodel.metagen.model.Column;
import de.freese.metamodel.metagen.model.ForeignKey;
import de.freese.metamodel.metagen.model.Index;
import de.freese.metamodel.metagen.model.PrimaryKey;
import de.freese.metamodel.metagen.model.Schema;
import de.freese.metamodel.metagen.model.Sequence;
import de.freese.metamodel.metagen.model.Table;
import de.freese.metamodel.metagen.model.UniqueConstraint;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class TestHsqlPersonDb
{
    /**
    *
    */
    private static DataSource dataSource = null;

    /**
     * @throws Exception Falls was schief geht.
     */
    @AfterAll
    public static void afterAll() throws Exception
    {
        TestUtil.closeDataSource(dataSource);
    }

    /**
    *
    */
    @BeforeAll
    public static void beforeAll()
    {
        dataSource = TestUtil.createHsqlDBDataSource("jdbc:hsqldb:res:hsqldb/person;create=false;readonly=true");
    }

    /**
     *
     */
    private final MetaExporter generator = new HsqldbMetaExporter();

    /**
     *
     */
    private final String schemaName = "PUBLIC";

    /**
     * Erstellt ein neues {@link TestHsqlPersonDb} Object.
     */
    public TestHsqlPersonDb()
    {
        super();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test010Schema() throws Exception
    {
        Schema schema = this.generator.export(dataSource, this.schemaName, null);
        assertNotNull(schema);
        assertEquals(this.schemaName, schema.getName());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test020Sequences() throws Exception
    {
        Schema schema = this.generator.export(dataSource, this.schemaName, null);
        assertNotNull(schema);
        assertEquals(this.schemaName, schema.getName());

        List<Sequence> sequences = schema.getSequences();
        assertNotNull(sequences);
        assertEquals(2, sequences.size());

        Collections.sort(sequences, Comparator.comparing(Sequence::getName));
        assertEquals("ADDRESS_SEQ", sequences.get(0).getName());
        assertEquals("PERSON_SEQ", sequences.get(1).getName());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test030Table() throws Exception
    {
        Schema schema = this.generator.export(dataSource, this.schemaName, null);
        assertNotNull(schema);
        assertEquals(this.schemaName, schema.getName());

        List<Table> tables = schema.getTables();
        assertNotNull(tables);
        assertEquals(2, tables.size());

        Collections.sort(tables, Comparator.comparing(Table::getName));
        assertEquals("T_ADDRESS", tables.get(0).getName());
        assertEquals("T_PERSON", tables.get(1).getName());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test040Columns() throws Exception
    {
        Schema schema = this.generator.export(dataSource, this.schemaName, "T_PERSON");
        assertNotNull(schema);
        assertEquals(this.schemaName, schema.getName());

        List<Table> tables = schema.getTables();
        assertNotNull(tables);
        assertEquals(1, tables.size());

        assertEquals("T_PERSON", tables.get(0).getName());

        List<Column> columns = tables.get(0).getColumnsOrdered();
        assertNotNull(columns);
        assertEquals(3, columns.size());

        assertEquals("ID", columns.get(0).getName());
        assertEquals("NAME", columns.get(1).getName());
        assertEquals("VORNAME", columns.get(2).getName());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test050PrimaryKey() throws Exception
    {
        Schema schema = this.generator.export(dataSource, this.schemaName, "T_PERSON");
        assertNotNull(schema);
        assertEquals(this.schemaName, schema.getName());

        Table table = schema.getTable("T_PERSON");
        assertNotNull(table);

        PrimaryKey primaryKey = table.getPrimaryKey();
        assertNotNull(primaryKey);
        assertEquals("PERSON_PK", primaryKey.getName());

        List<Column> columns = primaryKey.getColumnsOrdered();
        assertEquals(1, columns.size());
        assertEquals("ID", columns.get(0).getName());
    }

    /**
     * T_ADDRESS.PERSON_ID -> T_PERSON.ID
     *
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test060ForeignKey() throws Exception
    {
        Schema schema = this.generator.export(dataSource, this.schemaName, "T_ADDRESS");
        assertNotNull(schema);
        assertEquals(this.schemaName, schema.getName());

        Table table = schema.getTable("T_ADDRESS");
        assertNotNull(table);

        Column column = table.getColumn("PERSON_ID");
        assertNotNull(column);

        ForeignKey foreignKey = column.getForeignKey();
        assertNotNull(foreignKey);
        assertNotNull(foreignKey.getColumn());
        assertNotNull(foreignKey.getRefColumn());

        assertEquals("FK_PERSON", foreignKey.getName());
        assertEquals("PERSON_ID", foreignKey.getColumn().getName());
        assertEquals("T_ADDRESS", foreignKey.getColumn().getTable().getName());
        assertEquals("ID", foreignKey.getRefColumn().getName());
        assertEquals("T_PERSON", foreignKey.getRefColumn().getTable().getName());
    }

    /**
     * T_ADDRESS.PERSON_ID -> T_PERSON.ID
     *
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test070Indices() throws Exception
    {
        Schema schema = this.generator.export(dataSource, this.schemaName, "T_PERSON");
        assertNotNull(schema);
        assertEquals(this.schemaName, schema.getName());

        Table table = schema.getTable("T_PERSON");
        assertNotNull(table);

        // UniqueConstraint
        List<UniqueConstraint> uniqueConstraints = table.getUniqueConstraints();
        assertNotNull(uniqueConstraints);
        assertEquals(1, uniqueConstraints.size());

        UniqueConstraint uniqueConstraint = uniqueConstraints.get(0);
        assertEquals("PERSON_UNQ", uniqueConstraint.getName());

        List<Column> columns = uniqueConstraint.getColumnsOrdered();
        assertNotNull(columns);
        assertEquals(2, columns.size());
        assertEquals("NAME", columns.get(0).getName());
        assertEquals("VORNAME", columns.get(1).getName());

        // Index
        List<Index> indices = table.getIndices();
        assertNotNull(indices);
        assertEquals(1, indices.size());

        Index index = indices.get(0);
        assertEquals("PERSON_IDX", index.getName());

        columns = index.getColumnsOrdered();
        assertNotNull(columns);
        assertEquals(1, columns.size());
        assertEquals("NAME", columns.get(0).getName());
    }
}
