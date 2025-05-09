package com.wztc.demo.controller;

import com.wztc.demo.common.NoTokenAccess;
import com.wztc.demo.common.Response;
import com.wztc.demo.entity.AiChat.Movie;
import com.wztc.demo.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@RestController
@RequestMapping("/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    @GetMapping("/chat")
    @NoTokenAccess
    public void handleSse(String model, String message, HttpServletResponse response) {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("utf-8");
        try (PrintWriter pw = response.getWriter()) {
            aiService.getAiResultStream(pw, message, model);
            pw.write("data:end\n\n");
            pw.flush();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/recommend")
    @NoTokenAccess
    public Response getRecommendList(String message) {
        try {
            List<Movie> prelist = aiService.getAiRecMovieList(message);
            List<Movie> finalist = aiService.handleMovieList(prelist);
            return Response.success(finalist);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
