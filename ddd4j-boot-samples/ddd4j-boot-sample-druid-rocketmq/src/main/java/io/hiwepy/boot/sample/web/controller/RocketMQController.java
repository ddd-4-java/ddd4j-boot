package io.hiwepy.boot.sample.web.controller;

import io.hiwepy.boot.api.ApiRestResponse;
import io.hiwepy.boot.sample.service.MQProducerService;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rocketmq")
public class RocketMQController {

    @Autowired
    private MQProducerService mqProducerService;

    @GetMapping("/send")
    public ApiRestResponse<String> send(String msgBody) {
        mqProducerService.send(msgBody);
        return ApiRestResponse.success("发送消息成功");
    }

    /**
     * 发送同步消息（阻塞当前线程，等待broker响应发送结果，这样不太容易丢失消息）
     * （msgBody也可以是对象，sendResult为返回的发送结果）
     */
    @GetMapping("/sendSyncMsg")
    public ApiRestResponse<SendResult> sendSyncMsg(String msgBody) {
        SendResult sendResult = mqProducerService.sendSyncMsg(msgBody);
        return ApiRestResponse.success(sendResult);
    }

    /**
     * 发送异步消息（通过线程池执行发送到broker的消息任务，执行完后回调：在SendCallback中可处理相关成功失败时的逻辑）
     * （适合对响应时间敏感的业务场景）
     */
    @GetMapping("/sendAsyncMsg")
    public ApiRestResponse<String> sendAsyncMsg(String msgBody) {
        mqProducerService.sendAsyncMsg(msgBody);
        return ApiRestResponse.success("发送异步消息成功");
    }

    /**
     * 发送延时消息（上面的发送同步消息，delayLevel的值就为0，因为不延时）
     * 在start版本中 延时消息一共分为18个等级分别为：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     */
    @GetMapping("/sendDelayMsg")
    public ApiRestResponse<String> sendDelayMsg(String msgBody, int delayLevel) {
        mqProducerService.sendDelayMsg(msgBody, delayLevel);
        return ApiRestResponse.success("发送延时消息成功");
    }

    /**
     * 发送单向消息（只负责发送消息，不等待应答，不关心发送结果，如日志）
     */
    @GetMapping("/sendOneWayMsg")
    public ApiRestResponse<String> sendOneWayMsg(String msgBody) {
        mqProducerService.sendOneWayMsg(msgBody);
        return ApiRestResponse.success("发送单向消息成功");
    }

    /**
     * 发送带tag的消息，直接在topic后面加上":tag"
     */
    @GetMapping("/sendTag")
    public ApiRestResponse<SendResult> sendTag() {
        SendResult sendResult = mqProducerService.sendTagMsg("带有tag的字符消息");
        return ApiRestResponse.success(sendResult);
    }


}