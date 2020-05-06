/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moonshot.order.JPA;

import com.moonshot.order.JPA.exceptions.NonexistentEntityException;
import com.moonshot.order.JPA.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.moonshot.order.entities.Order;
import com.moonshot.order.entities.Orderdetail;
import com.moonshot.order.entities.OrderdetailPK;
import com.moonshot.order.entities.Product;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author surya
 */
public class OrderdetailJpaController implements Serializable {

    public OrderdetailJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Orderdetail orderdetail) throws PreexistingEntityException, Exception {
        if (orderdetail.getOrderdetailPK() == null) {
            orderdetail.setOrderdetailPK(new OrderdetailPK());
        }
        orderdetail.getOrderdetailPK().setProductId(orderdetail.getProduct().getId());
        orderdetail.getOrderdetailPK().setOrderId(orderdetail.getOrder1().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Order order1 = orderdetail.getOrder1();
            if (order1 != null) {
                order1 = em.getReference(order1.getClass(), order1.getId());
                orderdetail.setOrder1(order1);
            }
            Product product = orderdetail.getProduct();
            if (product != null) {
                product = em.getReference(product.getClass(), product.getId());
                orderdetail.setProduct(product);
            }
            em.persist(orderdetail);
            if (order1 != null) {
                order1.getOrderdetailList().add(orderdetail);
                order1 = em.merge(order1);
            }
            if (product != null) {
                product.getOrderdetailList().add(orderdetail);
                product = em.merge(product);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findOrderdetail(orderdetail.getOrderdetailPK()) != null) {
                throw new PreexistingEntityException("Orderdetail " + orderdetail + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Orderdetail orderdetail) throws NonexistentEntityException, Exception {
        orderdetail.getOrderdetailPK().setProductId(orderdetail.getProduct().getId());
        orderdetail.getOrderdetailPK().setOrderId(orderdetail.getOrder1().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Orderdetail persistentOrderdetail = em.find(Orderdetail.class, orderdetail.getOrderdetailPK());
            Order order1Old = persistentOrderdetail.getOrder1();
            Order order1New = orderdetail.getOrder1();
            Product productOld = persistentOrderdetail.getProduct();
            Product productNew = orderdetail.getProduct();
            if (order1New != null) {
                order1New = em.getReference(order1New.getClass(), order1New.getId());
                orderdetail.setOrder1(order1New);
            }
            if (productNew != null) {
                productNew = em.getReference(productNew.getClass(), productNew.getId());
                orderdetail.setProduct(productNew);
            }
            orderdetail = em.merge(orderdetail);
            if (order1Old != null && !order1Old.equals(order1New)) {
                order1Old.getOrderdetailList().remove(orderdetail);
                order1Old = em.merge(order1Old);
            }
            if (order1New != null && !order1New.equals(order1Old)) {
                order1New.getOrderdetailList().add(orderdetail);
                order1New = em.merge(order1New);
            }
            if (productOld != null && !productOld.equals(productNew)) {
                productOld.getOrderdetailList().remove(orderdetail);
                productOld = em.merge(productOld);
            }
            if (productNew != null && !productNew.equals(productOld)) {
                productNew.getOrderdetailList().add(orderdetail);
                productNew = em.merge(productNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                OrderdetailPK id = orderdetail.getOrderdetailPK();
                if (findOrderdetail(id) == null) {
                    throw new NonexistentEntityException("The orderdetail with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(OrderdetailPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Orderdetail orderdetail;
            try {
                orderdetail = em.getReference(Orderdetail.class, id);
                orderdetail.getOrderdetailPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The orderdetail with id " + id + " no longer exists.", enfe);
            }
            Order order1 = orderdetail.getOrder1();
            if (order1 != null) {
                order1.getOrderdetailList().remove(orderdetail);
                order1 = em.merge(order1);
            }
            Product product = orderdetail.getProduct();
            if (product != null) {
                product.getOrderdetailList().remove(orderdetail);
                product = em.merge(product);
            }
            em.remove(orderdetail);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Orderdetail> findOrderdetailEntities() {
        return findOrderdetailEntities(true, -1, -1);
    }

    public List<Orderdetail> findOrderdetailEntities(int maxResults, int firstResult) {
        return findOrderdetailEntities(false, maxResults, firstResult);
    }

    private List<Orderdetail> findOrderdetailEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Orderdetail.class));
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

    public Orderdetail findOrderdetail(OrderdetailPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Orderdetail.class, id);
        } finally {
            em.close();
        }
    }

    public int getOrderdetailCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Orderdetail> rt = cq.from(Orderdetail.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
