package com.example.demo;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.entity.DownloadStatus;
import com.example.demo.repository.DownloadStatusRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT, classes = FileDownloaderApplicationWithoutExecutors.class)
@AutoConfigureMockMvc
public class DownloadPollerIntegrationTest {
	
	private static final DownloadType DEFAULT_TYPE = DownloadType.HTTP;
	private static final Integer DEFAULT_PRIORITY = 1;
	
	@Autowired
	private DownloadStatusRepository repository;
	@Autowired
	private DownloadPoller poller;
	
	@Before
	public void init() {
		repository.deleteAll();
	}
	
	@Test
	public void whenAllFilesHasStatusFinished_thenPoolerIsNotReturningAnything() throws Exception {
		//given
		createDwonload(DEFAULT_PRIORITY, DownloadState.FINISHED);
		
		//when
		Optional<DownloadStatus> download = poller.getNextFileToDownload(DEFAULT_TYPE);
		
		//then
		Assert.assertFalse(download.isPresent());
	}
	
	@Test
	public void whenAllFilesHasStatusError_thenPoolerIsNotReturningAnything() throws Exception {
		//given
		createDwonload(DEFAULT_PRIORITY, DownloadState.ERROR);
		
		//when
		Optional<DownloadStatus> download = poller.getNextFileToDownload(DEFAULT_TYPE);
		
		//then
		Assert.assertFalse(download.isPresent());
	}
	
	@Test
	public void whenAllFilesHasStatusProgress_thenPoolerIsNotReturningAnything() throws Exception {
		//given
		createDwonload(DEFAULT_PRIORITY, DownloadState.PROGRESS);
		
		//when
		Optional<DownloadStatus> download = poller.getNextFileToDownload(DEFAULT_TYPE);
		
		//then
		Assert.assertFalse(download.isPresent());
	}
	
	@Test
	public void whenAtLeastOneFileWithStateProgress_thenPoolerReturnIt() throws Exception {
		//given
		createDwonload(DEFAULT_PRIORITY, DownloadState.PROGRESS);
		
		//when
		Optional<DownloadStatus> download = poller.getNextFileToDownload(DEFAULT_TYPE);
		
		//then
		Assert.assertFalse(download.isPresent());
	}
	
	@Test
	public void whenTwoFileWithStateProgress_thenPoolerReturnFileWithHigherPriority() throws Exception {
		//given
		Integer higherPriority = 5;
		createDwonload(DEFAULT_PRIORITY, DownloadState.STARTED);
		createDwonload(higherPriority, DownloadState.STARTED);
		
		//when
		Optional<DownloadStatus> download = poller.getNextFileToDownload(DEFAULT_TYPE);
		
		//then
		Assert.assertTrue(download.isPresent());
		Assert.assertEquals(higherPriority, download.get().getPriority());
	}
	
	@Test
	public void whenFileTypeIsS3ButAskedForHTTP_IsNotReturningAnything() throws Exception {
		//given
		createDwonload(DEFAULT_PRIORITY, DownloadState.STARTED, DownloadType.S3);
		
		//when
		Optional<DownloadStatus> download = poller.getNextFileToDownload(DEFAULT_TYPE);
		
		//then
		Assert.assertFalse(download.isPresent());
	}
	
	private void createDwonload(Integer priority, DownloadState state) {
		createDwonload(priority, state, DEFAULT_TYPE);
	}
	
	private void createDwonload(Integer priority, DownloadState state, DownloadType type) {
		DownloadStatus download = new DownloadStatus();
		
		download.setType(type.toString());
		download.setPriority(priority);
		download.setUrl("http:/google/images/1.avi");
		download.setState(state.toString());
		
		repository.save(download);
	}

}
