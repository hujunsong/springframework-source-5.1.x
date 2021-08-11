/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.aop.aspectj.annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.autoproxy.AspectJAwareAdvisorAutoProxyCreator;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link AspectJAwareAdvisorAutoProxyCreator} subclass that processes all AspectJ
 * annotation aspects in the current application context, as well as Spring Advisors.
 *
 * <p>Any AspectJ annotated classes will automatically be recognized, and their
 * advice applied if Spring AOP's proxy-based model is capable of applying it.
 * This covers method execution joinpoints.
 *
 * <p>If the &lt;aop:include&gt; element is used, only @AspectJ beans with names matched by
 * an include pattern will be considered as defining aspects to use for Spring auto-proxying.
 *
 * <p>Processing of Spring Advisors follows the rules established in
 * {@link org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator}.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see org.springframework.aop.aspectj.annotation.AspectJAdvisorFactory
 */

/**
 * 【AOP入口】
 * 1、@EnableAspectJAutoProxy ： 开启AOP支持
 * 2、@Import(AspectJAutoProxyRegistrar.class) ：通过Import注解给容器中导入AspectJAutoProxyRegistrar组件.
 * 3、AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry); 如果有必要则给容器中注册一个AspectJAnnotationAutoProxyCreator组件.
 *
 * 【类结构图】
 * AspectJAwareAdvisorAutoProxyCreator : 公开了AspectJ的调用上下文，并弄清楚来自同一切面的多个Advisor在AspectJ中的优先级规则。
 * AbstractAdvisorAutoProxyCreator : 通用自动代理创建器，它基于检测到的每个顾问程序为特定bean构建AOP代理。
 * AbstractAutoProxyCreator : 扩展了 ProxyProcessorSupport，实现了SmartInstantiationAwareBeanPostProcessor、BeanFactoryAware 接口，是BeanPostProcessor 实现，
 * 								该实现使用AOP代理包装每个合格的bean，并在调用bean本身之前委派给指定的拦截器。
 *     BeanFactoryAware : 实现了该接口的Bean可以知道它属于那个 BeanFactory，Bean可以通过Spring容器查找它的协同者（依赖查找），
 * 						但大多数的Bean是通过构造器参数和Bean方法（依赖注入）来获取它的协同者。
 *     BeanPostProcessor ：工厂钩子，允许自定义修改新的bean实例。例如，检查标记接口或使用代理包装bean。如果我们需要在Spring容器中完成Bean的实例化，
 * 						配置和其初始化前后添加一些自己的逻辑处理，我们就可以定义一个或多个BeanPostProcessor接口的实现，然后注册到容器中。
 * InstantiationAwareBeanPostProcessor : BeanPostProcessor 的子接口，它添加了实例化之前的回调，以及实例化之后但设置了显式属性或自动装配之前的回调。
 * 										它内部提供了3个方法，再加上BeanPostProcessor接口内部的2个方法，实现这个接口需要实现5个方法。
 * 										InstantiationAwareBeanPostProcessor 接口的主要作用在于目标对象的实例化过程中需要处理的事情，
 * 										包括实例化对象的前后过程以及实例的属性设置。
 * SmartInstantiationAwareBeanPostProcessor : InstantiationAwareBeanPostProcessor 接口的扩展，多出了3个方法，添加了用于预测已处理bean的最终类型的回调，
 * 									再加上父接口的5个方法，所以实现这个接口需要实现8个方法，主要作用也是在于目标对象的实例化过程中需要处理的事情。
 * 总之：AspectJAwareAdvisorAutoProxyCreator为 AspectJ 切面类创建自动代理。
 */
@SuppressWarnings("serial")
public class AnnotationAwareAspectJAutoProxyCreator extends AspectJAwareAdvisorAutoProxyCreator {

	@Nullable
	private List<Pattern> includePatterns;

	@Nullable
	private AspectJAdvisorFactory aspectJAdvisorFactory;

	@Nullable
	private BeanFactoryAspectJAdvisorsBuilder aspectJAdvisorsBuilder;


	/**
	 * Set a list of regex patterns, matching eligible @AspectJ bean names.
	 * <p>Default is to consider all @AspectJ beans as eligible.
	 */
	public void setIncludePatterns(List<String> patterns) {
		this.includePatterns = new ArrayList<>(patterns.size());
		for (String patternText : patterns) {
			this.includePatterns.add(Pattern.compile(patternText));
		}
	}

	public void setAspectJAdvisorFactory(AspectJAdvisorFactory aspectJAdvisorFactory) {
		Assert.notNull(aspectJAdvisorFactory, "AspectJAdvisorFactory must not be null");
		this.aspectJAdvisorFactory = aspectJAdvisorFactory;
	}

	@Override
	protected void initBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		super.initBeanFactory(beanFactory);
		if (this.aspectJAdvisorFactory == null) {
			this.aspectJAdvisorFactory = new ReflectiveAspectJAdvisorFactory(beanFactory);
		}
		this.aspectJAdvisorsBuilder =
				new BeanFactoryAspectJAdvisorsBuilderAdapter(beanFactory, this.aspectJAdvisorFactory);
	}


	@Override
	protected List<Advisor> findCandidateAdvisors() {
		// Add all the Spring advisors found according to superclass rules.
		List<Advisor> advisors = super.findCandidateAdvisors();

		// Build Advisors for all AspectJ aspects in the bean factory.
		if (this.aspectJAdvisorsBuilder != null) {

			// 通过aspectJAdvisorsBuilder对象的buildAspectJAdvisors方法构建器构建通知器对象
			advisors.addAll(this.aspectJAdvisorsBuilder.buildAspectJAdvisors());
		}
		return advisors;
	}

	@Override
	protected boolean isInfrastructureClass(Class<?> beanClass) {
		// Previously we setProxyTargetClass(true) in the constructor, but that has too
		// broad an impact. Instead we now override isInfrastructureClass to avoid proxying
		// aspects. I'm not entirely happy with that as there is no good reason not
		// to advise aspects, except that it causes advice invocation to go through a
		// proxy, and if the aspect implements e.g the Ordered interface it will be
		// proxied by that interface and fail at runtime as the advice method is not
		// defined on the interface. We could potentially relax the restriction about
		// not advising aspects in the future.
		return (super.isInfrastructureClass(beanClass) ||
				(this.aspectJAdvisorFactory != null && this.aspectJAdvisorFactory.isAspect(beanClass)));
	}

	/**
	 * Check whether the given aspect bean is eligible for auto-proxying.
	 * <p>If no &lt;aop:include&gt; elements were used then "includePatterns" will be
	 * {@code null} and all beans are included. If "includePatterns" is non-null,
	 * then one of the patterns must match.
	 */
	protected boolean isEligibleAspectBean(String beanName) {
		if (this.includePatterns == null) {
			return true;
		}
		for (Pattern pattern : this.includePatterns) {
			if (pattern.matcher(beanName).matches()) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Subclass of BeanFactoryAspectJAdvisorsBuilderAdapter that delegates to
	 * surrounding AnnotationAwareAspectJAutoProxyCreator facilities.
	 */
	private class BeanFactoryAspectJAdvisorsBuilderAdapter extends BeanFactoryAspectJAdvisorsBuilder {

		public BeanFactoryAspectJAdvisorsBuilderAdapter(
				ListableBeanFactory beanFactory, AspectJAdvisorFactory advisorFactory) {

			super(beanFactory, advisorFactory);
		}

		@Override
		protected boolean isEligibleBean(String beanName) {
			return AnnotationAwareAspectJAutoProxyCreator.this.isEligibleAspectBean(beanName);
		}
	}

}
