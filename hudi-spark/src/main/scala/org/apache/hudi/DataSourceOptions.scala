/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hudi

import org.apache.hudi.common.model.HoodieTableType
import org.apache.hudi.hive.SlashEncodedDayPartitionValueExtractor

/**
  * List of options that can be passed to the Hoodie datasource,
  * in addition to the hoodie client configs
  */

/**
  * Options supported for reading hoodie datasets.
  */
object DataSourceReadOptions {
  /**
    * Whether data needs to be read, in
    * incremental mode (new data since an instantTime)
    * (or) Read Optimized mode (obtain latest view, based on columnar data)
    * (or) Real time mode (obtain latest view, based on row & columnar data)
    *
    * Default: READ_OPTIMIZED
    */
  val VIEW_TYPE_OPT_KEY = "hoodie.datasource.view.type"
  val VIEW_TYPE_READ_OPTIMIZED_OPT_VAL = "read_optimized"
  val VIEW_TYPE_INCREMENTAL_OPT_VAL = "incremental"
  val VIEW_TYPE_REALTIME_OPT_VAL = "realtime"
  val DEFAULT_VIEW_TYPE_OPT_VAL = VIEW_TYPE_READ_OPTIMIZED_OPT_VAL
  val DEFAULTPUSH_DOWN_FILTERS_OPT_VAL = ""

  /**
    * Instant time to start incrementally pulling data from. The instanttime here need not
    * necessarily correspond to an instant on the timeline. New data written with an
    * `instant_time > BEGIN_INSTANTTIME` are fetched out. For e.g: '20170901080000' will get
    * all new data written after Sep 1, 2017 08:00AM.
    *
    * Default: None (Mandatory in incremental mode)
    */
  val BEGIN_INSTANTTIME_OPT_KEY = "hoodie.datasource.read.begin.instanttime"


  /**
    * Instant time to limit incrementally fetched data to. New data written with an
    * `instant_time <= END_INSTANTTIME` are fetched out.
    *
    * Default: latest instant (i.e fetches all new data since begin instant time)
    *
    */
  val END_INSTANTTIME_OPT_KEY = "hoodie.datasource.read.end.instanttime"

  /**
    * For use-cases like DeltaStreamer which reads from Hoodie Incremental table and applies opaque map functions,
    * filters appearing late in the sequence of transformations cannot be automatically pushed down.
    * This option allows setting filters directly on Hoodie Source
    */
  val PUSH_DOWN_INCR_FILTERS_OPT_KEY = "hoodie.datasource.read.incr.filters"
}

/**
  * Options supported for writing hoodie datasets.
  */
object DataSourceWriteOptions {
  /**
    * The client operation, that this write should do
    *
    * Default: upsert()
    */
  val OPERATION_OPT_KEY = "hoodie.datasource.write.operation"
  val BULK_INSERT_OPERATION_OPT_VAL = "bulk_insert"
  val INSERT_OPERATION_OPT_VAL = "insert"
  val UPSERT_OPERATION_OPT_VAL = "upsert"
  val DELETE_OPERATION_OPT_VAL = "delete"
  val DEFAULT_OPERATION_OPT_VAL = UPSERT_OPERATION_OPT_VAL

  /**
    * The storage type for the underlying data, for this write.
    * Note that this can't change across writes.
    *
    * Default: COPY_ON_WRITE
    */
  val STORAGE_TYPE_OPT_KEY = "hoodie.datasource.write.storage.type"
  val COW_STORAGE_TYPE_OPT_VAL = HoodieTableType.COPY_ON_WRITE.name
  val MOR_STORAGE_TYPE_OPT_VAL = HoodieTableType.MERGE_ON_READ.name
  val DEFAULT_STORAGE_TYPE_OPT_VAL = COW_STORAGE_TYPE_OPT_VAL

  /**
    * Hive table name, to register the dataset into.
    *
    * Default:  None (mandatory)
    */
  val TABLE_NAME_OPT_KEY = "hoodie.datasource.write.table.name"

  /**
    * Field used in preCombining before actual write. When two records have the same
    * key value, we will pick the one with the largest value for the precombine field,
    * determined by Object.compareTo(..)
    */
  val PRECOMBINE_FIELD_OPT_KEY = "hoodie.datasource.write.precombine.field"
  val DEFAULT_PRECOMBINE_FIELD_OPT_VAL = "ts"


  /**
    * Payload class used. Override this, if you like to roll your own merge logic, when upserting/inserting.
    * This will render any value set for `PRECOMBINE_FIELD_OPT_VAL` in-effective
    */
  val PAYLOAD_CLASS_OPT_KEY = "hoodie.datasource.write.payload.class"
  val DEFAULT_PAYLOAD_OPT_VAL = classOf[OverwriteWithLatestAvroPayload].getName

  /**
    * Record key field. Value to be used as the `recordKey` component of `HoodieKey`. Actual value
    * will be obtained by invoking .toString() on the field value. Nested fields can be specified using
    * the dot notation eg: `a.b.c`
    *
    */
  val RECORDKEY_FIELD_OPT_KEY = "hoodie.datasource.write.recordkey.field"
  val DEFAULT_RECORDKEY_FIELD_OPT_VAL = "uuid"

