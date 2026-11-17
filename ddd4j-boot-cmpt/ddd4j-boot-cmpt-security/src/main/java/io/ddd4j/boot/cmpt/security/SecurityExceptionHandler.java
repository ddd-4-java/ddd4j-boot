/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.cmpt.security;

import io.ddd4j.boot.core.ApiCode;
import io.ddd4j.boot.core.R;
import io.ddd4j.boot.core.exception.ExceptinHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.boot.biz.SpringSecurityBizMessageSource;
import org.springframework.security.boot.biz.exception.*;
import org.springframework.security.boot.jwt.exception.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@ControllerAdvice
@ResponseBody
@Slf4j
public class SecurityExceptionHandler extends ExceptinHandler {

    protected final MessageSourceAccessor messages = SpringSecurityBizMessageSource.getAccessor();

    // --- Security Default Error ---

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<R<String>> accessDeniedException(AccessDeniedException ex) {
        this.logException(ex);
        return new ResponseEntity<>(ApiCode.ACCESS_DENIED.toResponse(), HttpStatus.OK);
    }

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<R<String>> authenticationException(AuthenticationException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHC_FAIL, ex);
    }


    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({AuthenticationServiceException.class})
    @ResponseBody
    public ResponseEntity<R<String>> authenticationServiceException(AuthenticationServiceException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHZ_THIRD_PARTY_SERVICE, ex);
    }

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({InternalAuthenticationServiceException.class})
    public ResponseEntity<R<String>> internalAuthenticationServiceException(InternalAuthenticationServiceException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHZ_THIRD_PARTY_SERVICE, ex);
    }

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<R<String>> usernameNotFoundException(UsernameNotFoundException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHC_ACCOUNT_NOT_FOUND, ex);
    }

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<R<String>> badCredentialsException(BadCredentialsException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHC_BAD_CREDENTIALS, ex);
    }

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({DisabledException.class})
    public ResponseEntity<R<String>> disabledException(DisabledException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHC_ACCOUNT_DISABLED, ex);
    }

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({LockedException.class})
    public ResponseEntity<R<String>> lockedException(LockedException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHC_ACCOUNT_LOCKED, ex);
    }

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({AccountExpiredException.class})
    public ResponseEntity<R<String>> accountExpiredException(AccountExpiredException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHC_ACCOUNT_EXPIRED, ex);
    }

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({CredentialsExpiredException.class})
    public ResponseEntity<R<String>> credentialsExpiredException(CredentialsExpiredException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHC_CREDENTIALS_EXPIRED, ex);
    }

    // --- Security Biz Error ---

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({AuthenticationServiceExceptionAdapter.class})
    public ResponseEntity<R<String>> externalAuthenticationServiceException(AuthenticationServiceExceptionAdapter ex) {
        this.logException(ex);
        return new ResponseEntity<>(R.of(ex.getCode(), messages.getMessage(ex.getMsgKey(), ex.getMessage())), HttpStatus.OK);
    }

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({AuthenticationMethodNotSupportedException.class})
    public ResponseEntity<R<String>> authenticationMethodNotSupportedException(AuthenticationMethodNotSupportedException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHC_METHOD_NOT_ALLOWED, ex);
    }

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({AuthenticationOverRetryRemindException.class})
    public ResponseEntity<R<String>> authenticationOverRetryRemindException(AuthenticationOverRetryRemindException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHC_OVER_RETRY_REMIND, ex);
    }

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({AuthenticationCaptchaNotFoundException.class})
    public ResponseEntity<R<String>> authenticationCaptchaNotFoundException(AuthenticationCaptchaNotFoundException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHC_CAPTCHA_REQUIRED, ex);
    }

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({AuthenticationCaptchaExpiredException.class})
    public ResponseEntity<R<String>> authenticationCaptchaExpiredException(AuthenticationCaptchaExpiredException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHC_CAPTCHA_EXPIRED, ex);
    }

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({AuthenticationCaptchaIncorrectException.class})
    public ResponseEntity<R<String>> authenticationCaptchaIncorrectException(AuthenticationCaptchaIncorrectException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHC_CAPTCHA_INCORRECT, ex);
    }

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({AuthenticationTokenNotFoundException.class})
    public ResponseEntity<R<String>> authenticationTokenNotFoundException(AuthenticationTokenNotFoundException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHZ_TOKEN_REQUIRED, ex);
    }

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({AuthenticationTokenExpiredException.class})
    public ResponseEntity<R<String>> authenticationTokenExpiredException(AuthenticationTokenExpiredException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHZ_TOKEN_EXPIRED, ex);
    }

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({AuthenticationTokenInvalidException.class})
    public ResponseEntity<R<String>> authenticationTokenInvalidException(AuthenticationTokenInvalidException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHZ_TOKEN_INCORRECT, ex);
    }

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({AuthenticationTokenIncorrectException.class})
    public ResponseEntity<R<String>> authenticationTokenIncorrectException(AuthenticationTokenIncorrectException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHZ_TOKEN_INCORRECT, ex);
    }


    // --- Security JWT Error ---

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({AuthenticationJwtIssuedException.class})
    public ResponseEntity<R<String>> authenticationJwtIssuedException(AuthenticationJwtIssuedException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHZ_TOKEN_ISSUED, ex);
    }

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({AuthenticationJwtNotFoundException.class})
    @ResponseBody
    public ResponseEntity<R<String>> authenticationJwtNotFoundException(AuthenticationJwtNotFoundException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHZ_TOKEN_REQUIRED, ex);
    }

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({AuthenticationJwtExpiredException.class})
    public ResponseEntity<R<String>> authenticationJwtExpiredException(AuthenticationJwtExpiredException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHZ_TOKEN_EXPIRED, ex);
    }

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({AuthenticationJwtInvalidException.class})
    public ResponseEntity<R<String>> authenticationJwtInvalidException(AuthenticationJwtInvalidException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHZ_TOKEN_INVALID, ex);
    }

    /**
     * 401 (Unauthorized)
     */
    @ExceptionHandler({AuthenticationJwtIncorrectException.class})
    public ResponseEntity<R<String>> authenticationJwtIncorrectException(AuthenticationJwtIncorrectException ex) {
        return this.authenticationException(AuthResponseCode.SC_AUTHZ_TOKEN_INCORRECT, ex);
    }

    protected ResponseEntity<R<String>> authenticationException(AuthResponseCode responseCode, Exception ex) {
        this.logException(ex);
        String message = messages.getMessage(responseCode.getMsgKey(), ex.getMessage());
        return new ResponseEntity<>(R.of(responseCode.getCode(), message), HttpStatus.OK);
    }

}
