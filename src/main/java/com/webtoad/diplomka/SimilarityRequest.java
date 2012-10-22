/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.webtoad.diplomka;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Chates
 */
@XmlRootElement
public class SimilarityRequest {
    
    private String method;
    private String molfile;

    public String getMethod() {
	return method;
    }

    public void setMethod(String method) {
	this.method = method;
    }

    public String getMolfile() {
	return molfile;
    }

    public void setMolfile(String molfile) {
	this.molfile = molfile;
    }
    
    
    
}
