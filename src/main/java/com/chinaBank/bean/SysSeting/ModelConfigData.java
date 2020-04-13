package com.chinaBank.bean.SysSeting;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component("modelConfig")
@Data
public class ModelConfigData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Value("${model.path}")
	private String path;
	
    @Value(value = "${spring.datasource.druid.driver-class-name}")
    private String driver;   
    
    @Value(value = "${spring.datasource.druid.url}")
    private String url;   
    
    @Value(value = "${spring.datasource.druid.username}")
    private String userName;
    
    @Value(value = "${spring.datasource.druid.password}")
    private String password;
}
