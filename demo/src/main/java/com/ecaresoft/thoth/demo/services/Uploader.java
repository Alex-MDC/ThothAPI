package com.ecaresoft.thoth.demo.services;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;

public class Uploader {
    
    private Uploader() {
        throw new IllegalStateException ("utility class");
    }

    //upload transcript as txt
    //with no doctor ID
    public static void uploadTranscript(Region region, String bucketName, String objectKey, String transcription, AwsBasicCredentials awsCreds ){
        uploadTranscript( region, "",  bucketName,  objectKey,  transcription, awsCreds);
    }

    //version with doctor ID
    public static void uploadTranscript(Region region,String doctorID, String bucketName, String objectKey, String transcription,AwsBasicCredentials awsCreds){
        if(!doctorID.isEmpty()){
            doctorID = doctorID+"/";
        }
        //start client
        S3Client client = S3Client.builder()
        .region(region)
        .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
        .build();

        //prepare and package content to upload
        //requestBody contains the transcript
        RequestBody requestBody = RequestBody.fromString(transcription);
        //PutObjectRequest specifies where to store and content type
        PutObjectRequest request = PutObjectRequest.builder()
        .bucket(bucketName)
        //key is the name for the file/object to upload and location
        // the "/" character specifies the folder structure within the bucket
        //here text/transcripts is the folder and objectKey names the txt file
        .key("text-transcripts/"+doctorID+objectKey+".txt")
        .contentType("text/plain")
        .build();
        //store the transcription
        client.putObject(request, requestBody);
        client.close();

    }

}
