package com.blackducksoftware.integration.hub.swagger.dataservices.scanstatus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.blackducksoftware.integration.hub.api.scan.ScanStatus;
import com.blackducksoftware.integration.hub.api.scan.ScanSummaryItem;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.exception.HubTimeoutExceededException;
import com.blackducksoftware.integration.hub.swagger.ApiServicesFactory;
import com.blackducksoftware.integration.hub.swagger.DataService;
import com.blackducksoftware.integration.hub.swagger.MetaService;
import com.blackducksoftware.integration.log.IntLogger;

import io.swagger.client.api.CodelocationrestserverApi;
import io.swagger.client.api.ProjectrestserverApi;
import io.swagger.client.api.ProjectversionrestserverApi;
import io.swagger.client.api.ScanrestserverApi;
import io.swagger.client.model.ProjectView;
import io.swagger.client.model.ScanSummaryView;

public class ScanStatusDataService extends DataService {
	private static final long FIVE_SECONDS = 5 * 1000;

	private final ProjectrestserverApi projectRequestService;
	private final ProjectversionrestserverApi projectVersionRequestService;
	private final CodelocationrestserverApi codeLocationRequestService;
	private final ScanrestserverApi scanSummaryRequestService;
	private final MetaService metaService;

	public ScanStatusDataService(final ApiServicesFactory apiFactory, final MetaService metaService) {
		super(apiFactory);
		this.metaService = metaService;
		this.projectRequestService = apiFactory.getProjectsApi();
		this.projectVersionRequestService = apiFactory.getVersionApi();
		this.codeLocationRequestService = apiFactory.getCodeLocationsApi();
		this.scanSummaryRequestService = apiFactory.getScanStatusApi();
	}

	/**
	 * For the provided projectName and projectVersion, wait at most
	 * scanStartedTimeoutInMilliseconds for the project/version to exist and/or
	 * at least one pending bom import scan to begin. Then, wait at most
	 * scanFinishedTimeoutInMilliseconds for all discovered pending scans to
	 * complete.
	 *
	 * If the timeouts are exceeded, a HubTimeoutExceededException will be
	 * thrown.
	 *
	 * @param projectRequestService
	 * @param projectVersionRequestService
	 * @param codeLocationRequestService
	 * @param scanSummaryRequestService
	 * @param projectName
	 * @param projectVersion
	 * @param scanStartedTimeoutInMilliseconds
	 * @param scanFinishedTimeoutInMilliseconds
	 * @param logger
	 * @throws IOException
	 * @throws BDRestException
	 * @throws URISyntaxException
	 * @throws ProjectDoesNotExistException
	 * @throws UnexpectedHubResponseException
	 * @throws HubIntegrationException
	 * @throws HubTimeoutExceededException
	 * @throws InterruptedException
	 */
	public void assertBomImportScanStartedThenFinished(final String projectName, final String projectVersion, final long scanStartedTimeoutInMilliseconds, final long scanFinishedTimeoutInMilliseconds, final IntLogger logger)
			throws HubTimeoutExceededException, HubIntegrationException {
		final List<ScanSummaryView> pendingScans = waitForPendingScansToStart(projectName, projectVersion, scanStartedTimeoutInMilliseconds);
		waitForScansToComplete(pendingScans, scanFinishedTimeoutInMilliseconds);
	}

	/**
	 * For the given pendingScans, wait at most
	 * scanFinishedTimeoutInMilliseconds for the scans to complete.
	 *
	 * If the timeout is exceeded, a HubTimeoutExceededException will be thrown.
	 *
	 * @param scanSummaryRequestService
	 * @param scanSummaries
	 * @param scanFinishedTimeoutInMilliseconds
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws BDRestException
	 * @throws URISyntaxException
	 * @throws HubIntegrationException
	 * @throws ProjectDoesNotExistException
	 * @throws UnexpectedHubResponseException
	 * @throws HubTimeoutExceededException
	 */
	public void assertBomImportScansFinished(final List<ScanSummaryView> scanSummaries, final long scanStartedTimeoutInMilliseconds) throws HubTimeoutExceededException, HubIntegrationException {
		waitForScansToComplete(scanSummaries, scanStartedTimeoutInMilliseconds);
	}

	private void waitForScansToComplete(List<ScanSummaryView> pendingScans, long scanStartedTimeoutInMilliseconds) throws HubTimeoutExceededException, HubIntegrationException {
		pendingScans = getPendingScans(pendingScans);
		final long startedTime = System.currentTimeMillis();
		boolean pendingScansOk = pendingScans.isEmpty();
		while (!done(pendingScansOk, scanStartedTimeoutInMilliseconds, startedTime, "The pending scans have not completed within the specified wait time: %d minutes")) {
			try {
				Thread.sleep(FIVE_SECONDS);
			} catch (final InterruptedException e) {
				throw new HubIntegrationException("The thread waiting for the scan to complete was interrupted: " + e.getMessage(), e);
			}
			pendingScans = getPendingScans(pendingScans);
			pendingScansOk = pendingScans.isEmpty();
		}
	}

