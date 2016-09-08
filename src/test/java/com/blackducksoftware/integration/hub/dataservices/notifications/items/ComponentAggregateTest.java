package com.blackducksoftware.integration.hub.dataservices.notifications.items;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import com.blackducksoftware.integration.hub.api.notification.VulnerabilitySourceQualifiedId;
import com.blackducksoftware.integration.hub.api.policy.PolicyRule;
import com.blackducksoftware.integration.hub.api.project.ProjectVersion;
import com.blackducksoftware.integration.hub.dataservices.notification.items.ComponentAggregateData;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyOverrideContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.PolicyViolationContentItem;
import com.blackducksoftware.integration.hub.dataservices.notification.items.VulnerabilityContentItem;

public class ComponentAggregateTest {

	@Test
	public void testComponentAggregateCount() {
		final ProjectVersion projectVersion = new ProjectVersion();
		projectVersion.setProjectName("Project Name");
		projectVersion.setProjectVersionName("1.0");
		projectVersion.setProjectVersionLink("versionLink");
		final String componentName = "componentName";
		final String componentVersion = "componentVersion";
		final String firstName = "firstName";
		final String lastName = "lastName";
		final UUID componentId = UUID.randomUUID();
		final UUID componentVersionId = UUID.randomUUID();
		final int total = 3;
		final List<PolicyViolationContentItem> violationList = new ArrayList<>();
		final List<PolicyOverrideContentItem> overrideList = new ArrayList<>();
		final List<VulnerabilityContentItem> vulnerabilityList = new ArrayList<>();
		final PolicyRule rule = new PolicyRule(null, "aRule", "", true, true, null, "", "", "", "");
		final List<PolicyRule> ruleList = new ArrayList<>();
		ruleList.add(rule);
		final PolicyViolationContentItem violationContent = new PolicyViolationContentItem(projectVersion,
				componentName, componentVersion, componentId, componentVersionId, ruleList);
		final PolicyOverrideContentItem overrideContent = new PolicyOverrideContentItem(projectVersion, componentName,
				componentVersion, componentId, componentVersionId, ruleList, firstName, lastName);

		final List<VulnerabilitySourceQualifiedId> sourceIdList = new ArrayList<>();
		sourceIdList.add(new VulnerabilitySourceQualifiedId("source", "id"));
		final VulnerabilityContentItem vulnerabilityContent = new VulnerabilityContentItem(projectVersion,
				componentName, componentVersion, componentId, componentVersionId, sourceIdList, sourceIdList,
				sourceIdList);
		violationList.add(violationContent);
		overrideList.add(overrideContent);
		vulnerabilityList.add(vulnerabilityContent);
		final int vulnSize = sourceIdList.size();
		final ComponentAggregateData data = new ComponentAggregateData(componentName, componentVersion, violationList,
				overrideList, vulnerabilityList, vulnSize, vulnSize, vulnSize);

		assertNotNull(data);
		assertEquals(total, data.getTotal());
		assertEquals(componentName, data.getComponentName());
		assertEquals(componentVersion, data.getComponentVersion());
		assertEquals(violationList.size(), data.getPolicyViolationCount());
		assertEquals(overrideList.size(), data.getPolicyOverrideCount());
		assertEquals(vulnerabilityList.size(), data.getVulnerabilityCount());
		assertEquals(sourceIdList.size(), data.getVulnAddedCount());
		assertEquals(sourceIdList.size(), data.getVulnUpdatedCount());
		assertEquals(sourceIdList.size(), data.getVulnDeletedCount());
		assertEquals(violationList, data.getPolicyViolationList());
		assertEquals(overrideList, data.getPolicyOverrideList());
		assertEquals(vulnerabilityList, data.getVulnerabilityList());
	}
}