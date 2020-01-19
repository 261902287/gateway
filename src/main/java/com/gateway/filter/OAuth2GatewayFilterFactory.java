package com.gateway.filter;

import net.sf.json.JSONObject;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Component
public class OAuth2GatewayFilterFactory extends AbstractGatewayFilterFactory<OAuth2GatewayFilterFactory.Config> {

    public OAuth2GatewayFilterFactory() {
        super(OAuth2GatewayFilterFactory.Config.class);
    }

    @Override
    public GatewayFilter apply(OAuth2GatewayFilterFactory.Config config) {
        return (exchange, chain) -> {

            System.out.println("welcome,I am OAuth2GatewayFilterFactory.");

            if (!config.isEnabled()) {
                return chain.filter(exchange);
            }

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
        };
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("enabled");
    }
    public static class Config {
        // 控制是否开启认证
        private boolean enabled;

        public Config() {}

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
