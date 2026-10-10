package com.springAI.springaiquickstart.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
@Slf4j
public class ChartController {

    @Resource
    private DeepSeekChatModel deepSeekChatModel;  //与deep seek聊天对象

    @GetMapping("/generate")
    public String generate(@RequestParam(value = "message",defaultValue = "介绍一下你自己!") String message){
        String deepSeekResponse = deepSeekChatModel.call(message);
        log.info("deepSeekResponse: {}", deepSeekResponse);
        System.out.println("deepSeekResponse: " + deepSeekResponse);
        return deepSeekResponse;
    }

    /**
     * 流式对话接口：以文本流方式逐 token 输出，空格、换行、引号原样保留。
     * 不用 SSE（text/event-stream）的原因：SSE 协议规定客户端剥离 "data:" 后的
     * 一个空格，而 Spring 的 SSE 编码器对以空格开头的 token 不做保护，
     * 会导致代码缩进、单词间空格丢失；换行也会被拆成多个 data 行，前端易丢换行。
     * 前端用 fetch + ReadableStream 逐段解码追加即可。
     */
    @GetMapping(value = "/generate/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "介绍一下你自己!") String message) {
        log.info("stream request: {}", message);
        return deepSeekChatModel.stream(message);
    }
}
