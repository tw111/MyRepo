package com.imooc.mall.controller;

import com.imooc.mall.consts.MallConst;
import com.imooc.mall.form.UserLoginForm;
import com.imooc.mall.form.UserRegisterForm;
import com.imooc.mall.pojo.User;
import com.imooc.mall.service.IUserService;
import com.imooc.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/user/register")
    public ResponseVo<User> register(@Valid @RequestBody UserRegisterForm userRegisterForm){
        User user = new User();
        BeanUtils.copyProperties(userRegisterForm,user);
        //dto系统大，涉及的对象多
        return userService.register(user);
    }

    @PostMapping("/user/login")
    public ResponseVo<User> login(@Valid @RequestBody UserLoginForm userLoginForm,
                                  HttpSession session){
        ResponseVo<User> userResponseVo = userService.login(userLoginForm.getUsername(),userLoginForm.getPassword());

        //設置session
        session.setAttribute(MallConst.CURRENT_USER,userResponseVo.getData());
        log.info("/login sessionId={}",session.getId());

        return userResponseVo;
    }
    //  /user/  與 /user 不同
    // session保存在内存裏，改進版：token + redis
    @GetMapping("/user")
    public ResponseVo<User> userInfo(HttpSession session){
        log.info("/user sessionId={}",session.getId());
        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        return ResponseVo.success(user);
    }

    //TODO 判斷登陸狀態，攔截器
    @PostMapping("/user/logout")
    /**
     * {@link org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory}
     */
    public ResponseVo logout(HttpSession session){
        log.info("/user/logout/ sessionId={}",session.getId());
        session.removeAttribute(MallConst.CURRENT_USER);
        return ResponseVo.success();
    }
}
