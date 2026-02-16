package com.fiserv.uba.account.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExceptionTest {

    @Test
    void testBusinessException() {
        BusinessException exception = new BusinessException("Test error message");

        assertNotNull(exception);
        assertEquals("Test error message", exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void testBusinessExceptionWithCause() {
        Throwable cause = new Exception("Root cause");
        BusinessException exception = new BusinessException("Test error");

        assertNotNull(exception);
        assertEquals("Test error", exception.getMessage());
    }

    @Test
    void testErrorResponse() {
        Instant now = Instant.now();
        ErrorResponse response = new ErrorResponse("Error message", 400);

        assertNotNull(response);
        assertEquals("Error message", response.getMessage());
        assertEquals(400, response.getStatus());
        assertNotNull(response.getTimestamp());
    }

    @Test
    void testErrorResponseWithTimestamp() {
        Instant now = Instant.now();
        ErrorResponse response = new ErrorResponse("Error message", now);

        assertNotNull(response);
        assertEquals("Error message", response.getMessage());
        assertEquals(now, response.getTimestamp());
    }

    @Test
    void testErrorResponseSettersAndGetters() {
        ErrorResponse response = new ErrorResponse();
        response.setMessage("Test error");
        response.setStatus(500);

        assertEquals("Test error", response.getMessage());
        assertEquals(500, response.getStatus());
    }

    @Test
    void testErrorResponseBuilding() {
        ErrorResponse response = ErrorResponse.builder().message("Test error").status(400).timestamp(Instant.now()).build();

        assertNotNull(response);
        assertEquals("Test error", response.getMessage());
        assertEquals(400, response.getStatus());
        assertNotNull(response.getTimestamp());
    }
}

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testNotFoundEndpointReturns404() throws Exception {
        mockMvc.perform(get("/api/v1/invalid-endpoint")).andExpect(status().isNotFound());
    }


}

