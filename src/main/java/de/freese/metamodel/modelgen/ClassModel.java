/**
 * Created: 29.07.2018
 */

package de.freese.metamodel.modelgen;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import de.freese.metamodel.metagen.model.Table;

/**
 * Definiert das Model einer Klasse.
 *
 * @author Thomas Freese
 */
public class ClassModel extends AbstractModel
{
    /**
    *
    */
    private final List<FieldModel> fields = new ArrayList<>();

    /**
    *
    */
    private final Set<String> imports = new TreeSet<>();

    /**
    *
    */
    private final List<Class<?>> interfaces = new ArrayList<>();

    /**
     *
     */
    private final Table table;

    /**
     * Erstellt ein neues {@link ClassModel} Object.
     *
     * @param table {@link Table}
     */
    public ClassModel(final Table table)
    {
        super();

        this.table = Objects.requireNonNull(table, "table required");
    }

    /**
     * @param fieldModel {@link FieldModel}
     */
    public void addField(final FieldModel fieldModel)
    {
        this.fields.add(fieldModel);
    }

    /**
     * @param clazz {@link Class}
     */
    public void addImport(final Class<?> clazz)
    {
        addImport(clazz.getName());
    }

    /**
     * @param className String
     */
    public void addImport(final String className)
    {
        this.imports.add(className);
    }

    /**
     * @param iface {@link Class}
     */
    public void addInterface(final Class<?> iface)
    {
        this.interfaces.add(iface);
        addImport(iface);
    }

    /**
     * @return {@link List}
     */
    public List<FieldModel> getFields()
    {
        return new ArrayList<>(this.fields);
    }

    /**
     * @return {@link Set}
     */
    public Set<String> getImports()
    {
        return this.imports;
    }

    /**
     * @return {@link List}
     */
    public List<Class<?>> getInterfaces()
    {
        return new ArrayList<>(this.interfaces);
    }

    /**
     * @return {@link Table}
     */
    public Table getTable()
    {
        return this.table;
    }
}
