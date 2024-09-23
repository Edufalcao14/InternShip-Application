package be.vinci.pae.business.domain.user;

import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.FatalExceptionType;
import be.vinci.pae.utils.logging.LogMarker;
import be.vinci.pae.utils.logging.MyLogger;
import java.util.Objects;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Implémentation de l'interface User, représentant un utilisateur dans le domaine métier.
 */
public class UserImpl implements User {

  private int id;
  private String lastName;
  private String firstName;
  private String password;
  private String phoneNumber;
  private String email;
  private String registrationDate;
  private Role role;
  private String academicYear;
  private boolean hasInternship;
  private int version;

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
  public String getLastName() {
    return lastName;
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
  public String getFirstName() {
    return firstName;
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
  public String getPassword() {
    return password;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setPassword(String password) {
    this.password = password;
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
  public String getRegistrationDate() {
    return registrationDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setRegistrationDate(String inscriptionDate) {
    this.registrationDate = inscriptionDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Role getRole() {
    return role;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setRole(Role role) {
    this.role = role;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAcademicYear() {
    return academicYear;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAcademicYear(String academicYear) {
    this.academicYear = academicYear;
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
  public boolean hasInternship() {
    return this.hasInternship;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setHasInternship(boolean hasInternship) {
    this.hasInternship = hasInternship;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean checkPassword(String password) {
    try {
      return BCrypt.checkpw(password, this.password);
    } catch (Exception e) {
      MyLogger.logWarn(LogMarker.SERVER, "Erreur lors de la vérification du mot-de-passe", e);
      return false;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean checkPasswordValidity(String password) {
    if (password.length() < 5) {
      return false;
    }
    return password.matches("^(?=.*[0-9]).{5,}$");
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException {@link FatalExceptionType#SERVER} si une erreur s'est produite lors du
   *                        cryptage.
   */
  @Override
  public String setEncryptedPassword(String password) {
    try {
      this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw new FatalException("Erreur lors du cryptage du mot-de-passe.",
          FatalExceptionType.SERVER);
    }
    return this.password;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean checkEmailValidity(String email) {
    return email.endsWith("@student.vinci.be") || email.endsWith("@vinci.be");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean checkPhoneNumberValidity(String phoneNumber) {
    return phoneNumber.matches("\\d{10}");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean checkRoleValidity(String role) {
    try {
      Role.valueOf(role);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isRoleValidForEmail(String email, Role role) {
    if (email.endsWith("@student.vinci.be")) {
      return role.equals(Role.ETUDIANT);
    } else if (email.endsWith("@vinci.be")) {
      return role.equals(Role.PROFESSEUR) || role.equals(Role.ADMINISTRATIF);
    }
    return false;
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
    if (!(o instanceof UserImpl user)) {
      return false;
    }
    return id == user.id && hasInternship == user.hasInternship && version == user.version
        && Objects.equals(lastName, user.lastName) && Objects.equals(firstName,
        user.firstName) && Objects.equals(password, user.password)
        && Objects.equals(phoneNumber, user.phoneNumber) && Objects.equals(email,
        user.email) && Objects.equals(registrationDate, user.registrationDate)
        && role == user.role && Objects.equals(academicYear, user.academicYear);
  }

  /**
   * Renvoie un code de hachage pour cet objet.
   *
   * @return un code de hachage pour cet objet.
   */
  @Override
  public int hashCode() {
    return Objects.hash(id, lastName, firstName, password, phoneNumber, email, registrationDate,
        role,
        academicYear, hasInternship, version);
  }


  /**
   * Renvoie une représentation sous forme de chaîne de caractères de cet objet.
   *
   * @return une représentation sous forme de chaîne de caractères de cet objet.
   */
  @Override
  public String toString() {
    return "UserImpl{"
        + "id=" + id
        + ", lastName='" + lastName
        + ", firstName='" + firstName
        + ", password='" + password
        + ", phoneNumber='" + phoneNumber
        + ", email='" + email
        + ", registrationDate='" + registrationDate
        + ", role=" + role
        + ", academicYear='" + academicYear
        + ", hasInternship=" + hasInternship
        + ", version ='" + version
        + '}';
  }
}
