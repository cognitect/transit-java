# transit-java

## Usage

Use maven to test.

Test

```
mvn test
```

## Build

### Version

The build version is automatically incremented.  To determine the
current build version:

    build/version

### Package

Packaging builds the maven artifacts in `target/package` and installs
it into the local maven repository.  To package:

    build/package

### Deploy

Deployment requires that the AWS CLI tools be installed (see
https://aws.amazon.com/cli/).

The deploy script runs the package script, and then deploys the
artifacts to the S3 bucket "mrel".  To deploy:

    build/deploy

### Docs

To build and deploy api documentation:

    build/doc

Deployment of docs requires that the AWS CLI tools be installed.
Additionally, a profile named transit-upload should be configured
in your AWS configuration with permission to put objects into
the "transit-docs" bucket:

   [profile transit-upload]
   region = us-east-1
   aws_access_key_id = <ACCESS_KEY_ID>
   aws_secret_access_key = <SECRET_ACCESS_KEY>

## Copyright and License

Copyright © 2014 Cognitect

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
