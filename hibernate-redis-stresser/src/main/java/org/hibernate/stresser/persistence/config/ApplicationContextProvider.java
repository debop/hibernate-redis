package org.hibernate.stresser.persistence.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * @author Johno Crawford (johno@sulake.com)
 */
@Component
public class ApplicationContextProvider implements BeanFactoryAware {

    private static AutowireCapableBeanFactory autowireCapableBeanFactory = null;

    public static AutowireCapableBeanFactory getAutowireCapableBeanFactory() {
        return autowireCapableBeanFactory;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        ApplicationContextProvider.autowireCapableBeanFactory = (AutowireCapableBeanFactory) beanFactory;
    }
}
