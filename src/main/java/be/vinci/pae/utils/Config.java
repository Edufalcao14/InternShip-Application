package be.vinci.pae.utils;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Classe utilitaire pour la gestion des propriétés de configuration.
 */
public class Config {

  private static Properties props;

  /**
   * Charge les propriétés à partir du fichier spécifié.
   *
   * @param file le nom du fichier de configuration
   */
  public static void load(String file) {
    props = new Properties();
    try (InputStream input = new FileInputStream(file)) {
      props.load(input);
    } catch (IOException e) {
      throw new WebApplicationException(
          Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).type("text/plain")
              .build());
    }
  }

  /**
   * Obtient la valeur de la propriété associée à la clé spécifiée.
   *
   * @param key la clé de la propriété
   * @return la valeur de la propriété
   */
  public static String getProperty(String key) {
    return props.getProperty(key);
  }

  /**
   * Obtient la valeur entière de la propriété associée à la clé spécifiée.
   *
   * @param key la clé de la propriété
   * @return la valeur entière de la propriété
   */
  public static Integer getIntProperty(String key) {
    return Integer.parseInt(props.getProperty(key));
  }

  /**
   * Obtient la valeur booléenne de la propriété associée à la clé spécifiée.
   *
   * @param key la clé de la propriété
   * @return la valeur booléenne de la propriété
   */
  public static boolean getBoolProperty(String key) {
    return Boolean.parseBoolean(props.getProperty(key));
  }

}
