package com.example.eventhub.models

data class User(var name: String ?= null,
                var email: String ?= null,
                var userid: String ?= null,
                var userplace: String ?= null,
                var userphone: String ?= null,
                var userpfp: String ?= null)