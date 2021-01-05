# HMS-Mirror

"hms-mirror" is a utility used to bridge the gap between two clusters and migrate `hive` _metadata_ **AND** _data_.

[Getting / Building / Running HMS Mirror](./setup.md)

[Running HMS Mirror](./running.md)

[What does hms-mirror do? (Design-Spec)](./design-spec.md)

## Processes

There are 2 process catagories in `hms-mirror`.  They can be used in isolation.  Migrated schemas go through a set of rules that convert their definition to with Hive 3, while maintaining as much legacy behaviors as possible.  These include:
- Managed non-acid tables are converted to _EXTERNAL_ tables.

### METADATA

This process uses the 'storage' layer of a legacy cluster (LOWER) from the compute layer of a modern cluster (UPPER).

We treat hdfs on the **LOWER** cluster as a shared resource and migrate the metadata for Hive over to the **UPPER** cluster using _standard_ `hive` tools and techniques.

This shared storage approach, with replicated metadata, should address a large majority of `hive metastore` analytic use cases.  This includes work from Hive, Spark, and other technologies integrated with the `hive metastore`.

This process addresses `EXTERNAL` and **LEGACY** `MANAGED` tables.  It does **NOT** support `ACID` transactional tables.

The _upper_ cluster should be used as a **READ-ONLY** view of the data, until _Stage 2_ of the plan is implemented.

![hms-mirror](./images/HMS-Mirror.png)

This is helpful for:
- Testing a new cluster without having to migrate data.
- Testing data workflows and applications in a *test* cluster using the data in a *production* cluster.
- Migrating hive schemas from EMR/Dataproc/HDi to CDP Cloud.

There are a few limitations to note here:
- We're NOT sharing metadata stores between the clusters
- This technique is restricted to _external_ and _non-transactional managed_ tables.  **ACID Tables** are **NOT** supported.
- Partitioned datasets can be 'auto discovered' when the configuration `partitionDiscovery:auto=true` is set.
- Partitions that do NOT follow the standard directory structure and aren't discoverable via `msck` are NOT supported yet.

- [Link Clusters](./link_clusters.md) so the Upper Cluster can distcp from the lower cluster.  This is required to support storage access from the 'target' cluster to the 'source' cluster. The user/service account used to launch the jobs in the 'target' cluster is translated to the 'source' clusters storage access layer.  The appropriate ACL's on the 'source' cluster must be established to allow this connection.

#### Strategies

Strategies are the varies techniques used to migrate hive schemas.

##### DIRECT
This will extract a copy of the METADATA from the _source_ cluster, make any adjustments required for compatability, and replay the DDL on the _target_ cluster.

- For Legacy Managed tables the `external.table.purge` flag is NOT set because the target cluster is NOT the owner of the data.

##### EXPORT_IMPORT
This will create a transition temp table in the lower cluster based on the source table, but with no data in it.  Then use hive EXPORT/IMPORT processes to recreate the table in the _target_ cluster.  Once created there, it will adjust the location to look at the data in the _source_ cluster.

- For Legacy Managed tables the `external.table.purge` flag is NOT set because the target cluster is NOT the owner of the data.


##### DISTCP
This assumes the data has been moved from the _source_ cluster to the _target_ cluster with DISTCP.  This will copy the schema as in "DIRECT", but will adjust the location to the _target_ clusters namespace using the SAME relative location as was originally used in the _source_ cluster.

- For Legacy Managed tables the `external.table.purge` flag is set because the target cluster owns the data.

### STORAGE

This phase is used to copy the _metadata_ AND _data_ from the 'source' cluster to the 'target' cluster.  There are several transfer options available:

#### Strategies

##### SQL
Use `hive` to migrate the data between clusters using a series of transfer tables and _rename_ semantics to minimize disruption and reduce the number of times data is moved.

##### EXPORT_IMPORT
Use 'hive' EXPORT / IMPORT built in processes to create a copy of the data then transfer it to the target cluster.  NOTE: For datasets with a high number of partitions, the EXPORT/IMPORT process can be quite time-consuming.  Consider using 'SQL' or 'DISTCP' methods.

##### DISTCP
TBD.





