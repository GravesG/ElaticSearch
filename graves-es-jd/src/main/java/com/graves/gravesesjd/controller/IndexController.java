package com.graves.gravesesjd.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Graves
 * @DESCRIPTION 首页
 * @create 2020/6/11
 */
@Controller
public class IndexController {

    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }
}
