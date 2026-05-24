package org.zunr1.backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.zunr1.backend.dto.LinkResponse;
import org.zunr1.backend.dto.Result;
import org.zunr1.backend.service.LinkService;
import org.zunr1.backend.utils.JwtUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/link")
public class LinkController {
    @Autowired
    private LinkService linkService;
    @Autowired
    private JwtUtil jwtUtil;

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
    public ResponseEntity<Result<String>> switchUrl(@RequestBody Map<String,String> body, HttpServletRequest request){
        String url = body.get("url");
        if (url == null || url.isEmpty()) {
            return ResponseEntity.badRequest().body(Result.error(400, "url为空"));
        }
        String name = body.get("name");
        if (name == null || name.isEmpty()){
            return ResponseEntity.badRequest().body(Result.error(400,"name为空"));
        }
        String expireAt = body.get("expire");//yyyy-MM-dd'T'HH:mm:ss

        // 从请求头获取 token 并解析 userId
        String authHeader = request.getHeader("Authorization");
        Long userId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            userId = jwtUtil.verify(token);  // 需要注入 JwtUtil
        }
        if (userId == null) {
            return ResponseEntity.status(401).body(Result.error(401, "未认证"));
        }

        String shortCode = linkService.switchUrl(url, name, expireAt, userId);
        return ResponseEntity.ok(Result.success(shortCode));
    }

    @GetMapping("/list")
    public ResponseEntity<Result<List<LinkResponse>>> getUserLinks(HttpServletRequest request) {
        // 从请求头获取 token 并解析 userId
        String authHeader = request.getHeader("Authorization");
        Long userId = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            userId = jwtUtil.verify(token);
        }

        if (userId == null) {
            return ResponseEntity.status(401).body(Result.error(401, "未认证"));
        }

        List<LinkResponse> links = linkService.getUserLinks(userId);
        return ResponseEntity.ok(Result.success(links));
    }



}
