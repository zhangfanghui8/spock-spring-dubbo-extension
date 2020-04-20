package org.spockframework.spring.dubbo;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;

/**
 *
 *   The main function of this class is to replace the member variable that depends on the annotation @Reference with
 *    a mock bean。
 *
 * @author zhangfanghui
 * @since 2020-04-20
 */
public class ReferenceAnnotationProcessor implements BeanPostProcessor, ApplicationContextAware {

    private ConfigurableListableBeanFactory configurableBeanFactory;

    @Autowired
    public ReferenceAnnotationProcessor(ConfigurableListableBeanFactory beanFactory) {
        this.configurableBeanFactory = beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        this.scanReferenceAnnotation(bean,beanName);
        return bean;
    }

    private void scanReferenceAnnotation(Object bean, String beanName){
        this.configureFieldInjection(bean);
    }

    private void configureFieldInjection(Object bean){
        Class beanClz = bean.getClass();
        ReflectionUtils.FieldCallback fieldCallback = new ReferenceFieldCallback(configurableBeanFactory, bean);
        ReflectionUtils.doWithFields(beanClz, fieldCallback);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
