package com.springAI.service.impl;

import com.springAI.service.LLMService;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LLMServiceImpl implements LLMService {

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.openai.api-key:sk-replace-with-your-gateway-key}")
    private String apiKey;
    @Override
    public OpenAiChatModel buildModel(String model) {
        OpenAiApi api = OpenAiApi.builder()
                .baseUrl(this.baseUrl)
                .apiKey(this.apiKey)
                .build();
        return OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(model)
                        .temperature(0.7)
                        .build())
                .build();
    }
}
