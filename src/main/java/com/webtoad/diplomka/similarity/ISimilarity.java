/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.similarity;

import com.webtoad.diplomka.CompoundSearchException;
import com.webtoad.diplomka.entities.Compound;
import java.util.List;

/**
 *
 * @author Chates
 */
public interface ISimilarity {
    
    public void screen();

    public List<Compound> findAllSimilar() throws CompoundSearchException;
    
    public Boolean isSimilar(Compound c) throws CompoundSearchException;
    
    public void setCompounds(List<Compound> lc);
    
    
}
