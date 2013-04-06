/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.similarity;

import cz.compoundsearch.results.SimilarityResult;
import cz.compoundsearch.exceptions.CompoundSearchException;
import cz.compoundsearch.entities.Compound;
import cz.compoundsearch.entities.ICompound;
import cz.compoundsearch.exceptions.NoMoreCompoundsException;
import java.util.List;
import org.openscience.cdk.AtomContainer;

/**
 *
 * @author Chates
 */
public interface ISimilarity {
    
    public List<? extends ICompound> screen(Integer start, Integer limit) throws CompoundSearchException, NoMoreCompoundsException;

    public List<SimilarityResult> findAllSimilar() throws CompoundSearchException;
    
    public Double calculateSimilarity(AtomContainer c) throws CompoundSearchException;
    
    public List<? extends ICompound> getCompounds(Integer start, Integer limit) throws CompoundSearchException;
    
    public Compound getCompoundById(Long id) throws CompoundSearchException;

    public void setParameters(List<String> parameters) throws CompoundSearchException;
    
    public Object[] getParameters();
    
    public String[] getParameterNames();
    
    public Object getParameterType(String name);
    
    public ICompound getRequestCompound();
    
    public void setRequestCompound(ICompound c) throws CompoundSearchException;
    
}
