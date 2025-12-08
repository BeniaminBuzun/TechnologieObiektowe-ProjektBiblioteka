package pl.agh.edu.library.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.agh.edu.library.controller.UserController;
import pl.agh.edu.library.model.User;

@RestController
public class LibraryService {
    private final UserController userController;
    public LibraryService(UserController userController) {

        this.userController = userController;
    }
    @PostConstruct
    public void addExampleUser() {
        System.out.println("Adding example user");
    }

    @GetMapping("/")
    public String hello() {
        return "Main Library page";
    }

}
