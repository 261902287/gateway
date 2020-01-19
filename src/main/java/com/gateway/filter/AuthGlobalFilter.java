package com.gateway.filter;

import net.sf.json.JSONObject;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 全局过滤器，不启动，因为在yml default-filters 配置了OAuth2GatewayFilterFactory的全局过滤器  - OAuth2= true
 */
//@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        System.out.println("Welcome to AuthGlobalFilter.");
        ServerHttpRequest request = exchange.getRequest();
        String method = request.getMethodValue();
        if(request.getHeaders().get("token") != null && request.getHeaders().get("token").size()>0){
            String token = request.getHeaders().get("token").get(0);
            System.out.println("token======"+token);
            if("1234".equals(token)) {
                return chain.filter(exchange);
            }
        }
        ServerHttpResponse response = exchange.getResponse();
        JSONObject message = new JSONObject();
        message.put("status", -1);
        message.put("data", "鉴权失败");
        byte[] bits = message.toString().getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
