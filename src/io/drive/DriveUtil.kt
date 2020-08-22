package io.drive

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import java.io.*

object DriveUtil {
    private const val APPLICATION_NAME = "Google Drive API Java Quickstart"
    private val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
    private val CREDENTIALS_FOLDER = File(
            "C:/Users/user/Desktop/Utility/Credentials") // Change Directory
    private const val CLIENT_SECRET_FILE_NAME = "client_secret.json"
    private val SCOPES = listOf<String>(DriveScopes.DRIVE)
    private var DATA_STORE_FACTORY: FileDataStoreFactory? = null
    private var HTTP_TRANSPORT: HttpTransport? = null
    private var _driveService: Drive? = null

    @get:Throws(IOException::class)
    val credentials: Credential
        get() {
            val clientSecretFilePath = File(CREDENTIALS_FOLDER, CLIENT_SECRET_FILE_NAME)
            if (!clientSecretFilePath.exists()) {
                throw FileNotFoundException(
                        "Please copy " + CLIENT_SECRET_FILE_NAME + " to folder: " + CREDENTIALS_FOLDER.absolutePath)
            }
            val `in`: InputStream = FileInputStream(clientSecretFilePath)
            val clientSecrets: GoogleClientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(`in`))
            val flow: GoogleAuthorizationCodeFlow = GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
                    clientSecrets, SCOPES).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build()
            return AuthorizationCodeInstalledApp(flow, LocalServerReceiver()).authorize("user")
        }

    @get:Throws(IOException::class)
    val driveService: Drive?
        get() {
            if (_driveService != null) {
                return _driveService
            }
            val credential = credentials
            _driveService = Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME)
                    .build()
            return _driveService
        }

    init {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
            DATA_STORE_FACTORY = FileDataStoreFactory(CREDENTIALS_FOLDER)
        } catch (t: Throwable) {
            t.printStackTrace()
            System.exit(1)
        }
    }
}