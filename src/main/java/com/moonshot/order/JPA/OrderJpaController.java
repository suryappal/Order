/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moonshot.order.JPA;

import com.moonshot.order.JPA.exceptions.IllegalOrphanException;
import com.moonshot.order.JPA.exceptions.NonexistentEntityException;
import com.moonshot.order.JPA.exceptions.PreexistingEntityException;
import com.moonshot.order.entities.Order;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.moonshot.order.entities.User;
import com.moonshot.order.entities.Orderdetail;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author surya
 */
public class OrderJpaController implements Serializable {

    public OrderJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Order order) throws PreexistingEntityException, Exception {
        if (order.getOrderdetailList() == null) {
            order.setOrderdetailList(new ArrayList<Orderdetail>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User userId = order.getUserId();
            if (userId != null) {
                userId = em.getReference(userId.getClass(), userId.getId());
                order.setUserId(userId);
            }
            List<Orderdetail> attachedOrderdetailList = new ArrayList<Orderdetail>();
            for (Orderdetail orderdetailListOrderdetailToAttach : order.getOrderdetailList()) {
                orderdetailListOrderdetailToAttach = em.getReference(orderdetailListOrderdetailToAttach.getClass(), orderdetailListOrderdetailToAttach.getOrderdetailPK());
                attachedOrderdetailList.add(orderdetailListOrderdetailToAttach);
            }
            order.setOrderdetailList(attachedOrderdetailList);
            em.persist(order);
            if (userId != null) {
                userId.getOrder1List().add(order);
                userId = em.merge(userId);
            }
            for (Orderdetail orderdetailListOrderdetail : order.getOrderdetailList()) {
                Order oldOrder1OfOrderdetailListOrderdetail = orderdetailListOrderdetail.getOrder1();
                orderdetailListOrderdetail.setOrder1(order);
                orderdetailListOrderdetail = em.merge(orderdetailListOrderdetail);
                if (oldOrder1OfOrderdetailListOrderdetail != null) {
                    oldOrder1OfOrderdetailListOrderdetail.getOrderdetailList().remove(orderdetailListOrderdetail);
                    oldOrder1OfOrderdetailListOrderdetail = em.merge(oldOrder1OfOrderdetailListOrderdetail);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findOrder(order.getId()) != null) {
                throw new PreexistingEntityException("Order " + order + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Order order) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Order persistentOrder = em.find(Order.class, order.getId());
            User userIdOld = persistentOrder.getUserId();
            User userIdNew = order.getUserId();
            List<Orderdetail> orderdetailListOld = persistentOrder.getOrderdetailList();
            List<Orderdetail> orderdetailListNew = order.getOrderdetailList();
            List<String> illegalOrphanMessages = null;
            for (Orderdetail orderdetailListOldOrderdetail : orderdetailListOld) {
                if (!orderdetailListNew.contains(orderdetailListOldOrderdetail)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Orderdetail " + orderdetailListOldOrderdetail + " since its order1 field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (userIdNew != null) {
                userIdNew = em.getReference(userIdNew.getClass(), userIdNew.getId());
                order.setUserId(userIdNew);
            }
            List<Orderdetail> attachedOrderdetailListNew = new ArrayList<Orderdetail>();
            for (Orderdetail orderdetailListNewOrderdetailToAttach : orderdetailListNew) {
                orderdetailListNewOrderdetailToAttach = em.getReference(orderdetailListNewOrderdetailToAttach.getClass(), orderdetailListNewOrderdetailToAttach.getOrderdetailPK());
                attachedOrderdetailListNew.add(orderdetailListNewOrderdetailToAttach);
            }
            orderdetailListNew = attachedOrderdetailListNew;
            order.setOrderdetailList(orderdetailListNew);
            order = em.merge(order);
            if (userIdOld != null && !userIdOld.equals(userIdNew)) {
                userIdOld.getOrder1List().remove(order);
                userIdOld = em.merge(userIdOld);
            }
            if (userIdNew != null && !userIdNew.equals(userIdOld)) {
                userIdNew.getOrder1List().add(order);
                userIdNew = em.merge(userIdNew);
            }
            for (Orderdetail orderdetailListNewOrderdetail : orderdetailListNew) {
                if (!orderdetailListOld.contains(orderdetailListNewOrderdetail)) {
                    Order oldOrder1OfOrderdetailListNewOrderdetail = orderdetailListNewOrderdetail.getOrder1();
                    orderdetailListNewOrderdetail.setOrder1(order);
                    orderdetailListNewOrderdetail = em.merge(orderdetailListNewOrderdetail);
                    if (oldOrder1OfOrderdetailListNewOrderdetail != null && !oldOrder1OfOrderdetailListNewOrderdetail.equals(order)) {
                        oldOrder1OfOrderdetailListNewOrderdetail.getOrderdetailList().remove(orderdetailListNewOrderdetail);
                        oldOrder1OfOrderdetailListNewOrderdetail = em.merge(oldOrder1OfOrderdetailListNewOrderdetail);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = order.getId();
                if (findOrder(id) == null) {
                    throw new NonexistentEntityException("The order with id " + id + " no longer exists.");
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
            Order order;
            try {
                order = em.getReference(Order.class, id);
                order.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The order with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Orderdetail> orderdetailListOrphanCheck = order.getOrderdetailList();
            for (Orderdetail orderdetailListOrphanCheckOrderdetail : orderdetailListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Order (" + order + ") cannot be destroyed since the Orderdetail " + orderdetailListOrphanCheckOrderdetail + " in its orderdetailList field has a non-nullable order1 field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            User userId = order.getUserId();
            if (userId != null) {
                userId.getOrder1List().remove(order);
                userId = em.merge(userId);
            }
            em.remove(order);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Order> findOrderEntities() {
        return findOrderEntities(true, -1, -1);
    }

    public List<Order> findOrderEntities(int maxResults, int firstResult) {
        return findOrderEntities(false, maxResults, firstResult);
    }

    private List<Order> findOrderEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Order.class));
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

    public Order findOrder(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Order.class, id);
        } finally {
            em.close();
        }
    }

    public int getOrderCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Order> rt = cq.from(Order.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
