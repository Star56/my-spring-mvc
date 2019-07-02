package com.qiwenfei.myspring.demo.action;

import com.qiwenfei.myspring.formework.annotation.MyController;
import com.qiwenfei.myspring.formework.annotation.MyRequestMapping;
import com.qiwenfei.myspring.formework.annotation.MyRequestParam;
import com.qiwenfei.myspring.formework.webmvc.MyModelAndView;

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
    public MyModelAndView say(HttpServletRequest request, HttpServletResponse response,
                              @MyRequestParam("msg") String msg){

        System.out.println("您说了："+msg);

        return  out(response,msg);
    }

    private MyModelAndView out(HttpServletResponse response, String msg){

        try {
            response.getWriter().write(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
           return null ;
    }


}
