package com.springAI.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chart")
public class ChartController {

    @Resource
    @Qualifier("ollamaChatModel")
    private ChatModel chatModel;

    @GetMapping(value = "/generate/stream", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "介绍下你自己!") String message, @RequestParam(value = "thinking", defaultValue = "true") boolean thinking) {
        // 是否思考由 thinking 参数控制，默认开启思考模式
        OllamaChatOptions.Builder optionsBuilder = OllamaChatOptions.builder()
                .model("qwen3:4b");
        if (thinking) {
            optionsBuilder.enableThinking();
        } else {
            optionsBuilder.disableThinking();
        }
        Prompt prompt = new Prompt(message, optionsBuilder.build());
        return chatModel.stream(prompt)
                .map(chatResponse -> {
                    String text = chatResponse.getResult() != null && chatResponse.getResult().getOutput() != null
                            ? chatResponse.getResult().getOutput().getText() : null;
                    return text != null ? text : "";
                })
                .filter(chunk -> !chunk.isEmpty());
    }
}
