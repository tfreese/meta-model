/**
 * Created: 22.04.2020
 */

package de.freese.metamodel.codegen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import org.apache.commons.lang3.StringUtils;
import de.freese.metamodel.codegen.model.ClassModel;
import de.freese.metamodel.codegen.model.FieldModel;
import de.freese.metamodel.metagen.model.Column;
import de.freese.metamodel.metagen.model.ForeignKey;
import de.freese.metamodel.metagen.model.Sequence;
import de.freese.metamodel.metagen.model.Table;
import de.freese.metamodel.metagen.model.UniqueConstraint;
import de.freese.metamodel.modelgen.mapping.ClassType;
import de.freese.metamodel.modelgen.mapping.Type;

/**
 * @author Thomas Freese
 */
public class JpaCodeGenerator extends PojoCodeGenerator
{
    /**
     * @see de.freese.metamodel.codegen.AbstractCodeGenerator#transformClassAnnotations(de.freese.metamodel.metagen.model.Table,
     *      de.freese.metamodel.codegen.model.ClassModel)
     */
    @Override
    protected void transformClassAnnotations(final Table table, final ClassModel classModel)
    {
        // Entity
        classModel.addImport(Entity.class);
        classModel.addAnnotation("@Entity");

        // Table
        classModel.addImport(javax.persistence.Table.class);

        StringBuilder sb = new StringBuilder();
        sb.append("@Table(");
        sb.append("name = \"").append(table.getName()).append("\"");
        sb.append(", schema = \"").append(table.getSchema().getName()).append("\"");

        if (!table.getUniqueConstraints().isEmpty())
        {
            classModel.addImport(javax.persistence.UniqueConstraint.class);
            sb.append(", uniqueConstraints = {");

            for (UniqueConstraint uc : table.getUniqueConstraints())
            {
                sb.append("@UniqueConstraint(name = \"").append(uc.getName()).append("\", columnNames = {");

                for (Iterator<Column> iterator = uc.getColumnsOrdered().iterator(); iterator.hasNext();)
                {
                    sb.append("\"").append(iterator.next().getName()).append("\"");

                    if (iterator.hasNext())
                    {
                        sb.append(", ");
                    }
                }

                sb.append("})");
            }

            sb.append("}");
        }

        sb.append(")");
        classModel.addAnnotation(sb.toString());

        // Cacheable
        classModel.addImport(Cacheable.class);
        classModel.addAnnotation("@Cacheable");

        // NamedNativeQuery
        classModel.addImport(NamedNativeQuery.class);
        String alias = classModel.getName().substring(0, 1).toLowerCase();

        sb = new StringBuilder();
        sb.append("@NamedNativeQuery(name = \"all").append(StringUtils.capitalize(classModel.getName())).append(".native\"");
        sb.append(", query = ").append(String.format("\"select %2$s.* from %1$s %2$s\")", table.getName(), alias));
        classModel.addAnnotation(sb.toString());

        super.transformClassAnnotations(table, classModel);
    }

