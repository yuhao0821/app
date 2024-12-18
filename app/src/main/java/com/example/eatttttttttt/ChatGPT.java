package com.example.eatttttttttt;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_3_5_TURBO;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;
import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class ChatGPT {

    public static void main(String[] args) {
        // 调用方法生成答案
        String response = chat("Say 'Hello World' and who you are");
        System.out.println(response); // 打印生成的回答
    }

    // 创建一个方法用于生成回答
    public static String chat(String prompt) {
        // 替换为你的 OpenAI API Key
        String apiKey = "demo";

        // 配置 OpenAiChatModel
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(GPT_4_O_MINI) // 使用 GPT-4 的模型
                .build();

        // 生成回答
        return model.generate(prompt);
    }
}
