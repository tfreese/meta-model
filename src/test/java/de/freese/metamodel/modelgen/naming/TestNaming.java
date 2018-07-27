/**
 * Created: 22.07.2018
 */

package de.freese.metamodel.modelgen.naming;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * TestCase für die Namenskonvertierung.
 *
 * @author Thomas Freese
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestNaming
{
    /**
     * Erstellt ein neues {@link TestNaming} Object.
     */
    public TestNaming()
    {
        super();
    }

    /**
     *
     */
    @Test
    public void testClassNaming()
    {
        NamingStrategy namingStrategy = new DefaultNamingStrategy();

        Assert.assertEquals("Test", namingStrategy.getClassName("Test"));
        Assert.assertEquals("Test", namingStrategy.getClassName("TEST"));
        Assert.assertEquals("Test", namingStrategy.getClassName("T_Test"));
        Assert.assertEquals("Test", namingStrategy.getClassName("T_TEST"));
        Assert.assertEquals("TestTest", namingStrategy.getClassName("Test_Test"));
        Assert.assertEquals("TestTest", namingStrategy.getClassName("TEST_TEST"));
    }

    /**
    *
    */
    @Test
    public void testFieldNaming()
    {
        NamingStrategy namingStrategy = new DefaultNamingStrategy();

        Assert.assertEquals("test", namingStrategy.getFieldName("Test"));
        Assert.assertEquals("test", namingStrategy.getFieldName("TEST"));
        Assert.assertEquals("test", namingStrategy.getFieldName("T_Test"));
        Assert.assertEquals("test", namingStrategy.getFieldName("T_TEST"));
        Assert.assertEquals("testTest", namingStrategy.getFieldName("Test_Test"));
        Assert.assertEquals("testTest", namingStrategy.getFieldName("TEST_TEST"));
    }
}