    /**
     * @see de.freese.metamodel.codegen.PojoCodeGenerator#transformClassJavaDoc(de.freese.metamodel.metagen.model.Table,
     *      de.freese.metamodel.codegen.model.ClassModel)
     */
    @Override
    protected void transformClassJavaDoc(final Table table, final ClassModel classModel)
    {
        if (StringUtils.isNotBlank(table.getComment()))
        {
            classModel.addComment(table.getComment());
        }

        classModel.addComment("JPA-Entity für Tabelle " + table.getSchema().getName() + "." + table.getName() + ".");
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeGenerator#transformField(de.freese.metamodel.metagen.model.Column,
     *      de.freese.metamodel.codegen.model.ClassModel)
     */
    @Override
    protected void transformField(final Column column, final ClassModel classModel)
    {
        ForeignKey fk = column.getForeignKey();
        List<Column> reverseFKs = column.getReverseForeignKeys();

        if (column.isPrimaryKey() || (reverseFKs.isEmpty() && (fk == null)))
        {
            String fieldName = getNamingStrategy().getFieldName(column.getName());
            ClassType type = (ClassType) getTypeMapping().getType(column.getJdbcType(), column.isNullable());

            // Normales Attribut
            FieldModel fieldModel = classModel.addField(fieldName, type.getJavaClass());
            fieldModel.setDefaultValueAsString(type.getDefaultValueAsString());
            fieldModel.setPayload(column);

            transformFieldComments(column, fieldModel);
            transformFieldAnnotations(column, fieldModel);
        }

        if (fk != null)
        {
            // Anderes Objekt.
            String refClassName = getNamingStrategy().getClassName(fk.getRefColumn().getTable().getName());

            FieldModel fieldModel = classModel.addField(refClassName.toLowerCase(), getPackageName() + "." + refClassName);
            fieldModel.setAssoziationn(true);
            fieldModel.setPayload(column);

            classModel.addImport(ManyToOne.class);
            classModel.addImport(FetchType.class);
            classModel.addImport(JoinColumn.class);
            classModel.addImport(javax.persistence.ForeignKey.class);

            fieldModel.addAnnotation("@ManyToOne(fetch = FetchType.LAZY)");

            StringBuilder sb = new StringBuilder();
            sb.append("@JoinColumn(name = \"").append(column.getName()).append("\"");
            sb.append(", foreignKey = @ForeignKey(name = \"").append(fk.getName()).append("\")");
            sb.append(", nullable = false)");
            fieldModel.addAnnotation(sb.toString());

            transformFieldComments(column, fieldModel);
            transformFieldAnnotations(column, fieldModel);
        }

        if (!reverseFKs.isEmpty())
        {
            // 1:n Childs
            for (Column reverseFK : reverseFKs)
            {
                String refClassName = getNamingStrategy().getClassName(reverseFK.getTable().getName());

                FieldModel fieldModel = classModel.addField(refClassName.toLowerCase() + "es", getPackageName() + "." + refClassName);
                fieldModel.setAssoziationn(true);
                fieldModel.setCollection(true);
                fieldModel.setDefaultValueAsString("new ArrayList<>()");
                fieldModel.setPayload(column);

                classModel.addImport(List.class);
                classModel.addImport(ArrayList.class);
                classModel.addImport(OneToMany.class);
                classModel.addImport(FetchType.class);
                classModel.addImport(CascadeType.class);

                StringBuilder sb = new StringBuilder();
                sb.append("@OneToMany(mappedBy = \"").append(classModel.getName().toLowerCase());
                sb.append("\", fetch = FetchType.LAZY, orphanRemoval = true, cascade =");
                sb.append("{CascadeType.ALL}");
                sb.append(")");

                fieldModel.addAnnotation(sb.toString());

                transformFieldComments(reverseFK, fieldModel);
                transformFieldAnnotations(reverseFK, fieldModel);
            }
        }
    }

    /**
     * @see de.freese.metamodel.codegen.AbstractCodeGenerator#transformFieldAnnotations(de.freese.metamodel.metagen.model.Column,
     *      de.freese.metamodel.codegen.model.FieldModel)
     */
    @Override
    protected void transformFieldAnnotations(final Column column, final FieldModel fieldModel)
    {
        Type type = getTypeMapping().getType(column.getJdbcType(), column.isNullable());

        if (type.isAssoziation())
        {
            return;
        }

        // ID
        if (column.isPrimaryKey())
        {
            fieldModel.addImport(Id.class);
            fieldModel.addAnnotation("@Id");
        }

        // TODO Composite PrimaryKeys !

        // Column
        fieldModel.addImport(javax.persistence.Column.class);

        StringBuilder sb = new StringBuilder();
        sb.append("@Column(");
        sb.append("name = \"").append(column.getName()).append("\"");
        sb.append(", nullable = ").append(column.isNullable());

        if (column.isPrimaryKey())
        {
            sb.append(", unique = true");
        }

        if (column.hasSize())
        {
            sb.append(", length = ").append(column.getSize());
        }

        sb.append(")");
        fieldModel.addAnnotation(sb.toString());

        // Versuchen Sequence für Entity zu finden.
        if (column.isPrimaryKey())
        {
            // @formatter:off
           List<Sequence> sequences = column.getTable().getSchema().getSequences().stream()
                .filter(seq -> seq.getName().toLowerCase().contains(fieldModel.getClassModel().getName().toLowerCase()))
                .sorted(Comparator.comparing(seq -> seq.getName().length()))
                .collect(Collectors.toList());
            // @formatter:on

            Sequence sequence = null;

            if (!sequences.isEmpty())
            {
                // Wir nehmen die Sequence mit dem kürzesten Namen.
                sequence = sequences.get(0);
            }

            if (sequence != null)
            {
                fieldModel.addImport(SequenceGenerator.class);
                fieldModel.addImport(GeneratedValue.class);
                fieldModel.addImport(GenerationType.class);

                String generatorName = sequence.getName().toLowerCase() + "_gen";

                fieldModel.addAnnotation("@SequenceGenerator(name = \"" + generatorName + "\", sequenceName = \"" + sequence.getName() + "\")");
                fieldModel.addAnnotation("@GeneratedValue(generator = \"" + generatorName + "\", strategy = GenerationType.SEQUENCE)");
            }
        }

        // Access
        // classModel.addImport(Access.class);
        // classModel.addImport(AccessType.class);
        // fieldModel.addAnnotation("@Access(AccessType.FIELD)")

        super.transformFieldAnnotations(column, fieldModel);
    }
}
