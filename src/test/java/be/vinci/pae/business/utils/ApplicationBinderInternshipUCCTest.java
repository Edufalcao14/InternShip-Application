package be.vinci.pae.business.utils;

import be.vinci.pae.business.Factory;
import be.vinci.pae.business.FactoryImpl;
import be.vinci.pae.business.ucc.internship.InternshipUCC;
import be.vinci.pae.business.ucc.internship.InternshipUCCImpl;
import be.vinci.pae.dal.DALServiceUCC;
import be.vinci.pae.dal.dao.internship.InternshipDAO;
import be.vinci.pae.dal.dao.supervisor.SupervisorDAO;
import jakarta.ws.rs.ext.Provider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.mockito.Mockito;

/**
 * Binder HK2 configurant les liaisons entre les interfaces et leurs implémentations concrètes.
 */
@Provider
public class ApplicationBinderInternshipUCCTest extends AbstractBinder {

  @Override
  public void configure() {
    bind(FactoryImpl.class).to(Factory.class);
    bind(InternshipUCCImpl.class).to(InternshipUCC.class);
    // DAL
    bind(Mockito.mock(DALServiceUCC.class)).to(DALServiceUCC.class);
    // DAO
    bind(Mockito.mock(InternshipDAO.class)).to(InternshipDAO.class);
    bind(Mockito.mock(SupervisorDAO.class)).to(SupervisorDAO.class);
  }
}
