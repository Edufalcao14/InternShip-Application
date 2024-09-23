package be.vinci.pae.business.utils;

import be.vinci.pae.business.Factory;
import be.vinci.pae.business.FactoryImpl;
import be.vinci.pae.business.ucc.contact.ContactUCC;
import be.vinci.pae.business.ucc.contact.ContactUCCImpl;
import be.vinci.pae.business.ucc.internship.InternshipUCC;
import be.vinci.pae.dal.DALServiceUCC;
import be.vinci.pae.dal.dao.company.CompanyDAO;
import be.vinci.pae.dal.dao.contact.ContactDAO;
import be.vinci.pae.dal.dao.user.UserDAO;
import jakarta.ws.rs.ext.Provider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.mockito.Mockito;

/**
 * Binder HK2 configurant les liaisons entre les interfaces et leurs implémentations concrètes.
 */
@Provider
public class ApplicationBinderContactUCCTest extends AbstractBinder {

  /**
   * Configure les liaisons entre les interfaces et leurs implémentations concrètes.
   */
  @Override
  public void configure() {
    bind(FactoryImpl.class).to(Factory.class);
    bind(ContactUCCImpl.class).to(ContactUCC.class);
    // DAL
    bind(Mockito.mock(DALServiceUCC.class)).to(DALServiceUCC.class);
    // UCC
    bind(Mockito.mock(InternshipUCC.class)).to(InternshipUCC.class);
    // DAOs
    bind(Mockito.mock(ContactDAO.class)).to(ContactDAO.class);
    bind(Mockito.mock(UserDAO.class)).to(UserDAO.class);
    bind(Mockito.mock(CompanyDAO.class)).to(CompanyDAO.class);
  }
}
