package com.capstone.recycle.Service;

import com.capstone.recycle.DTO.request.LoginRequest;
import com.capstone.recycle.DTO.response.LoginResponse;
import com.capstone.recycle.Entity.Admin;
import com.capstone.recycle.Repository.AdminRepository;
import com.capstone.recycle.Security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        Admin admin = adminRepository.findByUsernameAndIsActiveTrue(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        // 마지막 로그인 시간 업데이트
        admin.setLastLoginAt(LocalDateTime.now());
        adminRepository.save(admin);

        String token = jwtUtil.generateToken(admin.getUsername());
        return new LoginResponse(token, admin.getUsername(), admin.getName(), admin.getRole());
    }
}
