package com.mesh_microservices.classroom_service.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * A service class that encapsulates the logic for interacting with Azure Blob Storage.
 * <p>
 * This service is responsible for uploading files to a designated container.
 */
@Service
public class AzureBlobService {

    /**
     * A client representing the specific container in Azure Blob Storage
     * where files will be stored.
     */
    private final BlobContainerClient containerClient;

    /**
     * Constructs the AzureBlobService and initializes the connection to Azure.
     * <p>
     * On startup, this constructor uses the application properties to build a
     * client, connect to the specified container, and ensures that the container
     * exists, creating it if necessary.
     *
     * @param connectionString The full connection string for the Azure Storage account.
     * @param containerName    The name of the blob container where files will be stored.
     */
    public AzureBlobService(
            @Value("${azure.storage.blob.connection-string}") String connectionString,
            @Value("${azure.storage.blob.container-name}") String containerName
    ) {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
        this.containerClient = blobServiceClient.getBlobContainerClient(containerName);
        this.containerClient.createIfNotExists();
    }

    /**
     * Uploads a given file to the Azure Blob Storage container.
     * <p>
     * This method generates a unique name for the file to prevent overwrites.
     * It also sets the appropriate HTTP headers (Content-Type and Content-Disposition)
     * on the blob, which instructs browsers to display the file inline if possible,
     * rather than immediately downloading it.
     *
     * @param file The {@link MultipartFile} to be uploaded.
     * @return The public URL of the successfully uploaded file.
     * @throws IOException if an I/O error occurs during the file upload process.
     */
    public String uploadFile(MultipartFile file) throws IOException {
        // Generate a unique file name to avoid collisions and ensure file integrity.
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        BlobClient blobClient = containerClient.getBlobClient(fileName);

        // Create headers to tell the browser how to handle the file when accessed via URL.
        BlobHttpHeaders headers = new BlobHttpHeaders()
                // Set the MIME type (e.g., "image/jpeg", "application/pdf").
                .setContentType(file.getContentType())
                // Instructs the browser to attempt to display the file within the browser window.
                .setContentDisposition("inline");

        // Upload the file's data stream and size. The 'true' flag allows overwriting if needed.
        blobClient.upload(file.getInputStream(), file.getSize(), true);
        // Apply the configured HTTP headers to the blob that was just uploaded.
        blobClient.setHttpHeaders(headers);

        return blobClient.getBlobUrl();
    }
}