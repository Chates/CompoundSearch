/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.similarity;

import cz.compoundsearch.results.SimilarityResult;
import cz.compoundsearch.exceptions.CompoundSearchException;
import cz.compoundsearch.entities.Compound;
import java.util.List;

/**
 *
 * @author Chates
 */
public interface ISimilarity {
    
    public void screen() throws CompoundSearchException;

    public List<SimilarityResult> findAllSimilar() throws CompoundSearchException;
    
    public Double calculateSimilarity(Compound c) throws CompoundSearchException;
    
    public List<Compound> getCompounds(Integer start, Integer limit) throws CompoundSearchException;;

    public void setParameters(Object[] parameters) throws CompoundSearchException;
    
    public Object[] getParameters();
    
    public String[] getParameterNames();
    
    public Object getParameterType(String name);
    
}
