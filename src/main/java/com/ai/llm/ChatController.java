package com.ai.llm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class ChatController {

    private final ChatModel chatModel;

    private final VectorStore vectorStore;

    private final RestTemplate restTemplate;

    @Value("${marketstack.access-key}")
    private String accessKey;

    private String prompt = """
            Your task is to answer the questions about Indian Constitution. Use the information from the DOCUMENTS
            section to provide accurate answers. If unsure or if the answer isn't found in the DOCUMENTS section, 
            simply state that you don't know the answer.
                        
            QUESTION:
            {input}
                        
            DOCUMENTS:
            {documents}
                        
            """;

    // will construct an instance of ollamaChatModel by default since that is the only implementation
    public ChatController(ChatModel chatModel, VectorStore vectorStore, RestTemplate restTemplate) {
        this.chatModel = chatModel;
        this.vectorStore = vectorStore;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/")
    public String prompt(@RequestParam String question) {
        PromptTemplate promptTemplate = new PromptTemplate(prompt);
        Map<String, Object> promptsParameters = new HashMap<>();
        promptsParameters.put("input", question);
        promptsParameters.put("documents", findSimilarData(question));
        return chatModel
                .call(promptTemplate.create(promptsParameters))
                .getResult()
                .getOutput()
                .getContent();
    }

    // Returns us the similar information or up to date context based on the query, and source of truth we are using
    // makes the response more accurate and relevant
    private String findSimilarData(String question) {
        List<Document> documentList = vectorStore.similaritySearch(SearchRequest.query(question).withTopK(1));
        log.info("Similar Data in vector store: {}", documentList);
        return documentList.stream().map(Document::getContent).collect(Collectors.joining());
    }

    @GetMapping("/stockDetails")
    public String stockDetails(@RequestParam String symbol) throws InterruptedException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("access_key", accessKey);

        HttpEntity<HttpHeaders> httpEntity = new HttpEntity<>(httpHeaders);
        System.out.println(httpEntity);
//        Future<?> executorService = Executors.newFixedThreadPool(1).submit(() ->
//        {
//            try {
//                Thread.sleep(2000);
//                System.out.println(Thread.currentThread());
//                System.out.println(restTemplate.exchange("https://api.marketstack.com/v2/eod?symbols=" + symbol + "&access_key=" + accessKey, HttpMethod.GET, httpEntity, StockDetails.class));
//                ;
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });

        Thread.sleep(2000);
        ParameterizedTypeReference<List<String>> parameterizedTypeReference = new ParameterizedTypeReference<>() {
        };
        System.out.println(restTemplate.exchange("http://localhost:8081/teams", HttpMethod.GET, httpEntity, parameterizedTypeReference));
        return "Hello World";
    }
}
