package org.talend.esb.locator.zookeeper.server;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:META-INF/spring-context.xml" })
@Configuration
@PropertySource("classpath:META-INF/zookeeper.properties")
@DirtiesContext
public class ZookeeperServerManagerTest {

	@Value("${dataDir}")
	String pathToZookeeperDataDir;

	@Test
	public void testZookeeperServer() {

		// Check if ZooKeeper data directory exists and not empty

		File dataDir = new File(pathToZookeeperDataDir);
		Assert.assertTrue(dataDir.exists());
		Assert.assertTrue(dataDir.isDirectory());
		Assert.assertTrue(dataDir.listFiles().length > 0);

		// TODO: perform another tests
	}
}