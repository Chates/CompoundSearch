package cz.compoundsearch.similarity;

import cz.compoundsearch.descriptor.SubstructureFingerprintDescriptor;
import cz.compoundsearch.entities.Compound;
import cz.compoundsearch.entities.ICompound;
import cz.compoundsearch.entities.IFingerprint;
import cz.compoundsearch.exceptions.CompoundSearchException;
import cz.compoundsearch.exceptions.NoMoreCompoundsException;
import cz.compoundsearch.resources.IdResource;
import cz.compoundsearch.resources.ListResource;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;

/**
 *
 * @author Martin Mates
 */
public class SubstructureSimilarity extends AbstractSimilarity {


    /**
     * Constructor accepting the query compound.
     *
     * Threshold is set automatically to 1.0 because there may be only
     * true/false result.
     *
     * @param requestCompound
     */
    public SubstructureSimilarity(ICompound requestCompound) {
	this.requestCompound = requestCompound;
	this.threshold = 1.0;
    }

    /**
     * Default constructor.
     *
     * Threshold is set automatically to 1.0 because there may be only
     * true/false result.
     */
    public SubstructureSimilarity() {
	this.threshold = 1.0;
    }

    /**
     * Computation of the similarity between current and query molecule.
     *
     * Method tests graph isomorphism using UniversalIsomorphismTester in CDK
     * library.
     *
     * @param c Molecule represented as AtomContainer from CDK library
     * @return Double Computed similarity between two molecules
     * @throws CompoundSearchException
     */
    @Override
    public Double calculateSimilarity(AtomContainer c) throws CompoundSearchException {
	try {
	    if (UniversalIsomorphismTester.isSubgraph(c, this.requestCompound.getAtomContainer()) == true) {
		return 1.0;
	    } else {
		return 0.0;
	    }
	} catch (CDKException e) {
	    throw new CompoundSearchException("Unable to test substructure presence CDK error.");
	}
    }

    /**
     * Implementation of the screening procedure.
     *
     * Screening is done by comparison of two fingerprints. These fingerprints
     * consist of a sequence of “0”s and “1s”. A “1” in a fingerprint usually
     * indicates the presence of a particular structural feature and a “0” its
     * absence. Thus if a feature is present in the substructure (there is a “1”
     * in its fingerprint) but not in the query molecule (the corresponding
     * value is “0”) then it can be readily determined from the fingerprint
     * comparison that the molecule cannot contain the substructure.
     *
     * @param start
     * @param limit
     * @return List<Compound> List of compounds that passed the screening
     * procedure
     * @throws CompoundSearchException
     * @throws NoMoreCompoundsException
     */
    @Override
    public List<Compound> screen(Integer start, Integer limit) throws CompoundSearchException, NoMoreCompoundsException {
	// Result of the screening
	List<Compound> screeningResult = new ArrayList<Compound>();

	// Fingerprint of requested compound
	SubstructureFingerprintDescriptor sfd = new SubstructureFingerprintDescriptor();
	BitSet reqCompFingerprint = (BitSet) sfd.calculate(this.requestCompound.getAtomContainer());

	// Selected fingeprints from DB
	List<? extends IFingerprint> sfResult;
	sfResult = this.getCompounds(start, limit);

	// Result empty. No more compounds in database throw exception
	if (sfResult.isEmpty()) {
	    throw new NoMoreCompoundsException();
	}


	// For each Compound perform screening
	for (IFingerprint sf : sfResult) {
	    BitSet curCompFingerprint = sf.getFingerprint();
	    BitSet reqCompFingerprintClone = (BitSet) reqCompFingerprint.clone();

	    // Perform logical AND to currentCompound
	    reqCompFingerprintClone.and(curCompFingerprint);

	    // All bits in requested compound has to be in current compound
	    if (reqCompFingerprint.equals(reqCompFingerprintClone)) {
		screeningResult.add(sf.getCompound()); // May be substructure
	    }
	}

	return screeningResult;

    }

