package com.sharespace.sharespace_server.product.controller;

import com.sharespace.sharespace_server.global.response.BaseResponse;
import com.sharespace.sharespace_server.product.dto.ProductRegisterRequest;
import com.sharespace.sharespace_server.product.service.ProductService;
//import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/register")
    public BaseResponse<Void> register(@Valid @RequestBody ProductRegisterRequest request) {
//            , HttpServletRequest httpRequest) {
//        Long userId = extractUserId(httpRequest);
        Long userId = 1L;
        return productService.register(request, userId);
    }
}
