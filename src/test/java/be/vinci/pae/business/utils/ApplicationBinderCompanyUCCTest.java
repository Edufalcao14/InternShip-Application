package be.vinci.pae.business.utils;

import be.vinci.pae.business.Factory;
import be.vinci.pae.business.FactoryImpl;
import be.vinci.pae.business.ucc.company.CompanyUCC;
import be.vinci.pae.business.ucc.company.CompanyUCCImpl;
import be.vinci.pae.dal.DALServiceUCC;
import be.vinci.pae.dal.dao.company.CompanyDAO;
import be.vinci.pae.dal.dao.contact.ContactDAO;
import jakarta.ws.rs.ext.Provider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.mockito.Mockito;

/**
 * Binder HK2 configurant les liaisons entre les interfaces et leurs implémentations concrètes.
 */
@Provider
public class ApplicationBinderCompanyUCCTest extends AbstractBinder {

  /**
   * Configure les liaisons entre les interfaces et leurs implémentations concrètes.
   */
  @Override
  public void configure() {
    bind(FactoryImpl.class).to(Factory.class);
    bind(CompanyUCCImpl.class).to(CompanyUCC.class);
    bind(Mockito.mock(DALServiceUCC.class)).to(DALServiceUCC.class);
    bind(Mockito.mock(CompanyDAO.class)).to(CompanyDAO.class);
    bind(Mockito.mock(ContactDAO.class)).to(ContactDAO.class);
  }
}
