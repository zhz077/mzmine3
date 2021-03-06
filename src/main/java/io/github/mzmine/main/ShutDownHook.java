/*
 * Copyright 2006-2016 The MZmine 3 Development Team
 * 
 * This file is part of MZmine 3.
 * 
 * MZmine 3 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 3 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 3; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package io.github.mzmine.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.msdk.datamodel.featuretables.FeatureTable;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.mzmine.project.MZmineProject;

/**
 * Shutdown hook - invoked on JRE shutdown. This method saves current
 * configuration to XML and closes (and removes) all opened temporary files.
 */
class ShutDownHook implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run() {

        logger.info("Running post-shutdown code");

        // Cancel all running tasks - this is important because tasks can spawn
        // additional processes (such as ThermoRawDump.exe on Windows) and these
        // will block the shutdown of the JVM. If we cancel the tasks, the
        // processes will be killed immediately.
        /*
         * for (WrappedTask wt : MZmineCore.getTaskController().getTaskQueue()
         * .getQueueSnapshot()) { Task t = wt.getActualTask(); if
         * ((t.getStatus() == TaskStatus.WAITING) || (t.getStatus() ==
         * TaskStatus.PROCESSING)) { t.cancel(); } }
         */

        // Save configuration
        try {
            MZmineConfiguration configuration = MZmineCore.getConfiguration();
            if (configuration != null) {
                configuration
                        .saveConfiguration(MZmineConfiguration.CONFIG_FILE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Remove all temporary files
        MZmineProject currentProject = MZmineCore.getCurrentProject();
        if (currentProject != null) {
            for (RawDataFile dataFile : currentProject.getRawDataFiles()) {
                dataFile.dispose();
            }
            for (FeatureTable featureTable : currentProject
                    .getFeatureTables()) {
                featureTable.dispose();
            }
        }

    }
}