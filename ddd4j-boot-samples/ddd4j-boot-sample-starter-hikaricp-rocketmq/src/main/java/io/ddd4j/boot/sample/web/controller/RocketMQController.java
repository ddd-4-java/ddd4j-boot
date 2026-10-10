package io.ddd4j.boot.sample.web.controller;

import io.ddd4j.boot.sample.service.MQProducerService;
import io.ddd4j.boot.sample.web.dto.MessageDTO;
import io.ddd4j.core.ApiRestResponse;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RocketMQ 消息发送前端控制器，提供普通消息与带 tag 消息发送接口。
 */
@RestController
@RequestMapping("/rocketmq")
public class RocketMQController {

    /**
     * 构造 RocketMQ 前端控制器实例。
     */
    public RocketMQController() {
    }

    /**
     * 消息生产服务。
     */
    @Autowired
    private MQProducerService mqProducerService;

    /**
     * 发送一条普通消息。
     */
    @GetMapping("/send")
    public void send() {
        MessageDTO message = new MessageDTO();
        mqProducerService.send(message);
    }

    /**
     * 发送带 tag 的消息。
     *
     * @return 接口返回对象，携带发送结果
     */
    @GetMapping("/sendTag")
    public ApiRestResponse<SendResult> sendTag() {
        SendResult sendResult = mqProducerService.sendTagMsg("带有tag的字符消息");
        return ApiRestResponse.success(sendResult);
    }

}
