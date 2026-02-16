package com.fiserv.uba.gateway.dto;

import javax.validation.constraints.NotBlank;

public class OtpValidateRequest {

    @NotBlank(message = "OTP session ID is required")
    private String otpSessionId;
    
    @NotBlank(message = "OTP is required")
    private String otp;

    public String getOtpSessionId() {
        return otpSessionId;
    }

    public void setOtpSessionId(String otpSessionId) {
        this.otpSessionId = otpSessionId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
