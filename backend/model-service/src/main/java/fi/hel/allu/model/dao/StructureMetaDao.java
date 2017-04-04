package fi.hel.allu.model.dao;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.meta.AttributeDataType;
import fi.hel.allu.model.domain.meta.AttributeMeta;
import fi.hel.allu.model.domain.meta.StructureMeta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QAttributeMeta.attributeMeta;
import static fi.hel.allu.QStructureMeta.structureMeta;

/**
 * Data access object for metadata structures.
 */
@Repository
public class StructureMetaDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<StructureMeta> structureMetaBean = bean(StructureMeta.class, structureMeta.all());
  final QBean<AttributeMeta> attributeMetaBean = bean(AttributeMeta.class, attributeMeta.all());

  /**
   * Get the latest metadata version
   *
   * @return Latest metadata version
   */
  @Transactional(readOnly = true)
  public Integer getLatestMetadataVersion() {
    return queryFactory.select(SQLExpressions.max(structureMeta.version)).from(structureMeta).fetchOne();
  }

  /**
   * Parse the complete metadata tree for an application type. Recurses into
   * each STRUCTURE type attribute it can find and puts all their attributes
   * into the result set, too. All found attributes are returned as a flat list,
   * so to prevent name clashes, the attribute names consist also of the path
   * into the attribute, for example "/applicant/postalAddress/streetAddress".
   *
   * The recursion can be guided by giving a set of path overrides that can used
   * to, for example, recurse into "EVENT" while handling the attribute
   * "/extension", instead of simply going into the statically defined
   * "ApplicationExtension" type.
   *
   *
   * @param typeName
   *          The name of the application type to parse
   * @param version
   *          Metadata version to use
   * @param pathOverrides
   *          Mapping of attributePath -> typeName to override the static types
   * @return Structure meta information
   */
  @Transactional(readOnly = true)
  public Optional<StructureMeta> findCompleteByApplicationType(String typeName, int version,
      Map<String, String> typeOverrides) {
    if (version > getLatestMetadataVersion()) {
      throw new NoSuchEntityException("No such metadata version");
    }
    return findCompleteInternal(typeName, version, typeOverrides);
  }

  /**
   * Parse the complete metadata tree for an application type. Recurses into
   * each STRUCTURE type attribute it can find and puts all their attributes
   * into the result set, too. All found attributes are returned as a flat list,
   * so to prevent name clashes, the attribute names consist also of the path
   * into the attribute, for example "/applicant/postalAddress/streetAddress".
   *
   * The recursion can be guided by giving a set of path overrides that can used
   * to, for example, recurse into "EVENT" while handling the attribute
   * "/extension", instead of simply going into the statically defined
   * "ApplicationExtension" type.
   *
   * This version uses the latest available version of the application type.
   *
   * @param typeName
   *          The name of the application type to parse
   * @param pathOverrides
   *          Mapping of attributePath -> typeName to override the static types
   * @return Structure meta information
   */
  @Transactional(readOnly = true)
  public Optional<StructureMeta> findCompleteByApplicationType(String typeName,
      Map<String, String> typeOverrides) {
    return findCompleteInternal(typeName, getLatestMetadataVersion(), typeOverrides);
  }

  /*
   * FindCompleteByApplicationType's implementation without the checks.
   */
  Optional<StructureMeta> findCompleteInternal(String typeName, int version,
      Map<String, String> typeOverrides) {
    Optional<StructureMeta> meta = findStructure(typeName, version);
    if (meta.isPresent()) {
      List<AttributeMeta> attributes = new ArrayList<>();
      recurseAttributes(meta.get().getId(), typeOverrides, "/", version, attributes);
      meta.get().setAttributes(attributes);
    }
    return meta;
  }

  /*
   * Find the structure metadata for the given type name and metadata version.
   */
  private Optional<StructureMeta> findStructure(String typeName, int metadataVersion) {
    return Optional.ofNullable(queryFactory.select(structureMetaBean).from(structureMeta)
        .where(structureMeta.typeName.eq(typeName).and(structureMeta.version.loe(metadataVersion)))
        .orderBy(structureMeta.version.desc()).fetchFirst());
  }

  /*
   * Recursively fetch all attributes for given structureType. Every attribute
   * name will be prefixed with given prefix when added into attributeList. If
   * an attribute type is STRUCTURE, instead of adding it into attributeList,
   * recursively add all its attributes. In recursion, if the attributes path is
   * found in typeOverrides, use the corresponding value as the recursion type.
   */
  private void recurseAttributes(int structureTypeId, Map<String, String> typeOverrides, String prefix,
      int metadataVersion, List<AttributeMeta> attributeList) {
    List<AttributeMeta> attributes = queryFactory.select(attributeMetaBean).from(attributeMeta)
        .where(attributeMeta.structureMetaId.eq(structureTypeId)).fetch();
    attributes.forEach(a -> doAttribute(a, typeOverrides, prefix, metadataVersion, attributeList));
  }

  /*
   * Handle single attribute in a structure. If attribute is a list, its name is
   * appended with "/*". If the attribute's data type (or list's element type)
   * is a structural type, recurse.
   */
  private void doAttribute(AttributeMeta attribute, Map<String, String> typeOverrides, String prefix,
      int metadataVersion, List<AttributeMeta> attributeList) {
    String attributeFullName = prefix + attribute.getName();
    AttributeDataType dataType = attribute.getDataType();
    if (dataType == AttributeDataType.LIST) {
      dataType = attribute.getListType();
      attributeFullName = attributeFullName + "/*";
    }
    attribute.setName(attributeFullName);
    attributeList.add(attribute);
    if (dataType == AttributeDataType.STRUCTURE || dataType == AttributeDataType.ENUMERATION) {
      // Structure or enum, so recurse deeper
      int childStructureId = attribute.getStructureAttribute();
      if (typeOverrides.containsKey(attributeFullName)) {
        // Type override defined, find its type id
        String overrideType = typeOverrides.get(attributeFullName);
        childStructureId = findStructure(overrideType, metadataVersion).map(s -> s.getId())
            .orElseThrow(() -> new NoSuchEntityException("No metadata for type", overrideType));
      }
      recurseAttributes(childStructureId, typeOverrides, attributeFullName + "/", metadataVersion, attributeList);
    }
  }
}
