package com.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * GlobalFilter ： 全局过滤器，对所有的路由均起作用
 * GatewayFilter ： 只对指定的路由起作用
 * 1、自定义GatewayFilter
 * 自定义GatewayFilter又有两种实现方式，一种是直接 实现GatewayFilter接口，另一种是 继承AbstractGatewayFilterFactory类 ,任意选一种即可
 */
@Component
public class CustomGatewayFilterFactory extends AbstractGatewayFilterFactory<CustomGatewayFilterFactory.Config>{

    public CustomGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            System.out.println("welcome,I am CustomGatewayFilterFactory.");

            if (!config.isEnabled()) {
                return chain.filter(exchange);
            }

            ServerHttpRequest request = exchange.getRequest();
            URI uri = request.getURI();
            System.out.println("url==============="+uri.getPath());
            return chain.filter(exchange);
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
