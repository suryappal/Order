/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moonshot.order.JPA;

import com.moonshot.order.JPA.exceptions.IllegalOrphanException;
import com.moonshot.order.JPA.exceptions.NonexistentEntityException;
import com.moonshot.order.JPA.exceptions.PreexistingEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.moonshot.order.entities.Order;
import com.moonshot.order.entities.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author surya
 */
public class UserJpaController implements Serializable {

    public UserJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(User user) throws PreexistingEntityException, Exception {
        if (user.getOrder1List() == null) {
            user.setOrder1List(new ArrayList<Order>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Order> attachedOrder1List = new ArrayList<Order>();
            for (Order order1ListOrderToAttach : user.getOrder1List()) {
                order1ListOrderToAttach = em.getReference(order1ListOrderToAttach.getClass(), order1ListOrderToAttach.getId());
                attachedOrder1List.add(order1ListOrderToAttach);
            }
            user.setOrder1List(attachedOrder1List);
            em.persist(user);
            for (Order order1ListOrder : user.getOrder1List()) {
                User oldUserIdOfOrder1ListOrder = order1ListOrder.getUserId();
                order1ListOrder.setUserId(user);
                order1ListOrder = em.merge(order1ListOrder);
                if (oldUserIdOfOrder1ListOrder != null) {
                    oldUserIdOfOrder1ListOrder.getOrder1List().remove(order1ListOrder);
                    oldUserIdOfOrder1ListOrder = em.merge(oldUserIdOfOrder1ListOrder);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUser(user.getId()) != null) {
                throw new PreexistingEntityException("User " + user + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(User user) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User persistentUser = em.find(User.class, user.getId());
            List<Order> order1ListOld = persistentUser.getOrder1List();
            List<Order> order1ListNew = user.getOrder1List();
            List<String> illegalOrphanMessages = null;
            for (Order order1ListOldOrder : order1ListOld) {
                if (!order1ListNew.contains(order1ListOldOrder)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Order " + order1ListOldOrder + " since its userId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Order> attachedOrder1ListNew = new ArrayList<Order>();
            for (Order order1ListNewOrderToAttach : order1ListNew) {
                order1ListNewOrderToAttach = em.getReference(order1ListNewOrderToAttach.getClass(), order1ListNewOrderToAttach.getId());
                attachedOrder1ListNew.add(order1ListNewOrderToAttach);
            }
            order1ListNew = attachedOrder1ListNew;
            user.setOrder1List(order1ListNew);
            user = em.merge(user);
            for (Order order1ListNewOrder : order1ListNew) {
                if (!order1ListOld.contains(order1ListNewOrder)) {
                    User oldUserIdOfOrder1ListNewOrder = order1ListNewOrder.getUserId();
                    order1ListNewOrder.setUserId(user);
                    order1ListNewOrder = em.merge(order1ListNewOrder);
                    if (oldUserIdOfOrder1ListNewOrder != null && !oldUserIdOfOrder1ListNewOrder.equals(user)) {
                        oldUserIdOfOrder1ListNewOrder.getOrder1List().remove(order1ListNewOrder);
                        oldUserIdOfOrder1ListNewOrder = em.merge(oldUserIdOfOrder1ListNewOrder);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = user.getId();
                if (findUser(id) == null) {
                    throw new NonexistentEntityException("The user with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            User user;
            try {
                user = em.getReference(User.class, id);
                user.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The user with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Order> order1ListOrphanCheck = user.getOrder1List();
            for (Order order1ListOrphanCheckOrder : order1ListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This User (" + user + ") cannot be destroyed since the Order " + order1ListOrphanCheckOrder + " in its order1List field has a non-nullable userId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(user);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<User> findUserEntities() {
        return findUserEntities(true, -1, -1);
    }

    public List<User> findUserEntities(int maxResults, int firstResult) {
        return findUserEntities(false, maxResults, firstResult);
    }

    private List<User> findUserEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(User.class));
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

    public User findUser(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public int getUserCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<User> rt = cq.from(User.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
