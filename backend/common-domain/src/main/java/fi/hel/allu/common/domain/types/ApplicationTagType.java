package fi.hel.allu.common.domain.types;

/**
 * Application tags that may be added to application.
 */
public enum ApplicationTagType {
  ADDITIONAL_INFORMATION_REQUESTED,   // täydennyspyyntö lähetetty
  STATEMENT_REQUESTED,                // lausunnolla
  DEPOSIT_REQUESTED,                  // vakuus määritetty
  DEPOSIT_PAID,                       // vakuus suoritettu
  PRELIMINARY_INSPECTION_REQUESTED,   // aloituskatselmointipyyntö lähetetty
  PRELIMINARY_INSPECTION_DONE,        // aloituskatselmus suoritettu
  FINAL_INSPECTION_AGREED,            // loppukatselmus sovittu
  FINAL_INSPECTION_DONE,              // loppukatselmus suoritettu
  WAITING,                            // odottaa. Hakemus odottaa lisätietoa, esimerkiksi selvitystä, mikä estää hakemuksen etenemisen
  COMPENSATION_CLARIFICATION,         // hyvitysselvitys. käytetään esim. hyvityslaskujen selvittämisen aikana
  PAYMENT_BASIS_CORRECTION            // maksuperusteet korjattava
}
