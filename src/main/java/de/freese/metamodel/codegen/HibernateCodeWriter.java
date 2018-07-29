/**
 * Created: 29.07.2018
 */

package de.freese.metamodel.codegen;

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import de.freese.metamodel.Config;
import de.freese.metamodel.modelgen.ClassModel;
import de.freese.metamodel.modelgen.FieldModel;

/**
 * Java-Implementierung für Hibernate-Entities eines {@link CodeWriter}.
 *
 * @author Thomas Freese
 */
public class HibernateCodeWriter extends JpaCodeWriter
{
    /**
     * Erstellt ein neues {@link HibernateCodeWriter} Object.
     */
    public HibernateCodeWriter()
    {
        super();
    }

    /**
     * @see de.freese.metamodel.codegen.JpaCodeWriter#createClassAnnotations(de.freese.metamodel.Config, de.freese.metamodel.modelgen.ClassModel)
     */
    @Override
    protected void createClassAnnotations(final Config config, final ClassModel classModel)
    {
        super.createClassAnnotations(config, classModel);

        String className = classModel.getName();

        classModel.addImport(DynamicInsert.class);
        classModel.addAnnotation("@DynamicInsert");

        classModel.addImport(DynamicUpdate.class);
        classModel.addAnnotation("@DynamicUpdate");

        classModel.addImport(Cache.class);
        classModel.addImport(CacheConcurrencyStrategy.class);
        classModel.addAnnotation("@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = \"" + className.toLowerCase() + "\")");

        classModel.addImport(NamedQueries.class);
        classModel.addImport(NamedQuery.class);
        classModel.addImport(QueryHint.class);

        String alias = className.substring(0, 1).toLowerCase();

        StringBuilder sb = new StringBuilder();

        sb.append("@NamedQueries({");
        sb.append("@NamedQuery(name = \"all").append(className).append("\"");
        sb.append(", query = ").append(String.format("\"select %2$s from %1$s %2$s\"", className, alias));
        sb.append(", hints = {@QueryHint(name = \"org.hibernate.cacheable\", value = \"true\")})");
        sb.append("})");
        classModel.addAnnotation(sb.toString());
    }

    /**
     * @see de.freese.metamodel.codegen.JpaCodeWriter#createFieldAnnotations(de.freese.metamodel.Config, de.freese.metamodel.modelgen.FieldModel)
     */
    @Override
    protected void createFieldAnnotations(final Config config, final FieldModel fieldModel)
    {
        super.createFieldAnnotations(config, fieldModel);

        if (fieldModel.getType().isAssoziation())
        {
            // Assoziation = Collections und Objekt-Referenzen.
            fieldModel.getClassModel().addImport(CacheConcurrencyStrategy.class);
            fieldModel.getClassModel().addImport(Cache.class);

            if (fieldModel.getType().isCollection())
            {
                fieldModel.getClassModel().addImport(Fetch.class);
                fieldModel.getClassModel().addImport(FetchMode.class);

                fieldModel.addAnnotation("@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = \"collections\")");
                fieldModel.addAnnotation("@Fetch(FetchMode.SELECT)");
            }
            else
            {
                fieldModel.addAnnotation("@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = \"" + fieldModel.getName() + "\")");
            }
        }
    }
}
