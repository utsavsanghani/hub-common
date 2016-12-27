/**
 * Hub Common
 *
 * Copyright (C) 2016 Black Duck Software, Inc..
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.hub.cli;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import com.blackducksoftware.integration.exception.EncryptionException;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.util.CIEnvironmentVariables;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CLIDownloadService {
    private final IntLogger logger;

    private final RestConnection restConnection;

    public CLIDownloadService(IntLogger logger, RestConnection restConnection) {
        this.logger = logger;
        this.restConnection = restConnection;
    }

    public void performInstallation(final File directoryToInstallTo, final CIEnvironmentVariables ciEnvironmentVariables,
            String hubUrl, String hubVersion, final String localHostName) throws HubIntegrationException, EncryptionException {
        if (StringUtils.isBlank(localHostName)) {
            throw new IllegalArgumentException("You must provided the hostName of the machine this is running on.");
        }

        final CLILocation cliLocation = new CLILocation(directoryToInstallTo);
        final String cliDownloadUrl = cliLocation.getCLIDownloadUrl(logger, hubUrl);
        if (StringUtils.isNotBlank(cliDownloadUrl)) {
            try {
                customInstall(cliLocation, ciEnvironmentVariables, new URL(cliDownloadUrl), hubVersion, localHostName);
            } catch (final MalformedURLException e) {
                throw new HubIntegrationException(String.format("The cli could not be downloaded from %s: %s", cliDownloadUrl, e.getMessage()), e);
            }
        } else {
            logger.error("Could not find the correct Hub CLI download URL.");
        }
    }

    public void customInstall(CLILocation cliLocation, CIEnvironmentVariables ciEnvironmentVariables, final URL archive,
            String hubVersion, final String localHostName) throws HubIntegrationException, EncryptionException {
        String directoryToInstallTo;
        try {
            directoryToInstallTo = cliLocation.getCanonicalPath();
        } catch (final IOException e) {
            throw new HubIntegrationException("Could not get the path for the install directory for the cli - does it exist?", e);
        }

        try {
            boolean cliMismatch = true;
            final File hubVersionFile = cliLocation.createHubVersionFile();
            if (hubVersionFile.exists()) {
                final String storedHubVersion = IOUtils.toString(new FileReader(hubVersionFile));
                if (hubVersion.equals(storedHubVersion)) {
                    cliMismatch = false;
                } else {
                    hubVersionFile.delete();
                    hubVersionFile.createNewFile();
                }
            }
            final File cliInstallDirectory = cliLocation.getCLIInstallDir();
            if (!cliInstallDirectory.exists()) {
                cliMismatch = true;
            }

            if (cliMismatch) {
                logger.debug("Attempting to download the Hub CLI.");
                final FileWriter writer = new FileWriter(hubVersionFile);
                writer.write(hubVersion);
                writer.close();
                hubVersionFile.setLastModified(0L);
            }
            final long cliTimestamp = hubVersionFile.lastModified();

            Response response;
            try {
                HttpUrl httpUrl = restConnection.createHttpUrl(archive);
                Map<String, String> headers = new HashMap<>();
                headers.put("If-Modified-Since", String.valueOf(cliTimestamp));
                Request request = restConnection.createGetRequest(httpUrl, headers);
                response = restConnection.handleExecuteClientCall(request);
            } catch (final IOException ioe) {
                logger.error("Skipping installation of " + archive + " to " + directoryToInstallTo + ": "
                        + ioe.toString());
                return;
            }
            if (response.code() == 304) {
                // CLI has not been modified
                return;
            }
            String lastModified = response.header("Last-Modified");
            Long lastModifiedLong = 0L;

            if (StringUtils.isNotBlank(lastModified)) {
                // Should parse the Date just like URLConnection did
                lastModifiedLong = Date.parse(lastModified);
            }

            if (cliInstallDirectory.exists() && cliInstallDirectory.listFiles().length > 0) {
                if (!cliMismatch && lastModifiedLong == cliTimestamp) {
                    logger.debug("The current Hub CLI is up to date.");
                    return;
                }
                for (final File file : cliInstallDirectory.listFiles()) {
                    FileUtils.deleteDirectory(file);
                }
            } else {
                cliInstallDirectory.mkdir();
            }

            logger.debug("Updating the Hub CLI.");
            hubVersionFile.setLastModified(lastModifiedLong);

            logger.info("Unpacking " + archive.toString() + " to " + directoryToInstallTo + " on "
                    + localHostName);
            ResponseBody responseBody = null;
            InputStream cliStream = null;
            try {
                responseBody = response.body();
                cliStream = responseBody.byteStream();
                final CountingInputStream cis = new CountingInputStream(cliStream);
                try {
                    unzip(cliInstallDirectory, cis, logger);
                    updateJreSecurity(logger, cliLocation, ciEnvironmentVariables);
                } catch (final IOException e) {
                    throw new HubIntegrationException(String.format("Failed to unpack %s (%d bytes read of total %d)", archive,
                            cis.getByteCount(), responseBody.contentLength()), e);
                }
            } finally {
                if (responseBody != null) {
                    responseBody.close();
                }
                if (cliStream != null) {
                    cliStream.close();
                }
            }
        } catch (final IOException e) {
            throw new HubIntegrationException("Failed to install " + archive + " to " + directoryToInstallTo, e);
        }
    }

    private void updateJreSecurity(final IntLogger logger, CLILocation cliLocation, CIEnvironmentVariables ciEnvironmentVariables) throws IOException {
        final String cacertsFilename = "cacerts";
        if (ciEnvironmentVariables.containsKey(CIEnvironmentVariables.BDS_CACERTS_OVERRIDE)) {
            final File securityDirectory = cliLocation.getJreSecurityDirectory();
            if (securityDirectory == null) {
                // the cli might not have the jre included
                return;
            }
            final String customCacertsPath = ciEnvironmentVariables
                    .getValue(CIEnvironmentVariables.BDS_CACERTS_OVERRIDE);
            final File customCacerts = new File(customCacertsPath);

            final File cacerts = new File(securityDirectory, cacertsFilename);
            final File cacertsBackup = new File(securityDirectory, cacertsFilename + System.currentTimeMillis());

            try {
                FileUtils.moveFile(cacerts, cacertsBackup);
                FileUtils.copyFile(customCacerts, cacerts);
            } catch (final IOException e) {
                logger.error("Could not copy the custom cacerts file from: " + customCacertsPath + " to: "
                        + cacerts.getAbsolutePath() + " msg: " + e.getMessage());
                throw e;
            }
        }
    }

    private void unzip(final File dir, final InputStream in, final IntLogger logger) throws IOException {
        // uses java.io.tmpdir
        final File tmpFile = File.createTempFile("tmpzip", null);

        try {
            copyInputStreamToFile(in, tmpFile);
            unzip(dir, tmpFile, logger);
        } finally {
            tmpFile.delete();
        }
    }

    private void unzip(File dir, final File zipFile, final IntLogger logger) throws IOException {
        // without getAbsoluteFile, getParentFile below seems to fail
        dir = dir.getAbsoluteFile();
        final ZipFile zip = new ZipFile(zipFile);
        final Enumeration<ZipEntry> entries = zip.getEntries();
        try {
            while (entries.hasMoreElements()) {
                final ZipEntry e = entries.nextElement();
                final File f = new File(dir, e.getName());
                if (e.isDirectory()) {
                    f.mkdirs();
                } else {
                    final File p = f.getParentFile();
                    if (p != null) {
                        p.mkdirs();
                    }
                    final InputStream input = zip.getInputStream(e);
                    try {
                        copyInputStreamToFile(input, f);
                    } finally {
                        input.close();
                    }
                    f.setLastModified(e.getTime());
                }
            }
        } finally {
            zip.close();
        }
    }

    private void copyInputStreamToFile(final InputStream in, final File f) throws IOException {
        final FileOutputStream fos = new FileOutputStream(f);
        try {
            org.apache.commons.io.IOUtils.copy(in, fos);
        } finally {
            org.apache.commons.io.IOUtils.closeQuietly(fos);
        }
    }

}
