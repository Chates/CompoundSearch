/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.similarity;

import com.webtoad.diplomka.entities.Compound;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;

/**
 *
 * @author Chates
 */
public class ExactMatchSimilarity {
    
    
//    List<Compound> similarityResults = new ArrayList();
//
//	// Obtain test compound from DB
//	List<Compound> sourceCompounds = listResource.getCompounds();
//
//	try {
//	    // Create molecule from request
//	    InputStream is = new ByteArrayInputStream(sr.getValue().getMolfile().getBytes());
//	    MDLV2000Reader reader = new MDLV2000Reader(is);
//	    AtomContainer requestMolecule = reader.read(new AtomContainer());
//	    is.close();
//	    reader.close();
//
//	    Boolean isSimilar;
//
//	    // Loop over compounds
//	    for (Compound c : sourceCompounds) {
//
//		// Create Molecule from DB molfile using CDK library
//		is = new ByteArrayInputStream(c.getMolfile().getBytes());
//		reader = new MDLV2000Reader(is);
//		AtomContainer resourceMolecule = reader.read(new AtomContainer());
//		is.close();
//		reader.close();
//
//		// Try to compare
//		isSimilar = UniversalIsomorphismTester.isIsomorph(requestMolecule, resourceMolecule); //Exact similarity
//		if (isSimilar) {
//		    similarityResults.add(c);
//		}
//	    }
//	} catch (Exception e) {
//	    throw e;
//	    //throw new WebApplicationException(Response.serverError().build());
//	}
//
//	if (similarityResults.isEmpty()) {
//	    throw new WebApplicationException(Response.status(404).entity("None compound is similar.").build());
//	}
//
//	return similarityResults;
    
    
    
}
