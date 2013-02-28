/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.compoundsearch.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Chates
 */
@Entity
@XmlRootElement
@Table(name = "compound_descriptor")
public class CompoundDescriptor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "atom_count")
    private Integer atomCount;
    
    @OneToOne
    @JoinColumn(name = "compound_id", unique = true)
    private Compound compound;

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }

    public Integer getAtomCount() {
	return atomCount;
    }

    public void setAtomCount(Integer atomCount) {
	this.atomCount = atomCount;
    }

    public Compound getCompound() {
	return compound;
    }

    public void setCompound(Compound compound) {
	this.compound = compound;
    }
    
    
    
    
}
