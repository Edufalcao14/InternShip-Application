package be.vinci.pae.business.utils;

import be.vinci.pae.business.Factory;
import be.vinci.pae.business.FactoryImpl;
import be.vinci.pae.business.ucc.supervisor.SupervisorUCC;
import be.vinci.pae.business.ucc.supervisor.SupervisorUCCImpl;
import be.vinci.pae.dal.DALServiceUCC;
import be.vinci.pae.dal.dao.company.CompanyDAO;
import be.vinci.pae.dal.dao.supervisor.SupervisorDAO;
import jakarta.ws.rs.ext.Provider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.mockito.Mockito;

/**
 * Binder HK2 configurant les liaisons entre les interfaces et leurs implémentations concrètes.
 */
@Provider
public class ApplicationBinderSupervisorUCCTest extends AbstractBinder {

  @Override
  public void configure() {
    bind(FactoryImpl.class).to(Factory.class);
    bind(SupervisorUCCImpl.class).to(SupervisorUCC.class);
    // DAL
    bind(Mockito.mock(DALServiceUCC.class)).to(DALServiceUCC.class);
    // DAO
    bind(Mockito.mock(SupervisorDAO.class)).to(SupervisorDAO.class);
    bind(Mockito.mock(CompanyDAO.class)).to(CompanyDAO.class);
  }
}
