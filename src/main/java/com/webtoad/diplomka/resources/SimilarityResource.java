/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.resources;

import com.webtoad.diplomka.SimilarityRequest;
import com.webtoad.diplomka.entities.Compound;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;

/**
 *
 * @author Chates
 */
@Path("/similarity/")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Stateless
public class SimilarityResource {

    @PersistenceContext(unitName = "com.webtoad_Diplomka_maven_war_1.0PU")
    protected EntityManager em;
    @EJB
    protected IdResource idResource;

    @POST
    @Path("/test/")
    public Response testSimilarity(JAXBElement<SimilarityRequest> sr) {

	// Obtain test compound from DB
	Compound c = idResource.getCompoundById(new Long(11420));

	Boolean isIsomorph;
	
	try {
	    // Create Molecule from DB molfile using CDK library
	    InputStream is = new ByteArrayInputStream(c.getMolfile().getBytes());
	    MDLV2000Reader reader = new MDLV2000Reader(is);
	    AtomContainer acResource = reader.read(new AtomContainer());
	    is.close();
	    reader.close();
	    
	    // Create molecule from request
	    is = new ByteArrayInputStream(sr.getValue().getMolfile().getBytes());
	    reader = new MDLV2000Reader(is);
	    AtomContainer acRequest = reader.read(new AtomContainer());
	    is.close();
	    reader.close();
	    
	    // Try to compare
	    isIsomorph = UniversalIsomorphismTester.isIsomorph(acResource, acRequest); //Exact similarity
	    
	    
	} catch (Exception e) {
	    return Response.serverError().build();
	}


	return Response.ok().build();
    }
}
