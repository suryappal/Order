/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moonshot.order.service;

import com.moonshot.order.DAO.ProductDAO;
import com.moonshot.order.DAO.UserDAO;
import com.moonshot.order.DTO.ProductDTO;
import com.moonshot.order.DTO.UserDTO;
import com.moonshot.order.JPA.exceptions.IllegalOrphanException;
import com.moonshot.order.JPA.exceptions.NonexistentEntityException;
import com.moonshot.order.JPA.exceptions.PreexistingEntityException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.moonshot.order.entities.User;
import com.moonshot.order.entities.Product;
import com.moonshot.order.entities.Order;
import com.moonshot.order.entities.Orderdetail;
import com.moonshot.order.response.OrderResponseCode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author surya
 */
public class MasterDataService {

    private EntityManagerFactory emf;

    public MasterDataService() {
        emf = Persistence.createEntityManagerFactory("com.moonshot_Order_war_1.0-SNAPSHOTPU");
    }

//    --------------User related methods start
    public UserDTO authenticateUser(UserDTO userDTO) {

        String userID = userDTO.getUserID();

        UserDAO userDAO = new UserDAO(emf);
        User user = userDAO.findUser(userID);

        if (user.getPassword().equals(userDTO.getPassword())) {
            userDTO.setResponse(OrderResponseCode.SUCCESS);
            userDTO.setUserName(user.getName());

        } else {
            userDTO.setResponse(OrderResponseCode.USER_AUTH_FAILURE);
        }
        return userDTO;

    }
//    --------------User related methods end 

//    --------------Product related methods start
    public int addProduct(ProductDTO productDTO) {

        Product product = new Product();
        product.setId(productDTO.getProductID());
        product.setName(productDTO.getProductName());

        ProductDAO productDAO = new ProductDAO(emf);

        try {
            productDAO.create(product);
            return OrderResponseCode.SUCCESS;
        } catch (PreexistingEntityException pe) {
            return OrderResponseCode.DB_DUPLICATE;
        } catch (Exception ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return OrderResponseCode.SERVICE_CONNECTION_FAILURE;
        }
    }

    public int editProduct(ProductDTO productDTO) {

        Product product = new Product();
        product.setId(productDTO.getProductID());
        product.setName(productDTO.getProductName());

        ProductDAO productDAO = new ProductDAO(emf);

        try {
            productDAO.edit(product);
            return OrderResponseCode.SUCCESS;
        } catch (PreexistingEntityException pe) {
            return OrderResponseCode.DB_DUPLICATE;
        } catch (Exception ex) {
            Logger.getLogger(MasterDataService.class.getName()).log(Level.SEVERE, null, ex);
            return OrderResponseCode.SERVICE_CONNECTION_FAILURE;
        }
    }

    public int deleteProduct(ProductDTO productDTO) {

        ProductDAO productDAO = new ProductDAO(emf);

        try {
            productDAO.destroy(productDTO.getProductID());
            return OrderResponseCode.SUCCESS;
        } catch (IllegalOrphanException ex) {
            return OrderResponseCode.SERVICE_CONNECTION_FAILURE;
        } catch (NonexistentEntityException ex) {
            return OrderResponseCode.NON_EXISTANT_DATA;
        }

    }

    public ProductDTO getProductValue(int productId) {

        ProductDAO productDAO = new ProductDAO(emf);
        Product product = productDAO.findProduct(productId);
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductID(product.getId());
        productDTO.setProductName(product.getName());

        return productDTO;

    }

    public List<ProductDTO> getProductList() {

        ProductDAO productDAO = new ProductDAO(emf);
        List<ProductDTO> productDTOList = new ArrayList<>();
        List<Product> productList = productDAO.findProductEntities();

        for (Product product : productList) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductID(product.getId());
            productDTO.setProductName(product.getName());
            productDTOList.add(productDTO);
        }

        return productDTOList;
    }

//    --------------Product related methods end
}
