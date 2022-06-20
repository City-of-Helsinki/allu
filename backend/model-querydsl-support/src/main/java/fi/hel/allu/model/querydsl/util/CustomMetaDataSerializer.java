package fi.hel.allu.model.querydsl.util;

import com.querydsl.codegen.Property;
import com.querydsl.codegen.TypeMappings;
import com.querydsl.sql.codegen.MetaDataSerializer;
import com.querydsl.sql.codegen.NamingStrategy;
import com.querydsl.sql.codegen.SQLCodegenModule;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * querydsl library on spring boot 2.6 is broken on postgresql. generation of imports creates wrong path.
 * Needed custom implementation where generation creates correct path.
 */
public class CustomMetaDataSerializer extends MetaDataSerializer {

    @Inject
    public CustomMetaDataSerializer(TypeMappings typeMappings,
                                    NamingStrategy namingStrategy,
                                    @Named(SQLCodegenModule.INNER_CLASSES_FOR_KEYS) boolean innerClassesForKeys,
                                    @Named(SQLCodegenModule.IMPORTS) Set<String> imports,
                                    @Named(SQLCodegenModule.COLUMN_COMPARATOR) Comparator<Property> columnComparator,
                                    @Named(SQLCodegenModule.ENTITYPATH_TYPE) Class<?> entityPathType,
                                    @Named(SQLCodegenModule.GENERATED_ANNOTATION_CLASS) Class<? extends Annotation> generatedAnnotationClass) {
        super(typeMappings,
              namingStrategy,
              innerClassesForKeys,
              new HashSet<>(Collections.singletonList("com.querydsl.spatial")),
              columnComparator,
              entityPathType,
              generatedAnnotationClass);
    }

}