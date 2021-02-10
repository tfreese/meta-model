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
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import de.freese.metamodel.codegen.PojoCodeGenerator;
import de.freese.metamodel.codegen.writer.JavaCodeWriter;
import de.freese.metamodel.metagen.HsqldbMetaExporter;
import de.freese.metamodel.modelgen.mapping.JavaTypeMapping;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestPojoCodeGenerator
{
    /**
    *
    */
    private static DataSource dataSource = null;

    /**
     * @throws Exception Falls was schief geht.
     */
    @AfterAll
    static void afterAll() throws Exception
    {
        TestUtil.closeDataSource(dataSource);
    }

    /**
    *
    */
    @BeforeAll
    static void beforeAll()
    {
        dataSource = TestUtil.createHsqlDBDataSource("jdbc:hsqldb:res:hsqldb/person;create=false;readonly=true");
    }

    /**
     * @throws Exception Falls was schief geht.
     */
    @Test
    @Order(1)
    void testCreate() throws Exception
    {
        Path path = Paths.get("src/test/generated", "test", "pojo");
        Files.deleteIfExists(path.resolve("Address.java"));
        Files.deleteIfExists(path.resolve("Person.java"));

        PojoCodeGenerator codeGenerator = new PojoCodeGenerator();
        codeGenerator.setMetaExporter(new HsqldbMetaExporter());
        codeGenerator.setTypeMapping(new JavaTypeMapping());
        codeGenerator.setCodeWriter(new JavaCodeWriter());
        // codeGenerator.setNamingStrategy(new DefaultNamingStrategy());
        codeGenerator.setSchemaName("");
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
