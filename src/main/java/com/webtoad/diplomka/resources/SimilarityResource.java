/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.resources;

import com.webtoad.diplomka.CompoundSearchException;
import com.webtoad.diplomka.SimilarityRequestXML;
import com.webtoad.diplomka.entities.Compound;
import com.webtoad.diplomka.similarity.AtomCountSimilarity;
import com.webtoad.diplomka.similarity.SimilarityResult;
import com.webtoad.diplomka.similarity.SubstructureSimilarity;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

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
    private EntityManager em;
    @EJB
    private ListResource listResource;

    @POST
    @Path("/substructure/")
    public List<SimilarityResult> substructureSimilarity(JAXBElement<SimilarityRequestXML> sr) {
	try {
	    Compound requestCompound = new Compound(sr.getValue().getMolfile());
	    SubstructureSimilarity ss = new SubstructureSimilarity(requestCompound);

	    List<SimilarityResult> similarityResults = ss.findAllSimilar();

	    if (similarityResults.isEmpty()) {
		CompoundResponse cr = new CompoundResponse("None compound is similar.", 404);
		throw new WebApplicationException(cr.buildResponse());
	    }

	    return similarityResults;
	} catch (CompoundSearchException e) {
	    CompoundResponse cr = new CompoundResponse(500, e);
	    throw new WebApplicationException(cr.buildResponse());
	}

    }

    @POST
    @Path("/atom-count/")
    public List<SimilarityResult> atomCountSimilarity(JAXBElement<SimilarityRequestXML> sr) {

	try {
	    Compound requestCompound = new Compound(sr.getValue().getMolfile());
	    AtomCountSimilarity acs = new AtomCountSimilarity(requestCompound);
	    
	    Object[] parameters = new Object[2];
	    parameters[0] = 0.8;
	    parameters[1] = 10;
	    acs.setParameters(parameters);	    
	    acs.setEntityManager(this.em);

	    List<SimilarityResult> similarityResults = acs.findAllSimilar();

	    if (similarityResults.isEmpty()) {
		CompoundResponse cr = new CompoundResponse("None compound is similar.", 404);
		throw new WebApplicationException(cr.buildResponse());
	    }

	    return similarityResults;

	} catch (CompoundSearchException e) {
	    CompoundResponse cr = new CompoundResponse(500, e);
	    throw new WebApplicationException(cr.buildResponse());
	}




	// Obtain test compound from DB
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
	//
	//		// Descriptor testing
	//		AtomCountDescriptor acd = new AtomCountDescriptor();
	////		String[] descriptorNames = acd.getDescriptorNames(); // Descriptor Names
	////		IDescriptorResult descriptorResultType = acd.getDescriptorResultType(); // Descriptor Result Type
	////		String[] parameterNames = acd.getParameterNames(); // Parameter names
	////		
	////		Object[] parameterTypes = new Object[parameterNames.length]; // Parameter types
	////		Integer i = 0;
	////		for (String pn : parameterNames) {
	////		    parameterTypes[i] = acd.getParameterType(pn);
	////		}
	//
	//
	//
	//
	////		Object[] params = new Object[acd.getParameterNames().length];
	////		params[0] = "C";
	////		acd.setParameters(params);
	//
	//		DescriptorValue dValue = acd.calculate(resourceMolecule);
	//		IDescriptorResult idR = dValue.getValue();
	//		try {
	//		    CompoundDescriptor cd = new CompoundDescriptor();
	//		    cd.setAtomCount(Integer.parseInt(idR.toString()));
	//		    cd.setCompound(c);
	//		    em.persist(cd);
	//
	//		    em.flush();
	//		} catch (PersistenceException e) { // Is it in the database already?
	//		    
	//		    
	//		    Throwable test = e.getCause();
	//
	//		    System.out.println("");
	//		}
	////		
	////		String test = idR.toString();
	//		// Try to compare
	//		//isSimilar = UniversalIsomorphismTester.isIsomorph(requestMolecule, resourceMolecule); //Exact similarity
	//
	//		// Using substructure we need to add implicit hydrogens to resource and to request
	////		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(resourceMolecule); // perceive atom types
	////		CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(resourceMolecule.getBuilder());
	////		adder.addImplicitHydrogens(resourceMolecule); // add implicit hydrogens
	////		AtomContainerManipulator.convertImplicitToExplicitHydrogens(resourceMolecule);
	////		// request molecule
	////		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(requestMolecule); // perceive atom types
	////		adder = CDKHydrogenAdder.getInstance(requestMolecule.getBuilder());
	////		adder.addImplicitHydrogens(requestMolecule); // add implicit hydrogens
	////		AtomContainerManipulator.convertImplicitToExplicitHydrogens(requestMolecule);
	//
	//		isSimilar = UniversalIsomorphismTester.isSubgraph(resourceMolecule, requestMolecule);
	//
	//		if (isSimilar) {
	//		    similarityResults.add(c);
	//		}
	//	    }
	//	} catch (CDKException e) {
	//	    throw new WebApplicationException(Response.serverError().build());
	//	}


    }
}
