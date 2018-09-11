package com.qiwenfei.myspring.demo.action;

import com.qiwenfei.myspring.formework.annotation.MyController;
import com.qiwenfei.myspring.formework.annotation.MyRequestMapping;
import com.qiwenfei.myspring.formework.annotation.MyRequestParam;
import com.qiwenfei.myspring.formework.annotation.MyService;
import com.qiwenfei.myspring.formework.webmvc.MyModeAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 2018/9/6
 * sunshine
 */

@MyController
@MyRequestMapping("/web")
public class MyFirstAction {

    @MyRequestMapping("/say*.json")
    public MyModeAndView  say(HttpServletRequest request, HttpServletResponse response,
                              @MyRequestParam String msg){

        System.out.println("您说了："+msg);

        return  out(response,msg);
    }

    private MyModeAndView  out(HttpServletResponse response,String msg){

        try {
            response.getWriter().write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
           return null ;
    }


}
