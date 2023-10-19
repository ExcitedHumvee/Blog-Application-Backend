package com.stanydesa.blog.application.user.controller;

import com.stanydesa.blog.application.user.service.UserService;
import com.stanydesa.blog.domain.user.User;
import com.stanydesa.blog.domain.user.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;

@Slf4j//Simple Logging Facade for Java
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/api/users")
    public ModelAndView signUp(@RequestBody SignUpRequest request, HttpServletRequest httpServletRequest) {// (1/5)
        //1 (record SignUpReqest)input controller
        //2 check if user already there in DB
        //3 create new User and save (to entity User)
        //4 redirect to login controller (to record LoginRequest)
        //5 check if username and password ok, create Token and return (to record UserVO to record UserRecord)

        System.out.println(httpServletRequest.getRequestURI());
        userService.signUp(request);
        //token not generated here
        // Redirect to login API to automatically login when signup is complete
        LoginRequest loginRequest = new LoginRequest(request.email(), request.password());
        httpServletRequest.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        //ModelAndView(view, model) used to redirect to other endpoints
        //ModelAndView is a container for both the model and view in the MVC pattern
        return new ModelAndView("redirect:/api/users/login", "user", Map.of("user", loginRequest));
    }

    @ResponseStatus(CREATED)
    @PostMapping("/api/users/login")
    public UserRecord login(@RequestBody LoginRequest request) {//4
        UserVO userVO = userService.login(request);
        return new UserRecord(userVO);
    }

    @GetMapping("/api/user")
    public UserRecord getCurrentUser(User me) {
        UserVO userVO = new UserVO(me);
        return new UserRecord(userVO);
    }
}
