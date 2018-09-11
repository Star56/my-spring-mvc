package com.qiwenfei.myspring.formework.webmvc;

import java.util.Map;

/**
 * 2018/9/7
 * sunshine
 */
public class MyModeAndView {

    private  String viewName ;

    private Map<String,?> model;

    public MyModeAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
}
