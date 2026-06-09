# cd0636 Spire API — AWS Lambda Deployment

This document records how the Spire API (originally a **Firebase Cloud Functions v2**
HTTP function) was adapted for and deployed to **AWS Lambda**, exposed publicly via a
**Lambda Function URL**. Every command below was executed successfully and is kept here
for future reference / re-deploys.

## Summary

| Item | Value |
| --- | --- |
| AWS Account | `udacity-content` |
| Region | `us-east-1` |
| Function name | `cd0636-spire-api` |
| Execution role | `cd0636-spire-lambda-role` |
| Runtime / Arch | `nodejs22.x` / `arm64` |
| **Public URL** | `https://nmzmnl3hly5odkriglauqar4uy0hinjr.lambda-url.us-east-1.on.aws/` |

### Live endpoints
- `GET /` → health string
- `GET /api/buildings` (optional `?page=&limit=`)
- `GET /api/buildings/paginated?offset=&limit=`
- `GET /api/cities` (optional `?page=&limit=`)
- `GET /api/countries` (optional `?page=&limit=`)

## What changed in the code

The Express app is now platform-agnostic and lives in `functions/app.js` — the app and
all routes, extracted from the old `index.js`, with `firebase-functions/logger` replaced
by `console`. It has no Firebase dependency, so the Lambda bundle stays small.

