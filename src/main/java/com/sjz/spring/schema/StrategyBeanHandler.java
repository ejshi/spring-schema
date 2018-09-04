package com.sjz.spring.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * 解析器注入
 * @author shijun03
 */
public class StrategyBeanHandler extends NamespaceHandlerSupport{

	@Override
	public void init() {
		registerBeanDefinitionParser("strategy", new StrategyBeanDefinitionParser());
	}

}
