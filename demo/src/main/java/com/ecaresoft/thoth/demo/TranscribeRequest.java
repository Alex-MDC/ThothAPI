package com.ecaresoft.thoth.demo;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.transcribe.TranscribeClient;
import software.amazon.awssdk.services.transcribe.model.Media;
import software.amazon.awssdk.services.transcribe.model.StartMedicalTranscriptionJobRequest;
import com.ecaresoft.thoth.demo.constants.transcribeConstants;


public class TranscribeRequest {
	//actual contents
	private String outputBucket;
	private String jobName;
	private String mediaFileUri;
	private String outputKey;
	private String region;
	private String doctorID;
    private Region regiontype;
	private JSONObject jsonDATA = new JSONObject(new HashMap<>());
	private  Media mediaReq;
	private  StartMedicalTranscriptionJobRequest medicalRequest;
	private TranscribeClient transcribeClient;
	private String jobStatus;
	private String transcriptedAudio;
	//credentials
	private AwsBasicCredentials awsCreds;
	private String accessKeyId;
	private String secretAccessKey;

	//default constructor with default values
	//!!!changing the empty values may affect the exception handler as it checks for empty fields!!!
	//do NOT actually use values as is to run jobs, it will FAIL
	public TranscribeRequest(){
		Map<String, String> mapRequest = new HashMap<>();
        mapRequest.put("outPutbucket", "");
		mapRequest.put("jobName", "");
		mapRequest.put("mediaFileUri", "");
		mapRequest.put("outputKey", "");
		mapRequest.put("region", "");
		mapRequest.put("accesKeyId","");
		mapRequest.put("secretAccessKey","");
		this.jsonDATA = new JSONObject(mapRequest);
		this.outputBucket = "";
		this.jobName = "";
		this.mediaFileUri = "";
		this.outputKey = "";
		this.accessKeyId ="";
		this.secretAccessKey="";
		//note: leaving blank the parameters results in an error, default non-working values are provided instead for init
		this.awsCreds = AwsBasicCredentials.create("accessKeyId", "secretAccessKey");
		this.region = "";
		if(!this.region.isEmpty()){
			this.regiontype = Region.of(this.region);
		}
	}

	//constructs all the contents for a medical transcribe request
	//public TranscribeRequest(String outputBucket, String jobName, String mediaFileUri, String outputKey, String region)
	public TranscribeRequest(String outputBucket, String jobName, String mediaFileUri, String region, String accessKeyId, String secretAccessKey)
	{
        this.outputBucket = outputBucket;
        this.jobName = jobName;
        this.mediaFileUri = mediaFileUri;
		this.outputKey = transcribeConstants.JOBS_OUTPUT_FOLDER;
		this.doctorID ="";
		//credentials
		this.accessKeyId = accessKeyId;
		this.secretAccessKey = secretAccessKey;
		this.awsCreds = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        this.region = region;
		if(!this.region.isEmpty()){
			this.regiontype = Region.of(this.region);
		}
		this.mediaReq = Media.builder().mediaFileUri(this.mediaFileUri).build();
		this.medicalRequest = StartMedicalTranscriptionJobRequest.builder()
		.medicalTranscriptionJobName(this.jobName)
		.languageCode(transcribeConstants.LAN_CODE)
		.media(this.mediaReq)
		.outputBucketName(this.outputBucket)
		.outputKey(this.outputKey)
		.specialty(transcribeConstants.SPECIALTY)
		.type(transcribeConstants.TYPE)
		.build();
		Map<String, String> mapRequest = new HashMap<>();
        mapRequest.put("outPutbucket", outputBucket);
		mapRequest.put("jobName", jobName);
		mapRequest.put("mediaFileUri", mediaFileUri);
		mapRequest.put("outputKey", transcribeConstants.JOBS_OUTPUT_FOLDER);
		mapRequest.put("region", region);
		mapRequest.put("doctorID", this.doctorID);
		mapRequest.put("awsKeys",this.awsCreds.toString());
		this.jsonDATA = new JSONObject(mapRequest);
	}

	// start the transcribe client
	//will use the default profile config
	public TranscribeClient startClient(Region region){
		return TranscribeClient.builder()
		.region(region)
		.credentialsProvider(StaticCredentialsProvider.create(this.awsCreds))
		.build();
	}
	//builder with same region as this's 
	public TranscribeClient startClient(){
		return TranscribeClient.builder()
		.region(this.regiontype)
		.credentialsProvider(StaticCredentialsProvider.create(this.awsCreds))
		.build();
	}


	//getters and setters
	public Region getRegiontype() {
		return regiontype;
	}

	public void setRegiontype(Region regiontype) {
		this.regiontype = regiontype;
	}

	public String getOutputBucket() {
		return outputBucket;
	}

	public void setOutputBucket(String outputBucket) {
		this.outputBucket = outputBucket;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getMediaFileUri() {
		return mediaFileUri;
	}

	public void setMediaFileUri(String mediaFileUri) {
		this.mediaFileUri = mediaFileUri;
	}

	public String getOutputKey() {
		return outputKey;
	}

	public void setOutputKey(String outputKey) {
		this.outputKey = outputKey;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public JSONObject getJsonDATA() {
		return jsonDATA;
	}

	public void setJsonDATA(JSONObject jsonDATA) {
		this.jsonDATA = jsonDATA;
	}

	public Media getMediaReq() {
		return mediaReq;
	}

	public void setMediaReq(Media mediaReq) {
		this.mediaReq = mediaReq;
	}

	public StartMedicalTranscriptionJobRequest getMedicalRequest() {
		return medicalRequest;
	}

	public void setMedicalRequest(StartMedicalTranscriptionJobRequest medicalRequest) {
		this.medicalRequest = medicalRequest;
	}

	public TranscribeClient getTranscribeClient() {
		return transcribeClient;
	}

	public void setTranscribeClient(TranscribeClient transcribeClient) {
		this.transcribeClient = transcribeClient;
	}

	public void startTranscribeClient(){
		this.transcribeClient =TranscribeClient.builder()
		.region(this.regiontype)
		.credentialsProvider(StaticCredentialsProvider.create(this.awsCreds))
		.build();
	}

	public String getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	public String getTranscriptedAudio() {
		return transcriptedAudio;
	}

	public void setTranscriptedAudio(String transcriptedAudio) {
		this.transcriptedAudio = transcriptedAudio;
	}

	public String getDoctorID() {
		return doctorID;
	}

	public void setDoctorID(String doctorID) {
		this.doctorID = doctorID;
	}
	
	public AwsBasicCredentials getAwsCreds() {
		return awsCreds;
	}

	public void setAwsCreds(AwsBasicCredentials awsCreds) {
		this.awsCreds = awsCreds;
	}

	public String getAccessKeyId() {
		return accessKeyId;
	}

	public void setAccessKeyId(String accessKeyID) {
		this.accessKeyId = accessKeyID;
	}

	public String getSecretAccessKey() {
		return secretAccessKey;
	}

	public void setSecretAccessKey(String secretAccessKey) {
		this.secretAccessKey = secretAccessKey;
	}
	

}