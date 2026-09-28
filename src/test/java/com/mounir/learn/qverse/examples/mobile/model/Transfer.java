package com.mounir.learn.qverse.examples.mobile.model;

/** Business data for a transfer (Java record, mapped by Jackson from testdata/transfer.json). */
public record Transfer(String fromAccount, String toAccount, String amount) {
}
