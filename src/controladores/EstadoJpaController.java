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
import entidades.Cliente;
import entidades.Estado;
import java.util.ArrayList;
import java.util.List;
import entidades.Vendedor;
import entidades.Ventas;
import entidades.Producto;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Ronald
 */
public class EstadoJpaController implements Serializable {

    public EstadoJpaController() {
        this.emf = Persistence.createEntityManagerFactory("proyectaVentasPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Estado estado) throws PreexistingEntityException, Exception {
        if (estado.getClienteList() == null) {
            estado.setClienteList(new ArrayList<Cliente>());
        }
        if (estado.getVendedorList() == null) {
            estado.setVendedorList(new ArrayList<Vendedor>());
        }
        if (estado.getVentasList() == null) {
            estado.setVentasList(new ArrayList<Ventas>());
        }
        if (estado.getProductoList() == null) {
            estado.setProductoList(new ArrayList<Producto>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Cliente> attachedClienteList = new ArrayList<Cliente>();
            for (Cliente clienteListClienteToAttach : estado.getClienteList()) {
                clienteListClienteToAttach = em.getReference(clienteListClienteToAttach.getClass(), clienteListClienteToAttach.getCedula());
                attachedClienteList.add(clienteListClienteToAttach);
            }
            estado.setClienteList(attachedClienteList);
            List<Vendedor> attachedVendedorList = new ArrayList<Vendedor>();
            for (Vendedor vendedorListVendedorToAttach : estado.getVendedorList()) {
                vendedorListVendedorToAttach = em.getReference(vendedorListVendedorToAttach.getClass(), vendedorListVendedorToAttach.getCedula());
                attachedVendedorList.add(vendedorListVendedorToAttach);
            }
            estado.setVendedorList(attachedVendedorList);
            List<Ventas> attachedVentasList = new ArrayList<Ventas>();
            for (Ventas ventasListVentasToAttach : estado.getVentasList()) {
                ventasListVentasToAttach = em.getReference(ventasListVentasToAttach.getClass(), ventasListVentasToAttach.getCodigo());
                attachedVentasList.add(ventasListVentasToAttach);
            }
            estado.setVentasList(attachedVentasList);
            List<Producto> attachedProductoList = new ArrayList<Producto>();
            for (Producto productoListProductoToAttach : estado.getProductoList()) {
                productoListProductoToAttach = em.getReference(productoListProductoToAttach.getClass(), productoListProductoToAttach.getCodigo());
                attachedProductoList.add(productoListProductoToAttach);
            }
            estado.setProductoList(attachedProductoList);
            em.persist(estado);
            for (Cliente clienteListCliente : estado.getClienteList()) {
                Estado oldEstadoCodigoOfClienteListCliente = clienteListCliente.getEstadoCodigo();
                clienteListCliente.setEstadoCodigo(estado);
                clienteListCliente = em.merge(clienteListCliente);
                if (oldEstadoCodigoOfClienteListCliente != null) {
                    oldEstadoCodigoOfClienteListCliente.getClienteList().remove(clienteListCliente);
                    oldEstadoCodigoOfClienteListCliente = em.merge(oldEstadoCodigoOfClienteListCliente);
                }
            }
            for (Vendedor vendedorListVendedor : estado.getVendedorList()) {
                Estado oldEstadoCodigoOfVendedorListVendedor = vendedorListVendedor.getEstadoCodigo();
                vendedorListVendedor.setEstadoCodigo(estado);
                vendedorListVendedor = em.merge(vendedorListVendedor);
                if (oldEstadoCodigoOfVendedorListVendedor != null) {
                    oldEstadoCodigoOfVendedorListVendedor.getVendedorList().remove(vendedorListVendedor);
                    oldEstadoCodigoOfVendedorListVendedor = em.merge(oldEstadoCodigoOfVendedorListVendedor);
                }
            }
            for (Ventas ventasListVentas : estado.getVentasList()) {
                Estado oldEstadoCodigoOfVentasListVentas = ventasListVentas.getEstadoCodigo();
                ventasListVentas.setEstadoCodigo(estado);
                ventasListVentas = em.merge(ventasListVentas);
                if (oldEstadoCodigoOfVentasListVentas != null) {
                    oldEstadoCodigoOfVentasListVentas.getVentasList().remove(ventasListVentas);
                    oldEstadoCodigoOfVentasListVentas = em.merge(oldEstadoCodigoOfVentasListVentas);
                }
            }
            for (Producto productoListProducto : estado.getProductoList()) {
                Estado oldEstadoCodigoOfProductoListProducto = productoListProducto.getEstadoCodigo();
                productoListProducto.setEstadoCodigo(estado);
                productoListProducto = em.merge(productoListProducto);
                if (oldEstadoCodigoOfProductoListProducto != null) {
                    oldEstadoCodigoOfProductoListProducto.getProductoList().remove(productoListProducto);
                    oldEstadoCodigoOfProductoListProducto = em.merge(oldEstadoCodigoOfProductoListProducto);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findEstado(estado.getCodigo()) != null) {
                throw new PreexistingEntityException("Estado " + estado + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Estado estado) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Estado persistentEstado = em.find(Estado.class, estado.getCodigo());
            List<Cliente> clienteListOld = persistentEstado.getClienteList();
            List<Cliente> clienteListNew = estado.getClienteList();
            List<Vendedor> vendedorListOld = persistentEstado.getVendedorList();
            List<Vendedor> vendedorListNew = estado.getVendedorList();
            List<Ventas> ventasListOld = persistentEstado.getVentasList();
            List<Ventas> ventasListNew = estado.getVentasList();
            List<Producto> productoListOld = persistentEstado.getProductoList();
            List<Producto> productoListNew = estado.getProductoList();
            List<String> illegalOrphanMessages = null;
            for (Cliente clienteListOldCliente : clienteListOld) {
                if (!clienteListNew.contains(clienteListOldCliente)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Cliente " + clienteListOldCliente + " since its estadoCodigo field is not nullable.");
                }
            }
            for (Vendedor vendedorListOldVendedor : vendedorListOld) {
                if (!vendedorListNew.contains(vendedorListOldVendedor)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Vendedor " + vendedorListOldVendedor + " since its estadoCodigo field is not nullable.");
                }
            }
            for (Ventas ventasListOldVentas : ventasListOld) {
                if (!ventasListNew.contains(ventasListOldVentas)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ventas " + ventasListOldVentas + " since its estadoCodigo field is not nullable.");
                }
            }
            for (Producto productoListOldProducto : productoListOld) {
                if (!productoListNew.contains(productoListOldProducto)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Producto " + productoListOldProducto + " since its estadoCodigo field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Cliente> attachedClienteListNew = new ArrayList<Cliente>();
            for (Cliente clienteListNewClienteToAttach : clienteListNew) {
                clienteListNewClienteToAttach = em.getReference(clienteListNewClienteToAttach.getClass(), clienteListNewClienteToAttach.getCedula());
                attachedClienteListNew.add(clienteListNewClienteToAttach);
            }
            clienteListNew = attachedClienteListNew;
            estado.setClienteList(clienteListNew);
            List<Vendedor> attachedVendedorListNew = new ArrayList<Vendedor>();
            for (Vendedor vendedorListNewVendedorToAttach : vendedorListNew) {
                vendedorListNewVendedorToAttach = em.getReference(vendedorListNewVendedorToAttach.getClass(), vendedorListNewVendedorToAttach.getCedula());
                attachedVendedorListNew.add(vendedorListNewVendedorToAttach);
            }
            vendedorListNew = attachedVendedorListNew;
            estado.setVendedorList(vendedorListNew);
            List<Ventas> attachedVentasListNew = new ArrayList<Ventas>();
            for (Ventas ventasListNewVentasToAttach : ventasListNew) {
                ventasListNewVentasToAttach = em.getReference(ventasListNewVentasToAttach.getClass(), ventasListNewVentasToAttach.getCodigo());
                attachedVentasListNew.add(ventasListNewVentasToAttach);
            }
            ventasListNew = attachedVentasListNew;
            estado.setVentasList(ventasListNew);
            List<Producto> attachedProductoListNew = new ArrayList<Producto>();
            for (Producto productoListNewProductoToAttach : productoListNew) {
                productoListNewProductoToAttach = em.getReference(productoListNewProductoToAttach.getClass(), productoListNewProductoToAttach.getCodigo());
                attachedProductoListNew.add(productoListNewProductoToAttach);
            }
            productoListNew = attachedProductoListNew;
            estado.setProductoList(productoListNew);
            estado = em.merge(estado);
            for (Cliente clienteListNewCliente : clienteListNew) {
                if (!clienteListOld.contains(clienteListNewCliente)) {
                    Estado oldEstadoCodigoOfClienteListNewCliente = clienteListNewCliente.getEstadoCodigo();
                    clienteListNewCliente.setEstadoCodigo(estado);
                    clienteListNewCliente = em.merge(clienteListNewCliente);
                    if (oldEstadoCodigoOfClienteListNewCliente != null && !oldEstadoCodigoOfClienteListNewCliente.equals(estado)) {
                        oldEstadoCodigoOfClienteListNewCliente.getClienteList().remove(clienteListNewCliente);
                        oldEstadoCodigoOfClienteListNewCliente = em.merge(oldEstadoCodigoOfClienteListNewCliente);
                    }
                }
            }
            for (Vendedor vendedorListNewVendedor : vendedorListNew) {
                if (!vendedorListOld.contains(vendedorListNewVendedor)) {
                    Estado oldEstadoCodigoOfVendedorListNewVendedor = vendedorListNewVendedor.getEstadoCodigo();
                    vendedorListNewVendedor.setEstadoCodigo(estado);
                    vendedorListNewVendedor = em.merge(vendedorListNewVendedor);
                    if (oldEstadoCodigoOfVendedorListNewVendedor != null && !oldEstadoCodigoOfVendedorListNewVendedor.equals(estado)) {
                        oldEstadoCodigoOfVendedorListNewVendedor.getVendedorList().remove(vendedorListNewVendedor);
                        oldEstadoCodigoOfVendedorListNewVendedor = em.merge(oldEstadoCodigoOfVendedorListNewVendedor);
                    }
                }
            }
            for (Ventas ventasListNewVentas : ventasListNew) {
                if (!ventasListOld.contains(ventasListNewVentas)) {
                    Estado oldEstadoCodigoOfVentasListNewVentas = ventasListNewVentas.getEstadoCodigo();
                    ventasListNewVentas.setEstadoCodigo(estado);
                    ventasListNewVentas = em.merge(ventasListNewVentas);
                    if (oldEstadoCodigoOfVentasListNewVentas != null && !oldEstadoCodigoOfVentasListNewVentas.equals(estado)) {
                        oldEstadoCodigoOfVentasListNewVentas.getVentasList().remove(ventasListNewVentas);
                        oldEstadoCodigoOfVentasListNewVentas = em.merge(oldEstadoCodigoOfVentasListNewVentas);
                    }
                }
            }
            for (Producto productoListNewProducto : productoListNew) {
                if (!productoListOld.contains(productoListNewProducto)) {
                    Estado oldEstadoCodigoOfProductoListNewProducto = productoListNewProducto.getEstadoCodigo();
                    productoListNewProducto.setEstadoCodigo(estado);
                    productoListNewProducto = em.merge(productoListNewProducto);
                    if (oldEstadoCodigoOfProductoListNewProducto != null && !oldEstadoCodigoOfProductoListNewProducto.equals(estado)) {
                        oldEstadoCodigoOfProductoListNewProducto.getProductoList().remove(productoListNewProducto);
                        oldEstadoCodigoOfProductoListNewProducto = em.merge(oldEstadoCodigoOfProductoListNewProducto);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = estado.getCodigo();
                if (findEstado(id) == null) {
                    throw new NonexistentEntityException("The estado with id " + id + " no longer exists.");
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
            Estado estado;
            try {
                estado = em.getReference(Estado.class, id);
                estado.getCodigo();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The estado with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Cliente> clienteListOrphanCheck = estado.getClienteList();
            for (Cliente clienteListOrphanCheckCliente : clienteListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Estado (" + estado + ") cannot be destroyed since the Cliente " + clienteListOrphanCheckCliente + " in its clienteList field has a non-nullable estadoCodigo field.");
            }
            List<Vendedor> vendedorListOrphanCheck = estado.getVendedorList();
            for (Vendedor vendedorListOrphanCheckVendedor : vendedorListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Estado (" + estado + ") cannot be destroyed since the Vendedor " + vendedorListOrphanCheckVendedor + " in its vendedorList field has a non-nullable estadoCodigo field.");
            }
            List<Ventas> ventasListOrphanCheck = estado.getVentasList();
            for (Ventas ventasListOrphanCheckVentas : ventasListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Estado (" + estado + ") cannot be destroyed since the Ventas " + ventasListOrphanCheckVentas + " in its ventasList field has a non-nullable estadoCodigo field.");
            }
            List<Producto> productoListOrphanCheck = estado.getProductoList();
            for (Producto productoListOrphanCheckProducto : productoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Estado (" + estado + ") cannot be destroyed since the Producto " + productoListOrphanCheckProducto + " in its productoList field has a non-nullable estadoCodigo field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(estado);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Estado> findEstadoEntities() {
        return findEstadoEntities(true, -1, -1);
    }

    public List<Estado> findEstadoEntities(int maxResults, int firstResult) {
        return findEstadoEntities(false, maxResults, firstResult);
    }

    private List<Estado> findEstadoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Estado.class));
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

    public Estado findEstado(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Estado.class, id);
        } finally {
            em.close();
        }
    }

    public int getEstadoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Estado> rt = cq.from(Estado.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
