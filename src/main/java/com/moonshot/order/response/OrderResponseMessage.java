/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moonshot.order.response;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author surya
 */
public class OrderResponseMessage {
    
    protected Map<Integer, String> responseMessageMap;

    public OrderResponseMessage() {
        responseMessageMap = new HashMap<>();
        responseMessageMap.put(OrderResponseCode.SUCCESS, "Success");
        responseMessageMap.put(OrderResponseCode.SERVICE_CONNECTION_FAILURE, "Ki je hoche bojha jache na");
        responseMessageMap.put(OrderResponseCode.DB_DUPLICATE, "Duplicate key in DB. Contact admin.");
        responseMessageMap.put(OrderResponseCode.USER_AUTH_FAILURE, "User authentication failed.");
        responseMessageMap.put(OrderResponseCode.NON_EXISTANT_DATA, "No data to delete.");
    }
    
    public String getResponseMessage (Integer responseCode) {
        return responseMessageMap.get(responseCode);
    }
    
}
