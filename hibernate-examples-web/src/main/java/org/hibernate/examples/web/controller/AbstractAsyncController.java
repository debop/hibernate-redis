package org.hibernate.examples.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.WebAsyncTask;

import java.util.concurrent.Callable;

/**
 * AbstractAsyncController
 * Created by debop on 2014. 3. 23.
 */
@Slf4j
public abstract class AbstractAsyncController {

    public <T> WebAsyncTask<ResponseEntity<T>> callAsync(final Callable<T> callable) {
        return new WebAsyncTask<ResponseEntity<T>>(new Callable<ResponseEntity<T>>() {
            @Override
            public ResponseEntity<T> call() throws Exception {
                try {
                    return success(callable.call());
                } catch (Exception e) {
                    log.error("메소드 실행에 실패했습니다.", e);
                    return serviceUnavailable();
                }
            }
        });
    }

    public <T> ResponseEntity<T> success(T body) {
        return new ResponseEntity<T>(body, HttpStatus.OK);
    }

    public <T> ResponseEntity<T> serviceUnavailable() {
        return new ResponseEntity<T>((T) null, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
