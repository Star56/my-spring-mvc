package com.qiwenfei.myspring.formework.annotation;

import java.lang.annotation.*;

/**
 * 2018/9/7
 * sunshine
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAutowired {
    String value() default "";
}
