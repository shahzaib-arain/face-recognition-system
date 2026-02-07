package com.zaiby.facerecognition.service;

import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.SearchResults;
import io.milvus.param.R;
import io.milvus.param.MetricType;
import io.milvus.param.dml.SearchParam;
import io.milvus.response.SearchResultsWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MilvusSearchService {

    private final MilvusServiceClient milvusClient;
    private static final String COLLECTION_NAME = "face_embeddings";
    private static final float SIMILARITY_THRESHOLD = 0.80f;

    public String searchNearest(List<Double> embedding) {
        // Convert Double → Float
        List<Float> vector = embedding.stream()
                .map(Double::floatValue)
                .toList();

        // Build search request
        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withVectorFieldName("embedding")
                .withVectors(Collections.singletonList(vector))
                .withTopK(1)
                .withMetricType(MetricType.L2)  // ✅ FIXED: Changed from IP to L2
                .withOutFields(Collections.singletonList("userId"))
                .withParams("{\"nprobe\":10}")
                .build();

        // Execute search
        R<SearchResults> response = milvusClient.search(searchParam);

        // Check if successful
        if (response.getStatus() != R.Status.Success.getCode()) {
            throw new RuntimeException("Milvus search failed: " + response.getMessage());
        }

        // Wrap results for easier access
        SearchResultsWrapper wrapper = new SearchResultsWrapper(response.getData().getResults());

        if (wrapper.getRowRecords().isEmpty()) {
            return null; // No match found
        }

        // Get first result
        SearchResultsWrapper.IDScore firstResult = wrapper.getIDScore(0).get(0);
        float score = firstResult.getScore();

        // ⚠️ IMPORTANT: With L2, LOWER scores = more similar
        // You may need to adjust this threshold logic
        if (score <= SIMILARITY_THRESHOLD) {  // Changed from >= to <=
            // Get userId from output fields
            List<?> userIds = (List<?>) wrapper.getFieldData("userId", 0);
            if (userIds != null && !userIds.isEmpty()) {
                return userIds.get(0).toString();
            }
        }

        return null;
    }

}