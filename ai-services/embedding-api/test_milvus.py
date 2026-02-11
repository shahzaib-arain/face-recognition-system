from pymilvus import connections, utility, Collection

try:
    # 1️⃣ Connect to Milvus
    connections.connect(host='localhost', port='19530')
    print("✅ Connected to Milvus successfully!\n")

    # 2️⃣ List collections
    collections_list = utility.list_collections()
    print(f"📋 Collections: {collections_list if collections_list else 'None (empty - new setup)'}\n")

    # 3️⃣ For each collection, show metadata and some vectors
    for name in collections_list:
        print(f"--- Collection: {name} ---")
        collection = Collection(name)

        # Show schema info
        print(f"Schema: {collection.schema}\n")

        # Show first 5 entries (vectors + metadata)
        results = collection.query(expr="userId != ''", output_fields=["embedding", "userId"])
        print("First 5 vectors and metadata:")
        for entry in results[:5]:
            print(f"userId: {entry['userId']}, embedding: {entry['embedding'][:10]}...")  # show first 10 dims
        print("\n")

    # 4️⃣ Get Milvus server version
    print(f"🔧 Milvus version: {utility.get_server_version()}\n")

    print("🎉 Milvus is working perfectly!")

except Exception as e:
    print(f"❌ Error: {e}")
    print("⚠️ Milvus might not be running or accessible")

finally:
    connections.disconnect("default")
