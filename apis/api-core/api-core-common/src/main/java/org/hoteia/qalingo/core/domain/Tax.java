/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version 0.7.0)
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2013
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package org.hoteia.qalingo.core.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

@Entity
@Table(name="TECO_TAX", uniqueConstraints = {@UniqueConstraint(columnNames= {"CODE"})})
public class Tax extends AbstractEntity {

	/**
	 * Generated UID
	 */
	private static final long serialVersionUID = 7515272027282284761L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ID", nullable=false)
	private Long id;
	
	@Version
	@Column(name="VERSION", nullable=false, columnDefinition="int(11) default 1")
	private int version;
	
	@Column(name="NAME")
	private String name;

	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="CODE")
	private String code;
	
	@Column(name="PERCENT")
	private BigDecimal percent;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAX_TYPE_ID", insertable = true, updatable = true)
    private TaxType taxType;
    
	@OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name="TAX_ID")
	private Set<TaxCountry> taxCountries = new HashSet<TaxCountry>(); 
	
	@Column(name="MARKET_AREA_ID")
	private Long marketAreaId;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_CREATE")
	private Date dateCreate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATE_UPDATE")
	private Date dateUpdate;
	
	public Tax() {
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public BigDecimal getPercent() {
		return percent;
	}
	
	public void setPercent(BigDecimal percent) {
		this.percent = percent;
	}
	
	public TaxType getTaxType() {
        return taxType;
    }
	
	public void setTaxType(TaxType taxType) {
        this.taxType = taxType;
    }
	
	public Date getDateCreate() {
		return dateCreate;
	}

	public void setDateCreate(Date dateCreate) {
		this.dateCreate = dateCreate;
	}

	public Date getDateUpdate() {
		return dateUpdate;
	}

	public void setDateUpdate(Date dateUpdate) {
		this.dateUpdate = dateUpdate;
	}

	public Set<TaxCountry> getTaxCountries() {
		return taxCountries;
	}

	public void setTaxeCountries(Set<TaxCountry> taxCountries) {
		this.taxCountries = taxCountries;
	}
	
	public Long getMarketAreaId() {
		return marketAreaId;
	}
	
	public void setMarketAreaId(Long marketAreaId) {
		this.marketAreaId = marketAreaId;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((code == null) ? 0 : code.hashCode());
        result = prime * result + ((dateCreate == null) ? 0 : dateCreate.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Tax other = (Tax) obj;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        if (dateCreate == null) {
            if (other.dateCreate != null)
                return false;
        } else if (!dateCreate.equals(other.dateCreate))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Tax [id=" + id + ", version=" + version + ", name=" + name + ", description=" + description + ", code=" + code + ", percent=" + percent + ", marketAreaId=" + marketAreaId
                + ", dateCreate=" + dateCreate + ", dateUpdate=" + dateUpdate + "]";
    }

}