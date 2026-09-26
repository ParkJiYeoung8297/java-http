package org.apache.coyote.http11.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.enums.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RegisterRequestHandler implements RequestHandler{
    private static final Logger log = LoggerFactory.getLogger(LoginRequestHandler.class);

    @Override
    public HttpResponse handle(HttpRequest httpRequest){
        final HttpResponse response = new HttpResponse();
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        String location = "/500.html";

        try{
            register(httpRequest.params());
            location = "/index.html";
            response.setStatus(HttpStatus.SEE_OTHER);
            log.info("계정 : {}, 이메일 : {} 회원가입 완료",
                    httpRequest.params().get("account"), httpRequest.params().get("email"));
        } catch (IllegalArgumentException e){
        }

        response.addHeader("Location", location);

        return response;
    }

    private void register(Map<String,String> params){
        if (InMemoryUserRepository.findByAccount(params.get("account")).isPresent()){
            throw new IllegalArgumentException("이미 가입한 회원");
        }

        InMemoryUserRepository.save(
                new User(params.get("account"), params.get("password"), params.get("email")));
    }
}
