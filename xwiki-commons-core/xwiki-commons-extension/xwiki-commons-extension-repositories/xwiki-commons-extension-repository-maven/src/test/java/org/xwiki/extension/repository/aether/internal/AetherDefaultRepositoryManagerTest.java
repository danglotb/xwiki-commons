/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.extension.repository.aether.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.Assert;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.xwiki.extension.DefaultExtensionAuthor;
import org.xwiki.extension.DefaultExtensionDependency;
import org.xwiki.extension.Extension;
import org.xwiki.extension.ExtensionAuthor;
import org.xwiki.extension.ExtensionDependency;
import org.xwiki.extension.ExtensionException;
import org.xwiki.extension.ExtensionId;
import org.xwiki.extension.ExtensionLicenseManager;
import org.xwiki.extension.ResolveException;
import org.xwiki.extension.repository.ExtensionRepositoryManager;
import org.xwiki.extension.repository.result.IterableResult;
import org.xwiki.extension.test.RepositoryUtil;
import org.xwiki.extension.version.Version;
import org.xwiki.extension.version.internal.DefaultVersionConstraint;
import org.xwiki.test.jmock.AbstractComponentTestCase;

public class AetherDefaultRepositoryManagerTest extends AbstractComponentTestCase
{
    private static final String GROUPID = "groupid";

    private static final String ARTIfACTID = "artifactid";

    private ExtensionRepositoryManager repositoryManager;

    private ExtensionId extensionId;

    private ExtensionId snapshotExtensionId;

    private ExtensionId extensionDependencyId;

    private ExtensionId extensionIdClassifier;

    private ExtensionDependency dependencyExtensionId;

    private ExtensionDependency dependencyExtensionIdRange;

    private ExtensionLicenseManager extensionLicenseManager;

    private ExtensionId bundleExtensionId;

    private ExtensionId sextensionId;

    private ExtensionId sextensionDependencyId;

    private RepositoryUtil repositoryUtil;

    @Override
    @Before
    public void setUp() throws Exception
    {
        super.setUp();

        this.repositoryUtil = new RepositoryUtil(getComponentManager(), getMockery());
        this.repositoryUtil.setup();

        this.extensionId = new ExtensionId(GROUPID + ':' + ARTIfACTID, "version");
        this.snapshotExtensionId = new ExtensionId(GROUPID + ':' + ARTIfACTID, "1.0-SNAPSHOT");
        this.extensionDependencyId = new ExtensionId("dgroupid:dartifactid", "dversion");

        this.extensionIdClassifier = new ExtensionId(GROUPID + ':' + ARTIfACTID + ":classifier", "version");
        this.dependencyExtensionId =
            new DefaultExtensionDependency(this.extensionDependencyId.getId(), new DefaultVersionConstraint(
                this.extensionDependencyId.getVersion().getValue()));
        this.dependencyExtensionIdRange =
            new DefaultExtensionDependency(this.extensionDependencyId.getId(), new DefaultVersionConstraint(
                "[dversion,)"));

        this.bundleExtensionId = new ExtensionId("groupid:bundleartifactid", "version");

        this.sextensionId = new ExtensionId("sgroupid:sartifactid", "version");
        this.sextensionDependencyId = new ExtensionId("sgroupid:sdartifactid", "version");

        // lookup

        this.repositoryManager = getComponentManager().getInstance(ExtensionRepositoryManager.class);
        this.extensionLicenseManager = getComponentManager().getInstance(ExtensionLicenseManager.class);
    }

    // Tests

