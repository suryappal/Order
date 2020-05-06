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
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

/**
 *
 * @author surya
 */
@Named(value = "productList")
@ViewScoped
public class ProductList implements Serializable {

    /**
     * Creates a new instance of ProductList
     */
    private int id;
    private String name;
    private List<ProductDTO> productDTOList;
    private ProductDTO selectedProductDTO;

    public ProductList() {
    }

    public void fillProductValues() {

        MasterDataService mds = new MasterDataService();
        productDTOList = mds.getProductList();

    }
    
    public String delete() {

        String redirectUrl;  
        FacesMessage message;
        OrderResponseMessage responseMessage = new OrderResponseMessage();

        MasterDataService mds = new MasterDataService();
        int response= mds.deleteProduct(selectedProductDTO);
        
         if ( response != OrderResponseCode.SUCCESS) {
            
            message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error", responseMessage.getResponseMessage(response));
            redirectUrl = "ProductList";
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", responseMessage.getResponseMessage(response));
            redirectUrl = "ProductList";
        }       
        FacesContext f = FacesContext.getCurrentInstance();
        f.getExternalContext().getFlash().setKeepMessages(true);
        f.addMessage(null, message);        
        
        return redirectUrl;
       

    }
    
    public String goToEditProduct() {
        return "ProductEdit";
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

    public List<ProductDTO> getProductDTOList() {
        return productDTOList;
    }

    public void setProductDTOList(List<ProductDTO> productDTOList) {
        this.productDTOList = productDTOList;
    }

    public ProductDTO getSelectedProductDTO() {
        return selectedProductDTO;
    }

    public void setSelectedProductDTO(ProductDTO selectedProductDTO) {
        this.selectedProductDTO = selectedProductDTO;
    }

}
