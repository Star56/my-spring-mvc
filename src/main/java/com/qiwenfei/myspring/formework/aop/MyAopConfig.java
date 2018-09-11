package com.qiwenfei.myspring.formework.aop;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 2018/9/11
 * sunshine
 */
//封装application中的expresssion表达式
public class MyAopConfig {

    //Method方法切入MyAopAspect
    private Map<Method,MyAopAspect> aopAspectMap = new ConcurrentHashMap<Method, MyAopAspect>();

    public  void put(Method target, Object aspect,Method[]points){
        aopAspectMap.put(target,new MyAopAspect(aspect, points));
    }

    public MyAopAspect get(Method target){

        return aopAspectMap.get(target);
    }

    public boolean contains(Method target){
        return aopAspectMap.containsKey(target);
    }

    //切面点
    public class MyAopAspect {

        private Object aspect ;//切面
        private Method[] points ;//切入方法before、after等

        public MyAopAspect(Object aspect, Method[] points) {
            this.aspect = aspect;
            this.points = points;
        }

        public Object getAspect(){return this.aspect;}

        public Method[] getPoints(){return this.points;}
    }
}
