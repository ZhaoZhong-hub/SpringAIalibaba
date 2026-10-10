package com.springAI.config;

import com.springAI.service.LLMService;
import jakarta.annotation.Resource;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LLM配置类
 * @author zhaozhong
 * @date 2026/10/10
 * 多模型配置类，用于配置多个语言模型
 */
@Configuration
public class LLMConfig {
    @Resource
    private LLMService llmService;

    @Bean("qwenChatModel")
    public OpenAiChatModel qwenChatModel() {
        return llmService.buildModel("qwen3.8-max");
    }

    @Bean("deepseekChatModel")
    public OpenAiChatModel deepseekChatModel() {
        return llmService.buildModel("deepseek-v4.1-flash");
    }

    // 想再加在线模型，照抄一个 @Bean 方法即可，例如：
    // @Bean("gptChatModel")
    // public OpenAiChatModel gptChatModel() { return buildModel("gpt-4o-mini"); }
}
