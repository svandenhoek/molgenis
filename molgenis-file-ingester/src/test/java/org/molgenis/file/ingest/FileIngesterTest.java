package org.molgenis.file.ingest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.molgenis.data.DatabaseAction.ADD_UPDATE_EXISTING;
import static org.molgenis.data.meta.DefaultPackage.PACKAGE_DEFAULT;

import java.io.File;

import org.molgenis.data.FileRepositoryCollectionFactory;
import org.molgenis.data.importer.ImportService;
import org.molgenis.data.importer.ImportServiceFactory;
import org.molgenis.data.jobs.Progress;
import org.molgenis.data.meta.model.EntityMetaData;
import org.molgenis.data.support.FileRepositoryCollection;
import org.molgenis.file.ingest.execution.FileIngester;
import org.molgenis.file.ingest.execution.FileStoreDownload;
import org.molgenis.file.ingest.meta.FileIngest;
import org.molgenis.file.ingest.meta.FileIngestFactory;
import org.molgenis.file.ingest.meta.FileIngestMetaData;
import org.molgenis.file.model.FileMetaFactory;
import org.molgenis.fileingester.test.AbstractMolgenisSpringTest;
import org.molgenis.framework.db.EntityImportReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@ContextConfiguration(classes = { FileIngesterTest.Config.class })
public class FileIngesterTest extends AbstractMolgenisSpringTest
{
	@Autowired
	private FileIngester fileIngester;

	@Autowired
	private FileStoreDownload fileStoreDownloadMock;

	@Autowired
	private ImportServiceFactory importServiceFactoryMock;

	@Autowired
	private FileRepositoryCollectionFactory fileRepositoryCollectionFactoryMock;

	@Autowired
	private FileIngestFactory fileIngestFactory;

	private ImportService importServiceMock;
	private FileRepositoryCollection fileRepositoryCollectionMock;

	private final String entityName = "test";
	private final String url = "http://www.test.nl/test";
	private final String identifier = "identifier";
	private final File f = new File("");
	private final EntityImportReport report = new EntityImportReport();
	private FileIngest fileIngest;

	private Progress progress;

	@BeforeMethod
	public void setUp()
	{
		fileRepositoryCollectionMock = mock(FileRepositoryCollection.class);
		importServiceMock = mock(ImportService.class);
		progress = mock(Progress.class);

		EntityMetaData entityMetaData = when(mock(EntityMetaData.class).getName()).thenReturn("target").getMock();
		fileIngest = fileIngestFactory.create();
		fileIngest.set(FileIngestMetaData.ENTITY_META_DATA, entityMetaData);
		fileIngest.set(FileIngestMetaData.URL, url);
		fileIngest.set(FileIngestMetaData.LOADER, "CSV");
	}

	@Test
	public void ingest()
	{
		when(fileStoreDownloadMock.downloadFile(url, identifier, entityName + ".csv")).thenReturn(f);
		when(fileRepositoryCollectionFactoryMock.createFileRepositoryCollection(f))
				.thenReturn(fileRepositoryCollectionMock);
		when(importServiceFactoryMock.getImportService(f, fileRepositoryCollectionMock)).thenReturn(importServiceMock);
		when(importServiceMock.doImport(fileRepositoryCollectionMock, ADD_UPDATE_EXISTING, PACKAGE_DEFAULT))
				.thenReturn(report);

		fileIngester.ingest(entityName, url, "CSV", identifier, progress, "a@b.com,x@y.com");

	}

	@Test(expectedExceptions = RuntimeException.class)
	public void ingestError()
	{
		Exception e = new RuntimeException();
		when(fileStoreDownloadMock.downloadFile(url, identifier, entityName + ".csv")).thenThrow(e);

		fileIngester.ingest(entityName, url, "CSV", identifier, progress, "a@b.com,x@y.com");
	}

	@Configuration
	@ComponentScan({ "org.molgenis.file.ingest.meta", "org.molgenis.security.owned", "org.molgenis.file.model",
			"org.molgenis.data.jobs.model", "org.molgenis.auth" })
	public static class Config
	{
		@Bean
		public FileIngester fileIngester()
		{
			return new FileIngester(fileStoreDownload(), importServiceFactory(), fileRepositoryCollectionFactory(),
					fileMetaFactory());
		}

		@Bean
		public FileStoreDownload fileStoreDownload()
		{
			return mock(FileStoreDownload.class);
		}

		@Bean
		public ImportServiceFactory importServiceFactory()
		{
			return mock(ImportServiceFactory.class);
		}

		@Bean
		public FileRepositoryCollectionFactory fileRepositoryCollectionFactory()
		{
			return mock(FileRepositoryCollectionFactory.class);
		}

		@Bean
		public FileMetaFactory fileMetaFactory()
		{
			return mock(FileMetaFactory.class);
		}
	}
}
