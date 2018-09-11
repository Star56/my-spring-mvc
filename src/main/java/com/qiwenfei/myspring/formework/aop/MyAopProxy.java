package com.qiwenfei.myspring.formework.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 2018/9/11
 * sunshine
 */
public class MyAopProxy implements InvocationHandler {

    private Object target ;

    private MyAopConfig config ;

    public void setConfig(MyAopConfig config) {
        this.config = config;
    }

    public Object getProxy(Object instance){
        this.target = instance ;

        return Proxy.newProxyInstance(instance.getClass().getClassLoader(),
                instance.getClass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        Method m = this.getClass().getMethod(method.getName(),method.getParameterTypes());

        //前置切点before
        //当前取第一个切入方法为前置切点
        if(config.contains(m)){
           MyAopConfig.MyAopAspect aopAspect =  config.get(m);
           aopAspect.getPoints()[0].invoke(aopAspect);
        }

        //调用原来的方法
        Object obj =   m.invoke(this.target,args);

        //后置切入点after
        if(config.contains(m)){
            MyAopConfig.MyAopAspect aopAspect = config.get(m);
            aopAspect.getPoints()[1].invoke(aopAspect);
        }

        return obj;
    }
}
