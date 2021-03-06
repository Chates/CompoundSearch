package cz.compoundsearch.similarity;

import cz.compoundsearch.descriptor.AtomCountDescriptor;
import cz.compoundsearch.descriptor.ICompoundDescriptor;
import cz.compoundsearch.entities.Compound;
import cz.compoundsearch.entities.ICompound;
import cz.compoundsearch.exceptions.CompoundSearchException;
import cz.compoundsearch.resources.IdResource;
import cz.compoundsearch.resources.ListResource;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.openscience.cdk.AtomContainer;

/**
 * Similarity based on number of atoms in molecule.
 *
 * @author Martin Mates
 */
public class AtomCountSimilarity extends AbstractSimilarity {

    private ICompoundDescriptor atomCountDescriptor;
    private Integer acdRequestResult;

    /**
     * Constructor accepting the query compound.
     *
     * AtomCount descriptor is initialized and computed for query molecule.
     *
     * @param requestCompound
     * @throws CompoundSearchException
     */
    public AtomCountSimilarity(ICompound requestCompound) throws CompoundSearchException {
	this.requestCompound = requestCompound;

	this.atomCountDescriptor = new AtomCountDescriptor();
	this.acdRequestResult = (Integer) this.atomCountDescriptor.calculate(this.requestCompound.getAtomContainer());
    }

    /**
     * Default constructor.
     *
     * AtomCount descriptor is initialized.
     */
    public AtomCountSimilarity() {
	this.atomCountDescriptor = new AtomCountDescriptor();
    }

    /**
     * Setter for query compound in current similarity
     *
     * @param c Molecule implementing ICompound interface
     * @throws CompoundSearchException
     */
    @Override
    public void setRequestCompound(ICompound c) throws CompoundSearchException {
	this.requestCompound = c;
	this.acdRequestResult = (Integer) this.atomCountDescriptor.calculate(this.requestCompound.getAtomContainer());
    }

    /**
     * Similarity computation between given and request compound.
     *
     * Method compares atom counts of both molecules and return the ratio between
     * them.
     * 
     * This method returns value between 0 and 1 where 0 marks no similarity by
     * definition.
     *
     * @param c Molecule represented as AtomContainer from CDK library
     * @return Double Computed similarity between two molecules
     * @throws CompoundSearchException
     */
    @Override
    public Double calculateSimilarity(AtomContainer c) throws CompoundSearchException {
	Integer requestAtoms = Integer.parseInt(this.acdRequestResult.toString());
	Integer compoundFromDBAtoms = Integer.parseInt(this.atomCountDescriptor.calculate(c).toString());

	Double similarity = (double) Math.min(requestAtoms, compoundFromDBAtoms) / Math.max(requestAtoms, compoundFromDBAtoms);

	return similarity;
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

	if (parameters.size() != 2) {
	    throw new CompoundSearchException("AtomCountSimilarity requires 2 parameters");
	}

	try {
	    this.threshold = Double.parseDouble(parameters.get(0));
	    if (this.threshold <= 0) {
		throw new CompoundSearchException("AtomCountSimilarity threshold parameter cannot be less or equal to 0.");
	    }
	} catch (NumberFormatException e) {
	    throw new CompoundSearchException("AtomCountSimilarity threshold parameter must be of type Double");
	}

	try {
	    this.numberOfResults = Integer.parseInt(parameters.get(1));
	    if (this.numberOfResults <= 0) {
		throw new CompoundSearchException("AtomCountSimilarity numberOfResults parameter cannot be less or equal to 0.");
	    }
	} catch (NumberFormatException e) {
	    throw new CompoundSearchException("AtomCountSimilarity numberOfResults parameter must be of type Integer");
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
	Object[] parameters = new Object[2];
	parameters[0] = this.threshold;
	parameters[1] = this.numberOfResults;

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
	String[] names = new String[2];
	names[0] = "treshold";
	names[1] = "numberOfResults";

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
	if (name.equals("treshold")) {
	    return 0.0;
	}

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
    public List<? extends ICompound> getCompounds(Integer start, Integer limit) throws CompoundSearchException {
	ListResource lr;
	List<? extends ICompound> result;
	try {
	    Context context = new InitialContext();
	    lr = (ListResource) context.lookup("java:module/ListResource");
	    // Must be set. 404 WebApplicationException is invoked and app stopped when empty result otherwise.
	    lr.setCalledFromApp(true);
	} catch (NamingException e) {
	    throw new CompoundSearchException("Database error in AtomCountSimilarity. Cannot obtain REST resources.");
	}

	result = lr.getCompounds(limit, start);

	return result;
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
    public ICompound getCompoundById(Long id) throws CompoundSearchException {
	IdResource ir;
	List<Compound> irResult;

	try {
	    Context context = new InitialContext();
	    ir = (IdResource) context.lookup("java:module/IdResource");
	    ir.setCalledFromApp(true);
	} catch (NamingException e) {
	    throw new CompoundSearchException("Database error in AtomCountSimilarity. Cannot obtain REST resources.");
	}

	irResult = ir.getCompoundById(id);

	if (irResult.isEmpty()) {
	    throw new CompoundSearchException("Database error in AtomCountSimilarity. Cannot obtain compound with an ID " + id + ".");
	}

	return irResult.get(0);
    }
}
