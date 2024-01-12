/* eslint-disable no-magic-numbers */

export const BAD_REQUEST_STATUS_CODE = 400;
export const UNAUTHORIZED_STATUS_CODE = 401;

export const RESET_PASSWORD_CONTENT_TYPE = "application/vnd.menumate.userResetsPassword.v1+json";

export const EMAIL_ALREADY_IN_USE_ERROR = {"message":"Email already in use!", "path":"registerUser.arg0.email"};

export const WAIT_TIME = 500;

export const GET_LANGUAGE_CODE = (language) => language.substring(0, 3);
