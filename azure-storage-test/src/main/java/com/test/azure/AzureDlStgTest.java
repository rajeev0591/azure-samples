package com.test.azure;

import java.io.FileNotFoundException;

import com.azure.storage.common.StorageSharedKeyCredential;
import com.azure.storage.file.datalake.DataLakeDirectoryClient;
import com.azure.storage.file.datalake.DataLakeFileClient;
import com.azure.storage.file.datalake.DataLakeFileSystemClient;
import com.azure.storage.file.datalake.DataLakeServiceClient;
import com.azure.storage.file.datalake.DataLakeServiceClientBuilder;

public class AzureDlStgTest {

	public static void main(String[] args) throws FileNotFoundException {
		
		//you can either use connection string or access keys
		// i have used access keys here... 1st param is you storage account name, 2nd is access key
		DataLakeServiceClient clientService = getDataLakeServiceClient("",
				"");

		System.out.println("Connected to storage account");
		
		createFileSystem(clientService);

		uploadFile(clientService.getFileSystemClient("my-file-system"));

		System.out.println("Upload Complete");

	}

	public static DataLakeFileSystemClient createFileSystem(DataLakeServiceClient serviceClient) {

		return serviceClient.createFileSystem("my-file-system");
	}

	public static DataLakeServiceClient getDataLakeServiceClient(String accountName, String accountKey) {

		StorageSharedKeyCredential sharedKeyCredential = new StorageSharedKeyCredential(accountName, accountKey);

		DataLakeServiceClientBuilder builder = new DataLakeServiceClientBuilder();

		builder.credential(sharedKeyCredential);
		builder.endpoint("https://" + accountName + ".dfs.core.windows.net");

		return builder.buildClient();
	}

	public static void uploadFile(DataLakeFileSystemClient fileSystemClient) throws FileNotFoundException {

		System.out.println("Url is:" + fileSystemClient.getFileSystemUrl());

		DataLakeDirectoryClient directoryClient = fileSystemClient.getDirectoryClient("inputdataliv");

		DataLakeFileClient fileClient = directoryClient.getFileClient("reds.jpg");

		fileClient.uploadFromFile("C:\\Users\\Rajeev\\Pictures\\Saved Pictures\\liverpool.jpg");
	}

}
