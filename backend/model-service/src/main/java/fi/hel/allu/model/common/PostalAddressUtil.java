package fi.hel.allu.model.common;

import com.querydsl.core.Tuple;
import fi.hel.allu.model.domain.PostalAddress;
import fi.hel.allu.model.domain.PostalAddressItem;

/**
 * Helper class for mapping postal address.
 */
public class PostalAddressUtil {
  /**
   * Maps tuple (PostalAddressItem, PostalAddress) by setting the <code>PostalAddress</code> to the <code>PostalAddressItem</code>.
   *
   * @param itemPostalAddress Tuple (PostalAddressItem, PostalAddress) to be mapped.
   * @return The tuple given as parameter.
   */
  public static Tuple mapPostalAddress(Tuple itemPostalAddress) {
    PostalAddress postalAddress = itemPostalAddress.get(1, fi.hel.allu.model.domain.PostalAddress.class);
    if (postalAddress != null && postalAddress.getId() != null) {
      itemPostalAddress.get(0, PostalAddressItem.class).setPostalAddress(postalAddress);
    }
    return itemPostalAddress;
  }
}
