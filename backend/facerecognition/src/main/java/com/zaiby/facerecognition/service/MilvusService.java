package com.zaiby.facerecognition.service;

import io.milvus.client.MilvusClient;
import io.milvus.grpc.DataType;
import io.milvus.param.R;
import io.milvus.param.collection.CreateCollectionParam;
import io.milvus.param.collection.FieldType;
import io.milvus.param.collection.LoadCollectionParam;
import io.milvus.param.collection.HasCollectionParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.param.index.DescribeIndexParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;

@Service
@RequiredArgsConstructor
public class MilvusService {

    private final MilvusClient milvusClient;
    private static final String COLLECTION_NAME = "face_embeddings";
    private static final String VECTOR_FIELD = "embedding";

    @PostConstruct
    public void initCollection() {

        // Check if collection already exists
        HasCollectionParam hasCollectionParam = HasCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build();

        R<Boolean> hasCollection = milvusClient.hasCollection(hasCollectionParam);

        if (hasCollection.getData() != null && hasCollection.getData()) {
            System.out.println("Collection already exists, checking index...");

            // ✅ Check if index exists, create if needed
            if (!hasIndex()) {
                System.out.println("Index not found, creating it...");
                createIndex();
            } else {
                System.out.println("Index already exists");
            }

            loadCollection();
            return;
        }

        System.out.println("Creating new collection...");

        // Create collection fields
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
                .withName(VECTOR_FIELD)
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
                        .withConsistencyLevel(ConsistencyLevelEnum.STRONG)
                        .build();

        R<?> createResponse = milvusClient.createCollection(createCollectionReq);

        if (createResponse.getStatus() != R.Status.Success.getCode()) {
            throw new RuntimeException("Failed to create collection: " + createResponse.getMessage());
        }

        System.out.println("Collection created successfully");

        // Create index on vector field
        createIndex();

        // Load the collection into memory
        loadCollection();
    }

    /**
     * Check if index exists on the vector field
     */
    private boolean hasIndex() {
        try {
            DescribeIndexParam describeIndexParam = DescribeIndexParam.newBuilder()
                    .withCollectionName(COLLECTION_NAME)
                    .build();

            R<?> response = milvusClient.describeIndex(describeIndexParam);

            // If successful response, index exists
            return response.getStatus() == R.Status.Success.getCode();
        } catch (Exception e) {
            // If exception or error, index doesn't exist
            System.out.println("Index check failed (probably doesn't exist): " + e.getMessage());
            return false;
        }
    }

    /**
     * Create index on the vector field
     */
    private void createIndex() {
        System.out.println("Creating index on vector field...");

        CreateIndexParam createIndexParam = CreateIndexParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withFieldName(VECTOR_FIELD)
                .withIndexType(IndexType.IVF_FLAT)
                .withMetricType(MetricType.L2)
                .withExtraParam("{\"nlist\":128}")
                .build();

        R<?> indexResponse = milvusClient.createIndex(createIndexParam);

        if (indexResponse.getStatus() != R.Status.Success.getCode()) {
            throw new RuntimeException("Failed to create index: " + indexResponse.getMessage());
        }

        System.out.println("Index created successfully");
    }

    /**
     * Load collection into memory
     */
    private void loadCollection() {
        System.out.println("Loading collection into memory...");

        LoadCollectionParam loadParam = LoadCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build();

        R<?> loadResponse = milvusClient.loadCollection(loadParam);

        if (loadResponse.getStatus() != R.Status.Success.getCode()) {
            throw new RuntimeException("Failed to load collection: " + loadResponse.getMessage());
        }

        System.out.println("Collection loaded successfully ✓");
    }

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
                VECTOR_FIELD,
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

        System.out.println("Embedding inserted for userId: " + userId);
    }
}