    @Test
    public void testResolve() throws ResolveException, IOException
    {
        Extension extension = this.repositoryManager.resolve(this.extensionId);

        Assert.assertNotNull(extension);
        Assert.assertEquals(this.extensionId.getId(), extension.getId().getId());
        Assert.assertEquals(this.extensionId.getVersion(), extension.getId().getVersion());
        Assert.assertEquals("type", extension.getType());
        Assert.assertEquals(this.repositoryUtil.getMavenRepositoryId(), extension.getRepository().getDescriptor()
            .getId());
        Assert.assertEquals("name", extension.getName());
        Assert.assertEquals("summary", extension.getSummary());
        Assert.assertEquals("http://website", extension.getWebSite());
        Assert.assertEquals(Arrays.asList(new DefaultExtensionAuthor("Full Name", new URL("http://profile"))),
            new ArrayList<ExtensionAuthor>(extension.getAuthors()));
        Assert.assertEquals(Arrays.asList("groupid1:feature1", "groupid2:feature2"),
            new ArrayList<String>(extension.getFeatures()));
        Assert.assertSame(this.extensionLicenseManager.getLicense("GNU Lesser General Public License 2.1"), extension
            .getLicenses().iterator().next());

        ExtensionDependency dependency = extension.getDependencies().iterator().next();
        Assert.assertEquals(this.dependencyExtensionId.getId(), dependency.getId());
        Assert.assertEquals(this.dependencyExtensionId.getVersionConstraint(), dependency.getVersionConstraint());

        // check that a new resolve of an already resolved extension provide the proper repository
        extension = this.repositoryManager.resolve(this.extensionId);
        Assert.assertEquals(this.repositoryUtil.getMavenRepositoryId(), extension.getRepository().getDescriptor()
            .getId());

        // TODO: see http://jira.xwiki.org/browse/XWIKI-7163 // Modify the file on the descriptor on the repository
        // File pomFile =
        // new File(this.repositoryUtil.getMavenRepository(), this.extensionId.getId().replace('.', '/')
        // .replace(':', '/')
        // + '/' + this.extensionId.getVersion() + '/' + ARTIfACTID + '-' + this.extensionId.getVersion() + ".pom");
        // FileUtils.writeStringToFile(
        // pomFile,
        // FileUtils.readFileToString(pomFile, "UTF-8").replace("<description>summary</description>",
        // "<description>modified summary</description>"), "UTF-8");
        // extension = this.repositoryManager.resolve(this.extensionId);
        // Assert.assertEquals("modified description", extension.getSummary());
    }

    @Test
    public void testResolveSNAPSHOT() throws ResolveException
    {
        Extension extension = this.repositoryManager.resolve(this.snapshotExtensionId);

        Assert.assertNotNull(extension);
        Assert.assertEquals(this.snapshotExtensionId.getId(), extension.getId().getId());
        Assert.assertEquals(this.snapshotExtensionId.getVersion(), extension.getId().getVersion());
        Assert.assertEquals("type", extension.getType());
        Assert.assertEquals(this.repositoryUtil.getMavenRepositoryId(), extension.getRepository().getDescriptor()
            .getId());
    }

    @Test
    public void testResolveAetherDependency() throws ResolveException
    {
        Artifact artifact = new DefaultArtifact("groupid", "artifactid", "", "type", "version");
        Dependency aetherDependency = new Dependency(artifact, null);
        AetherExtensionDependency dependency = new AetherExtensionDependency(aetherDependency, null);

        Extension extension = this.repositoryManager.resolve(dependency);

        Assert.assertNotNull(extension);
        Assert.assertEquals(this.extensionId.getId(), extension.getId().getId());
        Assert.assertEquals(this.extensionId.getVersion(), extension.getId().getVersion());
        Assert.assertEquals("type", extension.getType());
    }

    @Test
    public void testResolveAetherDependencySNAPSHOT() throws ResolveException
    {
        Artifact artifact = new DefaultArtifact("groupid", "artifactid", "", "type", "1.0-SNAPSHOT");
        Dependency aetherDependency = new Dependency(artifact, null);
        AetherExtensionDependency dependency = new AetherExtensionDependency(aetherDependency, null);

        Extension extension = this.repositoryManager.resolve(dependency);

        Assert.assertNotNull(extension);
        Assert.assertEquals(this.snapshotExtensionId.getId(), extension.getId().getId());
        Assert.assertEquals(this.snapshotExtensionId.getVersion(), extension.getId().getVersion());
        Assert.assertEquals("type", extension.getType());
        Assert.assertEquals(this.repositoryUtil.getMavenRepositoryId(), extension.getRepository().getDescriptor()
            .getId());
    }

    @Test
    public void testResolveOverriddenProperties() throws ResolveException
    {
        Extension extension = this.repositoryManager.resolve(new ExtensionId("groupid:oartifactid", "version"));

        Assert.assertNotNull(extension);
        Assert.assertEquals("oname", extension.getName());
        Assert.assertEquals("osummary", extension.getSummary());
        Assert.assertEquals("http://owebsite", extension.getWebSite());
    }

