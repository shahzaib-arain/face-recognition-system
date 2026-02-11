from pymilvus import connections, Collection, utility

connections.connect(host="localhost", port="19530")

collections = utility.list_collections()
print("Collections:", collections)

if collections:
    for name in collections:
        col = Collection(name)
        col.load()
        print(f"\n📦 Collection: {name}")
        print("Total vectors stored:", col.num_entities)
else:
    print("⚠️ No embeddings stored yet")

connections.disconnect("default")
