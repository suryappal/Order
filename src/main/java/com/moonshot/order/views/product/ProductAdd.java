/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moonshot.order.views.product;

import com.moonshot.order.DTO.ProductDTO;
import com.moonshot.order.response.OrderResponseCode;
import com.moonshot.order.response.OrderResponseMessage;
import com.moonshot.order.service.MasterDataService;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author surya
 */
@Named(value = "productAdd")
@ViewScoped
public class ProductAdd implements Serializable{

    /**
     * Creates a new instance of ProductAdd
     * @return 
     */
    
    public String addProduct(){
        String redirectUrl;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductID(id);
        productDTO.setProductName(name);
        
        OrderResponseMessage responseMessage = new OrderResponseMessage();
        FacesMessage message;
        
        
        MasterDataService mds = new MasterDataService();
        
        int response =mds.addProduct(productDTO);
        
        if ( response != OrderResponseCode.SUCCESS) {
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(response));
            redirectUrl = "ProductAdd";
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(response));
            redirectUrl = "ProductList";
        }       
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        f.addMessage(null, message);
        
        return redirectUrl;
    }
    int id;
    String name;
    
    public ProductAdd() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    
    
}