    /**
     * Make sure any <code>version</code> property coming from system properties will not be resolved instead of the
     * actual pom version.
     */
    @Test
    public void testResolveWithVersionAsSystemProperty() throws ResolveException
    {
        System.setProperty("version", "systemversion");
        System.setProperty("groupId", "systemgroupId");

        Extension extension = this.repositoryManager.resolve(this.sextensionId);

        Assert.assertNotNull(extension);
        Assert.assertEquals(this.sextensionId.getId(), extension.getId().getId());
        Assert.assertEquals(this.sextensionId.getVersion(), extension.getId().getVersion());

        ExtensionDependency dependency = extension.getDependencies().iterator().next();
        Assert.assertEquals(this.sextensionDependencyId.getId(), dependency.getId());
        Assert.assertEquals(this.sextensionDependencyId.getVersion().getValue(), dependency.getVersionConstraint()
            .getValue());
    }

    @Test
    public void testResolveVersionClassifier() throws ResolveException
    {
        Extension extension = this.repositoryManager.resolve(this.extensionIdClassifier);

        Assert.assertNotNull(extension);
        Assert.assertEquals(this.extensionIdClassifier.getId(), extension.getId().getId());
        Assert.assertEquals(this.extensionIdClassifier.getVersion(), extension.getId().getVersion());
    }

    @Test
    public void testResolveVersionRange() throws ResolveException
    {
        Extension extension = this.repositoryManager.resolve(this.dependencyExtensionIdRange);

        Assert.assertNotNull(extension);
        Assert.assertEquals(this.extensionDependencyId.getId(), extension.getId().getId());
        Assert.assertEquals(this.extensionDependencyId.getVersion(), extension.getId().getVersion());
    }

    @Test
    public void testDownload() throws ExtensionException, IOException
    {
        Extension extension = this.repositoryManager.resolve(this.extensionId);

        InputStream is = extension.getFile().openStream();

        try {
            Assert.assertEquals("content", IOUtils.toString(is));
        } finally {
            is.close();
        }

        // Make sure it has been removed from AETHER cache
        String filePrefix =
            "aether-repository/" + GROUPID + '/' + ARTIfACTID + '/' + extension.getId().getVersion() + '/' + ARTIfACTID
                + '-' + extension.getId().getVersion();
        File pomFile = new File(this.repositoryUtil.getTemporaryDirectory(), filePrefix + ".pom");
        Assert.assertTrue("Can't find file " + pomFile, pomFile.exists());
        Assert
            .assertFalse(new File(this.repositoryUtil.getTemporaryDirectory(), filePrefix + '.' + extension.getType())
                .exists());
    }

    @Test
    public void testDownloadSNAPSHOT() throws ExtensionException, IOException
    {
        Extension extension = this.repositoryManager.resolve(this.snapshotExtensionId);

        InputStream is = extension.getFile().openStream();

        try {
            Assert.assertEquals("snapshot content", IOUtils.toString(is));
        } finally {
            is.close();
        }
    }

    @Test
    public void testDownloadClassifier() throws ExtensionException, IOException
    {
        Extension extension = this.repositoryManager.resolve(this.extensionIdClassifier);

        InputStream is = extension.getFile().openStream();

        try {
            Assert.assertEquals("classifier content", IOUtils.toString(is));
        } finally {
            is.close();
        }
    }

    @Test
    public void testDownloadBundle() throws ExtensionException, IOException
    {
        Extension extension = this.repositoryManager.resolve(this.bundleExtensionId);

        InputStream is = extension.getFile().openStream();

        try {
            Assert.assertEquals("content", IOUtils.toString(is));
        } finally {
            is.close();
        }
    }

    @Test
    public void testResolveVersions() throws ExtensionException
    {
        IterableResult<Version> versions = this.repositoryManager.resolveVersions(this.extensionId.getId(), 0, -1);

        Assert.assertEquals(2, versions.getTotalHits());
        Assert.assertEquals(2, versions.getSize());
        Assert.assertEquals(0, versions.getOffset());
    }
}
