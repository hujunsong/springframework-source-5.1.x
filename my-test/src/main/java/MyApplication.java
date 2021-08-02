import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.impl.HelloServiceImpl;

public class MyApplication {

	/**
	 * spring容器启动过程：
	 * 1、资源定位、加载、注册（把配置文件解释成beanDefinition）
	 * 2、依赖注入：
	 * 		2.1：读取beanDefinition,获取依赖关系，解释成beanWappper
	 * 		2.2: 实例化（代理对象）：createBeanInstance();创建实例放入IOC容器中
	 * 		2.3：注入(设值)：populateBean()
	 */
	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		HelloServiceImpl hello = (HelloServiceImpl) ctx.getBean("helloService");
		hello.sayHello();
	}
}