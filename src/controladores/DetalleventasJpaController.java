/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import controladores.exceptions.NonexistentEntityException;
import entidades.Detalleventas;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Producto;
import entidades.Ventas;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Ronald
 */
public class DetalleventasJpaController implements Serializable {

    public DetalleventasJpaController() {
        this.emf = emf;
    }
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("proyectaVentasPU");

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Detalleventas detalleventas) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Producto productoCodigo = detalleventas.getProductoCodigo();
            if (productoCodigo != null) {
                productoCodigo = em.getReference(productoCodigo.getClass(), productoCodigo.getCodigo());
                detalleventas.setProductoCodigo(productoCodigo);
            }
            Ventas ventasCodigo = detalleventas.getVentasCodigo();
            if (ventasCodigo != null) {
                ventasCodigo = em.getReference(ventasCodigo.getClass(), ventasCodigo.getCodigo());
                detalleventas.setVentasCodigo(ventasCodigo);
            }
            em.persist(detalleventas);
            if (productoCodigo != null) {
                productoCodigo.getDetalleventasList().add(detalleventas);
                productoCodigo = em.merge(productoCodigo);
            }
            if (ventasCodigo != null) {
                ventasCodigo.getDetalleventasList().add(detalleventas);
                ventasCodigo = em.merge(ventasCodigo);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Detalleventas detalleventas) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Detalleventas persistentDetalleventas = em.find(Detalleventas.class, detalleventas.getCodigo());
            Producto productoCodigoOld = persistentDetalleventas.getProductoCodigo();
            Producto productoCodigoNew = detalleventas.getProductoCodigo();
            Ventas ventasCodigoOld = persistentDetalleventas.getVentasCodigo();
            Ventas ventasCodigoNew = detalleventas.getVentasCodigo();
            if (productoCodigoNew != null) {
                productoCodigoNew = em.getReference(productoCodigoNew.getClass(), productoCodigoNew.getCodigo());
                detalleventas.setProductoCodigo(productoCodigoNew);
            }
            if (ventasCodigoNew != null) {
                ventasCodigoNew = em.getReference(ventasCodigoNew.getClass(), ventasCodigoNew.getCodigo());
                detalleventas.setVentasCodigo(ventasCodigoNew);
            }
            detalleventas = em.merge(detalleventas);
            if (productoCodigoOld != null && !productoCodigoOld.equals(productoCodigoNew)) {
                productoCodigoOld.getDetalleventasList().remove(detalleventas);
                productoCodigoOld = em.merge(productoCodigoOld);
            }
            if (productoCodigoNew != null && !productoCodigoNew.equals(productoCodigoOld)) {
                productoCodigoNew.getDetalleventasList().add(detalleventas);
                productoCodigoNew = em.merge(productoCodigoNew);
            }
            if (ventasCodigoOld != null && !ventasCodigoOld.equals(ventasCodigoNew)) {
                ventasCodigoOld.getDetalleventasList().remove(detalleventas);
                ventasCodigoOld = em.merge(ventasCodigoOld);
            }
            if (ventasCodigoNew != null && !ventasCodigoNew.equals(ventasCodigoOld)) {
                ventasCodigoNew.getDetalleventasList().add(detalleventas);
                ventasCodigoNew = em.merge(ventasCodigoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = detalleventas.getCodigo();
                if (findDetalleventas(id) == null) {
                    throw new NonexistentEntityException("The detalleventas with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Detalleventas detalleventas;
            try {
                detalleventas = em.getReference(Detalleventas.class, id);
                detalleventas.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detalleventas with id " + id + " no longer exists.", enfe);
            }
            Producto productoCodigo = detalleventas.getProductoCodigo();
            if (productoCodigo != null) {
                productoCodigo.getDetalleventasList().remove(detalleventas);
                productoCodigo = em.merge(productoCodigo);
            }
            Ventas ventasCodigo = detalleventas.getVentasCodigo();
            if (ventasCodigo != null) {
                ventasCodigo.getDetalleventasList().remove(detalleventas);
                ventasCodigo = em.merge(ventasCodigo);
            }
            em.remove(detalleventas);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Detalleventas> findDetalleventasEntities() {
        return findDetalleventasEntities(true, -1, -1);
    }

    public List<Detalleventas> findDetalleventasEntities(int maxResults, int firstResult) {
        return findDetalleventasEntities(false, maxResults, firstResult);
    }

    private List<Detalleventas> findDetalleventasEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Detalleventas.class));
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

    public Detalleventas findDetalleventas(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Detalleventas.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetalleventasCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Detalleventas> rt = cq.from(Detalleventas.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
