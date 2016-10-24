package fi.hel.allu.model.pricing;

public enum DiscountReason {
  EKOKOMPASSI(30), // Applicant has "Ekokompassi"
  URHEILUTAPAHTUMA(50), // Open to public but with heavy
                        // structures or attendance fee
  YLEISHYODYLLINEN(100), // Charity or citizen organization's common-good event
  TAIDE_JA_KULTTUURI(100), // Art an culture event with light structures
  MAKSUTON_URHEILU(100), // Sports with light structures and without attendance
                         // fee
  ASUKASYHDISTYS(100), // residents or district association
  AATTEELLINEN(100), // religious or ideological association
  KAUPUNGIN(100), // city orginizes or hosts
  TILATAIDE(100), // environmental art
  NUORISOJARJESTO(100), // youth association
  YKSITYISHLO_MERKKIPV(100), // private person's red letter day
  PUOLUSTUSVOIMAT(100); // armed forces

  private int discountPercentage;

  private DiscountReason(int discountPercentage) {
    this.discountPercentage = discountPercentage;
  }

  public int getDiscountPercentgage() {
    return discountPercentage;
  }
}
