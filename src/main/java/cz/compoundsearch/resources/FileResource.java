/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.resources;

import cz.compoundsearch.descriptor.SubstructureFingerprintDescriptor;
import cz.compoundsearch.descriptor.result.IDescriptorResult;
import cz.compoundsearch.entities.Compound;
import cz.compoundsearch.entities.SubstructureFingerprint;
import cz.compoundsearch.exceptions.CompoundSearchException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.BitSet;
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
import org.openscience.cdk.io.IChemObjectReader.Mode;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.io.MDLV2000Writer;
import org.openscience.cdk.smiles.SmilesGenerator;
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
    public Response readFile(JAXBElement<ChemFile> chemFileJSON) throws Exception, Throwable {

	String filename = "/Users/Chates/Diplomka/Diplomka_maven/Compound_000000001_000025000.sdf";

	File input = new File(filename);

	BufferedReader br = new BufferedReader(new FileReader(input));
	String sdfMolecule = "";
	String sdfCurrentLine;

	int counter = 0;
	int start = 5100;
	while ((sdfCurrentLine = br.readLine()) != null) {
	    // $$$$ marks end of molecule in SDF files if so save molecue to database
	    if (sdfCurrentLine.equals("$$$$")) {
		counter++;
		if (counter < start) {
		    sdfMolecule = "";
		    continue;
		}
		
		if (counter > 7100) {
		    break;
		}

		// Convert String into InputStream
		InputStream is = new ByteArrayInputStream(sdfMolecule.getBytes());
		// Create SDF reader
		MDLV2000Reader reader = new MDLV2000Reader(is, Mode.RELAXED);
		// Read SDF string
		IChemFile fileContents = (IChemFile) reader.read(new ChemFile());
		// Get all molecules
		List<IAtomContainer> containersList = ChemFileManipulator.getAllAtomContainers(fileContents);

		String smiles, mfString;
		SmilesGenerator sg = new SmilesGenerator();

		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		for (IAtomContainer ia : containersList) {

		    // Some MDL file fixes. Needed if mode is set to RELAXED
		    AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(ia); // perceive atom types
		    //CDKHydrogenAdder adder = CDKHydrogenAdder.getInstance(ia.getBuilder());
		    //adder.addImplicitHydrogens(ia); // add implicit hydrogens
		    // Remove hydrogens since we work only with implicit hydrogens and structure is smaller in database
		    ia = AtomContainerManipulator.removeHydrogens(ia);
//		    try {
//			// Deduce bond order 4 because SDF cannot hold bond order 4
//			DeduceBondSystemTool dbst = new DeduceBondSystemTool();
//			ia = dbst.fixAromaticBondOrders((IMolecule) ia); 
//		    } catch (NullPointerException e) {
//			// Deduce bond order 4 failed. Do nothing and continue.
//		    }

		    // Get SMILES		
		    smiles = sg.createSMILES(ia);

		    // If generating of SMILES failed. SDF molecule is malformed skip it.
		    if (!smiles.equals("")) {

			// Get molecular formula
			MolecularFormula molForm = (MolecularFormula) MolecularFormulaManipulator.getMolecularFormula(ia);
			mfString = MolecularFormulaManipulator.getString(molForm);

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


			    // Generate substructure fingerprint
			    SubstructureFingerprint sf = new SubstructureFingerprint();
			    SubstructureFingerprintDescriptor sfd = new SubstructureFingerprintDescriptor();
			    IDescriptorResult idr;
			    try {								
				idr = sfd.calculate(c);
			    } catch (CompoundSearchException e) {
				continue; // Cannot calculate fingerptint skip compound.
			    }
			    sf.setCompound(c);
			    sf.setFingerprint((BitSet) idr.getValue());

			    em.persist(c);
			    em.persist(sf);

			} catch (CDKException e) {
			    continue; // If mol file is not generated, skip it
			}
		    }

		}

		sdfMolecule = "";
	    } else {
		sdfMolecule += sdfCurrentLine + "\n";
	    }
	}


	// TODO: Generovani InChi kodu nefunguje, ale mohlo by fungovat na jine platforme
//	    InChIGeneratorFactory factory = InChIGeneratorFactory.getInstance();
//	    InChIGenerator generator = factory.getInChIGenerator(containersList.get(0));
//	    String inchi = generator.getInchi();




	return Response.ok().build();
    }
}
