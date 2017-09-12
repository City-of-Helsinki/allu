export enum ApplicationTagType {
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
  PAYMENT_BASIS_CORRECTION,            // maksuperusteet korjattava
  OPERATIONAL_CONDITION_REPORTED,     // toiminnallinen kunto ilmoitettu
  OPERATIONAL_CONDITION_ACCEPTED,     // toiminnallinen kunto hyväksytty
  OPERATIONAL_CONDITION_REJECTED,     // toiminnallinen kunto hylätty
  WORK_READY_REPORTED,                // työn valmistuminen ilmoitettu
  WORK_READY_ACCEPTED,                // työn valmistuminen hyväksytty
  WORK_READY_REJECTED                 // työn valmistuminen hylätty
}
