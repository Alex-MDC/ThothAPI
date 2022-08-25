package com.ecaresoft.thoth.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ecaresoft.thoth.demo.exceptions.JobNameNotFoundException;
import com.ecaresoft.thoth.demo.exceptions.MediaUriNotFoundException;
import com.ecaresoft.thoth.demo.exceptions.OutputBucketNotfoundException;
import com.ecaresoft.thoth.demo.exceptions.RegionNotFoundException;

@ControllerAdvice
public class RequestExceptionController {
    @ExceptionHandler(value = OutputBucketNotfoundException.class)
    public ResponseEntity<Object> bucketException(OutputBucketNotfoundException exception) {
       return new ResponseEntity<>("MANDATORY: OutputBucket must NOT be empty", HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = JobNameNotFoundException.class)
    public ResponseEntity<Object> jobNameException(JobNameNotFoundException exception) {
        return new ResponseEntity<>("MANDATORY: jobName must NOT be empty", HttpStatus.NOT_FOUND);
     }

    @ExceptionHandler(value = MediaUriNotFoundException.class)
    public ResponseEntity<Object> mediaException(MediaUriNotFoundException exception) {
        return new ResponseEntity<>("MANDATORY: MediaFileUri must NOT be empty", HttpStatus.NOT_FOUND);
     }

     @ExceptionHandler(value = RegionNotFoundException.class)
    public ResponseEntity<Object> regionException(RegionNotFoundException exception) {
        return new ResponseEntity<>("MANDATORY: region must NOT be empty. \n ADDITIONAL CRITICAL INFO: must be a supported region by Amazon Transcribe Medical. Find details at https://docs.aws.amazon.com/general/latest/gr/transcribe.html \nand\n  https://aws.amazon.com/transcribe/faqs/?nc=sn&loc=5#:~:text=Q.%20In%20which%20AWS%20regions%20is%20Amazon%20Transcribe%20Medical%20available%3F ", HttpStatus.NOT_FOUND);
     }
}
