package com.itdage.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @ClassName: ShiroCasConfiguration 
 * @Description: shiro + cas配置
 * @author: scy
 * @date: 2018年10月22日 下午2:33:50
 */
@Configuration
public class ShiroCasConfiguration {
	
	private static final Logger logger = LoggerFactory.getLogger(ShiroCasConfiguration.class);
	
	public static final String casServerUrlPrefix = "http://localhost:8443/cas";
}
