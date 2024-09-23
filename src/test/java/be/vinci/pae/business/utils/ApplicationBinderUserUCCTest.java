package be.vinci.pae.business.utils;

import be.vinci.pae.business.Factory;
import be.vinci.pae.business.FactoryImpl;
import be.vinci.pae.business.ucc.user.UserUCC;
import be.vinci.pae.business.ucc.user.UserUCCImpl;
import be.vinci.pae.dal.DALServiceUCC;
import be.vinci.pae.dal.dao.user.UserDAO;
import jakarta.ws.rs.ext.Provider;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.mockito.Mockito;

/**
 * Binder HK2 configurant les liaisons entre les interfaces et leurs implémentations concrètes.
 */
@Provider
public class ApplicationBinderUserUCCTest extends AbstractBinder {

  /**
   * Configure les liaisons entre les interfaces et leurs implémentations concrètes.
   */
  @Override
  public void configure() {
    bind(FactoryImpl.class).to(Factory.class);
    bind(UserUCCImpl.class).to(UserUCC.class);
    // DAL
    bind(Mockito.mock(DALServiceUCC.class)).to(DALServiceUCC.class);
    // DAO
    bind(Mockito.mock(UserDAO.class)).to(UserDAO.class);
  }
}
