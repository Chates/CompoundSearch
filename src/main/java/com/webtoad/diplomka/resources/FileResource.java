/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.resources;

import com.webtoad.diplomka.ChemFileJSON;
import com.webtoad.diplomka.entities.Compound;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.util.List;
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
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.formula.MolecularFormula;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.IChemObjectReader;
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.ISimpleChemObjectReader;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.smiles.DeduceBondSystemTool;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.tools.CDKHydrogenAdder;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

/**
 *
 * @author Chates
 */
@Path("/file/")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Stateless
public class FileResource {

    @PersistenceContext(unitName = "com.webtoad_Diplomka_maven_war_1.0PU")
    private EntityManager em;

    @POST
    @Path("/download/")
    public Response readFile(JAXBElement<ChemFileJSON> chemFileJSON) throws Exception, Throwable {

	// TODO: Stazeni souboru z dane URL
	String filename = "/Users/Chates/Diplomka/Diplomka_maven/apigenex.sdf";

	try {
	    File input = new File(filename);

	    // Vytvoreni readeru podle typu souboru
	    //ReaderFactory readerFactory = new ReaderFactory();
	    MDLV2000Reader reader = new MDLV2000Reader(new FileReader(input), Mode.RELAXED);

	    IChemFile fileContents = (IChemFile) reader.read(new ChemFile());
	    List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(fileContents);

	    // TODO: Generovani InChi kodu nefunguje, ale mohlo by fungovat na jine platforme
//	    InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
//	    InChIGenerator generator = factory.getInChIGenerator(containersList.get(0));
//	    String inchi = generator.getInchi();

	    String smiles, mfString;
	    SmilesGenerator sg = new SmilesGenerator();

	    ByteArrayOutputStream baos = new ByteArrayOutputStream();

	    for (IAtomContainer ia : containersList) {

		// Some MDL file fixes. Needed if mode is set to RELAXED
		AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ia); // perceive atom types
		CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(ia.getBuilder());
		adder.addImplicitHydrogens(ia); // add implicit hydrogens
		//AtomContainerManipulator.convertImplicitToExplicitHydrogens(ia); // Convert implicit hydrogens to explicit
		try {
		    DeduceBondSystemTool dbst = new DeduceBondSystemTool();
		    ia = dbst.fixAromaticBondOrders((IMolecule) ia); // Deduce bond order 4 because SDF cannot hold bond order 4
		} catch (NullPointerException e) {
		    // Deduce bond order 4 failed. Do nothing.
		}

		// Get SMILES		
		smiles = sg.createSMILES(ia);

		// TODO: Pro nektere molekuly je SMILES null proc?
		if (!smiles.equals("")) {

		    // Get molecular formula
		    MolecularFormula molForm = (MolecularFormula) MolecularFormulaManipulator.getMolecularFormula(ia);
		    mfString = MolecularFormulaManipulator.getString(molForm);

		    // TODO: Nektere molekuly vyhodi vyjimku pri generovani mol file proc? Zkusit vygenerovat molfile ze smiles.
		    try {
			// Get mol file
			baos.reset();
			MDLV2000Writer m2w = new MDLV2000Writer(baos);
			//m2w.setWriter(baos);
			m2w.write(ia);
			m2w.close();
			String molfile = new String(baos.toByteArray(), "UTF-8");

			Compound c = new Compound();
			c.setMolecularFormula(mfString);
			c.setSmiles(smiles);
			c.setMolfile(molfile);
			em.persist(c);
		    } catch (CDKException e) {
			continue; // If mol file is not generated, skip it
		    }
		}

	    }

	} catch (Exception e) {
	    throw e.getCause(); // Debug reasons
	    //return Response.serverError().build();
	}

	return Response.ok().build();
    }
}
