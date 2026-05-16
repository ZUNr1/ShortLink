package org.zunr1.backend.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zunr1.backend.service.LinkService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/link")
public class LinkController {
    @Autowired
    private LinkService linkService;
    @GetMapping("/{shortCode}")
    public void redirect(@PathVariable String shortCode, HttpServletResponse response) throws IOException {
        String longUrl = linkService.getLongUrl(shortCode);
        if (longUrl != null){
            response.sendRedirect(longUrl);//502跳转
        }else {
            response.sendError(HttpStatus.NOT_FOUND.value(),"短链不存在");
        }

    }

    @PostMapping("/switch")
    public ResponseEntity<String> switchUrl(@RequestBody Map<String,String> body){
        String url = body.get("url");
        if (url == null || url.isEmpty()){
            return ResponseEntity.badRequest().body("url不能为空");
        }
        String shortCode = linkService.switchUrl(url);
        return ResponseEntity.ok(shortCode);

    }

}
