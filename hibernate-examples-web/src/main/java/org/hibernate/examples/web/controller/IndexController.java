package org.hibernate.examples.web.controller;

import org.joda.time.DateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.WebAsyncTask;

import java.util.concurrent.Callable;

/**
 * IndexController
 * Created by debop on 2014. 3. 23.
 */
@Controller
@RequestMapping
public class IndexController extends AbstractAsyncController {

    @ResponseBody
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public WebAsyncTask<ResponseEntity<String>> index() {
        return callAsync(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "Hibernate-Example Web Application. response at " +
                       DateTime.now().toString("yyyy-MM-dd'T'hh:mm:ssZ");
            }
        });
    }
}
