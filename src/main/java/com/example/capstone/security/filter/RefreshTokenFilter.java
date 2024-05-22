package com.example.capstone.security.filter;


import com.example.capstone.apiPayload.ApiResponse;
import com.example.capstone.apiPayload.code.status.ErrorStatus;
import com.example.capstone.security.exception.TokenException;
import com.example.capstone.security.util.JWTUtil;
import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Log4j2
@RequiredArgsConstructor
public class RefreshTokenFilter extends OncePerRequestFilter {

    private final String refreshPath;

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (!path.equals(refreshPath)) {
            log.info("skip refresh token filter.....");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Refresh Token Filter.......................1");

        //accessToken과 refreshToken을 얻어온다.
        Map<String, String> tokens = null;
        try {
            tokens = parseRequestJSON(request);
        } catch (TokenException tokenException) {
            tokenException.sendResponseError(response);
            return;

        }

        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");


        try{
            checkAccessToken(accessToken);
        }catch(TokenException TokenException){
            TokenException.sendResponseError(response);
            return;
        }

        Map<String, Object> refreshClaims = null;

        try {

            refreshClaims = checkRefreshToken(refreshToken);
            log.info(refreshClaims);

        }catch(TokenException TokenException){
            TokenException.sendResponseError(response);
            return;
        }

        //Refresh Token의 유효시간이 얼마 남지 않은 경우
        Long exp = (Long)refreshClaims.get("exp");

        Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000);

        Date current = new Date(System.currentTimeMillis());

        //만일 3일 미만인 경우에는 Refresh Token 갱신
        long gapTime = (expTime.getTime() - current.getTime());

        log.info("-----------------------------------------");
        log.info("current: " + current);
        log.info("expTime: " + expTime);
        log.info("gap: " + gapTime );

        String loginId = (String)refreshClaims.get("loginId");
        Long id = Long.valueOf(String.valueOf(refreshClaims.get("id")));

        String accessTokenValue = jwtUtil.generateToken(Map.of("loginId", loginId,"id",id), 1);

        String refreshTokenValue = tokens.get("refreshToken");

        //RefrshToken이 3일 이내에 만료된다면
        if(gapTime < (1000 * 60 * 60 * 24 * 3  ) ){
            log.info("new Refresh Token required...  ");
            refreshTokenValue = jwtUtil.generateToken(Map.of("loginId", loginId,"id",id), 30);
        }

        log.info("Refresh Token result....................");

        sendTokens(accessTokenValue, refreshTokenValue, response);

    }

    private Map<String,String> parseRequestJSON(HttpServletRequest request) throws TokenException{

        Map<String,String> tokens = null;
        //JSON 데이터를 id와 password의 Map으로 변환
        try(Reader reader = new InputStreamReader(request.getInputStream())){

            Gson gson = new Gson();

            tokens = gson.fromJson(reader, Map.class);


        } catch(Exception e){
            log.error(e.getMessage());
            throw new RuntimeException();
        }

        if(tokens == null)
            throw new TokenException(ErrorStatus.TOKENS_NOT_ACCEPTED);

        return tokens;
    }

    private void checkAccessToken(String accessToken) throws TokenException {

        try{
            jwtUtil.validateToken(accessToken);
        }catch (ExpiredJwtException expiredJwtException){
            log.info("Access Token has expired");
        }catch(Exception exception){
            throw new TokenException(ErrorStatus.MALFORMED_ACCESS_TOKEN);
        }
    }

    private Map<String, Object> checkRefreshToken(String refreshToken) throws TokenException{

        try {

            return jwtUtil.validateToken(refreshToken);

        }catch(ExpiredJwtException expiredJwtException){
            throw new TokenException(ErrorStatus.EXPIRED_REFRESH_TOKEN);
        }catch(Exception exception){
            throw new TokenException(ErrorStatus.MALFORMED_REFRESH_TOKEN);
        }
    }

    private void sendTokens(String accessTokenValue, String refreshTokenValue, HttpServletResponse response) {


        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");

        Gson gson = new Gson();

        Map<String, String> tokens = Map.of("accessToken", accessTokenValue, "refreshToken", refreshTokenValue);

        String responseStr = gson.toJson(ApiResponse.onSuccess(tokens));

        try {
            response.getWriter().println(responseStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
















