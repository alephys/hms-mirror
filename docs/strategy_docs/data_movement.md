## Data Movement Strategies

### SQL

The **SQL** data strategy will use Hive SQL to move data between clusters.  When the cluster don't have direct line of sight to each other and can NOT be [linked](../README.md#linking-clusters-storage-layers), you can use options like `-cs` or `-is` to bridge the gap.

#### Options

##### `-ma|--migrate-acid` or `-mao|--migrate-acid-only`

Include ACID tables in processing.

##### `-da|--downgrade-acid`

Applicable only when `-ma|o` is used.  This will take the ACID tables and downgrade them to *EXTERNAL/PURGE* tables.  [Buckets adjustments](../README.md#acid-tables) are applicable.

##### `-dc|--distcp`

For EXTERNAL tables, this will build a `distcp` plan that can be used to transfer the database tables.  

When the option `-ma|o` is used, ACID tables will be migrated. *ACID* tables will be converted to EXTERNAL transfer tables for the `distcp` operations.  On the other cluster, the tables will be created initially as *EXTERNAL*, then transferred back over to *ACID* tables, unless `-da` is used.

If the `-da` is used the tables will remain *EXTERNAL* on the new cluster.

When `-is` is used, there will be `distcp` operations required on both clusters to handle data movement to/from the `intermediate-storage` area.

##### `-is|--intermediate-storage` or `-cs|--common-storage`

When the clusters aren't [linked](../README.md#linking-clusters-storage-layers) these two options provide a way to transfer data through an intermediate/common storage location.  Each cluster must have access to these locations.  These values are mutually exclusive.

`--intermediate-storage` requires an addition transfer of data.  The LEFT transfers data to the `-is` location and the RIGHT cluster uses that data and initiates a transfer from the location to the final hcfsNamespace value set for the cluster.

`--common-storage` assumes the location is also the final resting place for the data.  Some optimizations are possible (ACID downgrades) that reduce that times data need to move.

##### `-wd|--warehouse-directory` and `-ewd|--external-warehouse-directory`

Are used to set the *databases* default locations for managed and external tables.  This overrides the systems *hive metastore* properties.

##### `-rdl|--reset-to-default-location`

Regardless of where the source data _relative_ location was on the filesystem, this will reset it to the default location on the new cluster.

If `-dc|--distcp` is used, then the `warehouse` options are required in order for `hms-mirror` to build the `distcp` workplan.

### Export/Import




### Hybrid

