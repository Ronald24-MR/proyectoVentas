/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import controladores.exceptions.IllegalOrphanException;
import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Estado;
import entidades.Vendedor;
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
public class VendedorJpaController implements Serializable {

    public VendedorJpaController() {
        this.emf = Persistence.createEntityManagerFactory("proyectaVentasPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Vendedor vendedor) throws PreexistingEntityException, Exception {
        if (vendedor.getVentasList() == null) {
            vendedor.setVentasList(new ArrayList<Ventas>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Estado estadoCodigo = vendedor.getEstadoCodigo();
            if (estadoCodigo != null) {
                estadoCodigo = em.getReference(estadoCodigo.getClass(), estadoCodigo.getCodigo());
                vendedor.setEstadoCodigo(estadoCodigo);
            }
            List<Ventas> attachedVentasList = new ArrayList<Ventas>();
            for (Ventas ventasListVentasToAttach : vendedor.getVentasList()) {
                ventasListVentasToAttach = em.getReference(ventasListVentasToAttach.getClass(), ventasListVentasToAttach.getCodigo());
                attachedVentasList.add(ventasListVentasToAttach);
            }
            vendedor.setVentasList(attachedVentasList);
            em.persist(vendedor);
            if (estadoCodigo != null) {
                estadoCodigo.getVendedorList().add(vendedor);
                estadoCodigo = em.merge(estadoCodigo);
            }
            for (Ventas ventasListVentas : vendedor.getVentasList()) {
                Vendedor oldVendedorCedulaOfVentasListVentas = ventasListVentas.getVendedorCedula();
                ventasListVentas.setVendedorCedula(vendedor);
                ventasListVentas = em.merge(ventasListVentas);
                if (oldVendedorCedulaOfVentasListVentas != null) {
                    oldVendedorCedulaOfVentasListVentas.getVentasList().remove(ventasListVentas);
                    oldVendedorCedulaOfVentasListVentas = em.merge(oldVendedorCedulaOfVentasListVentas);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findVendedor(vendedor.getCedula()) != null) {
                throw new PreexistingEntityException("Vendedor " + vendedor + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Vendedor vendedor) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Vendedor persistentVendedor = em.find(Vendedor.class, vendedor.getCedula());
            Estado estadoCodigoOld = persistentVendedor.getEstadoCodigo();
            Estado estadoCodigoNew = vendedor.getEstadoCodigo();
            List<Ventas> ventasListOld = persistentVendedor.getVentasList();
            List<Ventas> ventasListNew = vendedor.getVentasList();
            List<String> illegalOrphanMessages = null;
            for (Ventas ventasListOldVentas : ventasListOld) {
                if (!ventasListNew.contains(ventasListOldVentas)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ventas " + ventasListOldVentas + " since its vendedorCedula field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (estadoCodigoNew != null) {
                estadoCodigoNew = em.getReference(estadoCodigoNew.getClass(), estadoCodigoNew.getCodigo());
                vendedor.setEstadoCodigo(estadoCodigoNew);
            }
            List<Ventas> attachedVentasListNew = new ArrayList<Ventas>();
            for (Ventas ventasListNewVentasToAttach : ventasListNew) {
                ventasListNewVentasToAttach = em.getReference(ventasListNewVentasToAttach.getClass(), ventasListNewVentasToAttach.getCodigo());
                attachedVentasListNew.add(ventasListNewVentasToAttach);
            }
            ventasListNew = attachedVentasListNew;
            vendedor.setVentasList(ventasListNew);
            vendedor = em.merge(vendedor);
            if (estadoCodigoOld != null && !estadoCodigoOld.equals(estadoCodigoNew)) {
                estadoCodigoOld.getVendedorList().remove(vendedor);
                estadoCodigoOld = em.merge(estadoCodigoOld);
            }
            if (estadoCodigoNew != null && !estadoCodigoNew.equals(estadoCodigoOld)) {
                estadoCodigoNew.getVendedorList().add(vendedor);
                estadoCodigoNew = em.merge(estadoCodigoNew);
            }
            for (Ventas ventasListNewVentas : ventasListNew) {
                if (!ventasListOld.contains(ventasListNewVentas)) {
                    Vendedor oldVendedorCedulaOfVentasListNewVentas = ventasListNewVentas.getVendedorCedula();
                    ventasListNewVentas.setVendedorCedula(vendedor);
                    ventasListNewVentas = em.merge(ventasListNewVentas);
                    if (oldVendedorCedulaOfVentasListNewVentas != null && !oldVendedorCedulaOfVentasListNewVentas.equals(vendedor)) {
                        oldVendedorCedulaOfVentasListNewVentas.getVentasList().remove(ventasListNewVentas);
                        oldVendedorCedulaOfVentasListNewVentas = em.merge(oldVendedorCedulaOfVentasListNewVentas);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = vendedor.getCedula();
                if (findVendedor(id) == null) {
                    throw new NonexistentEntityException("The vendedor with id " + id + " no longer exists.");
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
            Vendedor vendedor;
            try {
                vendedor = em.getReference(Vendedor.class, id);
                vendedor.getCedula();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The vendedor with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Ventas> ventasListOrphanCheck = vendedor.getVentasList();
            for (Ventas ventasListOrphanCheckVentas : ventasListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Vendedor (" + vendedor + ") cannot be destroyed since the Ventas " + ventasListOrphanCheckVentas + " in its ventasList field has a non-nullable vendedorCedula field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Estado estadoCodigo = vendedor.getEstadoCodigo();
            if (estadoCodigo != null) {
                estadoCodigo.getVendedorList().remove(vendedor);
                estadoCodigo = em.merge(estadoCodigo);
            }
            em.remove(vendedor);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Vendedor> findVendedorEntities() {
        return findVendedorEntities(true, -1, -1);
    }

    public List<Vendedor> findVendedorEntities(int maxResults, int firstResult) {
        return findVendedorEntities(false, maxResults, firstResult);
    }

    private List<Vendedor> findVendedorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Vendedor.class));
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

    public Vendedor findVendedor(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Vendedor.class, id);
        } finally {
            em.close();
        }
    }

    public int getVendedorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Vendedor> rt = cq.from(Vendedor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
