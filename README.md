如果要开发一个spring自定义的标签比如： 
<pre:strategy id="strategyFrist" interface="org.test.spring.schema.strategy.FristStrategy"
		url="tcp://strategy/frist" />

扩展Spring自定义标签大致需要如下几步：（把大象装冰箱，需要三步，开门，放，关门。。。）

1.创建需要扩展的组件
2.定义XSD文件描述组件内容
3.创建一个文件，实现BeanDefinitionParser接口，用来解析XSD文件中的定义和组件定义
4.创建Handler文件，扩展字NamespaceHandlerSupport,目的是将组件注册到Spring容器
5.编写Spring.handlers和Spring.schemas文件

详细如下：
1、创建maven工程spring-schema，添加spring相应jar包
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-beans</artifactId>
			<version>${spring.version}</version>
			<optional>true</optional>
		</dependency>
		
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-context</artifactId>
			<version>${spring.version}</version>
			<optional>true</optional>
		</dependency>

2、创建一个bean，接收自定义标签属性，如StrategyBean，这里实现了FactoryBean接口，为了将自定义的标签解析完成后注入spring容器 
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
	//省略get/set方法
}

3、在工程的META-INF文件夹下定义一个XSD文件描述组件内容，如src/main/resources/META-INF/strategy.xsd
<xs:schema attributeFormDefault="unqualified" elementFormDefault="qualified" targetNamespace="http://www.shijun.com/schema/strategy" xmlns:xs="http://www.w3.org/2001/XMLSchema">
  <xs:element name="strategy">
    <xs:complexType>
      <xs:simpleContent>
        <xs:extension base="xs:string">
          <xs:attribute type="xs:string" name="id" use="required"/>
          <xs:attribute type="xs:anySimpleType" name="interface" use="required"/>
          <xs:attribute type="xs:string" name="url" use="required"/>
        </xs:extension>
      </xs:simpleContent>
    </xs:complexType>
  </xs:element>
</xs:schema>

4、实现BeanDefinitionParser接口，用来解析XSD文件中的定义和组件定义，如StrategyBeanDefinitionParser
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

5、	创建Handler文件，扩展自NamespaceHandlerSupport，目的是将组件注册到Spring容器中，如StrategyBeanHandler
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

6、编写Spring.handlers和Spring.schemas文件

路径：src/main/resources/META-INF/Spring.handlers
http\://www.shijun.com/schema/strategy=com.sjz.spring.schema.StrategyBeanHandler

路径：src/main/resources/META-INF/Spring.schemas
http\://www.shijun.com/schema/strategy.xsd=META-INF/strategy.xsd

7、创建测试配置文件
新建spring工程，引入前面创建的工程jar以及其他的jar
<dependency>
	<groupId>com.sjz</groupId>
	<artifactId>spring-schema</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
//…省略其他的jar

创建FristStrategy对象
public class FristStrategy {
	public String hello(){
		return "hello schema";
	}
}

在src/main/resources下添加配置文件，如下spring-strategy.xml：
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xmlns:pre="http://www.shijun.com/schema/strategy"
	xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
     http://www.shijun.com/schema/strategy http://www.shijun.com/schema/strategy.xsd">

	<pre:strategy id="strategyFrist" interface="org.test.spring.schema.strategy.FristStrategy"
		url="tcp://strategy/frist" />

</beans>


通过Configuration注解引入spring-strategy.xml配置文件
@Configuration
@ImportResource(locations="spring-strategy.xml")
public class StrategyConfig {
}

测试
@SpringBootApplication
public class App { 
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(App.class, args);
		FristStrategy bean = context.getBean(FristStrategy.class);
		System.out.println("数据调用结果集========"+bean.hello());
	}
}

输出如下：
数据调用结果集========hello schema


8、详细代码
spring自定义标签工程：https://github.com/ejshi/spring-schema.git
测试工程：https://github.com/ejshi/es/tree/master/test-spring-schema
