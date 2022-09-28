/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Ronald
 */
@Entity
@Table(name = "ventas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ventas.findAll", query = "SELECT v FROM Ventas v")
    , @NamedQuery(name = "Ventas.findByCodigo", query = "SELECT v FROM Ventas v WHERE v.codigo = :codigo")
    , @NamedQuery(name = "Ventas.findByNumeroVentas", query = "SELECT v FROM Ventas v WHERE v.numeroVentas = :numeroVentas")
    , @NamedQuery(name = "Ventas.findByFechaVentas", query = "SELECT v FROM Ventas v WHERE v.fechaVentas = :fechaVentas")
    , @NamedQuery(name = "Ventas.findByMontos", query = "SELECT v FROM Ventas v WHERE v.montos = :montos")})
public class Ventas implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "codigo")
    private Integer codigo;
    @Basic(optional = false)
    @Column(name = "numeroVentas")
    private String numeroVentas;
    @Basic(optional = false)
    @Column(name = "fechaVentas")
    private String fechaVentas;
    @Basic(optional = false)
    @Column(name = "montos")
    private String montos;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ventasCodigo")
    private List<Detalleventas> detalleventasList;
    @JoinColumn(name = "cliente_cedula", referencedColumnName = "cedula")
    @ManyToOne(optional = false)
    private Cliente clienteCedula;
    @JoinColumn(name = "estado_codigo", referencedColumnName = "codigo")
    @ManyToOne(optional = false)
    private Estado estadoCodigo;
    @JoinColumn(name = "vendedor_cedula", referencedColumnName = "cedula")
    @ManyToOne(optional = false)
    private Vendedor vendedorCedula;

    public Ventas() {
    }

    public Ventas(Integer codigo) {
        this.codigo = codigo;
    }

    public Ventas(Integer codigo, String numeroVentas, String fechaVentas, String montos) {
        this.codigo = codigo;
        this.numeroVentas = numeroVentas;
        this.fechaVentas = fechaVentas;
        this.montos = montos;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNumeroVentas() {
        return numeroVentas;
    }

    public void setNumeroVentas(String numeroVentas) {
        this.numeroVentas = numeroVentas;
    }

    public String getFechaVentas() {
        return fechaVentas;
    }

    public void setFechaVentas(String fechaVentas) {
        this.fechaVentas = fechaVentas;
    }

    public String getMontos() {
        return montos;
    }

    public void setMontos(String montos) {
        this.montos = montos;
    }

    @XmlTransient
    public List<Detalleventas> getDetalleventasList() {
        return detalleventasList;
    }

    public void setDetalleventasList(List<Detalleventas> detalleventasList) {
        this.detalleventasList = detalleventasList;
    }

    public Cliente getClienteCedula() {
        return clienteCedula;
    }

    public void setClienteCedula(Cliente clienteCedula) {
        this.clienteCedula = clienteCedula;
    }

    public Estado getEstadoCodigo() {
        return estadoCodigo;
    }

    public void setEstadoCodigo(Estado estadoCodigo) {
        this.estadoCodigo = estadoCodigo;
    }

    public Vendedor getVendedorCedula() {
        return vendedorCedula;
    }

    public void setVendedorCedula(Vendedor vendedorCedula) {
        this.vendedorCedula = vendedorCedula;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codigo != null ? codigo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ventas)) {
            return false;
        }
        Ventas other = (Ventas) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Ventas[ codigo=" + codigo + " ]";
    }
    
}
