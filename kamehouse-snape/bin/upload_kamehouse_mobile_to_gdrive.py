import json
import os
import sys

from google.oauth2.credentials import Credentials
from google_auth_oauthlib.flow import InstalledAppFlow
from google.auth.transport.requests import Request
from googleapiclient.discovery import build
from googleapiclient.http import MediaFileUpload

from config.kamehouse_snape_cfg import kamehouse_snape_cfg
from pathlib import Path
from loguru import logger

SCOPES = ['https://www.googleapis.com/auth/drive.file']

def main():
    home_dir = str(Path.home())
    logger.info("Uploading kamehouse-mobile apk to google drive")
    uploadFile()

def uploadFile():
    folder_id = getGoogleDriveFolderId()
    file_path = getApFilePath()
    file_name = os.path.basename(file_path)    
    service = getService()
    try:
        query = f"name = '{file_name}' and '{folder_id}' in parents and trashed = false"
        response = service.files().list(q=query, spaces='drive', fields='files(id, name)').execute()
        files = response.get('files', [])
        media = MediaFileUpload(file_path, resumable=True)
        if files:
            logger.info("File exists in google drive, updating...")
            file_id = files[0]['id']
            file = service.files().update(
                fileId=file_id,
                media_body=media
            ).execute()
        else:
            logger.info("File doesn't exist in google drive, creating...")
            file_metadata = {
                'name': file_name,
                'parents': [folder_id]
            }
            file = service.files().create(
                body=file_metadata,
                media_body=media,
                fields='id'
            ).execute()
            print(f"No existing file found. Created New File ID: {file.get('id')}")
    except Exception as error:
        logger.error("Error uploading file to google drive. Error: " + str(error))
        sys.exit(1)
    logger.info ("File " + file_path + " uploaded successfully to google drive")

def getGoogleDriveFolderId():
    target_folder_id = kamehouse_snape_cfg.get('upload_kamehouse_mobile_to_gdrive', 'target_folder_id')
    logger.debug("target_folder_id=" + target_folder_id)
    return target_folder_id

def getApFilePath():
    home_dir = str(Path.home())
    apk_path = kamehouse_snape_cfg.get('upload_kamehouse_mobile_to_gdrive', 'apk_path')
    apk_filename = kamehouse_snape_cfg.get('upload_kamehouse_mobile_to_gdrive', 'apk_filename')
    logger.debug("apk_path=" + apk_path)
    logger.debug("apk_filename=" + apk_filename)
    return home_dir + "/" + apk_path + "/" + apk_filename

def getService():
    home_dir = str(Path.home())
    credentials_path = kamehouse_snape_cfg.get('upload_kamehouse_mobile_to_gdrive', 'credentials_path')
    credentials_file = home_dir + "/" + credentials_path + "/credentials.json"
    token_path = kamehouse_snape_cfg.get('upload_kamehouse_mobile_to_gdrive', 'token_path')
    token_file = home_dir + "/" + token_path + "/token.json"
    logger.debug("credentials_path=" + credentials_path)
    logger.debug("token_path=" + token_path)
    creds = None
    # The file token.json stores the user's access and refresh tokens
    try:
        if os.path.exists(token_file):
            creds = readTokenFile(token_file)
        # If there are no credentials available, login
        if not creds or not creds.valid:
            if creds and creds.refresh_token:
                # google-auth versions older than 1.6.0 don't restore the access token or the expiry
                # from the token file, so the loaded credentials are always invalid and are never
                # reported as expired. Refresh whenever there's a refresh token instead of relying
                # on creds.expired, otherwise it asks for a new authorization on every run.
                try:
                    creds.refresh(Request())
                except Exception as error:
                    logger.warning("Failed to refresh the google drive token. "
                        + "Starting a new authorization flow. Error: " + str(error))
                    creds = None
            if not creds:
                flow = InstalledAppFlow.from_client_secrets_file(credentials_file, SCOPES)
                creds = flow.run_local_server(port=0)
            # Save the credentials for the next run
            os.makedirs(os.path.dirname(token_file), exist_ok=True)
            with open(token_file, 'w') as token:
                token.write(credentialsToJson(creds))
        return build('drive', 'v3', credentials=creds)
    except Exception as error:
        logger.error("Error setting up credentials for google drive. Error: " + str(error))
        sys.exit(1)

def readTokenFile(token_file):
    # Return None on an empty or invalid token file so a new authorization flow runs instead of failing
    try:
        return Credentials.from_authorized_user_file(token_file, SCOPES)
    except Exception as error:
        logger.warning("Ignoring invalid token file " + token_file + ". Error: " + str(error))
        return None

def credentialsToJson(creds):
    # google-auth versions older than 1.6.0 don't implement Credentials.to_json()
    if hasattr(creds, 'to_json'):
        return creds.to_json()
    return json.dumps({
        'token': creds.token,
        'refresh_token': creds.refresh_token,
        'token_uri': creds.token_uri,
        'client_id': creds.client_id,
        'client_secret': creds.client_secret,
        'scopes': ' '.join(creds.scopes) if creds.scopes else None
    })

if __name__ == "__main__":
    main()
