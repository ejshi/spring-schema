package com.sjz.spring.schema;

import org.springframework.beans.factory.FactoryBean;

/**
 * 策略配置Bean
 * @author shijun03
 *
 */
public class StrategyBean implements FactoryBean<Object>{
	
	private String id;
	private String url;
	private Class<?> interfaceName;
	
	@Override
	public Object getObject() throws Exception {
		
		return interfaceName.newInstance();
	}
	
	@Override
	public Class<?> getObjectType() {
		
		return interfaceName;
	}
	@Override
	public boolean isSingleton() {
		return true;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Class<?> getInterfaceName() {
		return interfaceName;
	}
	public void setInterfaceName(Class<?> interfaceName) {
		this.interfaceName = interfaceName;
	}
}
