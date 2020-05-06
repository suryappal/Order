/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moonshot.order.views.login;

import com.moonshot.order.DTO.UserDTO;
import com.moonshot.order.service.MasterDataService;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 *
 * @author surya
 */
@Named(value = "loginController")
@SessionScoped
public class LoginController implements Serializable {

    /**
     * Creates a new instance of LoginController
     */
    
    private String userID;
    private String password;
    private String userName;
    
    public LoginController() {
    }
    
    public String login(){
        String redirectURL;
        
        MasterDataService mds = new MasterDataService();
        UserDTO userDTO= new UserDTO();
                
        userDTO.setUserID(userID);
        userDTO.setPassword(password);
        
        
        userDTO = mds.authenticateUser(userDTO);
        
        if (userDTO.getResponse()==0){
            userName= userDTO.getUserName();
             redirectURL="LoginSucccess";
             
        }else{
            redirectURL="LoginFailure";
        }
        
        return redirectURL;
        
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    
}
