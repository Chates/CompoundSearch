/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import org.openscience.cdk.AtomContainer;

/**
 *
 * @author Chates
 */
@Entity
@XmlRootElement
@Table(name = "substructure_fingerprint")
@Access(AccessType.PROPERTY)
public class SubstructureFingerprint implements ICompound {

    private Long id;
    private BitSet fingerprint;
    private Compound compound;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    @Override
    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    @Column(name = "fingerprint")
    public BitSet getFingerprint() {
	return fingerprint;
    }

    public void setFingerprint(BitSet fingerprint) {
	this.fingerprint = fingerprint;
    }

    @OneToOne
    @JoinColumn(name = "compound_id", unique = true)
    public Compound getCompound() {
	return compound;
    }

    public void setCompound(Compound compound) {
	this.compound = compound;
    }

    @Override
    public AtomContainer getAtomContainer() throws CompoundSearchException {
	return this.compound.getAtomContainer();
    }
}
