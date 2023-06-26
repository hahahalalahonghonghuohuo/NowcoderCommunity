package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

// 这两个注解是 Spring MVC 的注解
@Controller
// 给类取一个访问的名字，通过 url 访问，浏览器就通过 alpha 这个名字访问这个 AlphaController 类
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    // sayHello() 方法能够被浏览器访问到的前提就是，前面也需要加上 @RequestMapping("/hello") 注解(给方法取一个访问路径 hello)
    @RequestMapping("/hello")
    // 如果不加 @ResponseBody 注解，浏览器会认为该方法返回的是一个网页而不是一个字符串
    @ResponseBody
    public String sayHello() {
        return "Hello SpringBoot";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData() {
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) {
        // 获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration =  request.getHeaderNames();

        // 消息头
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ": " + value);
        }
        System.out.println(request.getParameter("code"));

        // 返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try(
                PrintWriter writer = response.getWriter();
        ) {
            writer.write("<hi>牛客网</hi>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // GET 请求的处理方式

    // /students ? current = 1 & limit = 20
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1")int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10")int limit) {
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    // /student/123
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id) {
        System.out.println(id);
        return "a student";
    }

    // POST 请求
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    // 向浏览器响应一个动态的HTML数据

    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("name", "张三");
        mav.addObject("age", 30);
        mav.setViewName("demo/view");
        return mav;
    }

    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model) {
        model.addAttribute("name", "南京航空航天大学");
        model.addAttribute("age", 70);
        return "/demo/view";
    }

    // 向浏览器响应 JSON 数据 (通常用在异步请求中)
    // Java 对象 -> JSON 字符串 -> JS 对象

    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp() {
        Map<String, Object> emp = new HashMap();
        emp.put("name", "张三");
        emp.put("age", "23");
        emp.put("salary", 8000.00);
        return emp;
    }

    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps() {
        List<Map<String, Object>> list = new ArrayList();

        HashMap<String, Object> emp = new HashMap();
        emp.put("name", "张三");
        emp.put("age", "23");
        emp.put("salary", 8000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "李四");
        emp.put("age", "26");
        emp.put("salary", 8500.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "王五");
        emp.put("age", "27");
        emp.put("salary", 9000.00);
        list.add(emp);

        return list;
    }

    // cookie 示例

    @RequestMapping(path = "/cookie/set", method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response) {
        // 创建 cookie
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        // 设置 cookie 的生效范围
        cookie.setPath("/community/alpha");
        // 设置 cookie 的生存时间
        // cookie 默认发给浏览器后，浏览器关闭就丢失了
        cookie.setMaxAge(60 * 10);
        // 发送 cookie
        response.addCookie(cookie);
        return "set cookie";
    }

    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    public String getCookie(@CookieValue("code") String code) {
        System.out.println(code);
        return "get cookie";
    }

    // session 示例
    @RequestMapping(path = "/session/set", method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session) {
        // session 可以存储任何类型的数据
        session.setAttribute("id", 1);
        session.setAttribute("name", "Test");
        return "set session";
    }

    @RequestMapping(path = "/session/get", method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session) {
        // 从 session 中取值
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session";
    }

    // ajax 示例(小示例写在AlphaController中)
    // 异步请求不返回网页，而是返回字符串
    @RequestMapping(path = "/ajax", method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        // 注意这里返回的不是 map
        // 而后在网页上写jQuery代码
        return CommunityUtil.getJSONString(0, "操作成功!");
    }
}
