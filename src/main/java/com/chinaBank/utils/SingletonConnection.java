package com.chinaBank.utils;

import java.sql.Connection;
import java.sql.DriverManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chinaBank.bean.SysSeting.ModelConfigData;

@Component("singletonConnection")
public class SingletonConnection {

	@Autowired
	private ModelConfigData modelConfig;
	
	private Connection conn;

    private static SingletonConnection instance = new SingletonConnection();  
    
    private SingletonConnection (){
    	
    }  
    
    public static SingletonConnection getInstance() {  
       return instance;  
    }
    
	public Connection getConnection() throws Exception {
		if (null == conn) {
			Class.forName(modelConfig.getDriver());
	        String url = modelConfig.getUrl();        
	        //测试url中是否包含useSSL字段，没有则添加设该字段且禁用
	        if( url.indexOf("?") == -1 ){
	            url = url + "?useSSL=false" ;
	        }
	        else if( url.indexOf("useSSL=false") == -1 || url.indexOf("useSSL=true") == -1 )
	        {
	            url = url + "&useSSL=false";
	        }
	        String userName = modelConfig.getUserName();
	        String password = modelConfig.getPassword();
	        conn =  DriverManager.getConnection(url, userName, password);   
		}
		return conn;
	}
}
