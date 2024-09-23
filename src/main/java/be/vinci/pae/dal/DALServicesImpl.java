package be.vinci.pae.dal;

import be.vinci.pae.utils.Config;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.FatalExceptionType;
import be.vinci.pae.utils.logging.LogMarker;
import be.vinci.pae.utils.logging.MyLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 * Implémentation concrète de l'interface DALService fournissant des services d'accès aux données.
 */
public class DALServicesImpl implements DALServiceDAO, DALServiceUCC {

  private BasicDataSource connectionPool;
  private ThreadLocal<Connection> connectionThreadLocal;
  private AtomicInteger startCounter;

  /**
   * Initialise une nouvelle instance de DALServiceImpl en chargeant le driver et en établissant la
   * connexion à la base de données.
   *
   * @throws FatalException {@link FatalExceptionType#SERVER} si une erreur de chargement du driver
   *                        se produit.
   */
  public DALServicesImpl() {
    try {
      Class.forName("org.postgresql.Driver");

      connectionPool = new BasicDataSource();
      connectionThreadLocal = new ThreadLocal<>();
      startCounter = new AtomicInteger(0);

      connectionPool.setUrl(Config.getProperty("DatabaseFilePath"));
      connectionPool.setUsername(Config.getProperty("DbUser"));
      connectionPool.setPassword(Config.getProperty("DbPassword"));

      connectionPool.setInitialSize(1);
      connectionPool.setMaxTotal(5);

      Connection connection = null;
      try {
        connection = connectionPool.getConnection();
        if (connection != null) {
          MyLogger.logInfo(LogMarker.DATABASE, "Connexion à la DB réussie");
        } else {
          MyLogger.logError(LogMarker.DATABASE, "Connexion à la DB échouée");
          System.exit(1);
        }
      } catch (SQLException e) {
        MyLogger.logError(LogMarker.DATABASE, "Connexion à la DB échouée", e);
        System.exit(1);
      } finally {
        if (connection != null) {
          try {
            connection.close();
          } catch (SQLException e) {
            MyLogger.logWarn(LogMarker.DATABASE,
                "Fermeture de la connexion initiale échouée.", e);
          }
        }
      }
    } catch (ClassNotFoundException e) {
      MyLogger.logError(LogMarker.SERVER, "Chargement des drivers échoué", e);
      System.exit(1);
    }
  }


  /**
   * {@inheritDoc}
   *
   * @throws FatalException <ul>
   *                        <li>{@link FatalExceptionType#DATABASE}
   *                        - si une erreur de DB se produit</li>
   *                        <li>{@link FatalExceptionType#TRANSACTION}
   *                        - s'il n'y a pas de connexion disponible.</li>
   *                        </ul>
   */
  @Override
  public PreparedStatement getPs(String sql) {
    try {
      if (connectionThreadLocal.get() != null) {
        return connectionThreadLocal.get().prepareStatement(sql);
      } else {
        throw new FatalException("Pas de connexion disponible pour créer le Prepared Statement.",
            FatalExceptionType.TRANSACTION);
      }
    } catch (SQLException e) {
      throw new FatalException("Erreur SQL lors de la création du Prepared Statement.", e,
          FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException <ul>
   *                        <li>{@link FatalExceptionType#DATABASE}
   *                        - si une erreur de DB se produit.</li>
   *                        <li>{@link FatalExceptionType#TRANSACTION}
   *                        - si une transaction est déjà en cours.</li>
   *                        </ul>
   */
  @Override
  public void start() {
    try {
      startCounter.incrementAndGet();
      if (connectionThreadLocal.get() == null && startCounter.get() == 1) {
        Connection newConnection = connectionPool.getConnection();
        connectionThreadLocal.set(newConnection);
        connectionThreadLocal.get().setAutoCommit(false);
      }
    } catch (SQLException e) {
      closeConnection();
      throw new FatalException("Start Transaction", e, FatalExceptionType.DATABASE);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException <ul>
   *                        <li>{@link FatalExceptionType#DATABASE}
   *                        - si une erreur de DB se produit.</li>
   *                        <li>{@link FatalExceptionType#TRANSACTION}
   *                        - si il n'y a pas de transaction en cours.</li>
   *                        </ul>
   */
  @Override
  public void commit() {
    if (startCounter.decrementAndGet() == 0) {
      try {
        if (connectionThreadLocal.get() != null) {
          connectionThreadLocal.get().commit();
        }
      } catch (SQLException e) {
        throw new FatalException("Commit Transaction", e, FatalExceptionType.DATABASE);
      } finally {
        try {
          if (connectionThreadLocal.get() != null) {
            connectionThreadLocal.get().setAutoCommit(true);
          }
        } catch (SQLException e) {
          MyLogger.logWarn(LogMarker.DATABASE, "Set Auto Commit", e);
        } finally {
          if (connectionThreadLocal.get() != null) {
            closeConnection();
          }
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws FatalException <ul>
   *                        <li>{@link FatalExceptionType#DATABASE}
   *                        - si une erreur de DB se produit.</li>
   *                        <li>{@link FatalExceptionType#TRANSACTION}
   *                        - si il n'y a pas de transaction en cours.</li>
   *                        </ul>
   */
  @Override
  public void rollBack() {
    try {
      if (connectionThreadLocal.get() != null) {
        connectionThreadLocal.get().rollback();
      } else {
        throw new FatalException("Il n'y a pas de transaction en cours.",
            FatalExceptionType.TRANSACTION);
      }
    } catch (SQLException e) {
      throw new FatalException("Rollback Transaction", e, FatalExceptionType.DATABASE);
    } finally {
      try {
        if (connectionThreadLocal.get() != null) {
          connectionThreadLocal.get().setAutoCommit(true);
        }
      } catch (SQLException e) {
        MyLogger.logWarn(LogMarker.DATABASE, "Set Auto Commit", e);
      } finally {
        if (connectionThreadLocal.get() != null) {
          closeConnection();
        }
      }
    }
  }

  /**
   * Libère et remets la connexion dans la connectionPool.
   *
   * @throws FatalException <ul>
   *                        <li>{@link FatalExceptionType#DATABASE}
   *                        - si une erreur de DB se produit.</li>
   *                        <li>{@link FatalExceptionType#TRANSACTION}
   *                        - si il n'y a pas de connexion à fermer.</li>
   *                        </ul>
   */
  private void closeConnection() {
    try {
      if (connectionThreadLocal.get() != null) {
        connectionThreadLocal.get().close();
      } else {
        throw new FatalException("Pas de connexion à fermer.",
            FatalExceptionType.TRANSACTION);
      }
    } catch (SQLException e) {
      throw new FatalException("Close Connection", e, FatalExceptionType.DATABASE);
    } finally {
      if (connectionThreadLocal.get() != null) {
        connectionThreadLocal.remove();
      }
    }
  }
}

