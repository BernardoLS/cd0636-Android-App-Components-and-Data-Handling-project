/**
 * AWS Lambda entry point for the Spire API.
 *
 * Wraps the shared Express app (app.js) with serverless-http so the same
 * routes run unchanged behind a Lambda Function URL. Function URLs deliver
 * API Gateway "payload format 2.0" events, which serverless-http detects
 * automatically (via event.rawPath).
 */

const serverless = require("serverless-http");
const app = require("./app");

module.exports.handler = serverless(app);
