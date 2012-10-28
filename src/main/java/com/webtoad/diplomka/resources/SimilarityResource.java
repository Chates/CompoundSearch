/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.resources;

import com.webtoad.diplomka.SimilarityRequestXML;
import com.webtoad.diplomka.entities.Compound;
import com.webtoad.diplomka.entities.CompoundDescriptor;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.descriptors.molecular.AtomCountDescriptor;
import org.openscience.cdk.qsar.result.IDescriptorResult;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

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
    @Path("/test/")
    public List<Compound> testSimilarity(JAXBElement<SimilarityRequestXML> sr) throws Exception  {

	List<Compound> similarityResults = new ArrayList<Compound>();

	// Obtain test compound from DB
	List<Compound> sourceCompounds = listResource.getCompounds();

	try {
	    // Create molecule from request
	    InputStream is = new ByteArrayInputStream(sr.getValue().getMolfile().getBytes());
	    MDLV2000Reader reader = new MDLV2000Reader(is);
	    AtomContainer requestMolecule = reader.read(new AtomContainer());
	    is.close();
	    reader.close();

	    Boolean isSimilar;

	    // Loop over compounds
	    for (Compound c : sourceCompounds) {

		// Create Molecule from DB molfile using CDK library
		is = new ByteArrayInputStream(c.getMolfile().getBytes());
		reader = new MDLV2000Reader(is);
		AtomContainer resourceMolecule = reader.read(new AtomContainer());
		is.close();
		reader.close();


		// Descriptor testing
		AtomCountDescriptor acd = new AtomCountDescriptor();
//		String[] descriptorNames = acd.getDescriptorNames(); // Descriptor Names
//		IDescriptorResult descriptorResultType = acd.getDescriptorResultType(); // Descriptor Result Type
//		String[] parameterNames = acd.getParameterNames(); // Parameter names
//		
//		Object[] parameterTypes = new Object[parameterNames.length]; // Parameter types
//		Integer i = 0;
//		for (String pn : parameterNames) {
//		    parameterTypes[i] = acd.getParameterType(pn);
//		}




//		Object[] params = new Object[acd.getParameterNames().length];
//		params[0] = "C";
//		acd.setParameters(params);

		DescriptorValue dValue = acd.calculate(resourceMolecule);
		IDescriptorResult idR = dValue.getValue();
		try {
		    CompoundDescriptor cd = new CompoundDescriptor();
		    cd.setAtomCount(Integer.parseInt(idR.toString()));
		    cd.setCompound(c);
		    em.persist(cd);

		    em.flush();
		} catch (PersistenceException e) { // Is it in the database already?
		    
		    
		    Throwable test = e.getCause();

		    System.out.println("");
		}
//		
//		String test = idR.toString();
		// Try to compare
		//isSimilar = UniversalIsomorphismTester.isIsomorph(requestMolecule, resourceMolecule); //Exact similarity

		// Using substructure we need to add implicit hydrogens to resource and to request
//		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(resourceMolecule); // perceive atom types
//		CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(resourceMolecule.getBuilder());
//		adder.addImplicitHydrogens(resourceMolecule); // add implicit hydrogens
//		AtomContainerManipulator.convertImplicitToExplicitHydrogens(resourceMolecule);
//		// request molecule
//		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(requestMolecule); // perceive atom types
//		adder = CDKHydrogenAdder.getInstance(requestMolecule.getBuilder());
//		adder.addImplicitHydrogens(requestMolecule); // add implicit hydrogens
//		AtomContainerManipulator.convertImplicitToExplicitHydrogens(requestMolecule);

		isSimilar = UniversalIsomorphismTester.isSubgraph(resourceMolecule, requestMolecule);

		if (isSimilar) {
		    similarityResults.add(c);
		}
	    }
	} catch (CDKException e) {
	    throw new WebApplicationException(Response.serverError().build());
	}

	if (similarityResults.isEmpty()) {
	    throw new WebApplicationException(Response.status(404).entity("None compound is similar.").build());
	}

	return similarityResults;
    }
}
