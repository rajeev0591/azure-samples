package com.test.azure;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.SharedAccessBlobPermissions;
import com.microsoft.azure.storage.blob.SharedAccessBlobPolicy;

public class AzureSasTest {

	static String containerName = "data";
	static String blobname = "test1.txt";
	//specify your connection string
	static String connectionString = "";

	public static void main(String[] args)
			throws StorageException, InvalidKeyException, URISyntaxException, IOException, InterruptedException {
		CloudBlobClient blobClient = getBlobServiceClient(connectionString);
		CloudBlobContainer container = blobClient.getContainerReference(containerName);
		if (!container.exists()) {
			System.out.println(String.format("Container '%s' not found", containerName));
			System.exit(1);
		} else {
			System.out.println("Found container..." + container.getUri());
		}
		URI url = new URI(generateSasToken(container));
		CloudBlockBlob blob = new CloudBlockBlob(url);
		blob.uploadFromFile("C:\\AzureTest\\test.txt");
		System.out.println("Upload complete...");
	}

	/**
	 * @param container
	 * @return
	 * @throws StorageException
	 * @throws InvalidKeyException
	 * @throws URISyntaxException
	 * @throws InterruptedException
	 */
	private static String generateSasToken(CloudBlobContainer container)
			throws StorageException, InvalidKeyException, URISyntaxException, InterruptedException {

		// Create the container permissions.
		BlobContainerPermissions permissions = new BlobContainerPermissions();

		// define a general policy that allows reading for downloads
		SharedAccessBlobPolicy readPolicy = new SharedAccessBlobPolicy();
		readPolicy.setPermissions(EnumSet.of(SharedAccessBlobPermissions.READ));
		permissions.getSharedAccessPolicies().put("DownloadPolicy", readPolicy);

		// define a general policy that allows writing for uploads
		SharedAccessBlobPolicy writePolicy = new SharedAccessBlobPolicy();
		writePolicy.setPermissions(EnumSet.of(SharedAccessBlobPermissions.READ, SharedAccessBlobPermissions.WRITE,
				SharedAccessBlobPermissions.CREATE));
		permissions.getSharedAccessPolicies().put("UploadPolicy", writePolicy);

		container.uploadPermissions(permissions);

		// get reference to the Blob you want to generate the SAS for:
		CloudBlockBlob blob = container.getBlockBlobReference(blobname);

		SharedAccessBlobPolicy itemPolicy = new SharedAccessBlobPolicy();
		// calculate Start Time
		GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));

		// Specify the current time as the start time for the shared access
		// signature.
		//
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, -15); // backdate start time by 15 mins, doesn't work otherwise
		itemPolicy.setSharedAccessStartTime(calendar.getTime());
		
		calendar.add(Calendar.HOUR, 10);
		itemPolicy.setSharedAccessExpiryTime(calendar.getTime());

		// generate Upload SAS
		//once token is generated you can cache it so you can use later if not expired or share it with users
		String sasToken = blob.generateSharedAccessSignature(itemPolicy, "UploadPolicy");
				
		String sasUri = String.format("%s?%s", blob.getUri(), sasToken);

		System.out.println("Uri is.." + sasUri);

		return sasUri;
	}

	private static CloudBlobClient getBlobServiceClient(String connectionString)
			throws InvalidKeyException, URISyntaxException {
		CloudStorageAccount account = CloudStorageAccount.parse(connectionString);
		return account.createCloudBlobClient();
	}

}
