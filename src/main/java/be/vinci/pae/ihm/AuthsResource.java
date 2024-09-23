package be.vinci.pae.ihm;


import be.vinci.pae.business.domain.user.Role;
import be.vinci.pae.business.dto.UserDTO;
import be.vinci.pae.business.ucc.auths.AuthsUCC;
import be.vinci.pae.ihm.filters.Authorize;
import be.vinci.pae.utils.Config;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.sql.Date;
import java.time.LocalDate;
import org.glassfish.jersey.server.ContainerRequest;
import org.junit.platform.commons.util.StringUtils;

/**
 * La classe AuthsResource représente une ressource RESTful pour la gestion de l'authentification
 * des utilisateurs.
 */
@Singleton
@Path("/auths")
public class AuthsResource {

  private final Algorithm jwtAlgorithm = Algorithm.HMAC256(Config.getProperty("JWTSecret"));
  private final ObjectMapper jsonMapper = new ObjectMapper();

  @Inject
  private AuthsUCC myAuthsUCC;

  /**
   * Tente de connecter un utilisateur en utilisant les informations fournies dans le JSON.
   *
   * @param json le JSON contenant les informations d'authentification.
   * @return un objet JSON contenant le token JWT et les informations de l'utilisateur connecté.
   * @throws WebApplicationException si des champs requis sont manquants.
   */
  @POST
  @Path("login")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public ObjectNode login(JsonNode json) {
    // Récupère et vérifie les identifiants
    if (!json.hasNonNull("email")
        || !json.hasNonNull("password")
        // Blank
        || StringUtils.isBlank(json.get("email").asText())
        || StringUtils.isBlank(json.get("password").asText())) {
      throw new WebApplicationException(
          Response.status(Response.Status.BAD_REQUEST).entity("Email et mot-de-passe requis.")
              .type("text/plain").build());
    }

    String email = json.get("email").asText();
    String password = json.get("password").asText();

    // Tente de se connecter
    UserDTO userDTO = myAuthsUCC.login(email, password);
    return createUserObjectNode(userDTO);
  }

  /**
   * Tente de récupérer un utilisateur connecté en utilisant les informations fournies dans le
   * token.
   *
   * @param request le contexte de la requête.
   * @return un objet JSON contenant le token JWT et les informations de l'utilisateur connecté.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize({Role.PROFESSEUR, Role.ETUDIANT, Role.ADMINISTRATIF})
  public ObjectNode loginToken(@Context ContainerRequest request) {
    UserDTO userDTO = (UserDTO) request.getProperty("user");
    return createUserObjectNode(userDTO);
  }

  /**
   * Tente d'inscrire un nouvel utilisateur avec les informations fournies en paramètre.
   *
   * @param userDTO l'UserDTO contenant les informations d'inscription.
   * @return un objet JSON contenant le token et les informations de l'utilisateur crée.
   * @throws WebApplicationException si des champs requis sont manquants.
   */
  @POST
  @Path("register")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public ObjectNode register(UserDTO userDTO) {

    // Récupère et vérifie les identifiants
    if (userDTO.getFirstName() == null
        || userDTO.getLastName() == null
        || userDTO.getEmail() == null
        || userDTO.getPhoneNumber() == null
        || userDTO.getRole() == null
        || userDTO.getPassword() == null
        // Blank
        || StringUtils.isBlank(userDTO.getFirstName())
        || StringUtils.isBlank(userDTO.getLastName())
        || StringUtils.isBlank(userDTO.getPhoneNumber())
        || StringUtils.isBlank(userDTO.getEmail())
        || StringUtils.isBlank(userDTO.getPassword())
    ) {
      throw new WebApplicationException(
          Response.status(Response.Status.BAD_REQUEST).entity("Tous les champs sont requis.")
              .type("text/plain").build());
    }

    String password = userDTO.getPassword();
    String roleString = userDTO.getRole().toString();

    UserDTO responseUserDTO = myAuthsUCC.register(userDTO, password, roleString);
    return createUserObjectNode(responseUserDTO);
  }

  /**
   * Crée un objet JSON contenant le token et les informations de l'utilisateur connecté.
   *
   * @param userDTO l'UserDTO contenant les informations pour la création de l'objet JSON.
   * @return un objet JSON contenant le token et les informations de l'userDTO passé en paramètre.
   * @throws WebApplicationException si une erreur se produit lors de la création du token.
   */
  private ObjectNode createUserObjectNode(UserDTO userDTO) {
    String token;
    try {
      // Crée un token JWT
      token = JWT.create().withIssuer("auth0").withClaim("userID", userDTO.getId())
          .withExpiresAt(Date.valueOf(LocalDate.now().plusDays(1))).sign(this.jwtAlgorithm);
    } catch (Exception e) {
      throw new WebApplicationException(
          Response.status(Status.INTERNAL_SERVER_ERROR)
              .entity("Erreur lors de la création du token.")
              .type("text/plain").build());
    }
    ObjectNode userNode = jsonMapper.convertValue(userDTO, ObjectNode.class);
    userNode.remove("password");
    userNode.put("token", token);
    return userNode;
  }
}
