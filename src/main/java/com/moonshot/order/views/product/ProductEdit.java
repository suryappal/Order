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
@Named(value = "productEdit")
@ViewScoped
public class ProductEdit implements Serializable{

    /**
     * Creates a new instance of ProductEdit
     */
    
    private int id;
    private String name;
    private ProductDTO editProductDTO;
    public ProductEdit() {
    }
    
    public void fillEditProductValues() {

        MasterDataService mds = new MasterDataService();
        editProductDTO=mds.getProductValue(id);
        
        id=editProductDTO.getProductID();
        name=editProductDTO.getProductName();
        
    }
    
    public String save() {

        String redirectUrl;
        OrderResponseMessage responseMessage = new OrderResponseMessage();
        FacesMessage message;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductID(id);
        productDTO.setProductName(name);
        
        MasterDataService mds = new MasterDataService();        
        int response= mds.editProduct(productDTO);

        if ( response != OrderResponseCode.SUCCESS) {
            
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(response));
            redirectUrl = "ProductEdit";
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(response));
            redirectUrl = "ProductList";
        }       
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        f.addMessage(null, message);        
        
        return redirectUrl;

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

    public ProductDTO getEditProductDTO() {
        return editProductDTO;
    }

    public void setEditProductDTO(ProductDTO editProductDTO) {
        this.editProductDTO = editProductDTO;
    }
    
    
}
