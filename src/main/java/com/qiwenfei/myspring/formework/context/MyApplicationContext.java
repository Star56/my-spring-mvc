package com.qiwenfei.myspring.formework.context;

import com.qiwenfei.myspring.formework.annotation.MyAutowired;
import com.qiwenfei.myspring.formework.annotation.MyController;
import com.qiwenfei.myspring.formework.annotation.MyService;
import com.qiwenfei.myspring.formework.aop.MyAopConfig;
import com.qiwenfei.myspring.formework.beans.MyBeanDefinition;
import com.qiwenfei.myspring.formework.beans.MyBeanDefinitionReader;
import com.qiwenfei.myspring.formework.beans.MyBeanPostProcessor;
import com.qiwenfei.myspring.formework.beans.MyBeanWrapper;
import com.qiwenfei.myspring.formework.core.MyBeanFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

    //用来保证注册式单例的容器
    private Map<String,Object> beanCacheMap = new HashMap<String, Object>();
   
   //保存包装的bean的单例实例
   private Map<String,MyBeanWrapper> beanWrapperMap = new ConcurrentHashMap<String, MyBeanWrapper>();

    public MyApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        refresh();
    }

    public void refresh() {

        //定位
        this.beanDefinitionReader = new MyBeanDefinitionReader(this.configLocations);

        //加载 获取之前扫描得到的class集合
        List<String> beanDefinitions = beanDefinitionReader.loadBeanDefinitons();

        //注册  根据class获取封装的beanDefinition,都存放在Map<String,MyBeanDefinition> beanDefinitionMap
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

        MyBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);

        String className = beanDefinition.getBeanClassName();

        try{

            //生成通知事件
            MyBeanPostProcessor beanPostProcessor = new MyBeanPostProcessor();

            Object instance = instantionBean(beanDefinition);
            if(null == instance){ return  null;}

            //在实例初始化以前调用一次
            beanPostProcessor.postProcessBeforeInitialization(instance,beanName);

            MyBeanWrapper beanWrapper = new MyBeanWrapper(instance);
            beanWrapper.setAopConfig(instantionAopConfig(beanDefinition));
            beanWrapper.setPostProcessor(beanPostProcessor);
            this.beanWrapperMap.put(beanName,beanWrapper);

            //在实例初始化以后调用一次
            beanPostProcessor.postProcessAfterInitialization(instance,beanName);

//            populateBean(beanName,instance);

            //通过这样一调用，相当于给我们自己留有了可操作的空间
            return this.beanWrapperMap.get(beanName).getWrappedInstance();
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
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
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        try{

            //因为根据Class才能确定一个类是否有实例
            if(this.beanCacheMap.containsKey(className)){
                instance = this.beanCacheMap.get(className);
            }else{
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.beanCacheMap.put(className,instance);
            }

            return instance;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
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
    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public Properties getConfig(){
        return this.beanDefinitionReader.getConfig();
    }
}
