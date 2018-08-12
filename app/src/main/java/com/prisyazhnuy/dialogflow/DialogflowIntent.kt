package com.prisyazhnuy.dialogflow

enum class DialogflowIntent(val intent: String) {
    REGISTRATION("Registration"),
    LOGIN("Login"),
    PHONE_NUMBER("Phone number"),
    PASSWORD("Password"),
    CONFIRM_PASSWORD("Confirm password"),
    EMAIL("Email")
}