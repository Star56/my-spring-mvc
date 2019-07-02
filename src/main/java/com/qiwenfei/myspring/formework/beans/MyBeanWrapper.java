package com.qiwenfei.myspring.formework.beans;

import com.qiwenfei.myspring.formework.aop.MyAopConfig;
import com.qiwenfei.myspring.formework.aop.MyAopProxy;
import com.qiwenfei.myspring.formework.core.MyFactoryBean;

/**
 * 2018/9/11
 * sunshine
 */
public class MyBeanWrapper extends MyFactoryBean {

   private   MyAopProxy  aopProxy =  new MyAopProxy() ;
    //还会用到  观察者  模式
    //1、支持事件响应，会有一个监听
    private MyBeanPostProcessor postProcessor;

   private  Object originalInstance ;//保存原来的对象

    private Object wrapperInstance ;//保存包装后的对象

    public MyBeanWrapper(Object originalInstance) {
        this.originalInstance = originalInstance;
        wrapperInstance = aopProxy.getProxy(originalInstance);
    }

    public void setAopConfig(MyAopConfig config) {
        aopProxy.setConfig(config);
    }

    public Object getOriginalInstance() {
        return originalInstance;
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }
    public void setPostProcessor(MyBeanPostProcessor postProcessor) {
        this.postProcessor = postProcessor;
    }
    public Object getWrappedInstance(){
        return this.wrapperInstance;
    }
}
