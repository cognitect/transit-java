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

## License

Copyright © 2014 Cognitect, Inc.

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
