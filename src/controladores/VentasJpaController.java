/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import controladores.exceptions.IllegalOrphanException;
import controladores.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Cliente;
import entidades.Estado;
import entidades.Vendedor;
import entidades.Detalleventas;
import entidades.Ventas;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Ronald
 */
public class VentasJpaController implements Serializable {

    public VentasJpaController() {
        this.emf = Persistence.createEntityManagerFactory("proyectaVentasPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ventas ventas) {
        if (ventas.getDetalleventasList() == null) {
            ventas.setDetalleventasList(new ArrayList<Detalleventas>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cliente clienteCedula = ventas.getClienteCedula();
            if (clienteCedula != null) {
                clienteCedula = em.getReference(clienteCedula.getClass(), clienteCedula.getCedula());
                ventas.setClienteCedula(clienteCedula);
            }
            Estado estadoCodigo = ventas.getEstadoCodigo();
            if (estadoCodigo != null) {
                estadoCodigo = em.getReference(estadoCodigo.getClass(), estadoCodigo.getCodigo());
                ventas.setEstadoCodigo(estadoCodigo);
            }
            Vendedor vendedorCedula = ventas.getVendedorCedula();
            if (vendedorCedula != null) {
                vendedorCedula = em.getReference(vendedorCedula.getClass(), vendedorCedula.getCedula());
                ventas.setVendedorCedula(vendedorCedula);
            }
            List<Detalleventas> attachedDetalleventasList = new ArrayList<Detalleventas>();
            for (Detalleventas detalleventasListDetalleventasToAttach : ventas.getDetalleventasList()) {
                detalleventasListDetalleventasToAttach = em.getReference(detalleventasListDetalleventasToAttach.getClass(), detalleventasListDetalleventasToAttach.getCodigo());
                attachedDetalleventasList.add(detalleventasListDetalleventasToAttach);
            }
            ventas.setDetalleventasList(attachedDetalleventasList);
            em.persist(ventas);
            if (clienteCedula != null) {
                clienteCedula.getVentasList().add(ventas);
                clienteCedula = em.merge(clienteCedula);
            }
            if (estadoCodigo != null) {
                estadoCodigo.getVentasList().add(ventas);
                estadoCodigo = em.merge(estadoCodigo);
            }
            if (vendedorCedula != null) {
                vendedorCedula.getVentasList().add(ventas);
                vendedorCedula = em.merge(vendedorCedula);
            }
            for (Detalleventas detalleventasListDetalleventas : ventas.getDetalleventasList()) {
                Ventas oldVentasCodigoOfDetalleventasListDetalleventas = detalleventasListDetalleventas.getVentasCodigo();
                detalleventasListDetalleventas.setVentasCodigo(ventas);
                detalleventasListDetalleventas = em.merge(detalleventasListDetalleventas);
                if (oldVentasCodigoOfDetalleventasListDetalleventas != null) {
                    oldVentasCodigoOfDetalleventasListDetalleventas.getDetalleventasList().remove(detalleventasListDetalleventas);
                    oldVentasCodigoOfDetalleventasListDetalleventas = em.merge(oldVentasCodigoOfDetalleventasListDetalleventas);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Ventas ventas) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ventas persistentVentas = em.find(Ventas.class, ventas.getCodigo());
            Cliente clienteCedulaOld = persistentVentas.getClienteCedula();
            Cliente clienteCedulaNew = ventas.getClienteCedula();
            Estado estadoCodigoOld = persistentVentas.getEstadoCodigo();
            Estado estadoCodigoNew = ventas.getEstadoCodigo();
            Vendedor vendedorCedulaOld = persistentVentas.getVendedorCedula();
            Vendedor vendedorCedulaNew = ventas.getVendedorCedula();
            List<Detalleventas> detalleventasListOld = persistentVentas.getDetalleventasList();
            List<Detalleventas> detalleventasListNew = ventas.getDetalleventasList();
            List<String> illegalOrphanMessages = null;
            for (Detalleventas detalleventasListOldDetalleventas : detalleventasListOld) {
                if (!detalleventasListNew.contains(detalleventasListOldDetalleventas)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Detalleventas " + detalleventasListOldDetalleventas + " since its ventasCodigo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (clienteCedulaNew != null) {
                clienteCedulaNew = em.getReference(clienteCedulaNew.getClass(), clienteCedulaNew.getCedula());
                ventas.setClienteCedula(clienteCedulaNew);
            }
            if (estadoCodigoNew != null) {
                estadoCodigoNew = em.getReference(estadoCodigoNew.getClass(), estadoCodigoNew.getCodigo());
                ventas.setEstadoCodigo(estadoCodigoNew);
            }
            if (vendedorCedulaNew != null) {
                vendedorCedulaNew = em.getReference(vendedorCedulaNew.getClass(), vendedorCedulaNew.getCedula());
                ventas.setVendedorCedula(vendedorCedulaNew);
            }
            List<Detalleventas> attachedDetalleventasListNew = new ArrayList<Detalleventas>();
            for (Detalleventas detalleventasListNewDetalleventasToAttach : detalleventasListNew) {
                detalleventasListNewDetalleventasToAttach = em.getReference(detalleventasListNewDetalleventasToAttach.getClass(), detalleventasListNewDetalleventasToAttach.getCodigo());
                attachedDetalleventasListNew.add(detalleventasListNewDetalleventasToAttach);
            }
            detalleventasListNew = attachedDetalleventasListNew;
            ventas.setDetalleventasList(detalleventasListNew);
            ventas = em.merge(ventas);
            if (clienteCedulaOld != null && !clienteCedulaOld.equals(clienteCedulaNew)) {
                clienteCedulaOld.getVentasList().remove(ventas);
                clienteCedulaOld = em.merge(clienteCedulaOld);
            }
            if (clienteCedulaNew != null && !clienteCedulaNew.equals(clienteCedulaOld)) {
                clienteCedulaNew.getVentasList().add(ventas);
                clienteCedulaNew = em.merge(clienteCedulaNew);
            }
            if (estadoCodigoOld != null && !estadoCodigoOld.equals(estadoCodigoNew)) {
                estadoCodigoOld.getVentasList().remove(ventas);
                estadoCodigoOld = em.merge(estadoCodigoOld);
            }
            if (estadoCodigoNew != null && !estadoCodigoNew.equals(estadoCodigoOld)) {
                estadoCodigoNew.getVentasList().add(ventas);
                estadoCodigoNew = em.merge(estadoCodigoNew);
            }
            if (vendedorCedulaOld != null && !vendedorCedulaOld.equals(vendedorCedulaNew)) {
                vendedorCedulaOld.getVentasList().remove(ventas);
                vendedorCedulaOld = em.merge(vendedorCedulaOld);
            }
            if (vendedorCedulaNew != null && !vendedorCedulaNew.equals(vendedorCedulaOld)) {
                vendedorCedulaNew.getVentasList().add(ventas);
                vendedorCedulaNew = em.merge(vendedorCedulaNew);
            }
            for (Detalleventas detalleventasListNewDetalleventas : detalleventasListNew) {
                if (!detalleventasListOld.contains(detalleventasListNewDetalleventas)) {
                    Ventas oldVentasCodigoOfDetalleventasListNewDetalleventas = detalleventasListNewDetalleventas.getVentasCodigo();
                    detalleventasListNewDetalleventas.setVentasCodigo(ventas);
                    detalleventasListNewDetalleventas = em.merge(detalleventasListNewDetalleventas);
                    if (oldVentasCodigoOfDetalleventasListNewDetalleventas != null && !oldVentasCodigoOfDetalleventasListNewDetalleventas.equals(ventas)) {
                        oldVentasCodigoOfDetalleventasListNewDetalleventas.getDetalleventasList().remove(detalleventasListNewDetalleventas);
                        oldVentasCodigoOfDetalleventasListNewDetalleventas = em.merge(oldVentasCodigoOfDetalleventasListNewDetalleventas);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = ventas.getCodigo();
                if (findVentas(id) == null) {
                    throw new NonexistentEntityException("The ventas with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Ventas ventas;
            try {
                ventas = em.getReference(Ventas.class, id);
                ventas.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ventas with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Detalleventas> detalleventasListOrphanCheck = ventas.getDetalleventasList();
            for (Detalleventas detalleventasListOrphanCheckDetalleventas : detalleventasListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Ventas (" + ventas + ") cannot be destroyed since the Detalleventas " + detalleventasListOrphanCheckDetalleventas + " in its detalleventasList field has a non-nullable ventasCodigo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Cliente clienteCedula = ventas.getClienteCedula();
            if (clienteCedula != null) {
                clienteCedula.getVentasList().remove(ventas);
                clienteCedula = em.merge(clienteCedula);
            }
            Estado estadoCodigo = ventas.getEstadoCodigo();
            if (estadoCodigo != null) {
                estadoCodigo.getVentasList().remove(ventas);
                estadoCodigo = em.merge(estadoCodigo);
            }
            Vendedor vendedorCedula = ventas.getVendedorCedula();
            if (vendedorCedula != null) {
                vendedorCedula.getVentasList().remove(ventas);
                vendedorCedula = em.merge(vendedorCedula);
            }
            em.remove(ventas);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Ventas> findVentasEntities() {
        return findVentasEntities(true, -1, -1);
    }

    public List<Ventas> findVentasEntities(int maxResults, int firstResult) {
        return findVentasEntities(false, maxResults, firstResult);
    }

    private List<Ventas> findVentasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ventas.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Ventas findVentas(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ventas.class, id);
        } finally {
            em.close();
        }
    }

    public int getVentasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ventas> rt = cq.from(Ventas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
