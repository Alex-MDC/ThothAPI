package com.ecaresoft.thoth.demo.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.amazonaws.util.IOUtils;
import com.ecaresoft.thoth.demo.constants.transcribeConstants;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.presigner.PresignedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

 public class UrlAccess {

    private UrlAccess() {
        throw new IllegalStateException("Utility class");
      }
    
    public static PresignedGetObjectRequest shareAaccess(String bucket, String jobName, AwsBasicCredentials awsCreds, Region region) {
    // Create an S3Presigner using the user set region and credentials.
     S3Presigner presigner = S3Presigner.builder()
     .region(region)
     .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
     .build();

     //folder S3 path must be set statically
     // variable component is the name of the specific job object to access
     String key = transcribeConstants.JOBS_OUTPUT_FOLDER+"medical/"+ jobName+".json";

     // Create a GetObjectRequest to be pre-signed
     GetObjectRequest getObjectRequest =
             GetObjectRequest.builder()
                             .bucket(bucket)
                             .key(key)
                             .build();

     // Create a GetObjectPresignRequest to specify the signature duration
     GetObjectPresignRequest getObjectPresignRequest =
         GetObjectPresignRequest.builder()
                                .signatureDuration(Duration.ofMinutes(10))
                                .getObjectRequest(getObjectRequest)
                                .build();

     // Generate the presigned request
     PresignedGetObjectRequest presignedGetObjectRequest =
         presigner.presignGetObject(getObjectPresignRequest);

     // Log the presigned URL
     Logger logger = Logger.getLogger(
            UrlAccess.class.getName());

     logger.log(Level.INFO, "\nPresigned URL: {0}\n", presignedGetObjectRequest.url());
     

     // It is recommended to close the S3Presigner when it is done being used, because some credential
     // providers (e.g. if your AWS profile is configured to assume an STS role) require system resources
     // that need to be freed. If you are using one S3Presigner per application (as recommended), this
     // usually is not needed.
     presigner.close();
     return presignedGetObjectRequest;
    }

    public static JSONObject downloadDataAsJSON (PresignedRequest request) throws IOException {
        // Create a pre-signed request using one of the "presign" methods on S3Presigner
     PresignedRequest presignedRequest = request;

     // Create a JDK HttpURLConnection for communicating with S3
     HttpURLConnection connection = (HttpURLConnection) presignedRequest.url().openConnection();

     // Download the result of executing the request
     try (InputStream content = connection.getInputStream()) {
        Logger logger = Logger.getLogger(
            UrlAccess.class.getName());
         logger.log(Level.INFO, "Service returned response");
         return new JSONObject(IOUtils.toString(content));
     }
    }


}
