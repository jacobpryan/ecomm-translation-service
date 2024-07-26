# ecomm-translation-service

> Kubernetes Microservice in use for the translation of Electronic Communication messages and publishing based on the source's configured endpoint to a vendor's API. This repository has been scrubbed for any sensitive data for sharing.

### Current State
#### Translation Service Current State is as follows:
* Translation Service is functional end-to-end using **_MOCKED_** data
    * Sqs -> Polling Service -> EcommDb (**_Connection Only_**) -> Translate Ecomm Content JSON to OCRequestBody -> Ability to PUT Attachment files (**_byte[] datatype_**) to /v2/files endpoint -> POST final OCRequestBody to /v2/conversation endpoint -> Return reconciliation id (_plus error handling_)
* Starts with Autoscaled (Based on Queue Size) Pods configured to connect to the following:
    * SQS: ecomm-translation-sqs-int
    * S3: ecomm-common-api-int
    * OAUTH: GR Token Generation
    * OC_API: Ability to post Messages with or without attachments
        * /v2/conversations
        * /v2/files
    * JPA: ecomm-api-db-int-1:
        *  NOTE: All table configurations can be found by looking through the Entity classes
* Each Pod will start a scheduled poll to the queue
* Each Pod's polling listener will return 1 message per connection
* Each message is sent to OpenConnectorProxy where the message Translation to OC_RequestBody will occur
* That same service will then call OpenConnectorProxy to post the mocked OC_RequestBody via /v2/conversation

#### Postman Testing
Downloaded the latest Postman Collection .json in `./postman-testing/Collections`
* Service Tests (**_Requests send directly to local Translation-Service_**):
    * Base URL: `http://localhost:8443/api/v1/ecomm-translation-service`
    * TestE2EMockedMessageController
        * Endpoint: `/oc/mock/v2/files`
        * Description: General E2E Message flow through Translation Service:
            1. Using mocked json in Postman Body, sends mocked SqsMessage with _EcommContentId_ through **ecomm-translation-sqs-int**.
            2. SqsMessage will be picked up by the Polling Service which retrieves the _EcommContentId_ value and passes to OpenConnectorProxy.
            3. OpenConnectorProxy sends mocked OCRequestBody to OpenConnectorClient for posting a message to `/v2/conversation` without an attachment.
    * Local OC API Tests
        * TestMockedConversationController
            * Endpoint: `/oc/mock/v2/conversation`
            * Description: Use OpenConnectorProxy to publish **_MOCKED_** OCRequestBody to OC API `/v2/conversations`.
        * TestMockedConversationWithAttachmentController
            * Endpoint: `/oc/mock/v2/conversation/attachment`
            * Description: Use OpenConnectorProxy to publish **_MOCKED_** attachment-file.txt OC API `/v2/files` and OCRequestBody w/Attachment to `/v2/conversations`.
        * TestMockedAttachmentController
            * Endpoint: `/oc/mock/v2/files`
            * Description: Use OpenConnectorProxy to publish **_MOCKED_** attachment-file.txt OC API `/v2/files`.
* Open Connector Api V2 Tests (**_Not run through local Translation-Service_**):
    * Base URL: `https://conversations.api.globalrelay.com`
    * Conversation W/OAuth
        * Endpoint: `/v2/conversations`
        * Description: Sends mocked request body directly to OC API.
    * Conversation - Attach File W/OAuth
        * Endpoint: `/v2/conversations`
        * Description: Sends mocked request body with added file content to OC API.
    * File Upload W/OAuth
        * Prerequisite: Attach file through Post Request Body and replace `{file_key}` with the file name.
        * Endpoint: `/v2/files/default/{file_key}`
        * Description: Sends provided file as binary type to OC API.

#### Critical Bugs
* None!

### Purpose of Repository
* Deliver Ecomm Translation Service
* Deliver a Project which contains more Developer Driven Documentation than any other NM Gitlab Project.
    * Delivered from this feature will be documentation and training material allowing new and experienced Ecomm Devs to more easily:
        * Build New Services
        * Fork from Existing Services
        * Maintain Production Services
        * Debug Production Issues
        * Enhance Existing Services
        * Train New Developers
        * Allow Experienced Devs to give technical KT more organically.
        * The possibilities are endless!
    * Jacob is leading this effort
* Establish or work towards establishing basic standards across the following areas:
    * Development
    * Gitlab Repository Management
    * Code
    * DevOps

### Readme Maintenance & Guidelines for the benefit of the Development team
Please try to keep your Remaining Feature Work tasks updated when creating a MR for at least uat (develop would be ideal).
Feel free to add to any sections as you progress in development. Goal is to keep this a a som

### Definitions for Common Terms
* Ecomm - Electronic Communication
* OC API - Global Relays Open Connector API Service - /v2/

