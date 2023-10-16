package com.stanydesa.blog.application.user.controller;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("user")
public record SignUpRequest(String email, String username, String password) {}
//record is new kind of class primarily designed to model immutable data in a concise and expressive manner
//Fields in a record are implicitly final, making instances of the record immutable.
//Once a record is created, its state cannot be modified.
//Records automatically generate the equals(), hashCode(), and toString()