  /**
    * Partition path field. Value to be used at the `partitionPath` component of `HoodieKey`. Actual
    * value ontained by invoking .toString()
    */
  val PARTITIONPATH_FIELD_OPT_KEY = "hoodie.datasource.write.partitionpath.field"
  val DEFAULT_PARTITIONPATH_FIELD_OPT_VAL = "partitionpath"

  /**
    * Key generator class, that implements will extract the key out of incoming record
    *
    */
  val KEYGENERATOR_CLASS_OPT_KEY = "hoodie.datasource.write.keygenerator.class"
  val DEFAULT_KEYGENERATOR_CLASS_OPT_VAL = classOf[SimpleKeyGenerator].getName

  /**
    * Option keys beginning with this prefix, are automatically added to the commit/deltacommit metadata.
    * This is useful to store checkpointing information, in a consistent way with the hoodie timeline
    */
  val COMMIT_METADATA_KEYPREFIX_OPT_KEY = "hoodie.datasource.write.commitmeta.key.prefix"
  val DEFAULT_COMMIT_METADATA_KEYPREFIX_OPT_VAL = "_"

  /**
    * Flag to indicate whether to drop duplicates upon insert.
    * By default insert will accept duplicates, to gain extra performance.
    */
  val INSERT_DROP_DUPS_OPT_KEY = "hoodie.datasource.write.insert.drop.duplicates"
  val DEFAULT_INSERT_DROP_DUPS_OPT_VAL = "false"

  /**
    * Flag to indicate how many times streaming job should retry for a failed microbatch
    * By default 3
    */
  val STREAMING_RETRY_CNT_OPT_KEY = "hoodie.datasource.write.streaming.retry.count"
  val DEFAULT_STREAMING_RETRY_CNT_OPT_VAL = "3"

  /**
    * Flag to indicate how long (by millisecond) before a retry should issued for failed microbatch
    * By default 2000 and it will be doubled by every retry
    */
  val STREAMING_RETRY_INTERVAL_MS_OPT_KEY = "hoodie.datasource.write.streaming.retry.interval.ms"
  val DEFAULT_STREAMING_RETRY_INTERVAL_MS_OPT_VAL = "2000"

  /**
    * Flag to indicate whether to ignore any non exception error (e.g. writestatus error)
    * within a streaming microbatch
    * By default true (in favor of streaming progressing over data integrity)
    */
  val STREAMING_IGNORE_FAILED_BATCH_OPT_KEY = "hoodie.datasource.write.streaming.ignore.failed.batch"
  val DEFAULT_STREAMING_IGNORE_FAILED_BATCH_OPT_VAL = "true"

  // HIVE SYNC SPECIFIC CONFIGS
  //NOTE: DO NOT USE uppercase for the keys as they are internally lower-cased. Using upper-cases causes
  // unexpected issues with config getting reset
  val HIVE_SYNC_ENABLED_OPT_KEY = "hoodie.datasource.hive_sync.enable"
  val HIVE_DATABASE_OPT_KEY = "hoodie.datasource.hive_sync.database"
  val HIVE_TABLE_OPT_KEY = "hoodie.datasource.hive_sync.table"
  val HIVE_USER_OPT_KEY = "hoodie.datasource.hive_sync.username"
  val HIVE_PASS_OPT_KEY = "hoodie.datasource.hive_sync.password"
  val HIVE_URL_OPT_KEY = "hoodie.datasource.hive_sync.jdbcurl"
  val HIVE_PARTITION_FIELDS_OPT_KEY = "hoodie.datasource.hive_sync.partition_fields"
  val HIVE_PARTITION_EXTRACTOR_CLASS_OPT_KEY = "hoodie.datasource.hive_sync.partition_extractor_class"
  val HIVE_ASSUME_DATE_PARTITION_OPT_KEY = "hoodie.datasource.hive_sync.assume_date_partitioning"
  val HIVE_USE_PRE_APACHE_INPUT_FORMAT_OPT_KEY = "hoodie.datasource.hive_sync.use_pre_apache_input_format"

  // DEFAULT FOR HIVE SPECIFIC CONFIGS
  val DEFAULT_HIVE_SYNC_ENABLED_OPT_VAL = "false"
  val DEFAULT_HIVE_DATABASE_OPT_VAL = "default"
  val DEFAULT_HIVE_TABLE_OPT_VAL = "unknown"
  val DEFAULT_HIVE_USER_OPT_VAL = "hive"
  val DEFAULT_HIVE_PASS_OPT_VAL = "hive"
  val DEFAULT_HIVE_URL_OPT_VAL = "jdbc:hive2://localhost:10000"
  val DEFAULT_HIVE_PARTITION_FIELDS_OPT_VAL = ""
  val DEFAULT_HIVE_PARTITION_EXTRACTOR_CLASS_OPT_VAL = classOf[SlashEncodedDayPartitionValueExtractor].getCanonicalName
  val DEFAULT_HIVE_ASSUME_DATE_PARTITION_OPT_VAL = "false"
  val DEFAULT_USE_PRE_APACHE_INPUT_FORMAT_OPT_VAL = "false"
}
