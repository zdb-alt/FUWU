package com.course.controller;

import com.course.model.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.java.Log;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Log
@RestController
@Api(value = "v1",description = "用户管理系统")
@RequestMapping("v1")
public class UserManager {


    //首先获取一个执行sql语句的对象

    @Autowired
    private SqlSessionTemplate template;

    @ApiOperation(value = "登陆接口",httpMethod = "POST")
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public Boolean login(HttpServletResponse response, @RequestBody User user){
        int i  = template.selectOne("login",user);
        Cookie cookie = new Cookie("login","true");
        response.addCookie(cookie);
        log.info("查看到的结果是"+i);
        if(i==1){
            return true;
        }

        return false;
    }
    @ApiOperation(value = "添加用户接口",httpMethod = "POST")
    @RequestMapping(value = "/addUser",method = RequestMethod.POST)
    public boolean addUser(HttpServletRequest request,@RequestBody User user){
        Boolean x = verifyCookies(request);
        int result = 0;
        if(x != null){
            result = template.insert("addUser",user);
        }
        if(result>0){
            log.info("添加用户的数量是:"+result);
            return true;
        }
        return false;
    }

    private Boolean verifyCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies==null){
            log.info("cookies为空");
            return false;
        }
        for(Cookie cookie : cookies){
            if(cookie.getName().equals("login") &&
                    cookie.getValue().equals("true")){
                log.info("cookies验证通过");
                return true;
            }
        }
        return false;
    }

    @ApiOperation(value = "获取用户(列表)信息接口",httpMethod = "POST")
    @RequestMapping(value = "/getUser",method = RequestMethod.POST)
    public List<User> getUser(HttpServletRequest request,@RequestBody User user){
        Boolean x = verifyCookies(request);
        if(x==true){
            List<User> users = template.selectList("getUser",user);
            log.info("getUserInfo获取到的用户数量是" +users.size());
            return users;
        }else {
            return null;
        }
    }


    @ApiOperation(value = "更新/删除用户接口",httpMethod = "POST")
    @RequestMapping(value = "/updateUser",method = RequestMethod.POST)
    public int updateUser(HttpServletRequest request,@RequestBody User user){
        Boolean x = verifyCookies(request);
        int i = 0;
        if(x==true) {
            i = template.update("updateUser", user);
        }
        log.info("更新数据的条目数为:" + i);
        return i;

    }


}
