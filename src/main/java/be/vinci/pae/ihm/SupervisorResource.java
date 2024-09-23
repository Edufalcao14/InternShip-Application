package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.user.Role;
import be.vinci.pae.business.dto.SupervisorDTO;
import be.vinci.pae.business.ucc.supervisor.SupervisorUCC;
import be.vinci.pae.ihm.filters.Authorize;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.List;
import org.junit.platform.commons.util.StringUtils;

/**
 * Classe représentant une ressource RESTful pour la gestion des responsables de stage.
 */
@Singleton
@Path("/supervisors")
public class SupervisorResource {

  private final ObjectMapper jsonMapper = new ObjectMapper();
  @Inject
  private SupervisorUCC mySupervisorUCC;

  /**
   * Renvoie la liste de tous les responsables de stage. Si l'ID d'une entreprise est passée en
   * paramètre, renvoie la liste de tous les responsables de l'entreprise.
   *
   * @param companyId l'ID de l'entreprise, peut être null.
   * @return une liste d'objets JSON contenant les informations des responsables.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize({Role.ETUDIANT, Role.PROFESSEUR})
  public ObjectNode getAll(@DefaultValue("0") @QueryParam("companyId") int companyId) {
    List<SupervisorDTO> supervisors;

    if (companyId == 0) {
      supervisors = mySupervisorUCC.getAllSupervisors();
    } else {
      supervisors = mySupervisorUCC.getCompanySupervisors(companyId);
    }

    ObjectNode result = JsonNodeFactory.instance.objectNode();
    ArrayNode arrayNodeList = JsonNodeFactory.instance.arrayNode();

    for (SupervisorDTO s : supervisors) {
      ObjectNode companyNode = createSupervisorObjectNode(s);
      arrayNodeList.add(companyNode);
    }

    result.set("supervisors", arrayNodeList);

    return result;
  }

  /**
   * Ajoute un nouveau responsable.
   *
   * @param supervisorDTO le responsable à ajouter.
   * @param companyId     ID de l'entreprise du responsable.
   * @return un JSON contenant les informations du nouvel responsable.
   * @throws WebApplicationException si les données sont manquantes.
   */
  @Path("/{companyId}")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public ObjectNode addSupervisor(SupervisorDTO supervisorDTO,
      @PathParam("companyId") int companyId) {
    if (supervisorDTO.getFirstName() == null || supervisorDTO.getLastName() == null
        || supervisorDTO.getPhoneNumber() == null
        || supervisorDTO.getEmail() != null && StringUtils.isBlank(supervisorDTO.getEmail())
        // Blank
        || StringUtils.isBlank(supervisorDTO.getFirstName()) || StringUtils.isBlank(
        supervisorDTO.getLastName()) || StringUtils.isBlank(supervisorDTO.getPhoneNumber())) {
      throw new WebApplicationException(
          Response.status(Status.BAD_REQUEST).entity("Tous les champs sont requis.")
              .type("text/plain").build());
    }

    SupervisorDTO addedSupervisor = mySupervisorUCC.addSupervisor(supervisorDTO, companyId);
    return createSupervisorObjectNode(addedSupervisor);
  }

  /**
   * Crée un objet JSON contenant les informations du responsable.
   *
   * @param supervisorDTO le SupervisorDTO contenant les informations pour la création de l'objet
   *                      JSON.
   * @return un objet JSON contenant les informations du responsable passé en paramètre.
   */
  private ObjectNode createSupervisorObjectNode(SupervisorDTO supervisorDTO) {
    return jsonMapper.convertValue(supervisorDTO, ObjectNode.class);
  }
}
