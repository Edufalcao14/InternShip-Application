package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.user.Role;
import be.vinci.pae.business.dto.UserDTO;
import be.vinci.pae.business.ucc.user.UserUCC;
import be.vinci.pae.ihm.filters.Authorize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;
import org.glassfish.jersey.server.ContainerRequest;
import org.junit.platform.commons.util.StringUtils;

/**
 * La classe UserResource représente une ressource RESTful pour la gestion des utilisateurs.
 */

@Singleton
@Path("/users")
public class UserResource {

  private final ObjectMapper jsonMapper = new ObjectMapper();
  @Inject
  private UserUCC myUserUCC;

  /**
   * Renvoie la liste de tous les users.
   *
   * @return Un objet JSON contenant les informations de l'utilisateur passées en paramètre.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize({Role.PROFESSEUR, Role.ADMINISTRATIF})
  public List<ObjectNode> getAll() {
    List<ObjectNode> allUsers = new ArrayList<>();

    List<UserDTO> users = myUserUCC.getAllUsers();

    for (UserDTO user : users) {
      ObjectNode userNode = jsonMapper.createObjectNode();
      userNode.put("id", user.getId()).put("lastName", user.getLastName())
          .put("firstName", user.getFirstName()).put("phoneNumber", user.getPhoneNumber())
          .put("email", user.getEmail()).put("role", user.getRole().toString())
          .put("academicYear", user.getAcademicYear()).put("version", user.getVersion());
      if (user.getRole().equals(Role.ETUDIANT)) {
        userNode.put("hasInternship", user.hasInternship());
      }
      allUsers.add(userNode);
    }
    return allUsers;
  }


  /**
   * Renvoie un utilisateur spécifique par son ID.
   *
   * @param userId l'ID de l'utilisateur à récupérer.
   * @return un objet JSON contenant les informations de l'utilisateur correspondant à l'ID.
   */
  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public ObjectNode getUserById(@PathParam("id") int userId) {

    UserDTO user = myUserUCC.getUserById(userId);

    if (user == null) {
      throw new WebApplicationException(
          Response.status(Status.NOT_FOUND).entity("Utilisateur introuvable avec l'ID : " + userId)
              .type("text/plain").build());
    }

    return createUserObjectNode(user);
  }

  /**
   * Modifie un champ spécifique d'un utilisateur.
   *
   * @param userDTO l'objet UserDTO contenant les informations à mettre à jour chez l'utilisateur.
   * @param request le contexte de la requête.
   * @return Retourne un objet JSON représentant l'utilisateur modifié.
   * @throws WebApplicationException si les champs requis sont manquants ou invalides.
   */
  @PATCH
  @Path("/modifyUserField")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize({Role.ETUDIANT, Role.PROFESSEUR, Role.ADMINISTRATIF})
  @JsonIgnoreProperties(ignoreUnknown = true)
  public ObjectNode modifyUser(UserDTO userDTO, @Context ContainerRequest request) {

    if (userDTO.getRole() == null
        || StringUtils.isBlank(userDTO.getRole().toString())
        || userDTO.getVersion() < 0) {
      throw new WebApplicationException(
          Response.status(Response.Status.BAD_REQUEST)
              .entity("Les informations nécessaires sont absentes.")
              .type("text/plain").build());
    }
    UserDTO authenticatedUser = (UserDTO) request.getProperty("user");
    int userId = authenticatedUser.getId();
    userDTO.setId(userId);
    String fieldType;
    if (userDTO.getFirstName() != null) {
      fieldType = "firstName";
    } else if (userDTO.getLastName() != null) {
      fieldType = "lastName";
    } else if (userDTO.getEmail() != null) {
      fieldType = "email";
    } else if (userDTO.getPhoneNumber() != null) {
      fieldType = "phoneNumber";
    } else {
      throw new WebApplicationException(
          Response.status(Response.Status.BAD_REQUEST)
              .entity("Aucun champ à modifier n'a été fourni.")
              .type("text/plain").build());
    }

    UserDTO modifiedUser = myUserUCC.modifyUserField(userDTO, fieldType);

    return createUserObjectNode(modifiedUser);
  }


  /**
   * Modifie le mot-de-passe de l'utilisateur.
   *
   * @param request le contexte de la requête.
   * @param json    JSON contenant le mot-de-passe et le nouveau mot-de-passe.
   * @return un objet JSON contenant les informations de l'utilisateur après les modifications.
   */
  @PATCH
  @Path("/modifyPassword")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize({Role.ETUDIANT, Role.PROFESSEUR, Role.ADMINISTRATIF})
  public Response modifyPassword(JsonNode json, @Context ContainerRequest request) {

    //verifie si les données ne sont pas null
    if (json == null
        || !json.hasNonNull("currentPassword")
        || !json.hasNonNull("newPassword")
        || !json.hasNonNull("version")) {
      throw new WebApplicationException(
          Response.status(Response.Status.BAD_REQUEST)
              .entity("Tous les champs sont requis.")
              .type("text/plain").build());
    }

    UserDTO authenticatedUser = (UserDTO) request.getProperty("user");
    int userId = authenticatedUser.getId();

    int version = json.get("version").asInt();
    String currentPassword = json.get("currentPassword").asText();
    String newPassword = json.get("newPassword").asText();

    if (version < 0 || StringUtils.isBlank(currentPassword) || StringUtils.isBlank(
        newPassword)) {
      throw new WebApplicationException(
          Response.status(Response.Status.BAD_REQUEST).entity("Champs incorrects.")
              .type("text/plain").build());
    }

    myUserUCC.modifyUserPassword(userId, currentPassword, newPassword, version);

    return Response.status(Status.OK).entity("Le mot de passe a été changé avec succès.").build();
  }

  /**
   * Crée un objet JSON contenant les informations de l'utilisateur.
   *
   * @param userDTO le UserDTO contenant les informations pour la création de l'objet JSON.
   * @return un objet JSON contenant les informations de l'utilisateur passé en paramètre.
   */
  private ObjectNode createUserObjectNode(UserDTO userDTO) {
    ObjectNode objectNode = jsonMapper.convertValue(userDTO, ObjectNode.class);
    objectNode.remove("password");
    return objectNode;
  }
}
