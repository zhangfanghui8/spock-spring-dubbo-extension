package org.spockframework.spring.dubbo;

import org.spockframework.mock.MockUtil;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhangfanghui
 * @since 2020-04-20
 */
public class ReferenceFieldCallback implements ReflectionUtils.FieldCallback {
    private ConfigurableListableBeanFactory configurableBeanFactory;
    private Object bean;
    private String  reference = "Reference";
    MockUtil mockUtil = new MockUtil();
    Object springBean;
    List<Annotation> referenceAnn = null;


    public ReferenceFieldCallback(ConfigurableListableBeanFactory bf, Object bean) {
        configurableBeanFactory = bf;
        this.bean = bean;
    }


    @Override
    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
        //Get the mock bean from the spring container by name
        try{
            springBean = configurableBeanFactory.getBean(getBeanName(field));
            referenceAnn = Arrays.asList(field.getAnnotations()).stream().filter(f -> f.annotationType().getName().contains(reference)).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(referenceAnn) && Objects.nonNull(springBean) && mockUtil.isMock(springBean)){
                //If there is a member variable with annotation @Reference in the bean and the spring container has a
                // mock bean for that variable，the variable in the bean is replaced with the mock variable
                ReflectionUtils.makeAccessible(field);
                ReflectionUtils.setField(field, bean, springBean);
            }else {
                return;
            }
        }catch (Exception e){
            //no operate
        }
    }
    private String getBeanName(Field field){
        String generatedBeanName = field.getGenericType().getTypeName();
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry)configurableBeanFactory;
        String id = generatedBeanName;
        List<String> beanNames = new ArrayList<>();
        for(int counter = -1; counter == -1 || registry.containsBeanDefinition(id); id = generatedBeanName + "#" + counter) {
             ++counter;
            beanNames.add(id);
        }
        return beanNames.get(beanNames.size()-1);
    }
}
