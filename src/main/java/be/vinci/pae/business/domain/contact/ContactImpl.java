package be.vinci.pae.business.domain.contact;

import be.vinci.pae.business.dto.CompanyDTO;
import be.vinci.pae.business.dto.UserDTO;
import java.util.Objects;

/**
 * Implémentation de l'interface Contact, représentant un contact dans le domaine métier.
 */
public class ContactImpl implements Contact {

  private int id;
  private int companyId;
  private CompanyDTO companyDTO;
  private int studentId;
  private UserDTO studentDTO;
  private State state;
  private Place meetingPlace;
  private String refusalReason;
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
  public int getCompanyId() {
    return companyId;
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
  public State getState() {
    return state;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setState(State state) {
    this.state = state;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Place getMeetingPlace() {
    return meetingPlace;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setMeetingPlace(Place meetingPlace) {
    this.meetingPlace = meetingPlace;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getRefusalReason() {
    return refusalReason;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setRefusalReason(String refusalReason) {
    this.refusalReason = refusalReason;
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
  public boolean checkPreviousState(State state) {
    if (this.state.equals(State.INITIE) && state.equals(State.PRIS)) {
      return true;
    }
    if (this.state.equals(State.PRIS) && (state.equals(State.ACCEPTE) || state.equals(
        State.REFUSE))) {
      return true;
    }
    if ((this.state.equals(State.PRIS) || this.state.equals(State.INITIE)) && state.equals(
        State.NON_SUIVI)) {
      return true;
    }
    return this.state.equals(State.PRIS) || this.state.equals(State.INITIE) && state.equals(
        State.SUSPENDU);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean checkPlaceValidity(String place) {
    try {
      Place.valueOf(place);
      return true;
    } catch (IllegalArgumentException iae) {
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
    if (!(o instanceof ContactImpl contact)) {
      return false;
    }
    return id == contact.id && companyId == contact.companyId && studentId == contact.studentId
        && version == contact.version && Objects.equals(companyDTO, contact.companyDTO)
        && Objects.equals(studentDTO, contact.studentDTO) && state == contact.state
        && meetingPlace == contact.meetingPlace && Objects.equals(refusalReason,
        contact.refusalReason) && Objects.equals(academicYear, contact.academicYear);
  }

  /**
   * Renvoie un code de hachage pour cet objet.
   *
   * @return un code de hachage pour cet objet.
   */
  @Override
  public int hashCode() {
    return Objects.hash(id, companyId, companyDTO, studentId, studentDTO, state, meetingPlace,
        refusalReason, academicYear, version);
  }

  /**
   * Renvoie une représentation sous forme de chaîne de caractères de cet objet.
   *
   * @return une représentation sous forme de chaîne de caractères de cet objet.
   */
  @Override
  public String toString() {
    return "ContactImpl{"
        + "id=" + id
        + ", companyId=" + companyId
        + ", company" + companyDTO
        + ", studentId=" + studentId
        + ", student=" + studentDTO
        + ", state=" + state
        + ", meetingPlace=" + meetingPlace
        + ", refusalReason='" + refusalReason
        + ", academicYear='" + academicYear
        + ", version=" + version
        + '}';
  }
}