    /**
     * This is a setter for similarity parameters and place where parameter
     * validation is implemented.
     *
     * @param parameters List of parameters
     * @throws CompoundSearchException
     */
    @Override
    public void setParameters(List<String> parameters) throws CompoundSearchException {
	if (parameters.size() != 1) {
	    throw new CompoundSearchException("SubStructureSimilarity requires 1 parameter");
	}

	try {
	    this.numberOfResults = Integer.parseInt(parameters.get(0));
	    if (this.numberOfResults <= 0) {
		throw new CompoundSearchException("SubStructureSimilarity numberOfResults parameter cannot be less or equal to 0.");
	    }
	} catch (NumberFormatException e) {
	    throw new CompoundSearchException("SubStructureSimilarity numberOfResults parameter must be of type Integer");
	}

    }

    /**
     * Getter for similarity parameters.
     *
     * Returns array of type Object since the parameters may be of any type.
     *
     * @return Object[] Array of similarity parameters
     */
    @Override
    public Object[] getParameters() {
	Object[] parameters = new Object[1];
	parameters[0] = this.numberOfResults;

	return parameters;
    }

    /**
     * Getter returning parameter names.
     *
     * This is needed for client asking similarity parameters definition.
     *
     * @return String[] Array of parameter names
     */
    @Override
    public String[] getParameterNames() {
	String[] names = new String[1];
	names[0] = "numberOfResults";

	return names;
    }

    /**
     * This method accepts name of the parameter and returns its type.
     *
     * This is needed for client asking similarity parameters definition.
     *
     * @param name Name of the parameter
     * @return Object Instance of the same type as parameter of the given name
     */
    @Override
    public Object getParameterType(String name) {
	if (name.equals("numberOfResults")) {
	    return 1;
	}

	return null;
    }

    /**
     * Compound retrieval from database.
     *
     * Method calls an existing REST web service that has method for compound
     * retrieval. We will obtain a reference to this resource form context of
     * Java application container.
     *
     * @param start
     * @param limit
     * @return List<? extends ICompound> List of compound from database
     * @throws CompoundSearchException
     */
    @Override
    public List<? extends IFingerprint> getCompounds(Integer start, Integer limit) throws CompoundSearchException {
	ListResource lr;

	try {
	    Context context = new InitialContext();
	    lr = (ListResource) context.lookup("java:module/ListResource");
	    // Must be set. 404 WebApplicationException is invoked and app stopped when empty result otherwise.
	    lr.setCalledFromApp(true);
	} catch (NamingException e) {
	    throw new CompoundSearchException("Database error in SubstructureSimilarity. Cannot obtain REST resources.");
	}

	// Selected fingeprints from DB
	List<? extends IFingerprint> sfResult = lr.getSubstructureFingerprint(limit, start);

	return sfResult;
    }

    /**
     * Returns compound with a given id from database.
     *
     * Method calls an existing REST web service that has method for compound
     * retrieval. We will obtain a reference to this resource form context of
     * Java application container.
     *
     * @param id Identification number of the compound to be searched
     * @return ICompound Compound from database
     * @throws CompoundSearchException
     */
    @Override
    public Compound getCompoundById(Long id) throws CompoundSearchException {
	IdResource ir;
	List<Compound> irResult;

	try {
	    Context context = new InitialContext();
	    ir = (IdResource) context.lookup("java:module/IdResource");
	    ir.setCalledFromApp(true);
	} catch (NamingException e) {
	    throw new CompoundSearchException("Database error in SubstructureSimilarity. Cannot obtain REST resources.");
	}

	irResult = ir.getCompoundById(id);

	if (irResult.isEmpty()) {
	    throw new CompoundSearchException("Database error in SubstructureSimilarity. Cannot obtain compound with an ID " + id + ".");
	}

	return irResult.get(0);
    }
}