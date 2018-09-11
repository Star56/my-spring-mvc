package com.qiwenfei.myspring.formework.context;

import com.qiwenfei.myspring.formework.beans.MyBeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 2018/9/7
 * sunshine
 */
public class MyDefaultListableBeanFactory extends MyAbstractApplicationContext{

    public Map<String,MyBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, MyBeanDefinition>();


    @Override
    public void refreshBeanFactory() {

    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
    }
}
