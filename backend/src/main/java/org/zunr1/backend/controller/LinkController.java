package org.zunr1.backend.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zunr1.backend.dto.LinkResponse;
import org.zunr1.backend.dto.Result;
import org.zunr1.backend.service.LinkService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/link")
public class LinkController {
    @Autowired
    private LinkService linkService;

    //跳转接口，返回302重定向
    @GetMapping("/{shortCode}")
    public void redirect(@PathVariable String shortCode, HttpServletResponse response) throws IOException {
        String longUrl = linkService.getLongUrl(shortCode);
        if (longUrl != null){
            response.sendRedirect(longUrl);//302跳转
        }else {
            response.sendError(HttpStatus.NOT_FOUND.value(),"短链不存在");
        }
    }
    //查询详细接口
    @GetMapping("/find/{shortCode}")
    public ResponseEntity<Result<LinkResponse>> getLinkByShortCode(@PathVariable String shortCode){
        LinkResponse response = linkService.getLinkByShortCode(shortCode);
        return ResponseEntity.ok(Result.success(response));
    }

    @PostMapping("/switch")
    public ResponseEntity<Result<String>> switchUrl(@RequestBody Map<String,String> body){
        String url = body.get("url");
        if (url == null || url.isEmpty()) {
            return ResponseEntity.badRequest().body(Result.error(400, "url为空"));
        }
        String name = body.get("name");
        if (name == null || name.isEmpty()){
            return ResponseEntity.badRequest().body(Result.error(400,"name为空"));
        }
        String expireAt = body.get("expire");//yyyy-MM-dd'T'HH:mm:ss
        //todo,user从jwt获取
        Integer userId = null;
        String shortCode = linkService.switchUrl(url,name,expireAt,userId);
        return ResponseEntity.ok(Result.success(shortCode));
    }



}
