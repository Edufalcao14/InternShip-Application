package be.vinci.pae.main;

import be.vinci.pae.ihm.filters.CORSFilter;
import be.vinci.pae.utils.ApplicationBinder;
import be.vinci.pae.utils.Config;
import be.vinci.pae.utils.ExceptionMapper;
import be.vinci.pae.utils.logging.LogMarker;
import be.vinci.pae.utils.logging.MyLogger;
import java.io.IOException;
import java.net.URI;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Classe principale qui démarre le serveur HTTP Grizzly exposant les ressources JAX-RS de
 * l'application.
 */
public class Main {

  /**
   * URI de base sur lequel le serveur HTTP Grizzly écoutera.
   */
  public static String BASE_URI;

  // URI de base sur lequel le serveur HTTP Grizzly écoutera
  static {
    Config.load("dev.properties");
    BASE_URI = Config.getProperty("BaseUri");
  }

  /**
   * Démarre le serveur HTTP Grizzly exposant les ressources JAX-RS définies dans cette
   * application.
   *
   * @return Serveur HTTP Grizzly.
   */
  public static HttpServer startServer() {
    // Crée une configuration de ressource qui recherche les ressources et les fournisseurs JAX-RS
    // dans le package be.vinci
    final ResourceConfig rc = new ResourceConfig().packages("be.vinci.pae")
        .register(ApplicationBinder.class)
        .register(ExceptionMapper.class)
        .register(CORSFilter.class);

    // Crée et démarre une nouvelle instance du serveur HTTP Grizzly
    // exposant l'application Jersey à l'URI de base
    return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
  }

  /**
   * Méthode principale.
   *
   * @param args Les arguments de la ligne de commande
   * @throws IOException Exception d'entrée-sortie.
   */
  public static void main(String[] args) throws IOException {
    MyLogger.logInfo(LogMarker.SERVER, "Lancement du serveur");
    final HttpServer server = startServer();
    MyLogger.logInfo(LogMarker.SERVER,
        "Application Jersey démarrée avec des endpoints disponibles sur "
            + BASE_URI
            + "\nAppuyez sur Ctrl-C pour l'arrêter...\n");
    System.in.read();
    server.stop();
  }
}
