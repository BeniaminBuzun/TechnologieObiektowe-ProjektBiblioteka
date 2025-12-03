package pl.agh.edu.library.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LibraryService {
    @GetMapping("/")
    public String hello() {
        return "Main Library page";
    }

}
