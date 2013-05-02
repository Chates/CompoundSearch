package cz.compoundsearch.entities;

import cz.compoundsearch.exceptions.CompoundSearchException;
import java.util.BitSet;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import org.openscience.cdk.AtomContainer;

/**
 * Database entity for structural fingerprint from SubstructureFingerprint 
 * descriptor.
 * 
 * @author Martin Mates
 */
@Entity
@XmlRootElement
@Table(name = "substructure_fingerprint")
@Access(AccessType.PROPERTY)
public class SubstructureFingerprint implements ICompound, IFingerprint {

    private Long id;
    private BitSet fingerprint;
    private Compound compound;

    /**
     * Getter for compound ID.
     *
     * @return Long ID of the compound in specific repository or database
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
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
     * Getter for structural fingerprint.
     * 
     * @return BitSet Structural fingerprint
     */
    @Column(name = "fingerprint")
    @Override
    public BitSet getFingerprint() {
	return fingerprint;
    }

    /**
     * Setter for structural fingerprint.
     * 
     * @param fingerprint Structural fingerprint
     */
    @Override
    public void setFingerprint(BitSet fingerprint) {
	this.fingerprint = fingerprint;
    }

    /**
     * Getter for molecule from which the fingerprint is calculated.
     * 
     * This is a mapping of table column to Compound entity as a foreign key.
     * 
     * @return Compound Molecule from which the fingerprint is calculated.
     */
    @OneToOne
    @JoinColumn(name = "compound_id", unique = true)
    @Override
    public Compound getCompound() {
	return this.compound;
    }
    
    /**
     * Getter for molecule from which the fingerprint is calculated.
     * 
     * This is a mapping of table column to Compound entity as a foreign key.
     * 
     * @return Compound Molecule from which the fingerprint is calculated.
     */
    @OneToOne
    @JoinColumn(name = "compound_id", unique = true)
    public Compound getCompoundId() {
	return this.compound.getId();
    }

    /**
     * Setter for chemical compound.
     * @param compound 
     */
    public void setCompound(Compound compound) {
	this.compound = compound;
    }

    /**
     * Getter for AtomContainer.
     * 
     * @return AtomContainer Representation of the chemical molecule in CDK library
     * @throws CompoundSearchException 
     */
    @Override
    public AtomContainer getAtomContainer() throws CompoundSearchException {
	return this.compound.getAtomContainer();
    }
}
