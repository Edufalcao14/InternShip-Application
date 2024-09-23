package be.vinci.pae.business.domain.supervisor;

import be.vinci.pae.business.dto.CompanyDTO;
import java.util.Objects;

/**
 * Implémentation de l'interface Supervisor, représentant un responsable de stage dans le domaine
 * métier.
 */
public class SupervisorImpl implements Supervisor {

  private int id;
  private String firstName;
  private String lastName;
  private String phoneNumber;
  private String email;
  private int version;
  private int companyId;
  private CompanyDTO companyDTO;

  /**
   * {@inheritDoc}
   */
  @Override
  public int getId() {
    return this.id;
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
  public String getFirstName() {
    return this.firstName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getLastName() {
    return this.lastName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPhoneNumber() {
    return this.phoneNumber;
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
  public String getEmail() {
    return this.email;
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
  public int getCompanyId() {
    return this.companyId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCompanyId(int companyId) {
    this.companyId = companyId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CompanyDTO getCompanyDTO() {
    return this.companyDTO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setCompanyDTO(CompanyDTO companyDTO) {
    this.companyDTO = companyDTO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isASupervisorOfTheCompany(int companyId) {
    return this.companyId == companyId;
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
    if (!(o instanceof SupervisorImpl that)) {
      return false;
    }
    return id == that.id && version == that.version && companyId == that.companyId
        && Objects.equals(firstName, that.firstName) && Objects.equals(lastName,
        that.lastName) && Objects.equals(phoneNumber, that.phoneNumber)
        && Objects.equals(email, that.email) && Objects.equals(companyDTO,
        that.companyDTO);
  }

  /**
   * Renvoie un code de hachage pour cet objet.
   *
   * @return un code de hachage pour cet objet.
   */
  @Override
  public int hashCode() {
    return Objects.hash(id, firstName, lastName, phoneNumber, email, version, companyId,
        companyDTO);
  }

  /**
   * Renvoie une représentation sous forme de chaîne de caractères de cet objet.
   *
   * @return une représentation sous forme de chaîne de caractères de cet objet.
   */
  @Override
  public String toString() {
    return "SupervisorImpl{"
        + "id=" + id
        + ", firstName='" + firstName
        + ", lastName='" + lastName
        + ", phoneNumber='" + phoneNumber
        + ", email='" + email
        + ", version=" + version
        + ", companyId=" + companyId
        + ", companyDTO=" + companyDTO
        + '}';
  }
}
