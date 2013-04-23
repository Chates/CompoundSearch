package cz.compoundsearch.results;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * XML/JSON mapping of similarity parameter in HTTP response body.
 *
 * This is needed when client query information about available similarities and
 * their parameters.
 *
 * This class is used in {@link cz.compoundsearch.resources.SimilarityResource}.
 *
 * @author Martin Mates
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

    /**
     * Getter for parameter name.
     *
     * @return String Parameter name.
     */
    public String getName() {
	return name;
    }

    /**
     * Setter for parameter name.
     *
     * @param name Parameter name.
     */
    public void setName(String name) {
	this.name = name;
    }

    /**
     * Getter for parameter type.
     *
     * Type of String because of the XML/JSON mapping. This type is used only as
     * an information in HTTP response.
     *
     * @return String Parameter type.
     */
    public String getType() {
	return type;
    }

    /**
     * Setter for parameter type. 
     * 
     * Type of String because of the XML/JSON
     * mapping. This type is used only as an information in HTTP response.
     *
     * @param type Parameter type.
     */
    public void setType(String type) {
	this.type = type;
    }
}