	private List<ScanSummaryView> getPendingScans(final List<ScanSummaryView> scanSummaries) throws HubIntegrationException {
		final List<ScanSummaryView> pendingScans = new ArrayList<>();
		for (final ScanSummaryView scanSummaryView : scanSummaries) {
			// final String scanSummaryLink = metaService.getHref(scanSummaryItem);
			// final ScanSummaryItem currentScanSummaryItem = scanSummaryRequestService.getItem(scanSummaryLink);
			ScanStatus status = ScanStatus.getScanStatus(scanSummaryView.getStatus().toString());
			if (status.isPending()) {
				pendingScans.add(scanSummaryView);
			} else if (status.isError()) {
				throw new HubIntegrationException("There was a problem with one of the scans. Error Status : " + status.toString());
			}
		}
		return pendingScans;
	}

	private List<ScanSummaryView> waitForPendingScansToStart(final String projectName, final String projectVersion, final long scanStartedTimeoutInMilliseconds) throws HubIntegrationException {
		List<ScanSummaryView> pendingScans = getPendingScans(projectName, projectVersion);
		final long startedTime = System.currentTimeMillis();
		boolean pendingScansOk = pendingScans.size() > 0;
		while (!done(pendingScansOk, scanStartedTimeoutInMilliseconds, startedTime, "No scan has started within the specified wait time: %d minutes")) {
			try {
				Thread.sleep(FIVE_SECONDS);
			} catch (final InterruptedException e) {
				throw new HubIntegrationException("The thread waiting for the scan to start was interrupted: " + e.getMessage(), e);
			}
			pendingScans = getPendingScans(projectName, projectVersion);
			pendingScansOk = pendingScans.size() > 0;
		}

		return pendingScans;
	}

	private List<ScanSummaryView> getPendingScans(final String projectName, final String projectVersion) {
		List<ScanSummaryView> pendingScans = new ArrayList<>();
		try {
			final ProjectView projectItem = projectRequestService.findProjectsUsingGET(1, 0, null, "name:" + projectName).getItems().get(0);
			// final ProjectVersionView projectVersionItem = projectVersionRequestService.get
			// final String projectVersionUrl = metaService.getHref(projectVersionItem);

			// final List<CodeLocationItem> allCodeLocations = codeLocationRequestService.getAllCodeLocationsForCodeLocationType(CodeLocationTypeEnum.BOM_IMPORT);

			final List<String> allScanSummariesLinks = new ArrayList<>();
			// for (final CodeLocationItem codeLocationItem : allCodeLocations) {
			// final String mappedProjectVersionUrl = codeLocationItem.getMappedProjectVersion();
			// if (projectVersionUrl.equals(mappedProjectVersionUrl)) {
			//// final String scanSummariesLink = metaService.getLink(codeLocationItem, MetaService.SCANS_LINK);
			//// allScanSummariesLinks.add(scanSummariesLink);
			// }
			// }

			final List<ScanSummaryItem> allScanSummaries = new ArrayList<>();
			for (final String scanSummaryLink : allScanSummariesLinks) {
				// allScanSummaries.addAll(scanSummaryRequestService.getAllScanSummaryItems(scanSummaryLink));
			}

			pendingScans = new ArrayList<>();
			for (final ScanSummaryItem scanSummaryItem : allScanSummaries) {
				if (scanSummaryItem.getStatus().isPending()) {
					// pendingScans.add(scanSummaryItem);
				}
			}
		} catch (final Exception e) {
			pendingScans = new ArrayList<>();
			// ignore, since we might not have found a project or version, etc
			// so just keep waiting until the timeout
		}

		return pendingScans;
	}

	private boolean done(final boolean pendingScansOk, final long timeoutInMilliseconds, final long startedTime, final String timeoutMessage) throws HubTimeoutExceededException {
		if (pendingScansOk) {
			return true;
		}

		if (takenTooLong(timeoutInMilliseconds, startedTime)) {
			throw new HubTimeoutExceededException(String.format(timeoutMessage, TimeUnit.MILLISECONDS.toMinutes(timeoutInMilliseconds)));
		}

		return false;
	}

	private boolean takenTooLong(final long timeoutInMilliseconds, final long startedTime) {
		final long elapsed = System.currentTimeMillis() - startedTime;
		return elapsed > timeoutInMilliseconds;
	}

}
