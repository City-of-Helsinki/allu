package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.DefaultMapper;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.PostalAddress;
import fi.hel.allu.model.domain.PostalAddressItem;
import fi.hel.allu.model.querydsl.ExcludingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QPostalAddress.postalAddress;

/**
 * Postal address database access.
 */
@Repository
public class PostalAddressDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<PostalAddress> postalAddressBean = bean(PostalAddress.class, postalAddress.all());

  @Transactional(readOnly = true)
  public Optional<PostalAddress> findById(int id) {
    PostalAddress pa = queryFactory.select(postalAddressBean).from(postalAddress).where(postalAddress.id.eq(id)).fetchOne();
    return Optional.ofNullable(pa);
  }

  @Transactional
  public PostalAddress insert(PostalAddress postalAddressData) {
    Integer id = queryFactory
        .insert(postalAddress)
        .populate(
            postalAddressData,
            new ExcludingMapper(ExcludingMapper.NullHandling.WITH_NULL_BINDINGS, Collections.singletonList(postalAddress.id)))
        .executeWithKey(postalAddress.id);
    if (id == null) {
      throw new QueryException("Failed to insert record");
    }
    return findById(id).get();
  }

  @Transactional
  public Integer insertIfNotNull(PostalAddressItem postalAddressItem) {
    Integer insertedPostalAddressId = null;
    if (postalAddressItem.getPostalAddress() != null) {
      insertedPostalAddressId = insert(postalAddressItem.getPostalAddress()).getId();
    }
    return insertedPostalAddressId;
  }


  @Transactional
  public PostalAddress update(PostalAddress postalAddressData) {
    long count = queryFactory
        .update(postalAddress).populate(postalAddressData, DefaultMapper.WITH_NULL_BINDINGS)
        .where(postalAddress.id.eq(postalAddressData.getId())).execute();
    if (count != 1) {
      throw new NoSuchEntityException("No such postal address", Integer.toString(postalAddressData.getId()));
    }
    return findById(postalAddressData.getId()).get();
  }

  /**
   * Maps updated data to the current data. If necessary, creates or updates postal address in to the database. Does not delete database
   * data, because deletes cannot be done if there's any rows referring to the deleted row.
   *
   * @param currentData       Current data to be updated.
   * @param updateData        The update data.
   * @return  Id of <code>PostalAddress</code> to be deleted from the database or <code>null</code> if delete is not required.
   */
  @Transactional
  public Integer mapAndUpdatePostalAddress(
      PostalAddressItem currentData, PostalAddressItem updateData) {
    Integer deletedPostalAddressId = null;
    if (currentData.getPostalAddress() != null && updateData.getPostalAddress() != null) {
      // update existing postal address
      updateData.getPostalAddress().setId(currentData.getPostalAddress().getId());
      currentData.setPostalAddress(update(updateData.getPostalAddress()));
    } else if (currentData.getPostalAddress() == null && updateData.getPostalAddress() != null) {
      // insert new postal address
      currentData.setPostalAddress(insert(updateData.getPostalAddress()));
    } else if (currentData.getPostalAddress() != null && updateData.getPostalAddress() == null) {
      // delete existing postal address
      deletedPostalAddressId = currentData.getPostalAddress().getId();
      currentData.setPostalAddress(null);
    } else {
      // no postal address in current or updated location
    }

    return deletedPostalAddressId;
  }

  @Transactional
  public void delete(List<Integer> ids) {
    queryFactory.delete(postalAddress).where(postalAddress.id.in(ids)).execute();
  }
}
