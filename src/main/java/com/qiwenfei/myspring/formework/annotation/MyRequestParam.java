package com.qiwenfei.myspring.formework.annotation;

import java.lang.annotation.*;

/**
 * 2018/9/7
 * sunshine
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestParam {

    String value() default "";

    boolean required() default true ;
}
