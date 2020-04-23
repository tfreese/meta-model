/**
 * Created: 08.07.2018
 */

package de.freese.metamodel;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.metamodel.codegen.PojoCodeGenerator;
import de.freese.metamodel.codegen.writer.JavaCodeWriter;
import de.freese.metamodel.metagen.HsqldbMetaExporter;
import de.freese.metamodel.modelgen.mapping.JavaTypeMapping;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class TestPojoCodeGenerator
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
        Path path = Paths.get("src/test/generated", "test", "pojo");
        Files.deleteIfExists(path.resolve("Address.java"));
        Files.deleteIfExists(path.resolve("Person.java"));

        PojoCodeGenerator codeGenerator = new PojoCodeGenerator();
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

        assertTrue(Files.exists(path.resolve("Address.java")));
        assertTrue(Files.exists(path.resolve("Person.java")));
    }
}
