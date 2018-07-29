/**
 * Created: 29.07.2018
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
import de.freese.metamodel.Config;
import de.freese.metamodel.metagen.model.Column;
import de.freese.metamodel.metagen.model.ForeignKey;
import de.freese.metamodel.metagen.model.Sequence;
import de.freese.metamodel.metagen.model.Table;
import de.freese.metamodel.metagen.model.UniqueConstraint;
import de.freese.metamodel.modelgen.ClassModel;
import de.freese.metamodel.modelgen.FieldModel;
import de.freese.metamodel.modelgen.mapping.AssoziationType;
import de.freese.metamodel.modelgen.mapping.Type;

/**
 * Java-Implementierung für JPA-Entities eines {@link CodeWriter}.
 *
 * @author Thomas Freese
 */
public class JpaCodeWriter extends PojoCodeWriter
{
    /**
     * Erstellt ein neues {@link JpaCodeWriter} Object.
     */
    public JpaCodeWriter()
    {
        super();
    }

    /**
     * @see de.freese.metamodel.codegen.PojoCodeWriter#createClassAnnotations(de.freese.metamodel.Config, de.freese.metamodel.modelgen.ClassModel)
     */
    @Override
    protected void createClassAnnotations(final Config config, final ClassModel classModel)
    {
        Table table = classModel.getTable();

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

        super.createClassAnnotations(config, classModel);
    }

    /**
     * @see de.freese.metamodel.codegen.PojoCodeWriter#createFieldAnnotations(de.freese.metamodel.Config, de.freese.metamodel.modelgen.FieldModel)
     */
    @Override
    protected void createFieldAnnotations(final Config config, final FieldModel fieldModel)
    {
        if (fieldModel.getType().isAssoziation())
        {
            return;
        }

        ClassModel classModel = fieldModel.getClassModel();
        Column column = fieldModel.getColumn();

        // ID
        if (column.isPrimaryKey())
        {
            classModel.addImport(Id.class);
            fieldModel.addAnnotation("@Id");
        }

        // TODO Composite PrimaryKeys !

        // Column
        classModel.addImport(javax.persistence.Column.class);

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
                .filter(seq -> seq.getName().toLowerCase().contains(classModel.getName().toLowerCase()))
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
                classModel.addImport(SequenceGenerator.class);
                classModel.addImport(GeneratedValue.class);
                classModel.addImport(GenerationType.class);

                String generatorName = sequence.getName().toLowerCase() + "_gen";

                fieldModel.addAnnotation("@SequenceGenerator(name = \"" + generatorName + "\", sequenceName = \"" + sequence.getName() + "\")");
                fieldModel.addAnnotation("@GeneratedValue(generator = \"" + generatorName + "\", strategy = GenerationType.SEQUENCE)");
            }
        }

        // Access
        // classModel.addImport(Access.class);
        // classModel.addImport(AccessType.class);
        // fieldModel.addAnnotation("@Access(AccessType.FIELD)");

        super.createFieldAnnotations(config, fieldModel);
    }

    /**
     * @see de.freese.metamodel.codegen.PojoCodeWriter#createFields(de.freese.metamodel.Config, de.freese.metamodel.modelgen.ClassModel)
     */
    @Override
    protected void createFields(final Config config, final ClassModel classModel)
    {
        for (Column column : classModel.getTable().getColumnsOrdered())
        {
            ForeignKey fk = column.getForeignKey();
            List<Column> reverseFKs = column.getReverseForeignKeys();

            if (column.isPrimaryKey() || (reverseFKs.isEmpty() && (fk == null)))
            {
                // Normales Attribut
                FieldModel fieldModel = new FieldModel(classModel, column);

                String fieldName = config.getNamingStrategy().getFieldName(column.getName());
                Type type = config.getTypeMapping().getType(column.getJdbcType(), column.isNullable());

                fieldModel.setName(fieldName);
                fieldModel.setType(type);

                createFieldComments(config, fieldModel);
                createFieldAnnotations(config, fieldModel);
            }

            if (fk != null)
            {
                // Anderes Objekt.
                String refClassName = config.getNamingStrategy().getClassName(fk.getRefColumn().getTable().getName());

                FieldModel fieldModel = new FieldModel(classModel, fk.getRefColumn());
                fieldModel.setName(refClassName.toLowerCase());
                fieldModel.setType(new AssoziationType(refClassName, "null"));

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

                createFieldComments(config, fieldModel);
                createFieldAnnotations(config, fieldModel);
            }

            if (!reverseFKs.isEmpty())
            {
                // 1:n Childs
                for (Column reverseFK : reverseFKs)
                {
                    String refClassName = config.getNamingStrategy().getClassName(reverseFK.getTable().getName());

                    FieldModel fieldModel = new FieldModel(classModel, reverseFK);
                    fieldModel.setName(refClassName.toLowerCase() + "es");

                    AssoziationType type = new AssoziationType("List<" + refClassName + ">", "new ArrayList<>()");
                    type.setCollection(true);
                    fieldModel.setType(type);

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

                    createFieldComments(config, fieldModel);
                    createFieldAnnotations(config, fieldModel);
                }
            }
        }
    }
}
