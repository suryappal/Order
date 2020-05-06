/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moonshot.order.DAO;

import com.moonshot.order.JPA.UserJpaController;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author surya
 */
public class UserDAO extends UserJpaController{

    public UserDAO(EntityManagerFactory emf) {
        super(emf);
    }
    
    
}
