package cz.compoundsearch.similarity;

import cz.compoundsearch.descriptor.ICompoundDescriptor;
import cz.compoundsearch.descriptor.MolWeightDescriptor;
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
 * Similarity based on molecular weights.
 * 
 * @author Martin Mates
 */
public class MolWeightSimilarity extends AbstractSimilarity {

    // Descriptor for molecular weight retrieval
    private ICompoundDescriptor molWeightDescriptor;
    // Result of the molecular weight descriptor
    private Double mwdRequestResult;

    /**
     * Constructor for MolWeightSimilarity with request compound specified.
     * 
     * Descriptor is initialized and performed on the request compound.  
     * 
     * @param c Requested compound from client
     * @throws CompoundSearchException 
     */
    public MolWeightSimilarity(ICompound c) throws CompoundSearchException {
	this.requestCompound = c;

	this.molWeightDescriptor = new MolWeightDescriptor();
	this.mwdRequestResult = (Double) this.molWeightDescriptor.calculate(this.requestCompound.getAtomContainer());
    }

    /**
     * Default constructor for MolWeightSimilarity class.
     * 
     * Descriptor is initialized.
     */
    public MolWeightSimilarity() {
	this.molWeightDescriptor = new MolWeightDescriptor();
    }

    /**
     * Setter for request compound.
     * 
     * Molecular weight descriptor is performed over it.
     * 
     * @param c Requested compound from client
     * @throws CompoundSearchException 
     */
    @Override
    public void setRequestCompound(ICompound c) throws CompoundSearchException {
	this.requestCompound = c;
	this.mwdRequestResult = (Double) this.molWeightDescriptor.calculate(this.requestCompound.getAtomContainer());
    }

    /**
     * Implementation of similarity calculation between two molecules using the 
     * molecular weight descriptor.
     * 
     * Descriptor values from both the request compound and given compound are 
     * computed. Similarity is defined as the ratio of molecular weights
     * 
     * @param c Current compound from database
     * @return Double Calculated similarity
     * @throws CompoundSearchException 
     */
    @Override
    public Double calculateSimilarity(AtomContainer c) throws CompoundSearchException {
	Double requestMolWeight = Double.parseDouble(this.mwdRequestResult.toString());
	Double currentMolWeight = Double.parseDouble(this.molWeightDescriptor.calculate(c).toString());

	Double similarity = (double) Math.min(requestMolWeight, currentMolWeight) / Math.max(requestMolWeight, currentMolWeight);

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
	    throw new CompoundSearchException("MolWeightSimilarity requires 2 parameters");
	}

	try {
	    this.threshold = Double.parseDouble(parameters.get(0));
	    if (this.threshold <= 0) {
		throw new CompoundSearchException("MolWeightSimilarity threshold parameter cannot be less or equal to 0.");
	    }
	} catch (NumberFormatException e) {
	    throw new CompoundSearchException("MolWeightSimilarity threshold parameter must be of type Double");
	}
	
	try {
	    this.numberOfResults = Integer.parseInt(parameters.get(1));	
	    if (this.numberOfResults <= 0) {
		throw new CompoundSearchException("MolWeightSimilarity numberOfResults parameter cannot be less or equal to 0.");
	    }
	} catch (NumberFormatException e) {
	     throw new CompoundSearchException("MolWeightSimilarity numberOfResults parameter must be of type Integer");
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
	    throw new CompoundSearchException("Database error in MolWeightSimilarity. Cannot obtain REST resources.");
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
	    throw new CompoundSearchException("Database error in MolWeightSimilarity. Cannot obtain REST resources.");
	}	
	
	irResult = ir.getCompoundById(id);
	
	if (irResult.isEmpty()) {
	    throw new CompoundSearchException("Database error in MolWeightSimilarity. Cannot obtain compound with an ID " + id + ".");
	}
	
	return irResult.get(0);
    }
}
