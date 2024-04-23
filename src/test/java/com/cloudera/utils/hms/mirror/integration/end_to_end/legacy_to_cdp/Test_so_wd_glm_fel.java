/*
 * Copyright (c) 2024. Cloudera, Inc. All Rights Reserved
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.cloudera.utils.hms.mirror.integration.end_to_end.legacy_to_cdp;

import com.cloudera.utils.hms.mirror.Environment;
import com.cloudera.utils.hms.mirror.PhaseState;
import com.cloudera.utils.hms.mirror.integration.end_to_end.E2EBaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.cloudera.utils.hms.Mirror.class,
        args = {
//                "--hms-mirror.config.data-strategy=SCHEMA_ONLY",
//                "--hms-mirror.config.migrate-acid=true",
//                "--hms-mirror.config.migrate-acid-only=true",
                "--hms-mirror.config.warehouse-directory=/wrehouse/tablespace/managed/hive",
                "--hms-mirror.config.external-warehouse-directory=/wrehouse/tablespace/external/hive",
                "--hms-mirror.config.global-location-map=/warehouse/tablespace/external/hive=/chuck/me",
//                "--hms-mirror.config.downgrade-acid=true",
//                "--hms-mirror.config.read-only=true",
//                "--hms-mirror.config.sync=true",
                "--hms-mirror.config.force-external-location=true",
//                "--hms-mirror.config.evaluate-partition-location=true",
//                "--hms-mirror.config.intermediate-storage=s3a://my_is_bucket",
//                "--hms-mirror.config.common-storage=s3a://my_cs_bucket",
//                "--hms-mirror.config.reset-to-default-location=true",
                "--hms-mirror.config.distcp=true",
                "--hms-mirror.conversion.test-filename=/test_data/assorted_tbls_01.yaml",
                "--hms-mirror.config-filename=/config/default.yaml.hdp2-cdp",
                "--hms-mirror.config.output-dir=${user.home}/.hms-mirror/test-output/e2e/legacy_cdp/so_wd_glm_fel"
        })
@Slf4j
public class Test_so_wd_glm_fel extends E2EBaseTest {
    //        String[] args = new String[]{
////                "-rdl",
//                "-wd", "/wrehouse/tablespace/managed/hive",
//                "-ewd", "/wrehouse/tablespace/external/hive", // trigger warnings.
//                "-glm", "/warehouse/tablespace/external/hive=/chuck/me", // will adjust location
//                "-fel",
//                "-dc",
//                "-ltd", ASSORTED_TBLS_04,
//                "-cfg", HDP2_CDP,
//                "-o", outputDir
//        };
//
//        long rtn = 0;
//        MirrorLegacy mirror = new MirrorLegacy();
//        rtn = mirror.go(args);
//        int check = 0;
//        assertEquals("Return Code Failure: " + rtn + " doesn't match: " + check, check, rtn);
//
//        // Read the output and verify the results.
//        DBMirror[] resultsMirrors = getResults(outputDir,ASSORTED_TBLS_04);
//
//        validatePhase(resultsMirrors[0], "ext_part_01", PhaseState.SUCCESS);
//
//        validateTableIssueCount(resultsMirrors[0], "ext_part_01", Environment.RIGHT, 3);
//        validateTableLocation(resultsMirrors[0], "ext_part_01", Environment.RIGHT, "hdfs://HOME90/chuck/me/assorted_test_db.db/ext_part_01");

    @Test
    public void issueTest_01() {
        validateTableIssueCount("assorted_test_db", "ext_part_01",
                Environment.RIGHT, 3);
    }

    @Test
    public void locationTest_01() {
        validateTableLocation("assorted_test_db",
                "ext_part_01", Environment.RIGHT,
                "hdfs://HOME90/chuck/me/assorted_test_db.db/ext_part_01");
    }

    @Test
    public void phaseTest_01() {
        validatePhase("assorted_test_db", "ext_part_01", PhaseState.SUCCESS);
    }

    @Test
    public void phaseTest_02() {
        validatePhase("assorted_test_db", "legacy_mngd_01", PhaseState.SUCCESS);
    }

    @Test
    public void returnCodeTest() {
        // Get Runtime Return Code.
        long rtn = getReturnCode();
        // Verify the return code.
        long check = 0L;
        assertEquals("Return Code Failure: " + rtn, check * -1, rtn);
    }

}
