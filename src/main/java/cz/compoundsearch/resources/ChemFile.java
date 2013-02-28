/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.resources;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Chates
 */
@XmlRootElement
public class ChemFile {
    
    private String filename;

    public String getFilename() {
	return filename;
    }

    public void setFilename(String filename) {
	this.filename = filename;
    }
    
    
}
