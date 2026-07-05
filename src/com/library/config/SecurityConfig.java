package com.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
/**
 * Cấu hình mở khóa rào chắn bảo vệ của Spring Security cho phiên bản 6.2.x.
 * Giúp giao diện React Frontend gọi API sang Backend mượt mà không bị chặn lỗi 401 hoặc 403.
 */
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Kích hoạt cấu hình CORS mặc định để phối hợp với @CrossOrigin tại Controller
                // (Đặc biệt quan trọng để trình duyệt chấp nhận các request gửi từ port React sang port Spring Boot)
                .cors(Customizer.withDefaults())

                // 2. Vô hiệu hóa cơ chế chống giả mạo yêu cầu CSRF
                // (Bắt buộc phải tắt thì React mới gửi được các lệnh POST mượn/trả sách hoặc thêm mới độc giả)
                .csrf(csrf -> csrf.disable())

                // 3. Cấu hình phân quyền đường dẫn API theo chuẩn Lambda (Spring Security 6+)
                .authorizeHttpRequests(auth -> auth
                        // Cho phép tất cả các request (GET, POST, PUT, DELETE) đi qua tự do không cần đăng nhập
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}