package com.blackducksoftware.integration.hub.dataservices.transforms;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.blackducksoftware.integration.hub.api.ComponentVersionRestService;
import com.blackducksoftware.integration.hub.api.NotificationRestService;
import com.blackducksoftware.integration.hub.api.PolicyRestService;
import com.blackducksoftware.integration.hub.api.ProjectVersionRestService;
import com.blackducksoftware.integration.hub.api.VersionBomPolicyRestService;
import com.blackducksoftware.integration.hub.api.component.ComponentVersionStatus;
import com.blackducksoftware.integration.hub.api.notification.NotificationItem;
import com.blackducksoftware.integration.hub.api.notification.RuleViolationNotificationItem;
import com.blackducksoftware.integration.hub.api.policy.PolicyRule;
import com.blackducksoftware.integration.hub.api.project.ProjectVersion;
import com.blackducksoftware.integration.hub.api.version.ReleaseItem;
import com.blackducksoftware.integration.hub.dataservices.items.NotificationContentItem;
import com.blackducksoftware.integration.hub.dataservices.items.PolicyNotificationFilter;
import com.blackducksoftware.integration.hub.dataservices.items.PolicyViolationContentItem;
import com.blackducksoftware.integration.hub.exception.BDRestException;
import com.blackducksoftware.integration.hub.exception.HubItemTransformException;

public class PolicyViolationTransform extends AbstractPolicyTransform {

	private final Map<String, ReleaseItem> releaseItemMap = new ConcurrentHashMap<>();

	public PolicyViolationTransform(final NotificationRestService notificationService,
			final ProjectVersionRestService projectVersionService, final PolicyRestService policyService,
			final VersionBomPolicyRestService bomVersionPolicyService,
			final ComponentVersionRestService componentVersionService, final PolicyNotificationFilter policyFilter) {
		super(notificationService, projectVersionService, policyService, bomVersionPolicyService,
				componentVersionService, policyFilter);
	}

	@Override
	public List<NotificationContentItem> transform(final NotificationItem item) throws HubItemTransformException {
		final List<NotificationContentItem> templateData = new ArrayList<>();
		try {
			final RuleViolationNotificationItem policyViolation = (RuleViolationNotificationItem) item;
			final String projectName = policyViolation.getContent().getProjectName();
			final List<ComponentVersionStatus> componentVersionList = policyViolation.getContent()
					.getComponentVersionStatuses();
			final String projectVersionLink = policyViolation.getContent().getProjectVersionLink();
			final ReleaseItem releaseItem = getReleaseItem(projectVersionLink);
			final ProjectVersion projectVersion = new ProjectVersion();
			projectVersion.setProjectName(projectName);
			projectVersion.setProjectVersionName(releaseItem.getVersionName());
			projectVersion.setProjectVersionLink(policyViolation.getContent().getProjectVersionLink());

			handleNotification(componentVersionList, projectVersion, item, templateData);
		} catch (final IOException | BDRestException | URISyntaxException e) {
			throw new HubItemTransformException(e);
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}

		return templateData;
	}

	private ReleaseItem getReleaseItem(final String projectVersionLink)
			throws IOException, BDRestException, URISyntaxException {
		ReleaseItem releaseItem;
		if (releaseItemMap.containsKey(projectVersionLink)) {
			releaseItem = releaseItemMap.get(projectVersionLink);
		} else {
			releaseItem = getProjectVersionService().getProjectVersionReleaseItem(projectVersionLink);
			releaseItemMap.put(projectVersionLink, releaseItem);
		}
		return releaseItem;
	}

	@Override
	public void createContents(final ProjectVersion projectVersion, final String componentName,
			final String componentVersion, final UUID componentId, final UUID componentVersionId,
			final List<PolicyRule> policyRuleList, final NotificationItem item,
			final List<NotificationContentItem> templateData) {
		templateData.add(new PolicyViolationContentItem(projectVersion, componentName, componentVersion, componentId,
				componentVersionId, policyRuleList));
	}

	@Override
	public void reset() {
		super.reset();
		releaseItemMap.clear();
	}
}
