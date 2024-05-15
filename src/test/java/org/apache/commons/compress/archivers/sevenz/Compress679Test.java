/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.commons.compress.archivers.sevenz;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class Compress679Test {

    @Test
    @Disabled("Temp")
    public static void testCompress679() {
        final Path origin = Paths.get("file.7z");
        assertTrue(Files.exists(origin));
        final List<Exception> list = new CopyOnWriteArrayList<>();
        final Runnable runnable = () -> {
            try {
                try (SevenZFile sevenZFile = SevenZFile.builder().setPath(origin).get()) {
                    SevenZArchiveEntry sevenZArchiveEntry;
                    while ((sevenZArchiveEntry = sevenZFile.getNextEntry()) != null) {
                        if ("file4.txt".equals(sevenZArchiveEntry.getName())) { // The entry must not be the first of the ZIP archive to reproduce
                            final InputStream inputStream = sevenZFile.getInputStream(sevenZArchiveEntry);
                            // treatments...
                            break;
                        }
                    }
                }
            } catch (final Exception e) {
                // java.io.IOException: Checksum verification failed
                e.printStackTrace();
                list.add(e);
            }
        };
        IntStream.range(0, 30).forEach(i -> new Thread(runnable).start());
        assertTrue(list.isEmpty());
    }
}
