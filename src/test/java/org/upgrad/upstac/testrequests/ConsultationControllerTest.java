package org.upgrad.upstac.testrequests;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.testrequests.consultation.ConsultationController;
import org.upgrad.upstac.testrequests.consultation.CreateConsultationRequest;
import org.upgrad.upstac.testrequests.consultation.DoctorSuggestion;
import org.upgrad.upstac.testrequests.lab.CreateLabResult;
import org.upgrad.upstac.testrequests.lab.TestStatus;
import org.upgrad.upstac.testrequests.RequestStatus;
import org.upgrad.upstac.testrequests.TestRequestQueryService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Slf4j
class ConsultationControllerTest {


    @Autowired
    ConsultationController consultationController;


    @Autowired
    TestRequestQueryService testRequestQueryService;


    @Test
    @WithUserDetails(value = "doctor")
    public void calling_assignForConsultation_with_valid_test_request_id_should_update_the_request_status(){

        TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_COMPLETED);

        //Implement this method

        //Create another object of the TestRequest method and explicitly assign this object for Consultation using assignForConsultation() method
        // from consultationController class. Pass the request id of testRequest object.
        TestRequest testRequestResult = consultationController.assignForConsultation(testRequest.getRequestId());

        //Use assertThat() methods to perform the following two comparisons
        //  1. the request ids of both the objects created should be same
        //  2. the status of the second object should be equal to 'DIAGNOSIS_IN_PROCESS'
        // make use of assertNotNull() method to make sure that the consultation value of second object is not null
        // use getConsultation() method to get the lab result
        assertThat(testRequestResult.getRequestId(), is(testRequest.getRequestId()));
        assertThat(testRequestResult.getStatus(), is(RequestStatus.DIAGNOSIS_IN_PROCESS));
        assertNotNull(testRequestResult.getConsultation());


    }

    public TestRequest getTestRequestByStatus(RequestStatus status) {
        return testRequestQueryService.findBy(status).stream().findFirst().get();
    }

    @Test
    @WithUserDetails(value = "doctor")
    public void calling_assignForConsultation_with_valid_test_request_id_should_throw_exception(){

        Long InvalidRequestId= -34L;

        //Implement this method


        // Create an object of ResponseStatusException . Use assertThrows() method and pass assignForConsultation() method
        // of consultationController with InvalidRequestId as Id
        ResponseStatusException result = assertThrows(ResponseStatusException.class,()->{
            consultationController.assignForConsultation(InvalidRequestId);
        });


        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "Invalid ID"
        assertThat(result.getMessage(),containsString("Invalid ID"));
    }

    @Test
    @WithUserDetails(value = "doctor")
    public void calling_updateConsultation_with_valid_test_request_id_should_update_the_request_status_and_update_consultation_details(){

        TestRequest testRequest = getTestRequestByStatus(RequestStatus.DIAGNOSIS_IN_PROCESS);

        //Implement this method
        //Create an object of CreateConsultationRequest and call getCreateConsultationRequest() to create the object. Pass the above created object as the parameter
        CreateConsultationRequest createConsultationRequest = getCreateConsultationRequest(testRequest);

        //Create another object of the TestRequest method and explicitly update the status of this object
        // to be 'COMPLETED'. Make use of updateConsultation() method from labRequestController class (Pass the previously created two objects as parameters)
        TestRequest testRequest1 = consultationController.updateConsultation(testRequest.getRequestId(),createConsultationRequest);
        //Use assertThat() methods to perform the following three comparisons
        //  1. the request ids of both the objects created should be same
        //  2. the status of the second object should be equal to 'COMPLETED'
        // 3. the suggestion of both the objects created should be same. Make use of getSuggestion() method to get the results.
        assertThat(testRequest1.getRequestId(), is(testRequest.getRequestId()));
        assertThat(testRequest1.getStatus(), is(RequestStatus.COMPLETED));
        assertThat(testRequest1.getConsultation().getSuggestion(),is(testRequest.getConsultation().getSuggestion()));

    }


    @Test
    @WithUserDetails(value = "doctor")
    public void calling_updateConsultation_with_invalid_test_request_id_should_throw_exception(){

        TestRequest testRequest = getTestRequestByStatus(RequestStatus.DIAGNOSIS_IN_PROCESS);
        Long InvalidRequestId= -34L;

        //Implement this method

        //Create an object of CreateConsultationRequest and call getCreateConsultationRequest() to create the object. Pass the above created object as the parameter

        CreateConsultationRequest createConsultationRequest = getCreateConsultationRequest(testRequest);
        // Create an object of ResponseStatusException . Use assertThrows() method and pass updateConsultation() method
        // of consultationController with a negative long value as Id and the above created object as second parameter
        //Refer to the TestRequestControllerTest to check how to use assertThrows() method
        ResponseStatusException result = assertThrows(ResponseStatusException.class,()->{
            consultationController.updateConsultation(InvalidRequestId,createConsultationRequest);
        });


        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "Invalid ID"
        assertThat(result.getMessage(),containsString("Invalid ID"));

    }

    @Test
    @WithUserDetails(value = "doctor")
    public void calling_updateConsultation_with_invalid_empty_status_should_throw_exception(){

        TestRequest testRequest = getTestRequestByStatus(RequestStatus.DIAGNOSIS_IN_PROCESS);

        //Implement this method

        //Create an object of CreateConsultationRequest and call getCreateConsultationRequest() to create the object. Pass the above created object as the parameter
        // Set the suggestion of the above created object to null.
         CreateConsultationRequest createConsultationRequest = getCreateConsultationRequest(testRequest);
        createConsultationRequest.setSuggestion(null);
        // Create an object of ResponseStatusException . Use assertThrows() method and pass updateConsultation() method
        // of consultationController with request Id of the testRequest object and the above created object as second parameter
        //Refer to the TestRequestControllerTest to check how to use assertThrows() method
        ResponseStatusException result = assertThrows(ResponseStatusException.class,()->{
            consultationController.updateConsultation(testRequest.getRequestId(),createConsultationRequest);
        });

        assertThat(result.getMessage(),containsString("ConstraintViolationException"));
    }

    public CreateConsultationRequest getCreateConsultationRequest(TestRequest testRequest) {

        CreateConsultationRequest createConsultationRequest = new CreateConsultationRequest();
        //Create an object of CreateLabResult and set all the values
        CreateLabResult createLabResult = new CreateLabResult();
        createLabResult.setBloodPressure("120/80");
        createLabResult.setComments("Good");
        createLabResult.setHeartBeat("97");
        createLabResult.setOxygenLevel("99");
        createLabResult.setTemperature("98.7");
        createLabResult.setResult(TestStatus.NEGATIVE);
        // if the lab result test status is Positive, set the doctor suggestion as "HOME_QUARANTINE" and comments accordingly
        if(createLabResult.getResult().equals(TestStatus.POSITIVE)){
            createConsultationRequest.setSuggestion(DoctorSuggestion.HOME_QUARANTINE);
            createConsultationRequest.setComments("Maintain Social Distance");
        }else if(createLabResult.getResult().equals(TestStatus.NEGATIVE)){
            // else if the lab result status is Negative, set the doctor suggestion as "NO_ISSUES" and comments as "Ok"
            createConsultationRequest.setSuggestion(DoctorSuggestion.NO_ISSUES);
            createConsultationRequest.setComments("Ok");
        }
        // Return the object
        return createConsultationRequest; // Replace this line with your code

    }

}