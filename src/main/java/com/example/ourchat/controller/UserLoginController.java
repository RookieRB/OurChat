package com.example.ourchat.controller;

import com.example.ourchat.context.BaseContext;
import com.example.ourchat.exception.SendEmailCodeFailedException;
import com.example.ourchat.utils.CodeGeneratorUtil;
import com.example.ourchat.utils.JwtUtil;
import com.example.ourchat.constant.JWtClaimsConstant;
import com.example.ourchat.constant.MessageConstant;
import com.example.ourchat.dto.UserDTO;
import com.example.ourchat.dto.UserRegisterDTO;
import com.example.ourchat.entity.User;
import com.example.ourchat.property.JwtProperties;
import com.example.ourchat.result.Result;
import com.example.ourchat.service.UserService;
import com.example.ourchat.utils.MailUtils;
import com.example.ourchat.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/user/login")
@Slf4j
public class UserLoginController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private MailUtils mailUtils;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;



    // 用户登录请求
    @PostMapping("/login")
    public Result<UserLoginVO> userLogin(@RequestBody UserDTO userDTO){
        User currentUser = userService.userLogin(userDTO);

        // 登录成功后，生成jwt令牌
        Map<String,Object> claims = new HashMap<>();
        claims.put(JWtClaimsConstant.USER_ID,currentUser.getUserId());
        String accessToken = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims
        );
        // 生成刷新token（有效期更长）
        String refreshToken = JwtUtil.createRefreshJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl() * 7, // 7倍于访问token的有效期
                claims
        );
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .userId(currentUser.getUserId())
                .username(currentUser.getUsername())
                .nickname(currentUser.getNickname())
                .imgUrl(currentUser.getImgUrl())
                .email(currentUser.getEmail())
                .token(accessToken)
                .refreshToken(refreshToken)
                .phone(currentUser.getPhone())
                .lastLogin(currentUser.getLastLogin())
                .phone(currentUser.getPhone())
                .isActive(2)
                .build();

        return Result.success(userLoginVO);
    }
    // 发送邮箱验证码
    @GetMapping("/sendEmail/{email}")
    public Result<String> sendCode(@PathVariable String email){
        try{
            mailUtils.sendSimpleMail(email,"邮箱验证码", CodeGeneratorUtil.generateCode(6));
        }catch (Exception e){
            throw new SendEmailCodeFailedException(MessageConstant.SEND_EMAIL_CODE_FAILED);
        }

        return Result.success("验证码发送成功");
    }



    // 用户注册
    @PostMapping("/register")
    public Result<String> userRegister(@RequestBody UserRegisterDTO registerUser){
        if(
                registerUser.getUsername().isEmpty()
                || registerUser.getPassword().isEmpty()
                || registerUser.getEmail().isEmpty()
                || registerUser.getConfirmCode().isEmpty()
        ){
            return Result.error(MessageConstant.INFO_NOT_FULL);
        }

        // 验证用户是否已经被注册
        Boolean isUserExist = userService.isUserExist(registerUser.getUsername());

        if(isUserExist){
            return Result.error(MessageConstant.USER_ALREADY_EXIST);
        }
        // 查询验证码是否正确
        String confirmCode = registerUser.getConfirmCode();
        String codeInRedis = redisTemplate.opsForValue().get(registerUser.getEmail());

        if(Objects.equals(confirmCode, codeInRedis)){
            // 注册用户
            User user = User.builder()
                    .username(registerUser.getUsername())
                    .password(registerUser.getPassword())
                    .email(registerUser.getEmail())
                    .nickname(registerUser.getUsername())
                    .isActive(0)
                    .imgUrl("localhost:8080/img/avatar.png")
                    .lastLogin(new Date())
                    .build();
            boolean isTrue = userService.register(user);
            if(isTrue){
                return Result.success(MessageConstant.REGISTER_SUCCESS);
            }else{
                return Result.error(MessageConstant.REGISTER_ERROR);
            }
        }
        return Result.error(MessageConstant.CODE_ERROR);
    }

}
