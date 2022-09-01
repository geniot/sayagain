package io.github.geniot.sayagain.exception;

import io.github.geniot.sayagain.gen.model.ApiErrorDto;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandlerController {

    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandlerController.class);

    @Autowired
    Environment env;

    @Autowired
    ModelMapper modelMapper;

    @Bean
    public ErrorAttributes errorAttributes() {
        // Hide exception field in the return object
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
                Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
//                if (Arrays.asList(env.getActiveProfiles()).contains("prod")) {
                errorAttributes.remove("exception");
                errorAttributes.remove("trace");
                errorAttributes.remove("stackTrace");
//                }
                return errorAttributes;
            }
        };
    }

    @ExceptionHandler(ApiError.class)
    public ResponseEntity<Object> handleApiError(final ApiError apiError) {
        return new ResponseEntity<>(modelMapper.map(apiError, ApiErrorDto.class), new HttpHeaders(), apiError.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public void handleException(Exception ex, HttpServletResponse res) throws IOException {
        logger.error(ex.getMessage(), ex);
        res.sendError(HttpStatus.BAD_REQUEST.value(), "Something went wrong");
    }

}
