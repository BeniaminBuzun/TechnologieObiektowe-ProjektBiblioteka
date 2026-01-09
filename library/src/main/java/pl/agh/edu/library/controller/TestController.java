package pl.agh.edu.library.controller;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.agh.edu.library.security.JwtUtil;

import java.util.Map;


@RestController
public class TestController {

    private final JwtUtil jwtUtil;

    public TestController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // public endpoint that issues a token to anyone who asks
//    @GetMapping("/token")
//    public Map<String, String> token(@RequestParam(name = "user", defaultValue = "guest") String user) {
//        String token = jwtUtil.generateToken(user);
//        return Map.of("token", token);
//    }
    @GetMapping("/admin")
    public String Admin(){
        return "admin";
    }
    // protected endpoint, requires Authorization: Bearer <token>
    @GetMapping("/secure")
    public Map<String, String> secure(Authentication authentication) {
//        String name = authentication.toString();
        return Map.of(
                "message", "Hello " + "A" + " — you accessed a protected endpoint!",
                "user", "A"
        );
    }
}


