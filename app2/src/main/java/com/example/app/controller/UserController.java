package com.example.app.controller;


import com.example.app.model.User;
import com.example.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

    private final UserService service;
    @GetMapping("/allUsers")
    public @ResponseBody Iterable<User> getAllUsers(){
        return service.getAll();
    }

    @GetMapping("/getUserById")
    public @ResponseBody Optional<User> getUserById(@RequestParam Long _id){
        return service.getUser(_id);
    }

    //登录
    @PostMapping("/signIn")
    public @ResponseBody User signIn(@RequestBody User user) {
        if (service.existsByName(user)) {
            return service.findByNameAndPassword(user);
        }
        return null;
    }

    //注册
    @PostMapping("/signUp")
    public @ResponseBody boolean signUp(@RequestBody User user) {
        if (service.existsByName(user)){
             return false;
        }else {
            return service.insert(user);
        }
    }

    @PutMapping("update")
    public @ResponseBody boolean update(@RequestBody User user) {
        return service.update(user);
    }

    @DeleteMapping("delete")
    public @ResponseBody boolean delete(@RequestBody User user) {
        return service.delete(user);
    }
}
