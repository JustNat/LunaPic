package com.example.lunapic

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials

fun buildClient() : S3Client {
    return S3Client {
        credentialsProvider = StaticCredentialsProvider(
            credentials = Credentials(AWSCredentials.ACCESS_KEY, AWSCredentials.SECRET_KEY)
        )
        region = "sa-east-1"
    }
}