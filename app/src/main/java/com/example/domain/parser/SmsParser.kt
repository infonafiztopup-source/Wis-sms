package com.example.domain.parser

import com.example.domain.model.ParsedSmsResult

interface SmsParser {
    val parserId: String
    fun canParse(sender: String, body: String): Boolean
    fun parse(sender: String, body: String): ParsedSmsResult
}
