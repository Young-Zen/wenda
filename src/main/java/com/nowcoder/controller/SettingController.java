package com.nowcoder.controller;

import com.nowcoder.service.WendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SettingController {
    @Autowired
    WendaService wendaService;

    @RequestMapping(path={"/setting"},method = {RequestMethod.GET})
    @ResponseBody
    public String setting(){
        return "setting ok. "+wendaService.getMessage(1);
    }
}
