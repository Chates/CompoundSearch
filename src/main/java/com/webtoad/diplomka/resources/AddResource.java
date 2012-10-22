/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.resources;

import com.webtoad.diplomka.AddRequestXML;
import com.webtoad.diplomka.entities.Compound;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
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
 *
 * @author Chates
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

    @POST
    public Response addCompound(JAXBElement<AddRequestXML> requestXML) {
	Compound compoundToAdd = new Compound();
	compoundToAdd.setMolfile(requestXML.getValue().getMolfile());

	// Compute SMILES and Molecular formula
	try {
	    InputStream is = new ByteArrayInputStream(compoundToAdd.getMolfile().getBytes());
	    MDLV2000Reader reader = new MDLV2000Reader(is);
	    AtomContainer molecule = reader.read(new AtomContainer());
	    is.close();
	    reader.close();

	    // Smiles
	    SmilesGenerator sg = new SmilesGenerator();
	    compoundToAdd.setSmiles(sg.createSMILES(molecule));

	    // Molecular formula
	    MolecularFormula molForm = (MolecularFormula) MolecularFormulaManipulator.getMolecularFormula(molecule);
	    compoundToAdd.setMolecularFormula(MolecularFormulaManipulator.getString(molForm));
	    
	    em.persist(compoundToAdd);


	} catch (Exception e) {
	    throw new WebApplicationException(Response.serverError().build());
	}
	
	URI compoundUri = uriInfo.getAbsolutePathBuilder().path("/id/" + compoundToAdd.getId().toString()).build();
	return Response.created(compoundUri).build();

    }
}
