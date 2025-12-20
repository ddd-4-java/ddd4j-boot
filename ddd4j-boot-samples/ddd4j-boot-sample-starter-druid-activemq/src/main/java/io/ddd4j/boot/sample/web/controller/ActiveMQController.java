package io.ddd4j.boot.sample.web.controller;

import io.ddd4j.boot.sample.message.LogProducer;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActiveMQController {

    @Autowired
    private LogProducer logProducer;

    @GetMapping("/activemq/send")
    public String activemq(HttpServletRequest request, String msg) {
        msg = StringUtils.isEmpty(msg) ? "This is Empty Msg." : msg;

        try {
            logProducer.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Activemq has sent OK.";
    }


}
