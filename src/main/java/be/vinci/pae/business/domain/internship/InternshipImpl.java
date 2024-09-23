package be.vinci.pae.business.domain.internship;

import be.vinci.pae.business.dto.ContactDTO;
import be.vinci.pae.business.dto.SupervisorDTO;
import be.vinci.pae.business.dto.UserDTO;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * Implémentation de l'interface Internship, représentant un stage dans le domaine métier.
 */
public class InternshipImpl implements Internship {

  private int id;
  private int studentId;
  private UserDTO studentDTO;
  private int supervisorId;
  private SupervisorDTO supervisorDTO;
  private int contactId;
  private ContactDTO contactDTO;
  private String subject;
  private String signatureDate;
  private String academicYear;
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
  public int getStudentId() {
    return studentId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setStudentId(int studentId) {
    this.studentId = studentId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserDTO getStudentDTO() {
    return studentDTO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setStudentDTO(UserDTO studentDTO) {
    this.studentDTO = studentDTO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getSupervisorId() {
    return supervisorId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSupervisorId(int supervisorId) {
    this.supervisorId = supervisorId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SupervisorDTO getSupervisorDTO() {
    return supervisorDTO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSupervisorDTO(SupervisorDTO supervisorDTO) {
    this.supervisorDTO = supervisorDTO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getContactId() {
    return contactId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setContactId(int contactId) {
    this.contactId = contactId;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ContactDTO getContactDTO() {
    return contactDTO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setContactDTO(ContactDTO contactDTO) {
    this.contactDTO = contactDTO;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSubject() {
    return subject;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getSignatureDate() {
    return signatureDate;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSignatureDate(String signatureDate) {
    this.signatureDate = signatureDate;
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
    return version;
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
  public boolean checkSignatureDateValidity(String signatureDate) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    dateFormat.setLenient(false); // Pour éviter des conversions automatiques de dates erronées
    try {
      // Tente de parser la date
      dateFormat.parse(signatureDate);
      return true; // La date est valide et est au bon format
    } catch (ParseException e) {
      // La date n'est pas valide ou n'est pas au bon format
      return false;
    }
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
    if (!(o instanceof InternshipImpl that)) {
      return false;
    }
    return id == that.id && studentId == that.studentId && supervisorId == that.supervisorId
        && contactId == that.contactId && version == that.version && Objects.equals(
        studentDTO, that.studentDTO) && Objects.equals(supervisorDTO, that.supervisorDTO)
        && Objects.equals(contactDTO, that.contactDTO) && Objects.equals(subject,
        that.subject) && Objects.equals(signatureDate, that.signatureDate)
        && Objects.equals(academicYear, that.academicYear);
  }

  /**
   * Renvoie un code de hachage pour cet objet.
   *
   * @return un code de hachage pour cet objet.
   */
  @Override
  public int hashCode() {
    return Objects.hash(id, studentId, studentDTO, supervisorId, supervisorDTO, contactId,
        contactDTO,
        subject, signatureDate, academicYear, version);
  }

  /**
   * Renvoie une représentation sous forme de chaîne de caractères de cet objet.
   *
   * @return une représentation sous forme de chaîne de caractères de cet objet.
   */
  @Override
  public String toString() {
    return "InternshipImpl{"
        + "id=" + id
        + ", studentId=" + studentId
        + ", studentDTO=" + studentDTO
        + ", supervisorId=" + supervisorId
        + ", supervisorDTO=" + supervisorDTO
        + ", contactId=" + contactId
        + ", contactDTO=" + contactDTO
        + ", subject='" + subject
        + ", signatureDate='" + signatureDate
        + ", academicYear='" + academicYear
        + ", version=" + version
        + '}';
  }
}
