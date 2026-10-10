package com.springAI.controller;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/api/chartRemote")
public class ChartRemoteController {

    @Resource
    @Qualifier("deepseekChatModel")
    private ChatModel deepseekChatModel;

    @Resource
    @Qualifier("qwenChatModel")
    private ChatModel qwenChatModel;

    @GetMapping(value = "/generate/stream", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public Flux<String> generateStream(@RequestParam(value = "message", defaultValue = "介绍下你自己!") String message, @RequestParam(value = "model", defaultValue = "qwen") String model, @RequestParam(value = "thinking", defaultValue = "true") boolean thinking) {
        ChatModel target = "deepseek".equalsIgnoreCase(model) ? deepseekChatModel : qwenChatModel;
        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .extraBody(Map.of("enable_thinking", thinking))
                .build();
        //部分模型会使用英文思考，强制所有模型使用中文思考
        Prompt prompt;
        if (thinking) {
            List<Message> messages = List.of(
                    new SystemMessage("这是一道需要推理的问题。请务必使用简体中文进行思考（思考过程/reasoning 全部用中文），并使用简体中文回答。不要使用英文进行推理。"),
                    new UserMessage(message));
            prompt = new Prompt(messages, chatOptions);
        } else {
            prompt = new Prompt(message, chatOptions);
        }
        // Flux.defer 保证每次请求都有独立的分段状态，避免多个订阅共用标记
        return Flux.defer(() -> {
            AtomicBoolean thinkingHeaderWritten = new AtomicBoolean(false);
            AtomicBoolean answerHeaderWritten = new AtomicBoolean(false);
            return target.stream(prompt)
                    .concatMap(chatResponse -> {
                        AssistantMessage output = chatResponse.getResult() != null
                                ? chatResponse.getResult().getOutput() : null;
                        if (output == null) {
                            return Flux.<String>empty();
                        }
                        List<String> chunks = new ArrayList<>(2);
                        // 思考过程：网关放在 reasoningContent 元数据里，与正文分属不同增量
                        Object reasoning = output.getMetadata().get("reasoningContent");
                        String think = reasoning != null ? reasoning.toString() : "";
                        if (thinking && !think.isEmpty()) {
                            if (thinkingHeaderWritten.compareAndSet(false, true)) {
                                chunks.add("【思考过程】\n" + think);
                            } else {
                                chunks.add(think);
                            }
                        }
                        String content = output.getText() != null ? output.getText() : "";
                        if (!content.isEmpty()) {
                            // 答案段首次输出时打标题；若开启思考，同时作为思考结束分隔
                            if (answerHeaderWritten.compareAndSet(false, true)) {
                                chunks.add((thinking ? "\n【答案】\n" : "") + content);
                            } else {
                                chunks.add(content);
                            }
                        }
                        return Flux.fromIterable(chunks);
                    })
                    .filter(chunk -> !chunk.isEmpty());
        });
    }
}
