import org.springframework.context.support.ClassPathXmlApplicationContext;
import service.impl.HelloServiceImpl;

public class MyApplication {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		HelloServiceImpl hello = (HelloServiceImpl) ctx.getBean("helloService");
		hello.sayHello();
	}
}