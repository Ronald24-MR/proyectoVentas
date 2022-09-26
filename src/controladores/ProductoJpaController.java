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
import entidades.Estado;
import entidades.Detalleventas;
import entidades.Producto;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Ronald
 */
public class ProductoJpaController implements Serializable {

    public ProductoJpaController() {
        this.emf = Persistence.createEntityManagerFactory("proyectaVentasPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Producto producto) {
        if (producto.getDetalleventasList() == null) {
            producto.setDetalleventasList(new ArrayList<Detalleventas>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Estado estadoCodigo = producto.getEstadoCodigo();
            if (estadoCodigo != null) {
                estadoCodigo = em.getReference(estadoCodigo.getClass(), estadoCodigo.getCodigo());
                producto.setEstadoCodigo(estadoCodigo);
            }
            List<Detalleventas> attachedDetalleventasList = new ArrayList<Detalleventas>();
            for (Detalleventas detalleventasListDetalleventasToAttach : producto.getDetalleventasList()) {
                detalleventasListDetalleventasToAttach = em.getReference(detalleventasListDetalleventasToAttach.getClass(), detalleventasListDetalleventasToAttach.getCodigo());
                attachedDetalleventasList.add(detalleventasListDetalleventasToAttach);
            }
            producto.setDetalleventasList(attachedDetalleventasList);
            em.persist(producto);
            if (estadoCodigo != null) {
                estadoCodigo.getProductoList().add(producto);
                estadoCodigo = em.merge(estadoCodigo);
            }
            for (Detalleventas detalleventasListDetalleventas : producto.getDetalleventasList()) {
                Producto oldProductoCodigoOfDetalleventasListDetalleventas = detalleventasListDetalleventas.getProductoCodigo();
                detalleventasListDetalleventas.setProductoCodigo(producto);
                detalleventasListDetalleventas = em.merge(detalleventasListDetalleventas);
                if (oldProductoCodigoOfDetalleventasListDetalleventas != null) {
                    oldProductoCodigoOfDetalleventasListDetalleventas.getDetalleventasList().remove(detalleventasListDetalleventas);
                    oldProductoCodigoOfDetalleventasListDetalleventas = em.merge(oldProductoCodigoOfDetalleventasListDetalleventas);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Producto producto) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Producto persistentProducto = em.find(Producto.class, producto.getCodigo());
            Estado estadoCodigoOld = persistentProducto.getEstadoCodigo();
            Estado estadoCodigoNew = producto.getEstadoCodigo();
            List<Detalleventas> detalleventasListOld = persistentProducto.getDetalleventasList();
            List<Detalleventas> detalleventasListNew = producto.getDetalleventasList();
            List<String> illegalOrphanMessages = null;
            for (Detalleventas detalleventasListOldDetalleventas : detalleventasListOld) {
                if (!detalleventasListNew.contains(detalleventasListOldDetalleventas)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Detalleventas " + detalleventasListOldDetalleventas + " since its productoCodigo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (estadoCodigoNew != null) {
                estadoCodigoNew = em.getReference(estadoCodigoNew.getClass(), estadoCodigoNew.getCodigo());
                producto.setEstadoCodigo(estadoCodigoNew);
            }
            List<Detalleventas> attachedDetalleventasListNew = new ArrayList<Detalleventas>();
            for (Detalleventas detalleventasListNewDetalleventasToAttach : detalleventasListNew) {
                detalleventasListNewDetalleventasToAttach = em.getReference(detalleventasListNewDetalleventasToAttach.getClass(), detalleventasListNewDetalleventasToAttach.getCodigo());
                attachedDetalleventasListNew.add(detalleventasListNewDetalleventasToAttach);
            }
            detalleventasListNew = attachedDetalleventasListNew;
            producto.setDetalleventasList(detalleventasListNew);
            producto = em.merge(producto);
            if (estadoCodigoOld != null && !estadoCodigoOld.equals(estadoCodigoNew)) {
                estadoCodigoOld.getProductoList().remove(producto);
                estadoCodigoOld = em.merge(estadoCodigoOld);
            }
            if (estadoCodigoNew != null && !estadoCodigoNew.equals(estadoCodigoOld)) {
                estadoCodigoNew.getProductoList().add(producto);
                estadoCodigoNew = em.merge(estadoCodigoNew);
            }
            for (Detalleventas detalleventasListNewDetalleventas : detalleventasListNew) {
                if (!detalleventasListOld.contains(detalleventasListNewDetalleventas)) {
                    Producto oldProductoCodigoOfDetalleventasListNewDetalleventas = detalleventasListNewDetalleventas.getProductoCodigo();
                    detalleventasListNewDetalleventas.setProductoCodigo(producto);
                    detalleventasListNewDetalleventas = em.merge(detalleventasListNewDetalleventas);
                    if (oldProductoCodigoOfDetalleventasListNewDetalleventas != null && !oldProductoCodigoOfDetalleventasListNewDetalleventas.equals(producto)) {
                        oldProductoCodigoOfDetalleventasListNewDetalleventas.getDetalleventasList().remove(detalleventasListNewDetalleventas);
                        oldProductoCodigoOfDetalleventasListNewDetalleventas = em.merge(oldProductoCodigoOfDetalleventasListNewDetalleventas);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = producto.getCodigo();
                if (findProducto(id) == null) {
                    throw new NonexistentEntityException("The producto with id " + id + " no longer exists.");
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
            Producto producto;
            try {
                producto = em.getReference(Producto.class, id);
                producto.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The producto with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Detalleventas> detalleventasListOrphanCheck = producto.getDetalleventasList();
            for (Detalleventas detalleventasListOrphanCheckDetalleventas : detalleventasListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Producto (" + producto + ") cannot be destroyed since the Detalleventas " + detalleventasListOrphanCheckDetalleventas + " in its detalleventasList field has a non-nullable productoCodigo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Estado estadoCodigo = producto.getEstadoCodigo();
            if (estadoCodigo != null) {
                estadoCodigo.getProductoList().remove(producto);
                estadoCodigo = em.merge(estadoCodigo);
            }
            em.remove(producto);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Producto> findProductoEntities() {
        return findProductoEntities(true, -1, -1);
    }

    public List<Producto> findProductoEntities(int maxResults, int firstResult) {
        return findProductoEntities(false, maxResults, firstResult);
    }

    private List<Producto> findProductoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Producto.class));
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

    public Producto findProducto(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Producto.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Producto> rt = cq.from(Producto.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
