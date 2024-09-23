package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.user.Role;
import be.vinci.pae.business.dto.InternshipDTO;
import be.vinci.pae.business.dto.UserDTO;
import be.vinci.pae.business.ucc.internship.InternshipUCC;
import be.vinci.pae.ihm.filters.Authorize;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.glassfish.jersey.server.ContainerRequest;
import org.junit.platform.commons.util.StringUtils;

/**
 * Classe représentant une ressource RESTful pour la gestion des stages.
 */
@Singleton
@Path("/internships")
public class InternshipResource {

  private final ObjectMapper jsonMapper = new ObjectMapper();
  @Inject
  private InternshipUCC myInternshipUCC;

  /**
   * Renvoi le stage de l'étudiant récupéré grâce à l'ID ou grâce au token.
   *
   * @param studentId l'ID de l'étudiant.
   * @param request   le contexte de la requête.
   * @return le stage de l'étudiant.
   */
  @GET
  @Path("/one")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize({Role.ETUDIANT, Role.PROFESSEUR, Role.ADMINISTRATIF})
  public ObjectNode getOneByStudentId(@DefaultValue("0") @QueryParam("studentId") int studentId,
      @Context ContainerRequest request) {
    InternshipDTO internshipDTO;
    if (studentId == 0) {
      UserDTO authenticatedUser = (UserDTO) request.getProperty("user");
      internshipDTO = myInternshipUCC.getInternshipByUserId(authenticatedUser.getId());
    } else {
      internshipDTO = myInternshipUCC.getInternshipByUserId(studentId);
    }
    return createInternshipObjectNode(internshipDTO);
  }

  /**
   * Modifie le sujet du stage de l'étudiant.
   *
   * @param json le JSON contenant l'ID du stage, le sujet et la version.
   * @return le stage modifié de l'étudiant.
   * @throws WebApplicationException si les données sont manquantes.
   */
  @PATCH
  @Path("/modifySubject")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize({Role.ETUDIANT})
  public ObjectNode modifyInternshipSubject(JsonNode json) {
    //verifie si les données ne sont pas null
    if (json == null
        || !json.hasNonNull("id")
        || !json.hasNonNull("subject")
        || !json.hasNonNull("version")) {
      throw new WebApplicationException(
          Response.status(Response.Status.BAD_REQUEST)
              .entity("Champs requis manquants.")
              .type("text/plain").build());
    }

    int internshipId = json.get("id").asInt();
    int version = json.get("version").asInt();
    String subject = json.get("subject").asText();

    if (internshipId < 0 || StringUtils.isBlank(subject) || version < 0) {
      throw new WebApplicationException(
          Response.status(Status.BAD_REQUEST)
              .entity("Informations obligatoires manquantes.")
              .type("text/plain").build());
    }

    InternshipDTO internshipDTO = myInternshipUCC.modifyInternshipSubject(internshipId, subject,
        version);

    return createInternshipObjectNode(internshipDTO);
  }

  /**
   * Renvoie le nombre d'étudiants ayant un stage pour une année donnée.
   *
   * @param year l'année académique.
   * @return le nombre d'étudiants avec stage.
   */
  @GET
  @Path("/with")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize(Role.PROFESSEUR)
  public ObjectNode countStudentsWithInternship(@QueryParam("year") String year) {
    int result = myInternshipUCC.nbrOfStudentsWithInternship(year);
    return jsonMapper.createObjectNode().put("nbr", result);
  }

  /**
   * Renvoie le nombre d'étudiants n'ayant pas de stage pour une année donnée.
   *
   * @param year l'année académique.
   * @return le nombre d'étudiants avec stage.
   */
  @GET
  @Path("/without")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize(Role.PROFESSEUR)
  public ObjectNode countStudentsWithoutInternship(@QueryParam("year") String year) {
    int result = myInternshipUCC.nbrOfStudentsWithoutInternship(year);
    return jsonMapper.createObjectNode().put("nbr", result);
  }

  /**
   * Crée un objet JSON contenant les informations du stage.
   *
   * @param internshipDTO l'InternshipDTO contenant les informations pour la création de l'objet
   *                      JSON.
   * @return un objet JSON contenant les informations du stage passé en paramètre.
   */
  private ObjectNode createInternshipObjectNode(InternshipDTO internshipDTO) {
    return jsonMapper.convertValue(internshipDTO, ObjectNode.class);
  }
}
