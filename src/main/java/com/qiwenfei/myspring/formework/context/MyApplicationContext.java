package com.qiwenfei.myspring.formework.context;

import com.qiwenfei.myspring.formework.annotation.MyAutowired;
import com.qiwenfei.myspring.formework.annotation.MyController;
import com.qiwenfei.myspring.formework.annotation.MyService;
import com.qiwenfei.myspring.formework.aop.MyAopConfig;
import com.qiwenfei.myspring.formework.beans.MyBeanDefinition;
import com.qiwenfei.myspring.formework.beans.MyBeanDefinitionReader;
import com.qiwenfei.myspring.formework.beans.MyBeanWrapper;
import com.qiwenfei.myspring.formework.core.MyBeanFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 2018/9/7
 * sunshine
 */
public class MyApplicationContext extends MyDefaultListableBeanFactory implements MyBeanFactory {

   private String[] configLocations ;

   private MyBeanDefinitionReader  beanDefinitionReader ;
   
   //保存包装的bean的单例实例
   private Map<String,MyBeanWrapper> beanWrapperMap = new ConcurrentHashMap<String, MyBeanWrapper>();

    public MyApplicationContext(String[] configLocations) {
        this.configLocations = configLocations;
        refresh();
    }

    public void refresh() {

        //定位
        this.beanDefinitionReader = new MyBeanDefinitionReader(this.configLocations);

        //加载
        List<String> beanDefinitions = beanDefinitionReader.loadBeanDefinitons();

        //注册
        doRegistry(beanDefinitions);

        //依赖注入
        doAutowrited();
    }

    private void doAutowrited() {

        //根据BeanDefinition实例化Bean
        for (Map.Entry<String,MyBeanDefinition> entry:
             this.beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            if(!entry.getValue().isLazyInit()){
               Object obj = getBean(beanName);
            }
        }

        //对包装的对象进行依赖注入
        for (Map.Entry<String,MyBeanWrapper> entry:
                   beanWrapperMap.entrySet()) {
            populateBean(entry.getKey(),entry.getValue().getOriginalInstance());
        }

    }

    private void populateBean(String beanname, Object originalInstance) {

        Class<?> clazz = originalInstance.getClass();

        if(!(clazz.isAnnotationPresent(MyController.class)
                ||clazz.isAnnotationPresent(MyService.class))){
               return ;
        }
        Field[] fields = clazz.getFields();
        for (Field field:fields
             ) {
            if(!field.isAnnotationPresent(MyAutowired.class)){
                continue;
            }
            MyAutowired myAutowired = field.getAnnotation(MyAutowired.class);
            String autowireBeanName = myAutowired.value().trim();
            if("".equals(autowireBeanName)){
                autowireBeanName = field.getType().getName() ;
            }
            field.setAccessible(true);

            try {
                field.set(originalInstance,this.beanWrapperMap.get(autowireBeanName).getWrapperInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    public Object getBean(String beanName) {
        Object  bean = new Object();

        MyBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);

        //前置通知事件AOP

        //实例化bean
        try {
            bean = instantionBean(beanDefinition);

            //对bean经行包装

            MyBeanWrapper  beanWrapper = new MyBeanWrapper(bean);
            beanWrapper.setAopConfig(instantionAopConfig(beanDefinition));

            this.beanWrapperMap.put(beanName,beanWrapper);
            return  this.beanWrapperMap.get(beanName).getWrapperInstance() ;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null ;
    }

    private MyAopConfig instantionAopConfig(MyBeanDefinition beanDefinition) throws Exception{
        MyAopConfig aopConfig = new MyAopConfig();

        String expression =beanDefinitionReader.getConfig().getProperty("pointCut");
        String []before = beanDefinitionReader.getConfig().getProperty("aspectBefore").split("\\s");
        String []after = beanDefinitionReader.getConfig().getProperty("aspectAfter").split("\\s");

        String className = beanDefinition.getBeanClassName();
        Class<?> clazz = Class.forName(className);

        Pattern  pattern = Pattern.compile(expression);
        Class<?> aspectClass = Class.forName(before[0]);

        for (Method m: clazz.getMethods()
             ) {
            Matcher  matcher = pattern.matcher(m.getName());
            if(matcher.matches()){
                //将满足切面规则的类方法保存在aopconfig
                aopConfig.put(m,aspectClass.newInstance(),
                        new Method[]{aspectClass.getMethod(before[1]),aspectClass.getMethod(after[1])});
            }
        }

        return aopConfig ;
    }

    private Object instantionBean(MyBeanDefinition beanDefinition) {
        Object bean = new Object();

       return bean ;
    }

    private void doRegistry(List<String> beanDefinitions) {

        try {
            for (String className: beanDefinitions
                 ) {
                Class<?> beanClass = Class.forName(className);
                if(beanClass.isInterface()){
                    continue;
                }
               MyBeanDefinition beanDefinition = beanDefinitionReader.registerBeanDefinition(className);
               if(null !=beanDefinition && !beanDefinitionMap.containsKey(beanDefinition)){
                       synchronized (this.beanDefinitionMap){
                           beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
                       }
               }
              Class<?>[] interfaces  =   beanClass.getInterfaces();

                for (Class i:interfaces
                     ) {
                    this.beanDefinitionMap.put(i.getName(),beanDefinition);
                }
               


            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }


}
