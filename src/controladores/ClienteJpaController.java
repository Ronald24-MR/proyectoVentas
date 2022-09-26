/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladores;

import controladores.exceptions.IllegalOrphanException;
import controladores.exceptions.NonexistentEntityException;
import controladores.exceptions.PreexistingEntityException;
import entidades.Cliente;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Estado;
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
public class ClienteJpaController implements Serializable {

    public ClienteJpaController() {
        this.emf = Persistence.createEntityManagerFactory("proyectaVentasPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cliente cliente) throws PreexistingEntityException, Exception {
        if (cliente.getVentasList() == null) {
            cliente.setVentasList(new ArrayList<Ventas>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Estado estadoCodigo = cliente.getEstadoCodigo();
            if (estadoCodigo != null) {
                estadoCodigo = em.getReference(estadoCodigo.getClass(), estadoCodigo.getCodigo());
                cliente.setEstadoCodigo(estadoCodigo);
            }
            List<Ventas> attachedVentasList = new ArrayList<Ventas>();
            for (Ventas ventasListVentasToAttach : cliente.getVentasList()) {
                ventasListVentasToAttach = em.getReference(ventasListVentasToAttach.getClass(), ventasListVentasToAttach.getCodigo());
                attachedVentasList.add(ventasListVentasToAttach);
            }
            cliente.setVentasList(attachedVentasList);
            em.persist(cliente);
            if (estadoCodigo != null) {
                estadoCodigo.getClienteList().add(cliente);
                estadoCodigo = em.merge(estadoCodigo);
            }
            for (Ventas ventasListVentas : cliente.getVentasList()) {
                Cliente oldClienteCedulaOfVentasListVentas = ventasListVentas.getClienteCedula();
                ventasListVentas.setClienteCedula(cliente);
                ventasListVentas = em.merge(ventasListVentas);
                if (oldClienteCedulaOfVentasListVentas != null) {
                    oldClienteCedulaOfVentasListVentas.getVentasList().remove(ventasListVentas);
                    oldClienteCedulaOfVentasListVentas = em.merge(oldClienteCedulaOfVentasListVentas);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCliente(cliente.getCedula()) != null) {
                throw new PreexistingEntityException("Cliente " + cliente + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cliente cliente) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cliente persistentCliente = em.find(Cliente.class, cliente.getCedula());
            Estado estadoCodigoOld = persistentCliente.getEstadoCodigo();
            Estado estadoCodigoNew = cliente.getEstadoCodigo();
            List<Ventas> ventasListOld = persistentCliente.getVentasList();
            List<Ventas> ventasListNew = cliente.getVentasList();
            List<String> illegalOrphanMessages = null;
            for (Ventas ventasListOldVentas : ventasListOld) {
                if (!ventasListNew.contains(ventasListOldVentas)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ventas " + ventasListOldVentas + " since its clienteCedula field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (estadoCodigoNew != null) {
                estadoCodigoNew = em.getReference(estadoCodigoNew.getClass(), estadoCodigoNew.getCodigo());
                cliente.setEstadoCodigo(estadoCodigoNew);
            }
            List<Ventas> attachedVentasListNew = new ArrayList<Ventas>();
            for (Ventas ventasListNewVentasToAttach : ventasListNew) {
                ventasListNewVentasToAttach = em.getReference(ventasListNewVentasToAttach.getClass(), ventasListNewVentasToAttach.getCodigo());
                attachedVentasListNew.add(ventasListNewVentasToAttach);
            }
            ventasListNew = attachedVentasListNew;
            cliente.setVentasList(ventasListNew);
            cliente = em.merge(cliente);
            if (estadoCodigoOld != null && !estadoCodigoOld.equals(estadoCodigoNew)) {
                estadoCodigoOld.getClienteList().remove(cliente);
                estadoCodigoOld = em.merge(estadoCodigoOld);
            }
            if (estadoCodigoNew != null && !estadoCodigoNew.equals(estadoCodigoOld)) {
                estadoCodigoNew.getClienteList().add(cliente);
                estadoCodigoNew = em.merge(estadoCodigoNew);
            }
            for (Ventas ventasListNewVentas : ventasListNew) {
                if (!ventasListOld.contains(ventasListNewVentas)) {
                    Cliente oldClienteCedulaOfVentasListNewVentas = ventasListNewVentas.getClienteCedula();
                    ventasListNewVentas.setClienteCedula(cliente);
                    ventasListNewVentas = em.merge(ventasListNewVentas);
                    if (oldClienteCedulaOfVentasListNewVentas != null && !oldClienteCedulaOfVentasListNewVentas.equals(cliente)) {
                        oldClienteCedulaOfVentasListNewVentas.getVentasList().remove(ventasListNewVentas);
                        oldClienteCedulaOfVentasListNewVentas = em.merge(oldClienteCedulaOfVentasListNewVentas);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cliente.getCedula();
                if (findCliente(id) == null) {
                    throw new NonexistentEntityException("The cliente with id " + id + " no longer exists.");
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
            Cliente cliente;
            try {
                cliente = em.getReference(Cliente.class, id);
                cliente.getCedula();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cliente with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Ventas> ventasListOrphanCheck = cliente.getVentasList();
            for (Ventas ventasListOrphanCheckVentas : ventasListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cliente (" + cliente + ") cannot be destroyed since the Ventas " + ventasListOrphanCheckVentas + " in its ventasList field has a non-nullable clienteCedula field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Estado estadoCodigo = cliente.getEstadoCodigo();
            if (estadoCodigo != null) {
                estadoCodigo.getClienteList().remove(cliente);
                estadoCodigo = em.merge(estadoCodigo);
            }
            em.remove(cliente);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cliente> findClienteEntities() {
        return findClienteEntities(true, -1, -1);
    }

    public List<Cliente> findClienteEntities(int maxResults, int firstResult) {
        return findClienteEntities(false, maxResults, firstResult);
    }

    private List<Cliente> findClienteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cliente.class));
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

    public Cliente findCliente(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cliente.class, id);
        } finally {
            em.close();
        }
    }

    public int getClienteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cliente> rt = cq.from(Cliente.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
