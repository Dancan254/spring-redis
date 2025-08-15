# Redis Integration Guide for Java/SpringBoot

## Theoretical Foundations
### 1. What is Redis?
**Definition of Concepts**
- **R**emote **Di**ctionary **S**erver - an in-memory data structure store
- Serves as database, cache, message broker, and streaming engine
- Open-source, written in C, extremely fast due to its in-memory nature
- NoSQL key-value store with rich data types beyond simple strings

**Key Characteristics**
- **In-memory:** All data is stored in RAM for ultra-fast access
- **Persistent:** Optional data durability through snapshots and logs
- **Atomic:** All operations are atomic, either all or nothing
- **Distributed:** Scale horizontally with ease
- **Versatile:** Support for multiple data types, replication, sharding, and clustering
- **Highly Available:** Fault-tolerant, highly available, and scalable

### 2. Architecture Deep Dive
**Single-Threaded Model**
```
Client Request → Event Loop → Command Processing → Response
```

- The main event loop runs on a single thread
- Eliminates the need for locks and complex concurrency control
- Commands are processed sequentially, but responses are processed asynchronously
- Network I/O is handled by the event loop thread - event-driven architecture
- Single-threaded model is ideal for low-latency applications

**Multi-threading Evolution(Redis6+)**
- I/O operations can use multiple threads
- Command execution remains single-threaded
- Background tasks(persistence, replication, deletion) use separate threads
- Maintains consistency while improving throughput

**Memory Architecture**
```text
Redis Memory Layout:
├── Data Structures (your data)
├── Expiration Dictionary (TTL tracking)
├── Key Space (key metadata)
├── Client Buffers (connection overhead)
├── Replication Buffers
└── Module Memory (if using modules)
```
- Redis stores data in memory
- Data is stored in key-value pairs
- Keys are strings
- Values can be strings, hashes, lists, sets, sorted sets, bitmaps, hyperloglogs, and streams
- Keys are unique within a Redis instance
- Values are not
- Keys and values can be of any data type
- Keys and values can be up to 512 MB in size

### 3. Persistence Mechanisms
**RDB(Redis Database) Snapshots**
- Point-in-time snapshots saved to disk
- Compact binary format, faster restarts
- Good for backups and disaster recovery
- Risk: Data loos between snapshots

**Configuration Example**
```aiignore
save 900 1      # Save if at least 1 key changed in 900 seconds
save 300 10     # Save if at least 10 keys changed in 300 seconds
save 60 10000   # Save if at least 10000 keys changed in 60 seconds
```

**AOF(Append Only File)**
- logs every write operation
- Better durability, minimal data loss
- Larger file size, slower restarts
- Auto-rewrite to optimize file size

**Sync Policies**
- `always`: Sync after every write operation
- `everySec`: Sync every second(balanced)
- `no`: Disable sync. Lets the OS decide when to sync(fastest, riskiest)

**Hybrid Approach(Redis4+)**
- RDB snapshot + AOF incremental changes
- Best of both words: fast restarts, minimal data loss

### 4. Memory Management
**Eviction Policies**
- `noeviction`: Return error when memory limit is reached, no eviction
- `allkeys-lru`: Evict the least recently used keys
- `volatile-lru`: Evict the LRU keys with Time to Live(TTL) set
- `volatile-lfu`: Evict the least frequently used keys TTL set
- `allkeys-lfu`: Evict the LFU keys
- `allkeys-random`: Evict random keys
- `volatile-random`: Evict a random key with TTL set
- `volatile-ttl`: Evict the key with the nearest expire time(TTL)

**Memory Optimization Techniques**
- Ziplist / Listpack Encoding: Small collections (hashes, lists, sorted sets) stored in a compact, contiguous memory format to reduce overhead.

- `hash-max-ziplist-entries` & `hash-max-ziplist-value`: Control when hashes switch from compressed list encoding to a standard hash table.

- Shared Objects Pool: Frequently used small integers can be shared in memory instead of duplicated.

- Data Compression: Use `LZF` or `LZ4` (via Redis modules or RDB/AOF compression) to reduce memory usage.

### 5. Replication and Clustering
**Replication(Master-Replica)**
```
Master Node
├── Replica Node 1
├── Replica Node 2
└── Replica Node N
```
**Key Points**
- Asynchronous replication: replicas receive updates from the master after they happen
- Read Scaling: send read queries to replicas to reduce master load
- High Availability: User `Redis Sentinel` to manage master/replica failover and monitoring

**Redis Cluster**
```text
Hash Slot Distribution:
16384 slots distributed across nodes
Key → CRC16(key) mod 16384 → Slot → Node
Each slot is served by a single node, distributing keys automatically
```
**Benefits**
- Horizontal Scaling: add more nodes to handle more data and traffic
- Automatic Sharding: keys are transparently distributed across nodes
- High Availability: replication and failover within the cluster
- Automatic Rebalancing: keys are redistributed when nodes are added or removed