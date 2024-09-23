package be.vinci.pae.ihm;


import be.vinci.pae.business.domain.user.Role;
import be.vinci.pae.business.dto.ContactDTO;
import be.vinci.pae.business.dto.InternshipDTO;
import be.vinci.pae.business.dto.UserDTO;
import be.vinci.pae.business.ucc.contact.ContactUCC;
import be.vinci.pae.ihm.filters.Authorize;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
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
 * Classe représentant une ressource RESTful pour la gestion des contacts.
 */
@Singleton
@Path("/contacts")
public class ContactResource {

  private final ObjectMapper jsonMapper = new ObjectMapper();
  @Inject
  private ContactUCC myContactUCC;

  /**
   * Renvoie la liste de tous les contacts.
   *
   * @return Une liste d'objets JSON contenant les informations de tous les contacts.
   * @throws WebApplicationException si aucun contact n'a été trouvé.
   */
  @GET
  @Path("/all")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize({Role.PROFESSEUR})
  public List<ObjectNode> getAll() {
    List<ObjectNode> allContacts = new ArrayList<>();
    List<ContactDTO> contacts = myContactUCC.getAllContacts();
    if (contacts == null) {
      throw new WebApplicationException(
          Response.status(Status.NOT_FOUND)
              .entity("Aucun contact trouvé.")
              .type("text/plain").build());
    }
    for (ContactDTO contactDTO : contacts) {
      ObjectNode contactNode = createContactObjectNode(contactDTO);
      allContacts.add(contactNode);
    }
    return allContacts;
  }

  /**
   * Renvoie tous les contacts d'un utilisateur donné.
   *
   * @param idUser  l'ID de l'utilisateur dont les contacts doivent être récupérés. Si précédé de
   *                '-', renvoie tous les contacts, sinon seulement les suivis.
   * @param request le contexte de la requête.
   * @return une liste des contacts de l'utilisateur ou null en cas d'absence de données.
   * @throws WebApplicationException si un étudiant essaie de récupérer ses contacts autrement que
   *                                 via le token.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize({Role.ETUDIANT, Role.PROFESSEUR, Role.ADMINISTRATIF})
  public ObjectNode getAllUserContacts(@DefaultValue("0") @QueryParam("idUser") int idUser,
      @Context ContainerRequest request) {

    boolean isFollowed = true;

    if (idUser < 0) {
      isFollowed = false;
      idUser = Math.abs(idUser);
    }

    List<ContactDTO> contactDTOList;
    UserDTO authenticatedUser = (UserDTO) request.getProperty("user");

    if (idUser == 0) {
      contactDTOList = myContactUCC.getContactsByUserId(authenticatedUser,
          authenticatedUser.getId(), isFollowed);
    } else {
      contactDTOList = myContactUCC.getContactsByUserId(authenticatedUser, idUser, isFollowed);
    }

    ObjectNode result = JsonNodeFactory.instance.objectNode();

    ArrayNode arrayNodeList = JsonNodeFactory.instance.arrayNode();
    for (ContactDTO contactDTO : contactDTOList) {
      ObjectNode contactNode = createContactObjectNode(contactDTO);
      arrayNodeList.add(contactNode);
    }
    result.set("contacts", arrayNodeList);
    return result;
  }

  /**
   * Renvoie la liste de tous les contacts d'une entreprise.
   *
   * @param id l'ID de l'entreprise.
   * @return une liste d'objets JSON contenant les informations des contacts d'une entreprise.
   * @throws WebApplicationException si l'ID est invalide ou si aucun contact n'a été trouvé.
   */
  @GET
  @Path("/company")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize(Role.PROFESSEUR)
  public List<ObjectNode> getAllCompanyContacts(@QueryParam("id") int id) {
    if (id < 0) {
      throw new WebApplicationException(
          Response.status(Response.Status.BAD_REQUEST).entity("ID invalide.")
              .type("text/plain").build());
    }
    List<ObjectNode> allContacts = new ArrayList<>();
    List<ContactDTO> contacts = myContactUCC.getContactsByCompanyId(id);
    if (contacts == null) {
      throw new WebApplicationException(
          Response.status(Status.UNAUTHORIZED)
              .entity("Aucun contact trouvé.")
              .type("text/plain").build());
    }
    for (ContactDTO contactDTO : contacts) {
      ObjectNode contactNode = createContactObjectNode(contactDTO);
      allContacts.add(contactNode);
    }
    return allContacts;
  }