### Noted Dependencies
* Gradle
* Java
* Springboot
* AWS
* Apache

### Setting up the Translation Service Locally
#### Properties Configuration
Create application-local.properties file from example.application-local.properties
- Fill in the empty environment variables secrets
- Reach out to team if you need secret values
- Your application-local.properties may already be configured, so make sure this is cleaned up at least before uat if the case.

#### Running via Gradle - need BG-ADMIN access
1. Install the AWS CLI if you do not have it: <https://docs.aws.amazon.com/cli/latest/userguide/install-macos.html>
2. Grab AWS ENV variables from **NWM-LPA-NON** using the **BG-ADMIN** role here: <https://nm.awsapps.com/start#/>
    - AWS_ACCESS_KEY_ID = XXXXX
    - AWS_SECRET_ACCESS_KEY = XXXXX
    - AWS_SESSION_TOKEN = XXXXX
3. Copy and paste the 3 variables into the [./run-local.sh]() script in lines `4-6`.
4. Remove any `"` if present from Dashboard
5. Run script [./run-local.sh](run-local.sh) to start the project with the local spring profile.

#### Running via Docker
* TODO

#### Dependent Resources when running Locally
* Gradle Java Service - Running in the local profile on your machine at http://localhost:8443/api/v1/ecomm-translation-service
* AWS SQS - Running with int profile in account NWM-LPA-NON
  * Because your testing directly against int, be careful of mixing up messages if any other devs are connected to the SQS.
    * This will be ok long term due to branch management strategy and keeping qa and the most stable non-prod environment.
* AWS S3 - Running with int profile in account NWM-LPA-NON
  * S3Client is currently configured but not being used in Application
* AWS RDS - Not configured currently, but will run with int profile in account NWM-LPA-NON

#### Postman Testing
You will find everything needed in the /postman-testing folder to start api driven development and testing.
* Collection
  * Grab the json file from the Collection folder and import that into Postman. That should basically be it.
* Documentation
  * Folder contains Open-Connector specific documentation including swagger.yml file and reference pdf.
* Figure out how to call the /test-send-message api to push messages to the int-sqs to be consumed by your local machine.

#### Handy Dev Tips for Creating a Similar Service from Scratch
These tips will also be used as notes for creating more Comprehensive Dev guides
* ##### run-local.sh early and often
* ##### /keystore issues
* ##### Gradle Dependencies
  * run-local.sh especially when messing with Package dependencies 
* ##### IDE Issues
* ##### Learn the many resources at your disposal and when to use each
  * Gitlab
  * Confluence
  * NMGPT
  * Internet Browser
  * Copilot
  * Sharepoint
    * Let's sure hope not
  * Graph API
  * Other Devs

### Git Branch Management and Merging Strategy

| Git Branch Names | Deployment Environment                                               | AWS Account       | Git Branch  Perm/Temp | Usage | Merge Rules | Description                                                                                                                                                                                                                                                                                                                                |
|------------------|----------------------------------------------------------------------|-------------------|--------------------------|-------|-------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| main | prod                                                                 | HUBPROD/NMW-LPA   | Permanent | Production deployment | 2 Reviews and Approvals Required | The master branch is your production code. This is a protected branch and should never be directly modified unless in an emergency.                                                                                                                                                                                                        |
| uat | qa | HUBQA/NMW-LPA-NON | Permanent | Ensure a stable version of NonProd is always running. | 1 Review and Approval Required | Code merged into this branch must be configured/tested for any qa specific resources before being merged. Keep this branch consistently running and stable; will revert from here if needed. The uat branch may merge into main. Try to maintain a resources/service level definition as much division as possible between uat and deploy. |
| develop | int                                                                  | HUBINT/NMW-LPA-NON | Permanent | Test deployments across all dev branches; testing ground for recent changes. It's okay if you break this, just clean up your messes. | 1 Review and Approval Required | The develop branch is your intermediate branch. The develop branch contains code that may be ahead of main but is behind any of the feature branches.                                                                                                                                                                                      |
| feature/branch-name | NMW-LPA-NON/local                                                    | NMW-LPA-NON/LOCAL | Temp | Local laptop initial development & debugging | No Approval Required | Code in the feature gitflow branch is new work. This is where most of the work will happen in the Git environment. The feature branch may not merge into main.                                                                                                                                                                             |
| hotfix/branch-name | prod/qa/int/prod                                                     | ALL               | Temp | This branch type is reserved only for putting out fires. Do what you need to do for the fix, just get approval before prod. | 2 Reviews and Approvals Required | The hotfix branch is used to create patches to an issue that need to be deployed faster then the feature branch. The hotfix branch may merge into main but will need to be backmerged into uat and develop                                                                                                                                 |
