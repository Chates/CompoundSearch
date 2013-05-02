package cz.compoundsearch.resources;

import cz.compoundsearch.descriptor.SubstructureFingerprintDescriptor;
import cz.compoundsearch.entities.Compound;
import cz.compoundsearch.entities.SubstructureFingerprint;
import cz.compoundsearch.exceptions.CompoundSearchException;
import cz.compoundsearch.requests.AddRequest;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.BitSet;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 * REST resource adding compound to the database.
 *
 * Resource accepts both JSON and XML formats based on "Accept" header in HTTP
 * request.
 *
 * <p><strong>This resource is mapped to /add/ URL</strong></p>
 *
 * @author Martin Mates
 */
@Path("/add/")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Stateless
public class AddResource {

    @PersistenceContext(unitName = "com.webtoad_Diplomka_maven_war_1.0PU")
    private EntityManager em;
    @Context
    private UriInfo uriInfo;

    /**
     * Adds a new compound to the database.
     *
     * This method accepts MDL molfile as a String and generates all required
     * information such as SMILES line notation, substructure fingerprint and
     * molecular formula.
     * 
     * When compound is added successfully HTTP status 201 (Created) and 
     * link to new resource is returned.
     *
     * If there is any error during the processing of the MDL molfile response
     * with status 500 (Internal server error) is returned and header 
     * "Compound-search-error" is added to response explaining the error.
     *
     * <p><strong>This resource is mapped to /add/ URL</strong></p>
     *
     * @param requestXML JSON or XML request
     * @return Response HTTP response containing new URI of the added compound
     */
    @POST
    public Response addCompound(JAXBElement<AddRequest> requestXML) {
	// We initialize empty Compound entity
	Compound compoundToAdd = new Compound();
	// Set MDL molfile of the Compound entity from HTTP request
	compoundToAdd.setMolfile(requestXML.getValue().getMolfile());

	try {
	    // We create CDK Atom Container from MDL molfile String
	    InputStream is = new ByteArrayInputStream(compoundToAdd.getMolfile().getBytes());
	    MDLV2000Reader reader = new MDLV2000Reader(is);
	    AtomContainer molecule = reader.read(new AtomContainer());
	    is.close();
	    reader.close();

	    // Generate SMILES line notation and add it to the Compound
	    SmilesGenerator sg = new SmilesGenerator();
	    compoundToAdd.setSmiles(sg.createSMILES(molecule));

	    // Generate molecular formula of the molecule and add it to the Compound
	    MolecularFormula molForm = (MolecularFormula) MolecularFormulaManipulator.getMolecularFormula(molecule);
	    compoundToAdd.setMolecularFormula(MolecularFormulaManipulator.getString(molForm));


	    // We initialize empty SubstructureFingerprint entity for fingeprirnt persistation in the database.
	    SubstructureFingerprint sf = new SubstructureFingerprint();
	    // Generate structural key fingerprint
	    SubstructureFingerprintDescriptor sfd = new SubstructureFingerprintDescriptor();
	    BitSet idr;
	    try {
		// Use the SubstructureFingepritntDescriptor to generate fingerprint.
		idr = sfd.calculate(compoundToAdd.getAtomContainer());
	    } catch (CompoundSearchException e) {
		CompoundResponse crf = new CompoundResponse("Adding new compound to database failed. Cannot calculate substructure fingerprint.", 500);
		throw new WebApplicationException(crf.buildResponse());
	    }
	    sf.setCompound(compoundToAdd);
	    sf.setFingerprint(idr);

	    // Persist fingerprint and compound in the database
	    em.persist(compoundToAdd);
	    em.persist(sf);

	} catch (Exception e) {
	    CompoundResponse crf = new CompoundResponse("Adding new compound to database failed. MDL molfile may be malformed.", 500);
	    throw new WebApplicationException(crf.buildResponse());
	}

	// Retun URI of the new compound
	URI compoundUri = uriInfo.getBaseUriBuilder().path("/id/" + compoundToAdd.getId().toString()).build();
	return Response.created(compoundUri).build();

    }
}
