/**
 * Created: 08.07.2018
 */

package de.freese.metamodel.metagen;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.sql.DataSource;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import de.freese.metamodel.TestUtil;
import de.freese.metamodel.metagen.HsqldbMetaExporter;
import de.freese.metamodel.metagen.MetaExporter;
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
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestHsqlPersonDb
{
    /**
    *
    */
    private static DataSource dataSource = null;

    /**
     * @throws Exception Falls was schief geht.
     */
    @AfterClass
    public static void afterClass() throws Exception
    {
        TestUtil.closeDataSource(dataSource);
    }

    /**
    *
    */
    @BeforeClass
    public static void beforeClass()
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
        Assert.assertNotNull(schema);
        Assert.assertEquals(this.schemaName, schema.getName());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test020Sequences() throws Exception
    {
        Schema schema = this.generator.export(dataSource, this.schemaName, null);
        Assert.assertNotNull(schema);
        Assert.assertEquals(this.schemaName, schema.getName());

        List<Sequence> sequences = schema.getSequences();
        Assert.assertNotNull(sequences);
        Assert.assertEquals(2, sequences.size());

        Collections.sort(sequences, Comparator.comparing(Sequence::getName));
        Assert.assertEquals("ADDRESS_SEQ", sequences.get(0).getName());
        Assert.assertEquals("PERSON_SEQ", sequences.get(1).getName());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test030Table() throws Exception
    {
        Schema schema = this.generator.export(dataSource, this.schemaName, null);
        Assert.assertNotNull(schema);
        Assert.assertEquals(this.schemaName, schema.getName());

        List<Table> tables = schema.getTables();
        Assert.assertNotNull(tables);
        Assert.assertEquals(2, tables.size());

        Collections.sort(tables, Comparator.comparing(Table::getName));
        Assert.assertEquals("T_ADDRESS", tables.get(0).getName());
        Assert.assertEquals("T_PERSON", tables.get(1).getName());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test040Columns() throws Exception
    {
        Schema schema = this.generator.export(dataSource, this.schemaName, "T_PERSON");
        Assert.assertNotNull(schema);
        Assert.assertEquals(this.schemaName, schema.getName());

        List<Table> tables = schema.getTables();
        Assert.assertNotNull(tables);
        Assert.assertEquals(1, tables.size());

        Assert.assertEquals("T_PERSON", tables.get(0).getName());

        List<Column> columns = tables.get(0).getColumnsOrdered();
        Assert.assertNotNull(columns);
        Assert.assertEquals(3, columns.size());

        Assert.assertEquals("ID", columns.get(0).getName());
        Assert.assertEquals("NAME", columns.get(1).getName());
        Assert.assertEquals("VORNAME", columns.get(2).getName());
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test050PrimaryKey() throws Exception
    {
        Schema schema = this.generator.export(dataSource, this.schemaName, "T_PERSON");
        Assert.assertNotNull(schema);
        Assert.assertEquals(this.schemaName, schema.getName());

        Table table = schema.getTable("T_PERSON");
        Assert.assertNotNull(table);

        PrimaryKey primaryKey = table.getPrimaryKey();
        Assert.assertNotNull(primaryKey);
        Assert.assertEquals("PERSON_PK", primaryKey.getName());

        List<Column> columns = primaryKey.getColumnsOrdered();
        Assert.assertEquals(1, columns.size());
        Assert.assertEquals("ID", columns.get(0).getName());
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
        Assert.assertNotNull(schema);
        Assert.assertEquals(this.schemaName, schema.getName());

        Table table = schema.getTable("T_ADDRESS");
        Assert.assertNotNull(table);

        Column column = table.getColumn("PERSON_ID");
        Assert.assertNotNull(column);

        ForeignKey foreignKey = column.getForeignKey();
        Assert.assertNotNull(foreignKey);
        Assert.assertNotNull(foreignKey.getColumn());
        Assert.assertNotNull(foreignKey.getRefColumn());

        Assert.assertEquals("FK_PERSON", foreignKey.getName());
        Assert.assertEquals("PERSON_ID", foreignKey.getColumn().getName());
        Assert.assertEquals("T_ADDRESS", foreignKey.getColumn().getTable().getName());
        Assert.assertEquals("ID", foreignKey.getRefColumn().getName());
        Assert.assertEquals("T_PERSON", foreignKey.getRefColumn().getTable().getName());
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
        Assert.assertNotNull(schema);
        Assert.assertEquals(this.schemaName, schema.getName());

        Table table = schema.getTable("T_PERSON");
        Assert.assertNotNull(table);

        // UniqueConstraint
        List<UniqueConstraint> uniqueConstraints = table.getUniqueConstraints();
        Assert.assertNotNull(uniqueConstraints);
        Assert.assertEquals(1, uniqueConstraints.size());

        UniqueConstraint uniqueConstraint = uniqueConstraints.get(0);
        Assert.assertEquals("PERSON_UNQ", uniqueConstraint.getName());

        List<Column> columns = uniqueConstraint.getColumnsOrdered();
        Assert.assertNotNull(columns);
        Assert.assertEquals(2, columns.size());
        Assert.assertEquals("NAME", columns.get(0).getName());
        Assert.assertEquals("VORNAME", columns.get(1).getName());

        // Index
        List<Index> indices = table.getIndices();
        Assert.assertNotNull(indices);
        Assert.assertEquals(1, indices.size());

        Index index = indices.get(0);
        Assert.assertEquals("PERSON_IDX", index.getName());

        columns = index.getColumnsOrdered();
        Assert.assertNotNull(columns);
        Assert.assertEquals(1, columns.size());
        Assert.assertEquals("NAME", columns.get(0).getName());
    }
}
