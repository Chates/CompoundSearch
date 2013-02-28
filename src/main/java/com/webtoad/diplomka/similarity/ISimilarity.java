/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.similarity;

import com.webtoad.diplomka.results.SimilarityResult;
import com.webtoad.diplomka.CompoundSearchException;
import com.webtoad.diplomka.entities.Compound;
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
