package com.itmk.tool;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

// JWT工具类
@Component
@Data
@ConfigurationProperties(prefix = "jwt")
public class Utils {
    // JWT颁发者配置
    private String issuer;
    // JWT签名密钥配置
    private String secret;
    // JWT过期时间配置（分钟）
    private int expiration;

    /**
     * 验证令牌是否合法（仅校验签名和过期）
     *
     * @param token 待验证的token
     * @return boolean 验证结果
     */
    public boolean verify(String token) {
        try {
            // 使用HMAC256算法和密钥进行验证
            JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
        } catch (JWTVerificationException e) {
            // token验证失败，如签名错误或已过期
            return false;
        }
        return true;
    }




    /**
     * 从Token中获取指定的Claim声明值
     *
     * @param token     待解析的token
     * @param claimName Claim的名称
     * @return String Claim的值
     */
    public String getClaim(String token, String claimName) {
        try {
            DecodedJWT decodedJWT = jwtDecode(token);
            return decodedJWT.getClaim(claimName).asString();
        } catch (Exception e) {
            // 在解析失败或claim不存在时返回null
            return null;
        }
    }

    /**
     * 从Token中获取所有的Claim声明
     *
     * @param token 待解析的token
     * @return Map<String, Object> 包含所有Claim的Map
     */
    public Map<String, Object> getAllClaims(String token) {
        try {
            DecodedJWT decodedJWT = jwtDecode(token);
            // 将 com.auth0.jwt.interfaces.Claim 转换为常规的 Java 对象
            return decodedJWT.getClaims().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                        Claim claim = entry.getValue();
                        if (claim.asString() != null) return claim.asString();
                        if (claim.asInt() != null) return claim.asInt();
                        if (claim.asLong() != null) return claim.asLong();
                        if (claim.asDouble() != null) return claim.asDouble();
                        if (claim.asBoolean() != null) return claim.asBoolean();
                        if (claim.asDate() != null) return claim.asDate();
                        // 更多类型可以根据需要添加
                        return null;
                    }));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 检查Token是否已过期（不验证签名）
     *
     * @param token 待检查的token
     * @return boolean true表示已过期, false表示未过期或无过期时间
     */
    public boolean isTokenExpired(String token) {
        try {
            // decode仅解码不验证，以避免因签名错误等问题抛出异常
            Date expiresAt = JWT.decode(token).getExpiresAt();
            // 如果过期时间在当前时间之前，则判定为已过期
            return expiresAt != null && expiresAt.before(new Date());
        } catch (Exception e){
            // 对于格式错误的token，也视作无效/过期
            return true;
        }
    }

    /**
     * 从Token中获取过期时间
     *
     * @param token 待解析的token
     * @return Date 过期时间，若无则返回null
     */
    public Date getExpirationDate(String token) {
        try {
            return JWT.decode(token).getExpiresAt();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 生成具有自定义过期时间的JWT token
     *
     * @param map                 自定义payload参数
     * @param expirationInMinutes 过期时间（分钟）
     * @return String 生成的token
     */
    public String generateTokenWithCustomExpiration(Map<String, String> map, int expirationInMinutes) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE, expirationInMinutes);

        JWTCreator.Builder builder = JWT.create();
        map.forEach(builder::withClaim);

        return builder.withIssuer(issuer)
                .withIssuedAt(new Date())
                .withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 生成JWT token
     * @param map 自定义payload参数
     * @return String 生成的token
     */
    public String generateToken(Map<String, String> map) {
        // 设置token过期时间
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MINUTE, expiration);

        // 创建JWT Builder并设置payload
        JWTCreator.Builder builder = JWT.create();
        map.forEach((k, v) -> {
            builder.withClaim(k, v);
        });

        // 设置颁发者、签发时间、过期时间并签名
        String token = builder.withIssuer(issuer)
                .withIssuedAt(new Date())
                .withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256(secret)); // 使用配置的密钥签名
        return token;
    }

    /**
     * 解析token，并处理常见的JWT验证异常
     *
     * @param token 待解析的token
     * @return DecodedJWT 解析后的JWT对象
     */
    public DecodedJWT jwtDecode(String token) {
        try {
            // 使用密钥和HMAC256算法解析并验证token
            return JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
        } catch (SignatureVerificationException e) {
            // 签名验证失败
            throw new RuntimeException("token签名");
        } catch (AlgorithmMismatchException e) {
            // 算法不匹配
            throw new RuntimeException("token算法");
        } catch (TokenExpiredException e) {
            // token已过期
            throw new RuntimeException("token过期");
        } catch (Exception e) {
            // 其他解析或验证失败的情况
            throw new RuntimeException("token解析");
        }
    }
}