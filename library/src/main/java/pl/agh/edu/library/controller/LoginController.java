package pl.agh.edu.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.agh.edu.library.dto.LoginRequestDto;
import pl.agh.edu.library.model.User;
import pl.agh.edu.library.repository.UserRepository;
import pl.agh.edu.library.security.JwtUtil;

import java.util.Map;

@RestController
@RequestMapping("/account")
public class LoginController {
    private final JwtUtil jwtUtil;
    private UserRepository userRepository;

    @Autowired
    public LoginController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;

    }
    @PostMapping("/login")
    public String addUser(@RequestBody LoginRequestDto loginRequest) {
        if(userRepository.existsByUserName(loginRequest.userName)){
            User user = userRepository.findByUserName(loginRequest.userName);
            if (user.chekcPassword(loginRequest.password)) {
                return jwtUtil.generateToken(loginRequest.userName,user.getRole());
            }
        }
        return "wrong username or password";
    }
    //    public Map<String, String> token(@ (name = "user", defaultValue = "guest") String user) {
    //        String token = jwtUtil.generateToken(user);
    //        return Map.of("token", token);
    //    }


}
