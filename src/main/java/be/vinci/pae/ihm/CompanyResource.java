package be.vinci.pae.ihm;

import be.vinci.pae.business.domain.user.Role;
import be.vinci.pae.business.dto.CompanyDTO;
import be.vinci.pae.business.ucc.company.CompanyUCC;
import be.vinci.pae.ihm.filters.Authorize;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
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
 * La classe CompanyResource représente une ressource RESTful pour la gestion des entreprises.
 */
@Singleton
@Path("/companies")
public class CompanyResource {

  private final ObjectMapper jsonMapper = new ObjectMapper();
  @Inject
  private CompanyUCC companyUCC;

  /**
   * Renvoie la liste de toutes les entreprises de l'année académique si passée en paramètre.
   *
   * @param academicYear l'année académique éventuelle.
   * @param orderBy      l'ordre de tri, si précédé de '-', renvoie par ordre descendant.
   * @return un objet JSON contenant les informations de toutes les entreprises.
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize({Role.ETUDIANT, Role.PROFESSEUR})
  public ObjectNode getAll(@QueryParam("academicYear") String academicYear,
      @QueryParam("orderBy") String orderBy) {

    if (academicYear != null && academicYear.isEmpty()
        || orderBy != null && orderBy.isEmpty()) {
      throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(
              "Paramètres invalides.")
          .build());
    }

    boolean descOrder = false;

    if (orderBy != null) {
      if (orderBy.startsWith("-")) {
        descOrder = true;
        orderBy = orderBy.substring(1);
      }
      if (!checkOrderByValidity(orderBy)) {
        throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(
                "Paramètres invalides.")
            .build());
      }
    }

    List<CompanyDTO> companies = companyUCC.getAllCompanies(academicYear, orderBy, descOrder);

    ObjectNode result = JsonNodeFactory.instance.objectNode();
    ArrayNode arrayNodeList = JsonNodeFactory.instance.arrayNode();

    for (CompanyDTO c : companies) {
      ObjectNode companyNode = createCompanyObjectNode(c);
      arrayNodeList.add(companyNode);
    }

    result.set("companies", arrayNodeList);

    return result;
  }

  /**
   * Renvoie une entreprise selon son ID.
   *
   * @param companyId l'ID de l'entreprise à récupérer.
   * @return un objet JSON contenant les informations de l'entreprise.
   * @throws WebApplicationException si l'ID est invalide.
   */
  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize({Role.ETUDIANT, Role.PROFESSEUR})
  public ObjectNode getCompanyById(@PathParam("id") int companyId) {
    if (companyId < 1) {
      throw new WebApplicationException(Response.status(Status.BAD_REQUEST).entity(
              "ID invalide.")
          .build());
    }

    CompanyDTO company = companyUCC.getCompanyById(companyId);

    return createCompanyObjectNode(company);
  }

  /**
   * Blackliste une entreprise selon son ID.
   *
   * @param json      le JSON contenant la raison du blacklist de l'entreprise.
   * @param companyId l'ID de l'entreprise à blacklister.
   * @return un objet JSON contenant les informations mises à jour de l'entreprise.
   * @throws WebApplicationException Si les champs fournis sont incorrects ou manquants.
   */
  @POST
  @Path("/{id}/blacklist")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize(Role.PROFESSEUR)
  public ObjectNode blacklistCompany(JsonNode json, @PathParam("id") int companyId) {
    if (!json.hasNonNull("blacklist_reason")
        || StringUtils.isBlank("blacklist_reason")
        || !json.hasNonNull("version")
        || companyId < 0) {
      throw new WebApplicationException(
          Response.status(Response.Status.BAD_REQUEST)
              .entity("Les champs sont incorrects ou manquants.")
              .type("text/plain").build());
    }
    int version = json.get("version").asInt();
    String blacklistReason = json.get("blacklist_reason").asText();
    CompanyDTO blacklistedCompany = companyUCC.blacklistCompany(companyId, blacklistReason,
        version);
    return createCompanyObjectNode(blacklistedCompany);
  }


  /**
   * Ajoute une entreprise.
   *
   * @param companyDTO le CompanyDTO représentant l'entreprise à ajouter.
   * @return Un objet JSON contenant les informations de l'entreprise ajoutée.
   * @throws WebApplicationException si les champs requis sont manquants.
   */
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize(Role.ETUDIANT)
  public ObjectNode add(CompanyDTO companyDTO) {
    if (companyDTO.getName() == null
        || companyDTO.getAddress() == null
        || companyDTO.getEmail() != null && StringUtils.isBlank(companyDTO.getEmail())
        || companyDTO.getPhoneNumber() != null && StringUtils.isBlank(companyDTO.getPhoneNumber())
        || StringUtils.isBlank(companyDTO.getName())
        || StringUtils.isBlank(companyDTO.getAddress())
    ) {
      throw new WebApplicationException(
          Response.status(Response.Status.BAD_REQUEST).entity("Tous les champs sont requis.")
              .type("text/plain").build());
    }

    CompanyDTO responseCompanyDTO = companyUCC.addCompany(companyDTO);
    return createCompanyObjectNode(responseCompanyDTO);
  }

  /**
   * Crée un objet JSON contenant les informations de l'entreprise passée en paramètre.
   *
   * @param companyDTO le CompanyDTO contenant les informations pour la création de l'objet JSON.
   * @return un objet JSON contenant les informations de l'entreprise passée en paramètre.
   */
  private ObjectNode createCompanyObjectNode(CompanyDTO companyDTO) {
    return jsonMapper.convertValue(companyDTO, ObjectNode.class);
  }

  /**
   * Vérifie si l'ordre de tri est valide.
   *
   * @param value la valeur de l'ordre de tri.
   * @return true si l'ordre de tri est valide, false sinon.
   */
  private boolean checkOrderByValidity(String value) {
    String[] validValues = {"name", "designation", "is_blacklisted", "phone_number",
        "students_nbr"};

    for (String validValue : validValues) {
      if (value.equals(validValue)) {
        return true;
      }
    }
    return false;
  }

}
