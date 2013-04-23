package cz.compoundsearch.similarity;

import cz.compoundsearch.entities.ICompound;
import cz.compoundsearch.exceptions.CompoundSearchException;
import cz.compoundsearch.exceptions.NoMoreCompoundsException;
import cz.compoundsearch.results.SimilarityResult;
import java.util.List;
import org.openscience.cdk.AtomContainer;

/**
 * Interface for all implemented similarities. 
 * 
 * @author Martin Mates
 */
public interface ISimilarity {
    
    /**
     * Method is responsible for fast compound screening. 
     * 
     * This method should retrieve batch of compounds from database and performs 
     * rapid pre-elimination of inappropriate compounds, for example fingerprint 
     * comparison. 
     * 
     * If there are no more compounds in database NoMoreCompoundsException that 
     * marks the end of whole searching is thrown.
     * 
     * @param start Index of the compound where screening will be started
     * @param limit Limit of the compounds to screen
     * @return List<? extends ICompound> List of compounds that passed the 
     * screening phase
     * @throws CompoundSearchException
     * @throws NoMoreCompoundsException 
     */
    public List<? extends ICompound> screen(Integer start, Integer limit) throws CompoundSearchException, NoMoreCompoundsException;

    /**
     * In this method similarity algorithm should be specified. 
     * 
     * This method is meant for algorithm that goes through a given data set and 
     * uses predefined similarities for similarity matching.
     * 
     * @return List<SimilarityResult> List of results
     * @throws CompoundSearchException 
     */
    public List<SimilarityResult> findAllSimilar() throws CompoundSearchException;
    
    /**
     * This is a place for similarity computation between given and request 
     * compound. 
     * 
     * This method returns value between 0 and 1 where 0 marks no similarity by 
     * definition. This is where programmer uses descriptors and defines 
     * similarity metrics. 
     * 
     * @param c Molecule represented as AtomContainer from CDK library
     * @return Double Computed similarity between two molecules
     * @throws CompoundSearchException 
     */
    public Double calculateSimilarity(AtomContainer c) throws CompoundSearchException;
    
    /**
     * Method is responsible for compound retrieval from the database. 
     * 
     * This is place where database connection should be defined or used. 
     * 
     * @param start
     * @param limit
     * @return List<? extends ICompound> List of molecules from database
     * @throws CompoundSearchException 
     */
    public List<? extends ICompound> getCompounds(Integer start, Integer limit) throws CompoundSearchException;
    
    /**
     * This method should return one compound from the database identified by 
     * its id. 
     * 
     * Method is needed when similarity results are known and client calls for 
     * specific compound. This is place where database connection should be 
     * defined or used.
     * 
     * @param id ID of the compound
     * @return ICompound Compound from database
     * @throws CompoundSearchException 
     */
    public ICompound getCompoundById(Long id) throws CompoundSearchException;

    /**
     * This is a setter for similarity parameters and place where parameter 
     * validation should be implemented.
     * 
     * @param parameters 
     * @throws CompoundSearchException 
     */
    public void setParameters(List<String> parameters) throws CompoundSearchException;
    
    /**
     * Getter for similarity parameters.
     * 
     * Returns array of type Object since the parameters may be of any type.
     * 
     * @return Object[] Array of similarity parameters
     */
    public Object[] getParameters();
    
    /**
     * Getter returning parameter names. 
     * 
     * This is needed for client asking similarity parameters definition.
     * 
     * @return String[] Array of parameter names
     */
    public String[] getParameterNames();
    
    /**
     * This method accepts name of the parameter and returns its type. 
     * 
     * This is needed for client asking similarity parameters definition.
     * 
     * @param name Name of the parameter
     * @return Object Instance of the same type as parameter of the given name
     */
    public Object getParameterType(String name);
    
    /**
     * Getter for query compound in current similarity.
     * 
     * @return ICompound Query molecule
     */
    public ICompound getRequestCompound();
    
    /**
     * Setter for query compound in current similarity
     * 
     * @param c Molecule implementing ICompound interface
     * @throws CompoundSearchException 
     */
    public void setRequestCompound(ICompound c) throws CompoundSearchException;
    
}
