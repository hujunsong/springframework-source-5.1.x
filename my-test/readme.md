Spring的模板方法（默认实现），一般放在support包里。


**BeanFactory**
这个其实是所有Spring Bean的容器根接口，给Spring 的容器定义一套规范，给IOC容器提供了一套完整的规范，比如我们常用到的getBean方法等

The root interface for accessing a Spring bean container. This is the basic client view of a bean container;
定义方法：

getBean(String name): Spring容器中获取对应Bean对象的方法，如存在，则返回该对象
containsBean(String name)：Spring容器中是否存在该对象
isSingleton(String name)：通过beanName是否为单例对象
isPrototype(String name)：判断bean对象是否为多例对象
isTypeMatch(String name, ResolvableType typeToMatch):判断name值获取出来的bean与typeToMath是否匹配
getType(String name)：获取Bean的Class类型
getAliases(String name):获取name所对应的所有的别名
主要的实现类(包括抽象类)：
AbstractBeanFactory：抽象Bean工厂，绝大部分的实现类，都是继承于他
DefaultListableBeanFactory:Spring默认的工厂类
XmlBeanFactory：前期使用XML配置用的比较多的时候用的Bean工厂
AbstractXmlApplicationContext:抽象应用容器上下文对象
ClassPathXmlApplicationContext:XML解析上下文对象，用户创建Bean对象我们早期写Spring的时候用的就是他


**FactoryBean**
该类是SpringIOC容器是创建Bean的一种形式，这种方式创建Bean会有加成方式，融合了简单的工厂设计模式于装饰器模式
* Interface to be implemented by objects used within a {@link BeanFactory} which * are themselves factories for individual objects. If a bean implements this * interface, it is used as a factory for an object to expose, not directly as a * bean instance that will be exposed itself.
有些人就要问了，我直接使用Spring默认方式创建Bean不香么，为啥还要用FactoryBean做啥，在某些情况下，对于实例Bean对象比较复杂的情况下，使用传统方式创建bean会比较复杂，例如（使用xml配置），这样就出现了FactoryBean接口，可以让用户通过实现该接口来自定义该Bean接口的实例化过程。即包装一层，将复杂的初始化过程包装，让调用者无需关系具体实现细节。
方法：

T getObject()：返回实例
Class<?> getObjectType();:返回该装饰对象的Bean的类型
default boolean isSingleton():Bean是否为单例
常用类：

ProxyFactoryBean :Aop代理Bean
GsonFactoryBean:Gson

