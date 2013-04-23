package cz.compoundsearch.entities;

import cz.compoundsearch.exceptions.CompoundSearchException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.MDLV2000Reader;

/**
 * Implementation of the ICompound interface and database entity at the same
 * time.
 *
 * @author Martin Mates
 */
@Entity
@XmlRootElement
@Table(name = "compound")
public class Compound implements ICompound {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "molecular_formula", length = 255, nullable = false)
    private String molecularFormula;
    @Column(name = "smiles", length = 5000, nullable = false)
    private String smiles;
    @Column(name = "molfile", length = 15000, nullable = false)
    private String molfile;

    public Compound() {
    }

    public Compound(String molfile) {
	this.molfile = molfile;
    }

    /**
     * Getter for compound.
     *
     * @return Compound
     */
    public Compound getCompound() {
	return this;
    }

    /**
     * Getter for compound ID.
     *
     * @return Long ID of the compound in specific repository or database
     */
    @Override
    public Long getId() {
	return id;
    }

    /**
     * Setter for compound ID.
     * 
     * @param id ID of the compound in specific repository of database
     */
    @Override
    public void setId(Long id) {
	this.id = id;
    }

    /**
     * Getter for molecular formula of this compound.
     * 
     * @return String
     */
    public String getMolecularFormula() {
	return molecularFormula;
    }

    /**
     * Setter for molecular formula.
     * 
     * @param molecularFormula 
     */
    public void setMolecularFormula(String molecularFormula) {
	this.molecularFormula = molecularFormula;
    }

    /**
     * Getter for SMILES line notation of this chemical compound.
     * 
     * @return String SMILES line notation
     */
    public String getSmiles() {
	return smiles;
    }

    /**
     * Setter for SMILES line notation of the chemical compound.
     * 
     * @param smiles SMILES line notation
     */
    public void setSmiles(String smiles) {
	this.smiles = smiles;
    }

    /**
     * Getter for SDF molfile format of this compound.
     * 
     * @return String SDF molfile 
     */
    public String getMolfile() {
	return molfile;
    }

    /**
     * Setter for SDF molfile format of this compound.
     * 
     * @param molfile String SDF molfile 
     */
    public void setMolfile(String molfile) {
	this.molfile = molfile;
    }

    /**
     * Getter for AtomContainer.
     * 
     * @return AtomContainer Representation of the chemical molecule in CDK library
     * @throws CompoundSearchException 
     */
    @Override
    public AtomContainer getAtomContainer() throws CompoundSearchException {
	AtomContainer molecule;

	// If Compound doesnt have molfile throw an exception
	if (this.molfile != null) {
	    try {
		// Create AtomContainer using CDK library file reader.
		InputStream is = new ByteArrayInputStream(this.molfile.getBytes());
		MDLV2000Reader reader = new MDLV2000Reader(is);
		molecule = reader.read(new AtomContainer());
		is.close();
		reader.close();
	    } catch (CDKException e) {
		throw new CompoundSearchException("Unable to create AtomContainer from compound. CDK error parsing molfile.");
	    } catch (IOException e) {
		throw new CompoundSearchException("Unable to create AtomContainer from compound. Error reading molfile.");
	    }
	} else {
	    throw new CompoundSearchException("Unable to create AtomContainer from compound. Compound doesnt have molfile.");
	}

	return molecule;
    }
}
