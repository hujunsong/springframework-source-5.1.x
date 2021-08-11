package com.wb.spring.aop;

import com.wb.spring.aop.config.AopConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author wangbin33
 * @date Created in 18:48 2019/10/6
 *
 *  1）、 @EnableAspectJAutoProxy 开启AOP功能
 *  2）、 @EnableAspectJAutoProxy 会给容器中注册一个组件 AnnotationAwareAspectJAutoProxyCreator
 *  3）、AnnotationAwareAspectJAutoProxyCreator是一个后置处理器；
 *  4）、容器的创建流程：
 *         1）、registerBeanPostProcessors（）注册后置处理器；创建AnnotationAwareAspectJAutoProxyCreator对象
 *         2）、finishBeanFactoryInitialization（）初始化剩下的单实例bean
 *             1）、创建业务逻辑组件和切面组件
 *             2）、AnnotationAwareAspectJAutoProxyCreator拦截组件的创建过程
 *             3）、组件创建完之后，判断组件是否需要增强
 *                      是：切面的通知方法，包装成增强器（Advisor）;给业务逻辑组件创建一个代理对象（cglib）；
 *  5）、执行目标方法：
 *         1）、代理对象执行目标方法
 *         2）、CglibAopProxy.intercept()；
 *              1）、得到目标方法的拦截器链（增强器包装成拦截器MethodInterceptor）
 *              2）、利用拦截器的链式机制，依次进入每一个拦截器进行执行；
 *              3）、效果：
 *                       正常执行：前置通知-》目标方法-》后置通知-》返回通知
 *                       出现异常：前置通知-》目标方法-》后置通知-》异常通知
 *
 * ====================================================================================
 * 1、加载配置信息，解析成 AOPConfig
 * 2、交给AopProxyFactory,调用一个createAopProxy()方法，得到链路
 * 3、JdkDynamicAopProxy/CglibAopProxy调用AdvisedSupport的getInterceptorsAndDynamicInterceptionAdvice()方法得到方法拦截器，并保存到一个容器List中，(IOC容器为ConcurrentHashMap)
 * 4、递归执行拦截器方法proceed()
 * 5、最终目的就是扫描所有的切点，形成拦截器链，由Advisor来调用切面中的方法。
 *
 */
public class TestMain {
	public static void main(String[] args) {
		ApplicationContext acx = new AnnotationConfigApplicationContext(AopConfig.class);

//		DataSourceSwitch dataSourceSwitch = acx.getBean(DataSourceSwitch.class);
//		dataSourceSwitch.doSwitch();

//		UserService bean = acx.getBean(UserService.class);
//		bean.say();

//		LogAspects bean = acx.getBean(LogAspects.class);
//		System.out.println(bean);

		MathCalculator mathCalculator = acx.getBean(MathCalculator.class);
		mathCalculator.div(2, 1);
//		mathCalculator.add(1,2);
		mathCalculator.sub();
//		TestController testController = acx.getBean(TestController.class);
//		testController.test("wangbing");
	}
}
