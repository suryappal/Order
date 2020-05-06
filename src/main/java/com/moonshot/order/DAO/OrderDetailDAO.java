/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moonshot.order.DAO;

import com.moonshot.order.JPA.OrderdetailJpaController;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author surya
 */
public class OrderDetailDAO extends OrderdetailJpaController{

    public OrderDetailDAO(EntityManagerFactory emf) {
        super(emf);
    }
    
}
