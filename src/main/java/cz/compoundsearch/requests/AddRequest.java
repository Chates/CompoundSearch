package cz.compoundsearch.requests;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * XML/JSON mapping for HTTP request adding a new molecule to the database. 
 * 
 * Request is expecting MDL molfile as String.
 * 
 * @author Martin Mates
 */
@XmlRootElement
public class AddRequest {
    
    private String molfile;

    /**
     * Getter for MDL molfile.
     * 
     * @return String MDL molfile
     */
    public String getMolfile() {
	return molfile;
    }

    /**
     * Setter for MDL molfile
     * 
     * @param molfile MDL molfile
     */
    public void setMolfile(String molfile) {
	this.molfile = molfile;
    }
    
}
