/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka.results;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Chates
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SimilarityParameter {
    
    private String name;
    
    private String type;

    public SimilarityParameter() {

    }
    
    public SimilarityParameter(String name, String type) {
	this.name = name;
	this.type = type;
    }
       

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }
    
    
    
}
