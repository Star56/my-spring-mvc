package com.qiwenfei.myspring.formework.context;

/**
 * 2018/9/7
 * sunshine
 */
public abstract class MyAbstractApplicationContext {

    protected  void onRefresh(){
        //TODO: subclass to do somthing
    }

    public  abstract  void refreshBeanFactory() ;
}
