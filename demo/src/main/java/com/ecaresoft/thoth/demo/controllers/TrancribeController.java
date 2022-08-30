package com.ecaresoft.thoth.demo.controllers;


import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ecaresoft.thoth.demo.TranscribeRequest;
import java.io.IOException;
import org.json.JSONObject;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.transcribe.model.GetMedicalTranscriptionJobRequest;
import software.amazon.awssdk.services.transcribe.model.GetMedicalTranscriptionJobResponse;
import software.amazon.awssdk.services.transcribe.model.StartMedicalTranscriptionJobResponse;
import com.ecaresoft.thoth.demo.services.Uploader;
import com.ecaresoft.thoth.demo.services.UrlAccess;
import com.ecaresoft.thoth.demo.exceptions.JobNameNotFoundException;
import com.ecaresoft.thoth.demo.exceptions.MediaUriNotFoundException;
import com.ecaresoft.thoth.demo.exceptions.OutputBucketNotfoundException;
import com.ecaresoft.thoth.demo.exceptions.RegionNotFoundException;

@RestController
public class TrancribeController {
  
    //this functions as a context variable
    private  TranscribeRequest contextTranscribeRequest;
    
    @GetMapping("/")
	public String index() {
		return "Welcome to Thoth";
	}

    //build the transcribe request 
    //returns a JSON with the critital data needed to recreate the contents of the transcribe request
    @PostMapping("/transcribereq")
    public JSONObject buildTranscribeRequest(@RequestBody TranscribeRequest transcribeRequest) {
        //update the context var
        this.contextTranscribeRequest = new TranscribeRequest( 
        transcribeRequest.getOutputBucket(),
        transcribeRequest.getJobName(),
        transcribeRequest.getMediaFileUri(),
        transcribeRequest.getRegion());
        
        //Paramater handling: make sure none of these are empty as they are mandatory for Amazon Transcribe Medical to function
        if (this.contextTranscribeRequest.getOutputBucket().isEmpty()) throw new OutputBucketNotfoundException();
        if (this.contextTranscribeRequest.getJobName().isEmpty()) throw new JobNameNotFoundException();
        if (this.contextTranscribeRequest.getMediaFileUri().isEmpty()) throw new MediaUriNotFoundException();
        if (this.contextTranscribeRequest.getRegion().isEmpty()) throw new RegionNotFoundException();
       
        //handle doctor ID
        //update the context if the doctor id field exists and is not empty
        if(transcribeRequest.getDoctorID()!= null){
            this.contextTranscribeRequest.setDoctorID(transcribeRequest.getDoctorID());
            //update JSON data
            this.contextTranscribeRequest.setJsonDATA(this.contextTranscribeRequest.getJsonDATA().put("doctorID", this.contextTranscribeRequest.getDoctorID()));
        }

        return this.contextTranscribeRequest.getJsonDATA();
    }

    //start the transcribe client
    //returns message acknowledging that client is ready
    @GetMapping("/startTranscribeClient")
    public String transcribeClientStart(){
        this.contextTranscribeRequest.startTranscribeClient();
        return this.contextTranscribeRequest.getTranscribeClient().toString() + " is up and ready for jobs ";
    }

    //start the medical transcription job in Amazon Transcribe Medical
    //returns message on succesful start
    @GetMapping("/startTranscription")
    public String startTranscriptionJob(){
        StartMedicalTranscriptionJobResponse medicalResponse = this.contextTranscribeRequest.getTranscribeClient().
        startMedicalTranscriptionJob(this.contextTranscribeRequest.getMedicalRequest());
        medicalResponse.medicalTranscriptionJob();
        return medicalResponse.toString() + " the transcription job has started";
    }

    //check job status
    //returns status message, 3 kinds
    // COMPLETED: job is done
    // IN_PROGRESS: job underway, all good!
    // FAILED: FailureRrason provides details on why job failed
    @GetMapping("/jobStatus")
    public String jobStatus(){
        GetMedicalTranscriptionJobRequest getMedicalRequest = GetMedicalTranscriptionJobRequest.builder().medicalTranscriptionJobName(this.contextTranscribeRequest.getJobName()).build();
        GetMedicalTranscriptionJobResponse getMedicalResponse = this.contextTranscribeRequest.getTranscribeClient().getMedicalTranscriptionJob(getMedicalRequest);
        this.contextTranscribeRequest.setJobStatus(getMedicalResponse.medicalTranscriptionJob().transcriptionJobStatusAsString());
        return " Medical job status: " + this.contextTranscribeRequest.getJobStatus();
    }

    //get transcripted audio, JOB STATUS must be COMPLETE for this to work
    //returns the context's updated transcripted audio
    @GetMapping("/getTranscription")
    public String getTranscription() throws IOException{
        //generate a time-limited URL to download JSON job result
        PresignedGetObjectRequest presignedGetObjectRequest = 
            UrlAccess.shareAaccess(this.contextTranscribeRequest.getOutputBucket(), this.contextTranscribeRequest.getJobName());
        JSONObject jobContent = UrlAccess.downloadDataAsJSON(presignedGetObjectRequest);
        String transcriptedAudio = jobContent.getJSONObject("results").getJSONArray("transcripts").getJSONObject(0).toString();
        Logger logger = Logger.getLogger(
            UrlAccess.class.getName());
        this.contextTranscribeRequest.setTranscriptedAudio(transcriptedAudio);
        logger.log(Level.INFO, "Job content transcript: \n {0}", this.contextTranscribeRequest.getTranscriptedAudio());
        return this.contextTranscribeRequest.getTranscriptedAudio();
    }

    //Upload transcript to a desired bucket
    @GetMapping("/uploadTranscription")
    public String uploadTranscription(){
        Uploader.uploadTranscript(
            this.contextTranscribeRequest.getRegiontype(),
            this.contextTranscribeRequest.getDoctorID(),
            this.contextTranscribeRequest.getOutputBucket(),
             this.contextTranscribeRequest.getJobName(), 
             this.contextTranscribeRequest.getTranscriptedAudio());
        return "Transcript has been uploaded";
    }

    //close transcribe client
    //do this after finishing transcriptions
    @GetMapping("/closeTranscribeClient")
    public String closeTranscribeClient(){
        this.contextTranscribeRequest.getTranscribeClient().close();
        return "Transcribe client has been closed";
    }
}
