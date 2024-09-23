package be.vinci.pae.utils;

import be.vinci.pae.business.Factory;
import be.vinci.pae.business.FactoryImpl;
import be.vinci.pae.business.ucc.auths.AuthsUCC;
import be.vinci.pae.business.ucc.auths.AuthsUCCImpl;
import be.vinci.pae.business.ucc.company.CompanyUCC;
import be.vinci.pae.business.ucc.company.CompanyUCCImpl;
import be.vinci.pae.business.ucc.contact.ContactUCC;
import be.vinci.pae.business.ucc.contact.ContactUCCImpl;
import be.vinci.pae.business.ucc.internship.InternshipUCC;
import be.vinci.pae.business.ucc.internship.InternshipUCCImpl;
import be.vinci.pae.business.ucc.supervisor.SupervisorUCC;
import be.vinci.pae.business.ucc.supervisor.SupervisorUCCImpl;
import be.vinci.pae.business.ucc.user.UserUCC;
import be.vinci.pae.business.ucc.user.UserUCCImpl;
import be.vinci.pae.dal.DALServiceDAO;
import be.vinci.pae.dal.DALServiceUCC;
import be.vinci.pae.dal.DALServicesImpl;
import be.vinci.pae.dal.dao.company.CompanyDAO;
import be.vinci.pae.dal.dao.company.CompanyDAOImpl;
import be.vinci.pae.dal.dao.contact.ContactDAO;
import be.vinci.pae.dal.dao.contact.ContactDAOImpl;
import be.vinci.pae.dal.dao.internship.InternshipDAO;
import be.vinci.pae.dal.dao.internship.InternshipDAOImpl;
import be.vinci.pae.dal.dao.supervisor.SupervisorDAO;
import be.vinci.pae.dal.dao.supervisor.SupervisorDAOImpl;
import be.vinci.pae.dal.dao.user.UserDAO;
import be.vinci.pae.dal.dao.user.UserDAOImpl;
import jakarta.inject.Singleton;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * Binder HK2 configurant les liaisons entre les interfaces et leurs implémentations concrètes.
 */
public class ApplicationBinder extends AbstractBinder {

  /**
   * Configure les liaisons entre les interfaces et leurs implémentations concrètes.
   */
  @Override
  protected void configure() {
    // Factory
    bind(FactoryImpl.class).to(Factory.class).in(Singleton.class);
    // DAL
    bind(DALServicesImpl.class).to(DALServiceUCC.class).to(DALServiceDAO.class).in(Singleton.class);
    // UCCs
    bind(AuthsUCCImpl.class).to(AuthsUCC.class).in(Singleton.class);
    bind(UserUCCImpl.class).to(UserUCC.class).in(Singleton.class);
    bind(ContactUCCImpl.class).to(ContactUCC.class).in(Singleton.class);
    bind(CompanyUCCImpl.class).to(CompanyUCC.class).in(Singleton.class);
    bind(SupervisorUCCImpl.class).to(SupervisorUCC.class).in(Singleton.class);
    bind(InternshipUCCImpl.class).to(InternshipUCC.class).in(Singleton.class);
    // DAOs
    bind(UserDAOImpl.class).to(UserDAO.class).in(Singleton.class);
    bind(ContactDAOImpl.class).to(ContactDAO.class).in(Singleton.class);
    bind(CompanyDAOImpl.class).to(CompanyDAO.class).in(Singleton.class);
    bind(SupervisorDAOImpl.class).to(SupervisorDAO.class).in(Singleton.class);
    bind(InternshipDAOImpl.class).to(InternshipDAO.class).in(Singleton.class);
  }
}
