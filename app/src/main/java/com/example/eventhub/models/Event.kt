package com.example.eventhub.models

data class Event(
    var eventimage: String ?= null,
    var eventname: String ?= null,
    var eventdate: String ?= null,
    var eventvenue: String ?= null,
    var eventbyuser: String ?= null)