  /**
   * Ajoute un nouveau contact en utilisant les données JSON fournies.
   *
   * @param json    les données JSON contenant les informations sur le nouveau contact.
   * @param request le contexte de la requête.
   * @return un JSON contenant les informations sur le contact ajouté ou null si l'ajout a échoué.
   * @throws WebApplicationException si les données fournies sont incorrectes ou manquantes.
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize(Role.ETUDIANT)
  public ObjectNode add(JsonNode json, @Context ContainerRequest request) {

    if (!json.hasNonNull("company")
        || json.get("company").asInt() < 0
    ) {
      throw new WebApplicationException(
          Response.status(Response.Status.BAD_REQUEST)
              .entity("Les champs sont incorrects ou manquants.")
              .type("text/plain").build());
    }
    int company = json.get("company").asInt();
    UserDTO student = (UserDTO) request.getProperty("user");

    ContactDTO addedContact = myContactUCC.addContact(company, student.getId());
    return createContactObjectNode(addedContact);
  }

  /**
   * Refuse un contact.
   *
   * @param json le JSON contenant la raison du refus pour le contact.
   * @param id   l'ID du contact à refuser.
   * @return un objet JSON contenant les informations mises à jour du contact.
   * @throws WebApplicationException si les données fournies sont incorrectes.
   */
  @POST
  @Path("/{id}/deny")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize(Role.ETUDIANT)
  public ObjectNode deny(JsonNode json, @PathParam("id") int id) {

    if (!json.hasNonNull("refusalReason")
        || !json.hasNonNull("version")
        || StringUtils.isBlank("refusalReason")
        || id < 0) {
      throw new WebApplicationException(
          Response.status(Response.Status.BAD_REQUEST)
              .entity("Les champs sont incorrects ou manquants.")
              .type("text/plain").build());
    }

    String refusalReason = json.get("refusalReason").asText();
    int version = json.get("version").asInt();

    ContactDTO updatedContact = myContactUCC.denyContact(id, refusalReason, version);
    return createContactObjectNode(updatedContact);
  }

  /**
   * Prend contact.
   *
   * @param json le JSON contenant le lieu de rencontre du contact.
   * @param id   L'ID du contact à prendre.
   * @return un JSON représentant les informations mises à jour du contact.
   * @throws WebApplicationException si les données fournies sont incorrectes.
   */
  @POST
  @Path("/{id}/take")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize(Role.ETUDIANT)
  public ObjectNode take(JsonNode json, @PathParam("id") int id) {

    if (!json.hasNonNull("meetingPlace")
        || StringUtils.isBlank("meetingPlace")
        || !json.hasNonNull("version")
        || id < 0) {
      throw new WebApplicationException(
          Response.status(Response.Status.BAD_REQUEST)
              .entity("Les champs sont incorrects ou manquants.")
              .type("text/plain").build());
    }

    String meetingPlace = json.get("meetingPlace").asText();
    int version = json.get("version").asInt();

    ContactDTO updatedContact = myContactUCC.takeContact(id, meetingPlace, version);
    return createContactObjectNode(updatedContact);
  }

  /**
   * Cesse de suivre un contact.
   *
   * @param id   L'ID du contact à cesser de suivre.
   * @param json un JSON contenant la version.
   * @return un JSON contenant les informations du contact ou null si l'opération a échoué.
   * @throws WebApplicationException si les données fournies sont incorrectes ou manquantes.
   */
  @POST
  @Path("/{id}/unfollow")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize(Role.ETUDIANT)
  public ObjectNode unfollow(@PathParam("id") int id, JsonNode json) {
    if (!json.hasNonNull("version")
        || id < 0) {
      throw new WebApplicationException(
          Response.status(Response.Status.BAD_REQUEST)
              .entity("Les champs sont incorrects ou manquants.")
              .type("text/plain").build());
    }

    int version = json.get("version").asInt();

    ContactDTO unfollowedContact = myContactUCC.unfollowContact(id, version);

    return createContactObjectNode(unfollowedContact);
  }

  /**
   * Accepte un contact et crée un stage.
   *
   * @param internshipDTO le InternshipDTO contenant les informations du stage.
   * @param request       le contexte de la requête.
   * @param idContact     L'ID du contact associé à la demande de stage.
   * @return un JSON avec le contact mis à jour après avoir accepté la demande de stage.
   * @throws WebApplicationException si les données fournies sont incorrectes ou manquantes.
   */
  @POST
  @Path("/{id}/accept")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize(Role.ETUDIANT)
  public ObjectNode accept(InternshipDTO internshipDTO, @Context ContainerRequest request,
      @PathParam("id") int idContact) {
    if (internshipDTO.getSupervisorId() < 0
        || internshipDTO.getSignatureDate() == null
        || internshipDTO.getSubject() != null && internshipDTO.getSignatureDate().isBlank()
        || idContact < 0
    ) {
      throw new WebApplicationException(
          Response.status(Response.Status.BAD_REQUEST)
              .entity("Les champs sont incorrects ou manquants.")
              .type("text/plain").build());
    }
    UserDTO authenticatedUser = (UserDTO) request.getProperty("user");
    internshipDTO.setStudentId(authenticatedUser.getId());
    internshipDTO.setContactId(idContact);
    int version = internshipDTO.getVersion();

    ContactDTO updatedContact = myContactUCC.acceptContact(internshipDTO, version);

    return createContactObjectNode(updatedContact);
  }

  /**
   * Crée un objet JSON contenant les informations du contact passé en paramètre.
   *
   * @param contactDTO le ContactDTO contenant les informations pour la création de l'objet JSON.
   * @return un objet JSON contenant les informations du contact passé en paramètre.
   */
  private ObjectNode createContactObjectNode(ContactDTO contactDTO) {
    return jsonMapper.convertValue(contactDTO, ObjectNode.class);
  }

}
