package com.qiwenfei.myspring.demo.aspect;

/**
 * 2018/9/11
 * sunshine
 */
public class MyLogAspect {

    public void before(){
        System.out.println("执行方法前，执行before切点");
    }

    public void after(){
        System.out.println("执行方法后，执行after切点");
    }
}
