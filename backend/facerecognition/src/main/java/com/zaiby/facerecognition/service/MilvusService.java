package com.zaiby.facerecognition.service;

import io.milvus.client.MilvusClient;
import io.milvus.grpc.DataType;
import io.milvus.param.R;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.dml.InsertParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MilvusService {

    private final MilvusClient milvusClient;
    private static final String COLLECTION_NAME = "face_embeddings";

    // 🔥 Create collection on startup
    @PostConstruct
    public void initCollection() {

        FieldType idField = FieldType.newBuilder()
                .withName("id")
                .withDataType(DataType.Int64)
                .withPrimaryKey(true)
                .withAutoID(true)
                .build();

        FieldType userIdField = FieldType.newBuilder()
                .withName("userId")
                .withDataType(DataType.VarChar)
                .withMaxLength(50)
                .build();

        FieldType embeddingField = FieldType.newBuilder()
                .withName("embedding")
                .withDataType(DataType.FloatVector)
                .withDimension(512)
                .build();

        CreateCollectionParam createCollectionReq =
                CreateCollectionParam.newBuilder()
                        .withCollectionName(COLLECTION_NAME)
                        .withDescription("Face embeddings")
                        .withShardsNum(2)
                        .addFieldType(idField)
                        .addFieldType(userIdField)
                        .addFieldType(embeddingField)
                        .build();

        milvusClient.createCollection(createCollectionReq);
    }

    // 🔥 Insert embedding
    public void insertEmbedding(Long userId, List<Double> embedding) {

        List<Float> vector = embedding.stream()
                .map(Double::floatValue)
                .toList();

        List<InsertParam.Field> fields = new ArrayList<>();

        fields.add(new InsertParam.Field(
                "userId",
                Collections.singletonList(userId.toString())
        ));

        fields.add(new InsertParam.Field(
                "embedding",
                Collections.singletonList(vector)
        ));

        InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withFields(fields)
                .build();

        R<?> response = milvusClient.insert(insertParam);

        if (response.getStatus() != R.Status.Success.getCode()) {
            throw new RuntimeException("Milvus insert failed: " + response.getMessage());
        }
    }
}
