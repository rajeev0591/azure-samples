package com.test.azure;

import java.io.FileNotFoundException;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobProperties;
import com.azure.storage.blob.models.PublicAccessType;

public class AzureBlobTest {

	public static void main(String[] args) throws FileNotFoundException {
		
		
		//specify your storage account connection string here 
		String connectionString = "";

		BlobServiceClient blobService = getBlobServiceClient(connectionString);

		String containerName = "rajeevstg" + java.util.UUID.randomUUID();

		BlobContainerClient blobContainerClient = createContainer(blobService, containerName);

		setContainerAccess(blobContainerClient);

		uploadFile(blobContainerClient);

		System.out.println("Upload Complete");

		System.out.println("\nListing blobs...");

		for (BlobItem blobItem : blobContainerClient.listBlobs()) {
			System.out.println("\t" + blobItem.getName());
		}

		BlobProperties properties = blobContainerClient.getBlobClient("samp.txt").getProperties();
		System.out.println("Access tier: " + properties.getAccessTier());

	}

	public static BlobContainerClient createContainer(BlobServiceClient blobServiceClient, String containerName) {

		return blobServiceClient.createBlobContainer(containerName);
	}

	public static BlobServiceClient getBlobServiceClient(String connectionString) {

		return new BlobServiceClientBuilder().connectionString(connectionString).buildClient();

	}

	public static void uploadFile(BlobContainerClient blobContainerClient) throws FileNotFoundException {

		BlobClient blobClient = blobContainerClient.getBlobClient("samp.txt");

		System.out.println("\nUploading to Blob storage as blob:\n\t" + blobClient.getBlobUrl());

		blobClient.uploadFromFile("C:\\AzureTest\\sample.txt");
	}

	private static void setContainerAccess(BlobContainerClient containerClient) {
		containerClient.setAccessPolicy(PublicAccessType.BLOB, null);
	}

}
