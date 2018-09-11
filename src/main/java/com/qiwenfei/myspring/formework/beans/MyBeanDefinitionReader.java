package com.qiwenfei.myspring.formework.beans;

import com.sun.jndi.toolkit.url.UrlUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 2018/9/7
 * sunshine
 */
//查找、读取、解析配置文件
public class MyBeanDefinitionReader {

     private Properties  config = new Properties();

     private List<String> registerBeanClasses = new ArrayList<String>();
     //扫描包路径key
     private final String SCAN_PACKAGE = "scanPackage";

    public MyBeanDefinitionReader(String... locations) {

        InputStream is = getClass().getClassLoader()
                .getResourceAsStream(locations[0].replace("classpath:",""));

        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (null !=is){is.close();}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

      doScanner(config.getProperty(SCAN_PACKAGE));
    }

    public List<String>loadBeanDefinitons(){return this.registerBeanClasses ;}

    public Properties getConfig() {
        return config;
    }

    /**
     * 扫描指定包路径下所有class,并保存在List（registerBeanClasses）中
     * @param packageName
     */
    private void doScanner(String packageName) {

        URL  url = getClass().getClassLoader()
                .getResource("/"+packageName.replaceAll("\\.","/"));

        File classDir = new File(((URL) url).getFile());
        for (File file:classDir.listFiles()) {
            if(file.isDirectory()){
                doScanner(packageName+"."+file.getName());
            }else{
                registerBeanClasses.add(packageName+"."+file.getName().replace(".class",""));
            }
        }
    }
    //这里只实例化BeanDefinition
    public  MyBeanDefinition registerBeanDefinition(String classname){



        if(registerBeanClasses.contains(classname)){
            MyBeanDefinition beanDefinition = new MyBeanDefinition();
            beanDefinition.setBeanClassName(classname);
            beanDefinition.setFactoryBeanName(lowerFirstCase(classname.substring(classname.lastIndexOf(".")+1)));
            return beanDefinition ;
        }

        return  null ;
    }


    private String lowerFirstCase(String str){
        char [] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
