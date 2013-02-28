/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.requests;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Chates
 */
@XmlRootElement
public class AddRequest {
    
    private String molfile;

    public String getMolfile() {
	return molfile;
    }

    public void setMolfile(String molfile) {
	this.molfile = molfile;
    }
    
    
    
}
