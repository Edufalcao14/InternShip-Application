package be.vinci.pae.business.domain.company;

import java.util.Objects;

/**
 * Implémentation de l'interface Company, représentant un utilisateur dans le domaine métier.
 */
public class CompanyImpl implements Company {

  private int id;
  private String name;
  private String designation;
  private String address;
  private String email;
  private String phoneNumber;
  private String blacklistReason;
  private boolean isBlacklisted;
  private int version;
  private int studentsNumber;

  /**
   * {@inheritDoc}
   */
  @Override
  public int getId() {
    return id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(int id) {
    this.id = id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setName(String name) {
    this.name = name;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDesignation() {
    return designation;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDesignation(String designation) {
    this.designation = designation;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAddress() {
    return address;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAddress(String address) {
    this.address = address;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getEmail() {
    return email;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPhoneNumber() {
    return phoneNumber;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getBlacklistReason() {
    return blacklistReason;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setBlacklistReason(String blacklistReason) {
    this.blacklistReason = blacklistReason;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isBlacklisted() {
    return isBlacklisted;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setBlacklisted(boolean isBlacklisted) {
    this.isBlacklisted = isBlacklisted;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getVersion() {
    return this.version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(int version) {
    this.version = version;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getStudentsNumber() {
    return studentsNumber;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setStudentsNumber(int studentsNumber) {
    this.studentsNumber = studentsNumber;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean checkEmailValidity(String email) {
    return email.matches("^[^@]+@[^@]+\\.[a-zA-Z]{2,}$");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean checkPhoneNumberValidity(String phoneNumber) {
    return phoneNumber.matches("\\d{10}") || phoneNumber.matches("\\d{9}");
  }

  /**
   * Compare cet objet avec un autre objet et renvoie true si les objets sont égaux.
   *
   * @param o l'objet à comparer.
   * @return true si les objets sont égaux, false sinon.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof CompanyImpl company)) {
      return false;
    }
    return id == company.id && isBlacklisted == company.isBlacklisted && version == company.version
        && studentsNumber == company.studentsNumber && Objects.equals(name, company.name)
        && Objects.equals(designation, company.designation) && Objects.equals(
        address, company.address) && Objects.equals(email, company.email)
        && Objects.equals(phoneNumber, company.phoneNumber) && Objects.equals(
        blacklistReason, company.blacklistReason);
  }

  /**
   * Renvoie un code de hachage pour cet objet.
   *
   * @return un code de hachage pour cet objet.
   */
  @Override
  public int hashCode() {
    return Objects.hash(id, name, designation, address, email, phoneNumber, blacklistReason,
        isBlacklisted, version, studentsNumber);
  }


  /**
   * Renvoie une représentation sous forme de chaîne de caractères de cet objet.
   *
   * @return une représentation sous forme de chaîne de caractères de cet objet.
   */
  @Override
  public String toString() {
    return "CompanyImpl{"
        + "id=" + id
        + ", name='" + name
        + ", designation='" + designation
        + ", address='" + address
        + ", email='" + email
        + ", phone_number='" + phoneNumber
        + ", blacklist_reason='" + blacklistReason
        + ", is_blacklisted=" + isBlacklisted
        + ", version=" + version
        + ", students_number =" + studentsNumber
        + '}';
  }
}
