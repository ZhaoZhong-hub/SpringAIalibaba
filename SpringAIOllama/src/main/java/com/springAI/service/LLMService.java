package com.springAI.service;

import org.springframework.ai.openai.OpenAiChatModel;

public interface LLMService {
    OpenAiChatModel buildModel(String model);
}
