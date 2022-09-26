/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Ronald
 */
@Entity
@Table(name = "detalleventas")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Detalleventas.findAll", query = "SELECT d FROM Detalleventas d")
    , @NamedQuery(name = "Detalleventas.findByCodigo", query = "SELECT d FROM Detalleventas d WHERE d.codigo = :codigo")
    , @NamedQuery(name = "Detalleventas.findByCantidad", query = "SELECT d FROM Detalleventas d WHERE d.cantidad = :cantidad")
    , @NamedQuery(name = "Detalleventas.findByPrecioVenta", query = "SELECT d FROM Detalleventas d WHERE d.precioVenta = :precioVenta")})
public class Detalleventas implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "codigo")
    private Integer codigo;
    @Basic(optional = false)
    @Column(name = "cantidad")
    private int cantidad;
    @Basic(optional = false)
    @Column(name = "precioVenta")
    private int precioVenta;
    @JoinColumn(name = "producto_codigo", referencedColumnName = "codigo")
    @ManyToOne(optional = false)
    private Producto productoCodigo;
    @JoinColumn(name = "ventas_codigo", referencedColumnName = "codigo")
    @ManyToOne(optional = false)
    private Ventas ventasCodigo;

    public Detalleventas() {
    }

    public Detalleventas(Integer codigo) {
        this.codigo = codigo;
    }

    public Detalleventas(Integer codigo, int cantidad, int precioVenta) {
        this.codigo = codigo;
        this.cantidad = cantidad;
        this.precioVenta = precioVenta;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(int precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Producto getProductoCodigo() {
        return productoCodigo;
    }

    public void setProductoCodigo(Producto productoCodigo) {
        this.productoCodigo = productoCodigo;
    }

    public Ventas getVentasCodigo() {
        return ventasCodigo;
    }

    public void setVentasCodigo(Ventas ventasCodigo) {
        this.ventasCodigo = ventasCodigo;
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
        if (!(object instanceof Detalleventas)) {
            return false;
        }
        Detalleventas other = (Detalleventas) object;
        if ((this.codigo == null && other.codigo != null) || (this.codigo != null && !this.codigo.equals(other.codigo))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Detalleventas[ codigo=" + codigo + " ]";
    }
    
}
