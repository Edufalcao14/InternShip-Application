package be.vinci.pae.business;


import be.vinci.pae.business.domain.company.CompanyImpl;
import be.vinci.pae.business.domain.contact.ContactImpl;
import be.vinci.pae.business.domain.internship.InternshipImpl;
import be.vinci.pae.business.domain.supervisor.SupervisorImpl;
import be.vinci.pae.business.domain.user.UserImpl;
import be.vinci.pae.business.dto.CompanyDTO;
import be.vinci.pae.business.dto.ContactDTO;
import be.vinci.pae.business.dto.InternshipDTO;
import be.vinci.pae.business.dto.SupervisorDTO;
import be.vinci.pae.business.dto.UserDTO;

/**
 * Implémentation de l'interface Factory fournissant des méthodes pour créer des instances d'objets
 * du domaine.
 */
public class FactoryImpl implements Factory {

  /**
   * {@inheritDoc}
   */
  @Override
  public UserDTO getUserDTO() {
    return new UserImpl();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ContactDTO getContactDTO() {
    return new ContactImpl();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public SupervisorDTO getSupervisorDTO() {
    return new SupervisorImpl();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InternshipDTO getInternshipDTO() {
    return new InternshipImpl();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CompanyDTO getCompanyDTO() {
    return new CompanyImpl();
  }
}
