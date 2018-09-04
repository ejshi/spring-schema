package com.sjz.spring.schema;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * 配置解析器
 * @author shijun03
 */
public class StrategyBeanDefinitionParser extends AbstractSingleBeanDefinitionParser{

	@Override
	protected Class<?> getBeanClass(Element element) {
		return StrategyBean.class;
	}

	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		String id = element.getAttribute("id");
		String url = element.getAttribute("url");
		String interfaceName = element.getAttribute("interface");
		
		if(StringUtils.hasText(id)){
			//判断bean是否已经注册
			boolean hasRegistry  = parserContext.getRegistry().containsBeanDefinition(id);
			if(hasRegistry){
				throw new IllegalArgumentException("bean has registry ,please check bean ,id = "+id);
			}
			builder.addPropertyValue("id", id);
		}
		
		if(StringUtils.hasText(url)){
			builder.addPropertyValue("url", url);
		}
		
		if(StringUtils.hasText(interfaceName)){
			Class<?> forName;
			try {
				forName = Class.forName(interfaceName);
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException("interface class not found", e);
			}
			builder.addPropertyValue("interfaceName", forName);
		}
	}
	
}
