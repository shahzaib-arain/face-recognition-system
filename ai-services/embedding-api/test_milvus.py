from pymilvus import connections, utility

try:
    # Connect to Milvus
    connections.connect(host='localhost', port='19530')
    
    # Check if connected
    print("✅ Connected to Milvus successfully!")
    
    # List collections
    collections = utility.list_collections()
    print(f"📋 Collections: {collections if collections else 'None (empty - this is normal for new setup)'}")
    
    # Get server version
    print(f"🔧 Milvus version: {utility.get_server_version()}")
    
    print("\n🎉 Milvus is working perfectly!")
    
except Exception as e:
    print(f"❌ Error: {e}")
    print("\n⚠️ Milvus might not be running or accessible")

finally:
    connections.disconnect("default")