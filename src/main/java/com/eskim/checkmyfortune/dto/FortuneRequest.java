package com.eskim.checkmyfortune.dto;

public record FortuneRequest(
	String name,
	String gender,
	String birthYear,
	String birthMonth,
	String birthDay,
	String fortuneType) {
}
