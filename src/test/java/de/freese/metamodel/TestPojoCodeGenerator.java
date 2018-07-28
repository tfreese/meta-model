/**
 * Created: 08.07.2018
 */

package de.freese.metamodel;

import java.nio.file.Files;
import java.nio.file.Paths;
import javax.sql.DataSource;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import de.freese.metamodel.codegen.JavaCodeWriter;
import de.freese.metamodel.metagen.HsqldbMetaExporter;
import de.freese.metamodel.modelgen.mapping.JavaTypeMapping;

/**
 * @author Thomas Freese
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPojoCodeGenerator
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
     * Erstellt ein neues {@link TestPojoCodeGenerator} Object.
     */
    public TestPojoCodeGenerator()
    {
        super();
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    public void test010Create() throws Exception
    {
        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.setMetaExporter(new HsqldbMetaExporter());
        codeGenerator.setTypeMapping(new JavaTypeMapping());
        codeGenerator.setCodeWriter(new JavaCodeWriter());
        // codeGenerator.setNamingStrategy(new DefaultNamingStrategy());
        codeGenerator.setSchemaName("PUBLIC");
        codeGenerator.setTargetFolder(Paths.get("src/test/generated"));
        codeGenerator.setPackageName("test.pojo");
        codeGenerator.setSerializeable(true);
        codeGenerator.setValidationAnnotations(true);
        codeGenerator.setAddFullConstructor(true);

        codeGenerator.generate(dataSource);

        Assert.assertTrue(Files.exists(Paths.get("src/test/generated", "test", "pojo")));
    }
}
