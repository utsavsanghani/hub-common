package com.blackducksoftware.integration.hub.swagger;

import com.blackducksoftware.integration.exception.EncryptionException;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.squareup.okhttp.OkHttpClient;

import io.swagger.client.ApiClient;
import io.swagger.client.api.AggregatebomrestserverApi;
import io.swagger.client.api.CodelocationrestserverApi;
import io.swagger.client.api.ComponentrestserverApi;
import io.swagger.client.api.ComponentversionrestserverApi;
import io.swagger.client.api.CompositecodelocationrestserverApi;
import io.swagger.client.api.LicenserestserverApi;
import io.swagger.client.api.LinkeddatarestserverApi;
import io.swagger.client.api.MatchedfilerestserverApi;
import io.swagger.client.api.PolicyrulerestserverApi;
import io.swagger.client.api.ProjectrestserverApi;
import io.swagger.client.api.ProjectversionrestserverApi;
import io.swagger.client.api.ReportrestserverApi;
import io.swagger.client.api.RolerestserverApi;
import io.swagger.client.api.ScanrestserverApi;
import io.swagger.client.api.UserrestserverApi;
import io.swagger.client.api.VersionbompolicyrestserverApi;
import io.swagger.client.api.VersionriskprofileApi;
import io.swagger.client.api.VulnerabilityrestserverApi;
import io.swagger.client.api.VulnerablecomponentrestserverApi;

public class ApiServicesFactory {

	private final ApiClient apiClient;

	private final ProjectrestserverApi projectsApi;
	private final ProjectversionrestserverApi versionApi;
	private final CodelocationrestserverApi codeLocationsApi;
	private final VersionriskprofileApi versionRiskProfileApi;
	private final ReportrestserverApi versionReportApi;
	private final VersionbompolicyrestserverApi versionBomPolicyApi;
	private final LinkeddatarestserverApi linkedDataApi;
	private final CompositecodelocationrestserverApi compositeApi;
	private final VulnerablecomponentrestserverApi vulnerableComponentsApi;
	private final ComponentrestserverApi componentsApi;
	private final ComponentversionrestserverApi componentVersionApi;
	private final ScanrestserverApi scanStatusApi;
	private final LicenserestserverApi licenseApi;
	private final MatchedfilerestserverApi matchedFilesApi;
	private final AggregatebomrestserverApi aggregateBomApi;
	private final PolicyrulerestserverApi policyRuleApi;
	private final RolerestserverApi roleApi;
	private final VulnerabilityrestserverApi vulnerabilityApi;
	private final UserrestserverApi userApi;

	public ApiServicesFactory(final HubServerConfig serverConfig) throws IllegalArgumentException, EncryptionException, HubIntegrationException {
		apiClient = new ApiClient();
		CredentialsRestConnection crc = new CredentialsRestConnection(serverConfig);
		crc.connect();
		OkHttpClient ourOkHttpClient = crc.getClient();
		// Create an apiClient that can connect to the hub
		apiClient.setBasePath(serverConfig.getHubUrl().toString());
		apiClient.setHttpClient(ourOkHttpClient);

		projectsApi = new ProjectrestserverApi(apiClient);
		versionApi = new ProjectversionrestserverApi(apiClient);
		codeLocationsApi = new CodelocationrestserverApi(apiClient);
		versionRiskProfileApi = new VersionriskprofileApi(apiClient);
		versionReportApi = new ReportrestserverApi(apiClient);
		versionBomPolicyApi = new VersionbompolicyrestserverApi(apiClient);
		linkedDataApi = new LinkeddatarestserverApi(apiClient);
		compositeApi = new CompositecodelocationrestserverApi(apiClient);
		vulnerableComponentsApi = new VulnerablecomponentrestserverApi(apiClient);
		componentsApi = new ComponentrestserverApi(apiClient);
		componentVersionApi = new ComponentversionrestserverApi(apiClient);
		scanStatusApi = new ScanrestserverApi(apiClient);
		licenseApi = new LicenserestserverApi(apiClient);
		matchedFilesApi = new MatchedfilerestserverApi(apiClient);
		aggregateBomApi = new AggregatebomrestserverApi(apiClient);
		policyRuleApi = new PolicyrulerestserverApi(apiClient);
		roleApi = new RolerestserverApi(apiClient);
		vulnerabilityApi = new VulnerabilityrestserverApi(apiClient);
		userApi = new UserrestserverApi(apiClient);
	}

	public ApiClient getApiClient() {
		return apiClient;
	}

	public ProjectrestserverApi getProjectsApi() {
		return projectsApi;
	}

	public ProjectversionrestserverApi getVersionApi() {
		return versionApi;
	}

	public CodelocationrestserverApi getCodeLocationsApi() {
		return codeLocationsApi;
	}

	public VersionriskprofileApi getVersionRiskProfileApi() {
		return versionRiskProfileApi;
	}

	public ReportrestserverApi getVersionReportApi() {
		return versionReportApi;
	}

	public VersionbompolicyrestserverApi getVersionBomPolicyApi() {
		return versionBomPolicyApi;
	}

	public LinkeddatarestserverApi getLinkedDataApi() {
		return linkedDataApi;
	}

	public CompositecodelocationrestserverApi getCompositeApi() {
		return compositeApi;
	}

	public VulnerablecomponentrestserverApi getVulnerableComponentsApi() {
		return vulnerableComponentsApi;
	}

	public ComponentrestserverApi getComponentsApi() {
		return componentsApi;
	}

	public ComponentversionrestserverApi getComponentVersionApi() {
		return componentVersionApi;
	}

	public ScanrestserverApi getScanStatusApi() {
		return scanStatusApi;
	}

	public LicenserestserverApi getLicenseApi() {
		return licenseApi;
	}

	public MatchedfilerestserverApi getMatchedFilesApi() {
		return matchedFilesApi;
	}

	public AggregatebomrestserverApi getAggregateBomApi() {
		return aggregateBomApi;
	}

	public PolicyrulerestserverApi getPolicyRuleApi() {
		return policyRuleApi;
	}

	public RolerestserverApi getRoleApi() {
		return roleApi;
	}

	public VulnerabilityrestserverApi getVulnerabilityApi() {
		return vulnerabilityApi;
	}

	public UserrestserverApi getUserApi() {
		return userApi;
	}
}