`app.js` is shared by two entry points:
- `functions/lambda.js` — **AWS Lambda** entry: wraps `app.js` with
  [`serverless-http`](https://www.npmjs.com/package/serverless-http). Lambda Function URLs
  deliver API Gateway *payload format 2.0* events, which `serverless-http` detects via
  `event.rawPath`.
- `functions/index.js` — **Firebase** entry, unchanged behaviour: `onRequest` wrapping the
  shared `app.js` (Firebase deploy still works).

`functions/package.json` gains the `express` and `serverless-http` dependencies.

> The Lambda deployment bundle ships only `express` + `serverless-http` (not
> `firebase-admin`/`firebase-functions`), keeping the zip ~0.9 MB.

## Security notes (per the security best-practices guidance)

- **Least privilege IAM:** the execution role attaches only the AWS-managed
  `AWSLambdaBasicExecutionRole` (CloudWatch Logs). No `*:*` policies.
- **Encryption at rest:** Lambda code and environment are encrypted at rest by default
  using an AWS-managed KMS key.
- **Encryption in transit:** Lambda Function URLs are **HTTPS-only** (TLS).
- **Public access is intentional and reviewed** (project requirement). It is achieved
  with a Lambda Function URL.

---

## Reproducible deployment commands

> All commands assume the `udacity-content` profile is configured with valid (temporary)
> credentials for the target AWS account and region `us-east-1`.

### 0. Configure the AWS CLI profile (temporary credentials)

```bash
aws configure set aws_access_key_id     "<ACCESS_KEY_ID>"     --profile udacity-content
aws configure set aws_secret_access_key "<SECRET_ACCESS_KEY>" --profile udacity-content
aws configure set aws_session_token     "<SESSION_TOKEN>"     --profile udacity-content
aws configure set region us-east-1                            --profile udacity-content

# verify
aws sts get-caller-identity --profile udacity-content
```

### 1. Build the lean deployment bundle

From `server/`:

```bash
rm -rf build && mkdir -p build
cp functions/app.js functions/lambda.js functions/db.json build/

# build/package.json declares only the Lambda runtime deps:
#   { "main": "lambda.js", "dependencies": { "express": "^4.21.2", "serverless-http": "^3.2.0" } }
cd build
npm install --omit=dev --no-audit --no-fund
cd ..

# zip the bundle (code + node_modules at the archive root)
rm -f function.zip
cd build && zip -r -q -X ../function.zip app.js lambda.js db.json package.json node_modules && cd ..
```

> `build/` and `*.zip` are git-ignored.

### 2. Create the IAM execution role (least privilege)

```bash
cat > /tmp/cd0636-trust-policy.json <<'EOF'
{
  "Version": "2012-10-17",
  "Statement": [
    { "Effect": "Allow",
      "Principal": { "Service": "lambda.amazonaws.com" },
      "Action": "sts:AssumeRole" }
  ]
}
EOF

aws iam create-role \
  --role-name cd0636-spire-lambda-role \
  --assume-role-policy-document file:///tmp/cd0636-trust-policy.json \
  --description "Execution role for cd0636-spire-api Lambda (least privilege, logs only)" \
  --tags Key=Creator,Value=sudhanshu.kulshrestha@udacity.com Key=Project,Value=cd0636 \
  --profile udacity-content

aws iam attach-role-policy \
  --role-name cd0636-spire-lambda-role \
  --policy-arn arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole \
  --profile udacity-content
```

### 3. Create the Lambda function

```bash
aws lambda create-function \
  --function-name cd0636-spire-api \
  --runtime nodejs22.x \
  --role arn:aws:iam::<ACCOUNT_ID>:role/cd0636-spire-lambda-role \
  --handler lambda.handler \
  --zip-file fileb://function.zip \
  --timeout 15 \
  --memory-size 256 \
  --architectures arm64 \
  --description "Spire API (buildings/cities/countries) - migrated from Firebase Functions" \
  --tags Creator=sudhanshu.kulshrestha@udacity.com,Project=cd0636 \
  --region us-east-1 --profile udacity-content

aws lambda wait function-active --function-name cd0636-spire-api \
  --region us-east-1 --profile udacity-content
```

### 4. Create the public Function URL (HTTPS-only) + CORS

```bash
aws lambda create-function-url-config \
  --function-name cd0636-spire-api \
  --auth-type NONE \
  --cors '{"AllowOrigins":["*"],"AllowMethods":["GET"],"AllowHeaders":["content-type"],"MaxAge":86400}' \
  --region us-east-1 --profile udacity-content
```

### 5. Grant public invoke permissions

Public Function URL access on this account required **both** `lambda:InvokeFunctionUrl`
**and** `lambda:InvokeFunction` granted to all principals. With only the first statement,
anonymous requests returned `403 AccessDeniedException`.

```bash
# (a) allow anonymous invoke of the Function URL
aws lambda add-permission \
  --function-name cd0636-spire-api \
  --statement-id cd0636-public-function-url \
  --action lambda:InvokeFunctionUrl \
  --principal '*' \
  --function-url-auth-type NONE \
  --region us-east-1 --profile udacity-content

# (b) allow anonymous invoke of the function (required here for public access)
aws lambda add-permission \
  --function-name cd0636-spire-api \
  --statement-id cd0636-public-invoke-function \
  --action lambda:InvokeFunction \
  --principal '*' \
  --region us-east-1 --profile udacity-content
```

### 6. Verify

```bash
URL="https://nmzmnl3hly5odkriglauqar4uy0hinjr.lambda-url.us-east-1.on.aws"

curl -s "$URL/"                                      # -> Hello from AWS Lambda!
curl -s "$URL/api/buildings?page=1&limit=2"          # -> 2 buildings + pagination
curl -s "$URL/api/buildings"                         # -> 100 buildings
curl -s "$URL/api/cities"                            # -> 40 cities
curl -s "$URL/api/countries"                         # -> 13 countries
curl -s "$URL/api/buildings/paginated?offset=2&limit=3"
curl -s -w '\n[HTTP %{http_code}]\n' "$URL/api/buildings?page=999&limit=10"   # -> 404
```

### 7. Set CloudWatch Logs retention (30 days)

The Lambda log group was created with no expiry (`retentionInDays: None`); retention was
set to 30 days to cap log-storage cost.

```bash
aws logs put-retention-policy \
  --log-group-name /aws/lambda/cd0636-spire-api \
  --retention-in-days 30 --region us-east-1 --profile udacity-content

# verify
aws logs describe-log-groups --log-group-name-prefix /aws/lambda/cd0636-spire-api \
  --region us-east-1 --profile udacity-content \
  --query 'logGroups[].{name:logGroupName,retentionDays:retentionInDays}' --output table
```

---

## Useful inspection commands

```bash
aws lambda get-function-url-config --function-name cd0636-spire-api \
  --region us-east-1 --profile udacity-content

aws lambda get-policy --function-name cd0636-spire-api \
  --region us-east-1 --profile udacity-content --query Policy --output text | python3 -m json.tool
```
